package org.crosswire.jsword.book.filter.gbf;

public abstract class AbstractTag implements Tag {

    private String name;

    public AbstractTag(String name) { this.name = name; }

    public String getName()         { return this.name; }
}
