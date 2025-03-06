package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class BigTag extends AbstractTag {

    public String getTagName() { return "big"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element hiEle = OSISUtil.factory().createHI();
        hiEle.setAttribute("type", "x-big");
        if (ele != null) { ele.addContent((Content) hiEle); }
        return hiEle;
    }
}
