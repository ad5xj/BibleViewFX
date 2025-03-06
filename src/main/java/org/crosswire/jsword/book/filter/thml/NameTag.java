package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;

import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class NameTag extends AbstractTag {
    
    public String getTagName() { return "name"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element seg = OSISUtil.factory().createSeg();
        if (ele != null) { ele.addContent((Content) seg); }
        return seg;
    }
}