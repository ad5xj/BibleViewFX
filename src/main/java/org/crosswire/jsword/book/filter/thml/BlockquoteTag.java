package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class BlockquoteTag extends AbstractTag {

    public String getTagName() { return "blockquote"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element q = OSISUtil.factory().createQ();
        q.setAttribute("type", "blockquote");
        if (ele != null) { ele.addContent((Content) q); }
        return q;
    }
}
