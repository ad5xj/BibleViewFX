package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class ATag extends AbstractTag {

    public String getTagName() { return "a"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element reference = OSISUtil.factory().createReference();
        String href = attrs.getValue("href");
        if (href != null && href.length() > 0)
        {
            reference.setAttribute("osisRef", href);
        }
        if (ele != null)
        {
            ele.addContent((Content) reference);
        }
        return reference;
    }
}