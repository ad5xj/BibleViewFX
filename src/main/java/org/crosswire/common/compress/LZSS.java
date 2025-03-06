package org.crosswire.common.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;

public class LZSS extends AbstractCompressor {

    private static final short RING_SIZE = 4096;
    private static final short RING_WRAP = 4095;
    private static final short NOT_USED = 4096;

    private static final int MAX_STORE_LENGTH = 18;
    private static final int THRESHOLD = 3;


    private byte[] ringBuffer;

    private short matchPosition;
    private short matchLength;
    private short[] dad;
    private short[] leftSon;
    private short[] rightSon;

    private ByteArrayOutputStream out;

    public LZSS(InputStream input) {
        super(input);
        this.ringBuffer = new byte[4113];
        this.dad = new short[4097];
        this.leftSon = new short[4097];
        this.rightSon = new short[4353];
    }

    public ByteArrayOutputStream compress() throws IOException {
        this.out = new ByteArrayOutputStream(2048);
        byte[] codeBuff = new byte[17];
        initTree();
        codeBuff[0] = 0;
        short codeBufPos = 1;
        byte mask = 1;
        short s = 0;
        short r = 4078;
        Arrays.fill(this.ringBuffer, 0, r, (byte) 32);
        int readResult = this.input.read(this.ringBuffer, r, 18);
        if (readResult <= 0) {
            return this.out;
        }
        short len = (short) readResult;
        short i;
        for (i = 1; i <= 18; i = (short) (i + 1)) {
            insertNode((short) (r - i));
        }
        insertNode(r);
        do {
            if (this.matchLength > len) {
                this.matchLength = len;
            }
            if (this.matchLength < 3) {
                this.matchLength = 1;
                codeBuff[0] = (byte) (codeBuff[0] | mask);
                codeBufPos = (short) (codeBufPos + 1);
                codeBuff[codeBufPos] = this.ringBuffer[r];
            } else {
                codeBufPos = (short) (codeBufPos + 1);
                codeBuff[codeBufPos] = (byte) this.matchPosition;
                codeBufPos = (short) (codeBufPos + 1);
                codeBuff[codeBufPos] = (byte) (this.matchPosition >> 4 & 0xF0 | this.matchLength - 3);
            }
            mask = (byte) (mask << 1);
            if (mask == 0) {
                this.out.write(codeBuff, 0, codeBufPos);
                codeBuff[0] = 0;
                codeBufPos = 1;
                mask = 1;
            }
            short lastMatchLength = this.matchLength;
            for (i = 0; i < lastMatchLength; i = (short) (i + 1)) {
                readResult = this.input.read();
                if (readResult == -1) {
                    break;
                }
                byte c = (byte) readResult;
                deleteNode(s);
                this.ringBuffer[s] = c;
                if (s < 17) {
                    this.ringBuffer[s + 4096] = c;
                }
                s = (short) (s + 1 & 0xFFF);
                r = (short) (r + 1 & 0xFFF);
                insertNode(r);
            }
            for (i = (short) (i + 1); i < lastMatchLength;) {
                deleteNode(s);
                s = (short) (s + 1 & 0xFFF);
                r = (short) (r + 1 & 0xFFF);
                len = (short) (len - 1);
                if (len != 0) {
                    insertNode(r);
                }
            }
        } while (len > 0);
        if (codeBufPos > 1) {
            this.out.write(codeBuff, 0, codeBufPos);
        }
        return this.out;
    }

    public ByteArrayOutputStream uncompress() throws IOException {
        return uncompress(2048);
    }

