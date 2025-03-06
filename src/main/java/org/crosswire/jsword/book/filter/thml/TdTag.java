package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;

import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class TdTag extends AbstractTag {

    public String getTagName()   { return "td"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element cell = OSISUtil.factory().createCell();
        if (attrs != null)
        {
            String rows = attrs.getValue("rowspan");
            if (rows != null)    { cell.setAttribute("rows", rows); }
            String cols = attrs.getValue("colspan");
            if (cols != null)    { cell.setAttribute("cols", cols); }
        }
        if (ele != null)         { ele.addContent((Content) cell);  }
        return cell;
    }
}