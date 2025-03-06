package org.crosswire.jsword.book.filter.osis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.xml.XMLUtil;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.SourceFilter;

import org.crosswire.jsword.passage.Key;

import java.io.IOException;
import java.io.StringReader;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import org.xml.sax.InputSource;

public class OSISFilter implements SourceFilter {

    private static final Logger lgr = LoggerFactory.getLogger(OSISFilter.class);
    private static final String THISMODULE = "OSISFilter";

    private static final Pattern DIV_START = Pattern.compile("<div>", 16);
    private static final Pattern DIV_END = Pattern.compile("</div>", 16);
    private static final Pattern CHAPTER_END = Pattern.compile("</chapter>", 16);
    private static final Pattern SPEECH_START = Pattern.compile("<speech>", 16);
    private static final Pattern SPEECH_END = Pattern.compile("</speech>", 16);

    private BlockingQueue<SAXBuilder> saxBuilders = new ArrayBlockingQueue<SAXBuilder>(32);

    public List<Content> toOSIS(Book book, Key key, String plain)
    {
        Element ele = null;
        Exception ex = null;
        String clean = plain;
        clean = DIV_START.matcher(clean).replaceAll("<div sID=\"xyz\"/>");
        clean = DIV_END.matcher(clean).replaceAll("<div eID=\"xyz\"/>");
        clean = CHAPTER_END.matcher(clean).replaceAll("<chapter eID=\"xyz\"/>");
        clean = SPEECH_START.matcher(clean).replaceAll("<speech sID=\"xyz\"/>");
        clean = SPEECH_END.matcher(clean).replaceAll("<speech eID=\"xyz\"/>");
        if ("MapM".equals(book.getInitials()))
        {
            for (String tag : Arrays.<String>asList(new String[]
            {
                "cell", "row", "table"
            }))
            {
                int startPos = clean.indexOf("<" + tag + ">");
                int endPos = clean.indexOf("</" + tag + ">");
                if (endPos != -1 && (startPos == -1 || startPos > endPos))
                {
                    clean = "<" + tag + ">" + clean;
                }
            }
        }
        try
        {
            ele = parse(clean);
        }
        catch (JDOMException e)
        {
            String msg = "toOSIS(): JDOM Exception " + e.getMessage();
            lgr.error(msg,THISMODULE);
            JDOMException jDOMException1 = e;
        }
        catch (IOException e)
        {
            String msg = "toOSIS(): I/O Exception " + e.getMessage();
            lgr.error(msg,THISMODULE);
            ex = e;
        }
        if (ele == null)
        {
            String cleanedEntities = XMLUtil.cleanAllEntities(clean);
            if (cleanedEntities != null && !cleanedEntities.equals(clean))
            {
                clean = cleanedEntities;
                try
                {
                    ele = parse(clean);
                    ex = null;
                }
                catch (JDOMException e)
                {
                    String msg = "toOSIS(): JDOM Exception " + e.getMessage();
                    lgr.error(msg,THISMODULE);
                    JDOMException jDOMException1 = e;
                }
                catch (IOException e)
                {
                    String msg = "toOSIS(): I/O Exception " + e.getMessage();
                    lgr.error(msg,THISMODULE);
                    ex = e;
                }
            }
        }
        if (ele == null)
        {
            String reclosed = XMLUtil.recloseTags(clean);
            if (reclosed != null && !reclosed.equals(clean))
            {
                clean = reclosed;
                try
                {
                    ele = parse(clean);
                    ex = null;
                }
                catch (JDOMException e)
                {
                    String msg = "toOSIS(): JDOM Exception " + e.getMessage();
                    lgr.error(msg,THISMODULE);
                    JDOMException jDOMException1 = e;
                }
                catch (IOException e)
                {
                    String msg = "toOSIS(): I/O Exception " + e.getMessage();
                    lgr.error(msg,THISMODULE);
                    ex = e;
                }
            }
        }
        if (ex != null)
        {
            DataPolice.report(book, key, "Parse failed: " + ex.getMessage() + "\non: " + clean);
            ele = cleanTags(book, key, clean);
        }
        if (ele == null)  { ele = OSISUtil.factory().createP(); }
        return ele.removeContent();
    }

    public OSISFilter clone()
    {
        OSISFilter clone = null;
        try
        {
            clone = (OSISFilter) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            String msg = "clone(): Clone Support error " + e.getMessage();
            lgr.error(msg,THISMODULE);
            assert false : e;
        }
        return clone;
    }

    private Element cleanTags(Book book, Key key, String plain)
    {
        String shawn = XMLUtil.cleanAllTags(plain);
        Exception ex = null;
        try
        {
            return parse(shawn);
        }
        catch (JDOMException e)
        {
            String msg = "cleanTags(): JDOM Exception " + e.getMessage();
            lgr.error(msg,THISMODULE);
            JDOMException jDOMException1 = e;
        }
        catch (IOException e)
        {
            String msg = "cleanTags(): I/O Exception " + e.getMessage();
            lgr.error(msg,THISMODULE);
            ex = e;
        }
        //DataPolice.report(book, key, "Parse failed: " + ex.getMessage() + "\non: " + shawn);
        return null;
    }

    private Element parse(String plain) throws JDOMException, IOException
    {
        Element div;
        SAXBuilder builder = this.saxBuilders.poll();
        if (builder == null)
        {
            builder = new SAXBuilder();
        }
        StringReader in = null;
        try
        {
            in = new StringReader("<xxx>" + plain + "</xxx>");
            InputSource is = new InputSource(in);
            Document doc = builder.build(is);
            div = doc.getRootElement();
        }
        finally
        {
            if (in != null) { in.close(); }
        }
        this.saxBuilders.offer(builder);
        return div;
    }
}