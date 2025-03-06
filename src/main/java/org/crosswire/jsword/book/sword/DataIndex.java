package org.crosswire.jsword.book.sword;

public class DataIndex {
    private int offset;
    private int size;

    public DataIndex(int offset, int size) {
        this.offset = offset;
        this.size = size;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getSize() {
        return this.size;
    }
}
