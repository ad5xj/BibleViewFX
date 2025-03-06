package org.crosswire.jsword.book.filter.thml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.book.Book;
//import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class ScripRefTag extends AbstractTag {
    private static final Logger lgr = LoggerFactory.getLogger(ScripRefTag.class);
    private static final String THISMODULE = "ScripRefTag";

    public String getTagName() { return "scripRef"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        Element reference = null;
        String refstr = attrs.getValue("passage");
        if (refstr != null)
        {
            Passage ref = null;
            try
            {
                ref = PassageKeyFactory.instance().getKey(KeyUtil.getVersification(key), refstr, key);
            }
            catch (NoSuchKeyException ex)
            {
                String msg  = "processTag(): Unparsable passage: (" + refstr + ") due to " + ex.getMessage();
                lgr.error(msg, THISMODULE);
                //DataPolice.report(book, key, "Unparsable passage: (" + refstr + ") due to " + ex.getMessage());
            }
            String osisname = (ref != null) ? ref.getOsisRef() : refstr;
            reference = OSISUtil.factory().createReference();
            reference.setAttribute("osisRef", osisname);
        }
        else
        {
            reference = OSISUtil.factory().createReference();
        }
        if (ele != null)   { ele.addContent((Content) reference); }
        return reference;
    }

    public void processContent(Book book, Key key, Element ele)
    {
        String refstr = ele.getValue();
        try
        {
            if (ele.getAttribute("osisRef") == null)
            {
                Passage ref = PassageKeyFactory.instance().getKey(KeyUtil.getVersification(key), refstr, key);
                String osisname = ref.getOsisRef();
                ele.setAttribute("osisRef", osisname);
            }
        }
        catch (NoSuchKeyException ex)
        {
            String msg  = "processContent(): scripRef has no passage attribute, unable to guess: (" + refstr + ") due to " + ex.getMessage();
            lgr.error(msg, THISMODULE);
            //DataPolice.report(book, key, "scripRef has no passage attribute, unable to guess: (" + refstr + ") due to " + ex.getMessage());
        }
    }
}