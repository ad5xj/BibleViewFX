package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;

import org.crosswire.jsword.passage.Key;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class NoteTag extends AbstractTag {
    
    public String getTagName()  { return "note"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element note = OSISUtil.factory().createNote();
        note.setAttribute("type", "x-StudyNote");
        if (ele != null) { ele.addContent((Content) note); }
        return note;
    }
}