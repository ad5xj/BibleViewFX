package org.crosswire.jsword.book.sword;

import org.crosswire.common.crypt.Sapphire;

public class DataEntry {

    private static final byte SEP_NL = 10;
    private static final byte SEP_CR = 13;
    private static final byte SEP_BSLASH = 92;

    private int keyEnd;
    private int linkEnd;

    private byte[] data;

    private String name;
    private String charset;
    private String key;


    public DataEntry(String name, byte[] data, String charset) {
        this.name = name;
        this.data = (byte[]) data.clone();
        this.charset = charset;
        this.keyEnd = SwordUtil.findByte(this.data, (byte) 10);
    }

    public String getName() {
        return this.name;
    }

    public String getCharset() {
        return this.charset;
    }

    public String getKey() {
        if (this.key == null) {
            if (this.data.length == 0) {
                this.key = "";
                return this.key;
            }
            if (this.keyEnd < 0) {
                this.key = "";
                return this.key;
            }
            int end = this.keyEnd;
            if (end > 0 && this.data[end - 1] == 13) {
                end--;
            }
            if (end > 0 && this.data[end - 1] == 92) {
                end--;
            }
            if (end == 0) {
                this.key = "";
                return this.key;
            }
            this.key = SwordUtil.decode(this.name, this.data, end, this.charset);
        }
        return this.key;
    }

    public boolean isLinkEntry() {
        return (this.keyEnd + 6 < this.data.length && this.data[this.keyEnd + 1] == 64 && this.data[this.keyEnd + 2] == 76 && this.data[this.keyEnd + 3] == 73 && this.data[this.keyEnd + 4] == 78 && this.data[this.keyEnd + 5] == 75);
    }

    public String getLinkTarget() {
        int linkStart = this.keyEnd + 6;
        int len = getLinkEnd() - linkStart + 1;
        return SwordUtil.decode(this.name, this.data, linkStart, len, this.charset).trim();
    }

    public String getRawText(byte[] cipherKey) {
        int textStart = this.keyEnd + 1;
        cipher(cipherKey, textStart);
        return SwordUtil.decode(this.name, this.data, textStart, this.data.length - textStart, this.charset).trim();
    }

    public DataIndex getBlockIndex() {
        int start = this.keyEnd + 1;
        return new DataIndex(SwordUtil.decodeLittleEndian32(this.data, start), SwordUtil.decodeLittleEndian32(this.data, start + 4));
    }

    private int getLinkEnd() {
        if (this.linkEnd == 0) {
            this.linkEnd = SwordUtil.findByte(this.data, this.keyEnd + 1, (byte) 10);
            if (this.linkEnd == -1) {
                this.linkEnd = this.data.length - 1;
            }
        }
        return this.linkEnd;
    }

    public void cipher(byte[] cipherKey, int offset) {
        if (cipherKey != null && cipherKey.length > 0) {
            Sapphire cipherEngine = new Sapphire(cipherKey);
            for (int i = offset; i < this.data.length; i++) {
                this.data[i] = cipherEngine.cipher(this.data[i]);
            }
            cipherEngine.burn();
        }
    }
}
