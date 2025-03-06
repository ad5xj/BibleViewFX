package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;

import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class TrTag extends AbstractTag {

    public String getTagName()  { return "tr"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element row = OSISUtil.factory().createRow();
        if (ele != null)        { ele.addContent((Content) row); }
        return row;
    }
}
