package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

import org.jdom2.Element;

import org.xml.sax.Attributes;

public class AliasTag extends AbstractTag {

    private Tag tag;
    private String alias;

    public AliasTag(String alias, Tag tag)
    {
        alias = alias;
        tag = tag;
    }

    public String getTagName()  { return alias; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        return tag.processTag(book, key, ele, attrs);
    }
}
