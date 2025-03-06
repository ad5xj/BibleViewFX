package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class DivTag extends AbstractTag {
    private int level;

    public DivTag()           { this.level = 0; }

    public DivTag(int level)  { this.level = level; }

    public String getTagName()
    {
        if (this.level == 0)  { return "div"; }
        return "div" + this.level;
    }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        String typeAttr = attrs.getValue("type");
        if ("variant".equals(typeAttr))
        {
            Element seg = OSISUtil.factory().createSeg();
            seg.setAttribute("type", "x-variant");
            String classAttr = attrs.getValue("class");
            if (classAttr != null) { seg.setAttribute("subType", "x--" + classAttr); }
            if (ele != null)       { ele.addContent((Content) seg); }
            return seg;
        }

        Element div = OSISUtil.factory().createDiv();

        if (ele != null)           {  ele.addContent((Content) div); }
        return div;
    }
}