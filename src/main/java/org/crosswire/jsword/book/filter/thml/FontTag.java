package org.crosswire.jsword.book.filter.thml;

import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class FontTag extends AbstractTag {

    public String getTagName() { return "font"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element seg = OSISUtil.factory().createSeg();
        StringBuilder buf = new StringBuilder();
        String color = attrs.getValue("color");
        if (color != null)
        {
            buf.append("color: ");
            buf.append(color);
            buf.append(';');
        }
        String size = attrs.getValue("size");
        if (size != null)
        {
            buf.append("font-size: ");
            buf.append(size);
            buf.append(';');
        }
        String type = buf.toString();
        if (type.length() > 0)
        {
            seg.setAttribute("type", type);
        }
        else
        {
            DataPolice.report(book, key, "Missing color/size attribute.");
            XMLUtil.debugSAXAttributes(attrs);
        }
        if (ele != null)
        {
            ele.addContent((Content) seg);
        }
        return seg;
    }
}