package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;

import org.crosswire.jsword.passage.Key;

import org.jdom2.Element;

import org.xml.sax.Attributes;

public class RootTag extends AbstractTag {
    protected static final String TAG_ROOT = "root";

    public String getTagName() { return "root"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        return OSISUtil.factory().createDiv();
    }
}
