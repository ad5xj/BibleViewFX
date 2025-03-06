package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class HTag extends AbstractTag {
    private int level;

    public HTag(int level)     { level = level; }

    public String getTagName() { return "h" + this.level; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element title = OSISUtil.factory().createTitle();
        title.setAttribute("level", Integer.toString(this.level));
        if (ele != null) { ele.addContent((Content) title); }
        return ele;
    }
}