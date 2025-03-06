package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;

import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class UTag extends AbstractTag {

    public String getTagName()   { return "u"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element hi = OSISUtil.factory().createHI();
        hi.setAttribute("type", "underline");
        if (ele != null)         { ele.addContent((Content) hi); }
        return hi;
    }
}