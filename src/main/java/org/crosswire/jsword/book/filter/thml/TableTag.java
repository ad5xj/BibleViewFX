package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;

import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class TableTag extends AbstractTag {
    
    public String getTagName()  { return "table"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element table = OSISUtil.factory().createTable();
        if (attrs != null)
        {
            String attr = attrs.getValue("border");
            if (attr != null)  { table.setAttribute("border", attr); }
        }
        if (ele != null)  { ele.addContent((Content) table); }
        return table;
    }
}