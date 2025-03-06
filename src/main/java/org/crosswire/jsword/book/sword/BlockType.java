package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.JSOtherMsg;

public enum BlockType { 
    BLOCK_BOOK("BOOK", 'b'),
    BLOCK_CHAPTER("CHAPTER", 'c'),
    BLOCK_VERSE("VERSE", 'v');

    private String name;

    private char indicator;

    BlockType(String name, char indicator) {
        this.name = name;
        this.indicator = indicator;
    }

    public char getIndicator() {
        return this.indicator;
    }

    public static BlockType fromString(String name) {
        for (BlockType v : values()) {
            if (v.name.equalsIgnoreCase(name)) {
                return v;
            }
        }
        throw new ClassCastException(JSOtherMsg.lookupText("BlockType {0} is not defined!", new Object[]{name}));
    }

    public String toString() {
        return this.name;
    }
}
