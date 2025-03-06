package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

import org.jdom2.Element;

import org.xml.sax.Attributes;

public interface Tag {

    String getTagName();

    Element processTag(Book paramBook, Key paramKey, Element paramElement, Attributes paramAttributes);

    void processContent(Book paramBook, Key paramKey, Element paramElement);
}
