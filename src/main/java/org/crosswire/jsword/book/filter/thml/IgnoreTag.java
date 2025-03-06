package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

public class IgnoreTag extends AnonymousTag {
    
    public IgnoreTag(String name)  { super(name); }

    public void processContent(Book book, Key key, Element ele)
    {
        Element parent = ele.getParentElement();
        parent.removeContent((Content) ele);
        parent.addContent(ele.removeContent());
    }
}