    public ByteArrayOutputStream uncompress(int expectedSize) throws IOException {
        this.out = new ByteArrayOutputStream(expectedSize);
        byte[] c = new byte[18];
        int r = 4078;
        Arrays.fill(this.ringBuffer, 0, r, (byte) 32);
        byte flags = 0;
        int flagCount = 0;
        while (true) {
            if (flagCount > 0) {
                flags = (byte) (flags >> 1);
                flagCount--;
            } else {
                int readResult = this.input.read();
                if (readResult == -1) {
                    break;
                }
                flags = (byte) (readResult & 0xFF);
                flagCount = 7;
            }
            if ((flags & 0x1) != 0) {
                if (this.input.read(c, 0, 1) != 1) {
                    break;
                }
                this.out.write(c[0]);
                this.ringBuffer[r] = c[0];
                r = (short) (r + 1 & 0xFFF);
                continue;
            }
            if (this.input.read(c, 0, 2) != 2) {
                break;
            }
            short pos = (short) (c[0] & 0xFF | (c[1] & 0xF0) << 4);
            short len = (short) ((c[1] & 0xF) + 3);
            for (int k = 0; k < len; k++) {
                c[k] = this.ringBuffer[pos + k & 0xFFF];
                this.ringBuffer[r] = c[k];
                r = r + 1 & 0xFFF;
            }
            this.out.write(c, 0, len);
        }
        return this.out;
    }

    private void initTree() {
        Arrays.fill(this.dad, 0, this.dad.length, (short) 4096);
        Arrays.fill(this.leftSon, 0, this.leftSon.length, (short) 4096);
        Arrays.fill(this.rightSon, 0, this.rightSon.length, (short) 4096);
    }

    private void insertNode(short pos) {
        assert pos >= 0 && pos < 4096;
        int cmp = 1;
        short key = pos;
        short p = (short) (4097 + (this.ringBuffer[key] & 0xFF));
        assert p > 4096;
        this.leftSon[pos] = 4096;
        this.rightSon[pos] = 4096;
        this.matchLength = 0;
        while (true) {
            if (cmp >= 0) {
                if (this.rightSon[p] != 4096) {
                    p = this.rightSon[p];
                } else {
                    this.rightSon[p] = pos;
                    this.dad[pos] = p;
                    return;
                }
            } else if (this.leftSon[p] != 4096) {
                p = this.leftSon[p];
            } else {
                this.leftSon[p] = pos;
                this.dad[pos] = p;
                return;
            }
            short i = 0;
            for (i = 1; i < 18; i = (short) (i + 1)) {
                cmp = (this.ringBuffer[key + i] & 0xFF) - (this.ringBuffer[p + i] & 0xFF);
                if (cmp != 0) {
                    break;
                }
            }
            if (i > this.matchLength) {
                this.matchPosition = p;
                this.matchLength = i;
                if (i >= 18) {
                    break;
                }
            }
        }
        this.dad[pos] = this.dad[p];
        this.leftSon[pos] = this.leftSon[p];
        this.rightSon[pos] = this.rightSon[p];
        this.dad[this.leftSon[p]] = pos;
        this.dad[this.rightSon[p]] = pos;
        if (this.rightSon[this.dad[p]] == p) {
            this.rightSon[this.dad[p]] = pos;
        } else {
            this.leftSon[this.dad[p]] = pos;
        }
        this.dad[p] = 4096;
    }

    private void deleteNode(short node) {
        short q;
        assert node >= 0 && node < 4097;
        if (this.dad[node] == 4096) {
            return;
        }
        if (this.rightSon[node] == 4096) {
            q = this.leftSon[node];
        } else if (this.leftSon[node] == 4096) {
            q = this.rightSon[node];
        } else {
            q = this.leftSon[node];
            if (this.rightSon[q] != 4096) {
                do {
                    q = this.rightSon[q];
                } while (this.rightSon[q] != 4096);
                this.rightSon[this.dad[q]] = this.leftSon[q];
                this.dad[this.leftSon[q]] = this.dad[q];
                this.leftSon[q] = this.leftSon[node];
                this.dad[this.leftSon[node]] = q;
            }
            this.rightSon[q] = this.rightSon[node];
            this.dad[this.rightSon[node]] = q;
        }
        this.dad[q] = this.dad[node];
        if (this.rightSon[this.dad[node]] == node) {
            this.rightSon[this.dad[node]] = q;
        } else {
            this.leftSon[this.dad[node]] = q;
        }
        this.dad[node] = 4096;
    }
}
