package org.crosswire.jsword.book.sword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.NetUtil;

import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import java.net.URI;

public final class SwordUtil {

    public static int decodeLittleEndian32(byte[] data, int offset) {
        int byte1 = data[0 + offset] & 0xFF;
        int byte2 = (data[1 + offset] & 0xFF) << 8;
        int byte3 = (data[2 + offset] & 0xFF) << 16;
        int byte4 = (data[3 + offset] & 0xFF) << 24;
        return byte4 | byte3 | byte2 | byte1;
    }

    public static String decode(String key, byte[] data, String charset) {
        return decode(key, data, 0, data.length, charset);
    }

    public static String decode(String key, byte[] data, int length, String charset) {
        return decode(key, data, 0, length, charset);
    }

    public static String decode(String key, byte[] data, int offset, int length, String charset) {
        if ("WINDOWS-1252".equals(charset)) {
            clean1252(key, data, offset, length);
        }
        String txt = "";
        try {
            if (offset + length <= data.length) {
                txt = new String(data, offset, length, charset);
            }
        } catch (UnsupportedEncodingException ex) {
            log.error("{}: Encoding {} not supported.", new Object[]{key, charset, ex});
            txt = new String(data, offset, length);
        }
        return txt;
    }

    public static URI getExpandedDataPath(BookMetaData bookMetaData) throws BookException {
        URI loc = NetUtil.lengthenURI(bookMetaData.getLibrary(), bookMetaData.getProperty("DataPath"));
        if (loc == null) {
            throw new BookException(JSOtherMsg.lookupText("Missing data files for old and new testaments in {0}.", new Object[0]));
        }
        return loc;
    }

    protected static byte[] readRAF(RandomAccessFile raf, long offset, int theSize) throws IOException {
        raf.seek(offset);
        return readNextRAF(raf, theSize);
    }

    protected static byte[] readNextRAF(RandomAccessFile raf, int theSize) throws IOException {
        long offset = raf.getFilePointer();
        int size = theSize;
        long rafSize = raf.length();
        if (size == 0) {
            return new byte[0];
        }
        if (size < 0) {
            log.error("Nothing to read at offset = {} returning empty because negative size={}", Long.toString(offset), Integer.toString(size));
            return new byte[0];
        }
        if (offset >= rafSize) {
            log.error("Attempt to read beyond end. offset={} size={} but raf.length={}", new Object[]{Long.toString(offset), Integer.toString(size), Long.toString(rafSize)});
            return new byte[0];
        }
        if (offset + size > raf.length()) {
            log.error("Need to reduce size to avoid EOFException. offset={} size={} but raf.length={}", new Object[]{Long.toString(offset), Integer.toString(size), Long.toString(rafSize)});
            size = (int) (raf.length() - offset);
        }
        byte[] read = new byte[size];
        raf.readFully(read);
        return read;
    }

    protected static void writeRAF(RandomAccessFile raf, long offset, byte[] data) throws IOException {
        raf.seek(offset);
        writeNextRAF(raf, data);
    }

    protected static void writeNextRAF(RandomAccessFile raf, byte[] data) throws IOException {
        if (data == null) {
            return;
        }
        raf.write(data);
    }

    protected static byte[] readUntilRAF(RandomAccessFile raf, int offset, byte stopByte) throws IOException {
        raf.seek(offset);
        return readUntilRAF(raf, stopByte);
    }

    protected static byte[] readUntilRAF(RandomAccessFile raf, byte stopByte) throws IOException {
        long offset = raf.getFilePointer();
        int size = 0;
        int nextByte = -1;
        do {
            nextByte = raf.read();
            size++;
        } while (nextByte != -1 && nextByte != stopByte);
        return readRAF(raf, offset, size);
    }

    protected static void encodeLittleEndian32(int val, byte[] data, int offset) {
        data[0 + offset] = (byte) (val & 0xFF);
        data[1 + offset] = (byte) (val >> 8 & 0xFF);
        data[2 + offset] = (byte) (val >> 16 & 0xFF);
        data[3 + offset] = (byte) (val >> 24 & 0xFF);
    }

    protected static int decodeLittleEndian16(byte[] data, int offset) {
        int byte1 = data[0 + offset] & 0xFF;
        int byte2 = (data[1 + offset] & 0xFF) << 8;
        return byte2 | byte1;
    }

    protected static void encodeLittleEndian16(int val, byte[] data, int offset) {
        data[0 + offset] = (byte) (val & 0xFF);
        data[1 + offset] = (byte) (val >> 8 & 0xFF);
    }

    protected static int findByte(byte[] data, byte sought) {
        return findByte(data, 0, sought);
    }

    protected static int findByte(byte[] data, int offset, byte sought) {
        for (int i = offset; i < data.length; i++) {
            if (data[i] == sought) {
                return i;
            }
        }
        return -1;
    }

    private static void clean1252(String key, byte[] data, int offset, int length) {
        int end = offset + length;
        if (end > data.length) {
            end = data.length;
        }
        for (int i = offset; i < end; i++) {
            int c = data[i] & 0xFF;
            if ((c >= 0 && c < 32 && c != 9 && c != 10 && c != 13) || c == 129 || c == 141 || c == 143 || c == 144 || c == 157) {
                data[i] = 32;
                log.error("{} has bad character 0x{} at position {} in input.", new Object[]{key, Integer.toString(c, 16), Integer.toString(i)});
            }
        }
    }

    private static final Logger log = LoggerFactory.getLogger(SwordUtil.class);
}
