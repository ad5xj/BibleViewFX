package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.xml.sax.Attributes;

public class ForeignTag extends AbstractTag {
    
    public String getTagName() { return "foreign"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element div = OSISUtil.factory().createForeign();
        String lang = attrs.getValue("lang");
        if (lang != null)      { div.setAttribute("lang", lang, Namespace.XML_NAMESPACE); }
        if (ele != null)       { ele.addContent((Content) div); }
        return div;
    }
}