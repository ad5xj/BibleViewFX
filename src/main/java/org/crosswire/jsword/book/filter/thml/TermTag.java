package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class TermTag extends AbstractTag {

    public String getTagName()  { return "term"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element name = OSISUtil.factory().createName();
        if (ele != null)        { ele.addContent((Content) name); }
        return name;
    }
}