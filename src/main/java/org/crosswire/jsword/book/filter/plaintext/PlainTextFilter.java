package org.crosswire.jsword.book.filter.plaintext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.StringUtil;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.passage.Key;

import java.util.List;

import org.jdom2.Content;
import org.jdom2.Element;

public class PlainTextFilter implements SourceFilter {

    private static Logger lgr = LoggerFactory.getLogger(PlainTextFilter.class);
    private static String THISMODULE = "PlainTextFilter";

    public List<Content> toOSIS(Book book, Key key, String plain)
    {
        OSISUtil.OSISFactory factory = OSISUtil.factory();
        Element ele = factory.createDiv();
        String[] lines = StringUtil.splitAll(plain, '\n');
        int lastIndex = lines.length - 1;

        for (int i = 0; i < lastIndex; i++)
        {
            ele.addContent(lines[i]);
            ele.addContent((Content) factory.createLB());
        }

        if (lastIndex >= 0) { ele.addContent(lines[lastIndex]); }

        return ele.removeContent();
    }

    public PlainTextFilter clone()
    {
        PlainTextFilter clone = null;
        try
        {
            clone = (PlainTextFilter) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            String msg = "close(): Unsupported Clone ";
            lgr.error(msg, THISMODULE);
            assert false : e;
        }
        return clone;
    }
}