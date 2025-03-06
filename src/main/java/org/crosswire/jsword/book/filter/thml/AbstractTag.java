package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

import org.jdom2.Element;

import org.xml.sax.Attributes;

public abstract class AbstractTag implements Tag {

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        return null;
    }

    public void processContent(Book book, Key key, Element ele)
    {
    }
}
