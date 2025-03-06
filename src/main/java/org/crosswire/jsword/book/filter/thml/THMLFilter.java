package org.crosswire.jsword.book.filter.thml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.passage.Key;

import java.io.IOException;
import java.io.StringReader;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class THMLFilter implements SourceFilter {
    private static final Logger lgr = LoggerFactory.getLogger(THMLFilter.class);
    private static final String THISMODULE = "THMLFilter";

    private String errorMessage;
    private Exception error;
    private String finalInput;

    public List<Content> toOSIS(Book book, Key key, String plain)
    {
        Element ele = cleanParse(book, key, plain);
        if (ele == null)
        {
            if (error instanceof SAXParseException)
            {
                SAXParseException spe = (SAXParseException) error;
                int colNumber = spe.getColumnNumber();
                int start = Math.max(0, colNumber - 40);
                int stop = Math.min(this.finalInput.length(), colNumber + 40);
                int here = stop - start;
                String msg = "toOSIS() : Could not fix "
                    + book.getInitials()
                    + "("
                    + key.getName()
                    + ") by " + error.getMessage()
                    + ": Error here("
                    + Integer.toString(colNumber)
                    + ","
                    + Integer.toString(this.finalInput.length())
                    + ","
                    + this.finalInput.substring(start, stop)
                    + "): ";
                lgr.error(msg, THISMODULE);
            }
            else
            {
                String msg = "toOSIS(): Could not fix "
                    + book.getInitials()
                    + "("
                    + key.getName()
                    + ") by " + error.getMessage()
                    + ")";
                lgr.error(msg, THISMODULE);
                //log.error("Could not fix {}({}) by {}: {}", new Object[] { book.getInitials(), key.getName(), this.errorMessage, this.error.getMessage() });
            }
            ele = OSISUtil.factory().createP();
        }
        return ele.removeContent();
    }

    public THMLFilter clone()
    {
        THMLFilter clone = null;
        try
        {
            clone = (THMLFilter) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            assert false : e;
        }
        return clone;
    }

    private Element cleanParse(Book book, Key key, String plain)
    {
        String clean = XMLUtil.cleanAllEntities(plain);
        Element ele = parse(book, key, clean, "cleaning entities");
        if (ele == null)   { ele = cleanText(book, key, clean); }
        return ele;
    }

    private Element cleanText(Book book, Key key, String plain)
    {
        String clean = XMLUtil.cleanAllCharacters(plain);
        Element ele = parse(book, key, clean, "cleaning text");
        if (ele == null)   { ele = parse(book, key, XMLUtil.closeEmptyTags(clean), "closing empty tags"); }
        if (ele == null)   { ele = cleanTags(book, key, clean); }
        return ele;
    }

    private Element cleanTags(Book book, Key key, String plain)
    {
        String clean = XMLUtil.cleanAllTags(plain);
        return parse(book, key, clean, "cleaning tags");
    }

    private Element parse(Book book, Key key, String plain, String failMessage)
    {
        Exception ex = null;
        StringBuilder buf = new StringBuilder(15 + plain.length());
        buf.append('<').append("root").append('>').append(plain).append("</").append("root").append('>');
        this.finalInput = buf.toString();
        try
        {
            StringReader in = new StringReader(this.finalInput);
            InputSource is = new InputSource(in);
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            CustomHandler handler = new CustomHandler(book, key);
            parser.parse(is, handler);
            return handler.getRootElement();
        }
        catch ( Exception e)
        {
            String msg = "parse(): parse error- " + e.getMessage();
            lgr.error(msg, THISMODULE);
            ex = e;
        }
        this.errorMessage = failMessage;
        this.error = ex;
        return null;
    }
}
