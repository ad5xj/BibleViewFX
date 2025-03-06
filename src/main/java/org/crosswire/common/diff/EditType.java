package org.crosswire.common.diff;

public enum EditType {
    DELETE("Delete", '-'),
    INSERT("Insert", '+'),
    EQUAL("Equal", ' ');

    private String name;

    private char symbol;

    EditType(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public char getSymbol() {
        return this.symbol;
    }

    public static EditType fromString(String name) {
        for (EditType v : values()) {
            if (v.name().equalsIgnoreCase(name)) {
                return v;
            }
        }
        assert false;
        return null;
    }

    public static EditType fromSymbol(char symbol) {
        for (EditType v : values()) {
            if (v.symbol == symbol) {
                return v;
            }
        }
        assert false;
        return null;
    }

    public String toString() {
        return this.name;
    }
}
