package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;
import org.jdom2.Content;
import org.jdom2.Element;
import org.xml.sax.Attributes;

public class OlTag extends AbstractTag {

    public String getTagName()  { return "ol"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element list = OSISUtil.factory().createList();
        list.setAttribute("type", "x-ordered");
        if (ele != null)  { ele.addContent((Content) list); }
        return list;
    }
}