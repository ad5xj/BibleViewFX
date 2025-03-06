package org.crosswire.jsword.book.sword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSOtherMsg;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractPassageBook;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.PassageKeyFactory;

import org.crosswire.jsword.index.IndexStatus;

import org.crosswire.jsword.versification.Versification;

import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;

public class SwordBook extends AbstractPassageBook 
{
    private static final Logger log = LoggerFactory.getLogger(SwordBook.class);
    
    private SourceFilter filter;
    private Key global;

    public SwordBook(SwordBookMetaData sbmd, Backend<?> backend)
    {
        super((BookMetaData) sbmd, backend);
        this.filter = sbmd.getFilter();
        if (backend == null)
        {
            throw new IllegalArgumentException("AbstractBackend must not be null.");
        }
    }

    @Override
    public IndexStatus getIndexStatus() { return IndexStatus.DONE; }
    
    public final Key getGlobalKeyList()
    {
        if (this.global == null)
        {
            try
            {
                this.global = getBackend().getGlobalKeyList();
                return this.global;
            }
            catch (UnsupportedOperationException ex)
            {
                log.debug(ex.getMessage());
            }
            catch (BookException ex)
            {
                log.debug(ex.getMessage());
            }
            Versification v11n = getVersification();
            this.global = createEmptyKeyList();
            Key all = PassageKeyFactory.instance().getGlobalKeyList(v11n);
            for (Key key : all)
            {
                if (contains(key))
                {
                    this.global.addAll(key);
                }
            }
        }
        return this.global;
    }

    public void setRawText(Key key, String rawData) throws BookException
    {
        throw new BookException(JSOtherMsg.lookupText("This Book is read-only.", new Object[0]));
    }
    public void setAliasKey(Key alias, Key source) throws BookException { getBackend().setAliasKey(alias, source); }

    public boolean contains(Key key)  { return getBackend().contains(key); }
    public boolean isWritable() { return getBackend().isWritable(); }

    public String getRawText(Key key) throws BookException { return getBackend().getRawText(key); }

    public void addOSIS(Key key, Element div, List<Content> osisContent)
    {
        for (Content content : osisContent)
        {
            if (content instanceof Element)
            {
                Element ele = (Element) content;
                if (ele.getName().equals("verse"))
                {
                    super.addOSIS(key, div, osisContent);
                    return;
                }
            }
        }
        if (KeyUtil.getVerse(key).getVerse() == 0)
        {
            super.addOSIS(key, div, osisContent);
        }
        else
        {
            Element everse = OSISUtil.factory().createVerse();
            everse.setAttribute("osisID", key.getOsisID());
            div.addContent((Content) everse);
            super.addOSIS(key, everse, osisContent);
        }
    }

    public void addOSIS(Key key, List<Content> contentList, List<Content> osisContent)
    {
        if (osisContent.size() == 0)
        {
            return;
        }
        if (KeyUtil.getVerse(key).getVerse() == 0)
        {
            Element div = OSISUtil.factory().createDiv();
            div.setAttribute("osisID", key.getOsisID());
            div.setAttribute("type", "x-gen");
            div.addContent(osisContent);
            contentList.add(div);
            return;
        }
        int start = 0;
        int found = -1;
        boolean wrapped = false;
        Element preverse = null;
        for (Content content : osisContent)
        {
            if (content instanceof Element)
            {
                Element ele = (Element) content;
                String name = ele.getName();
                if ("verse".equals(name))
                {
                    wrapped = true;
                    continue;
                }
                Attribute typeAttr = ele.getAttribute("type");
                Attribute subTypeAttr = ele.getAttribute("subType");
                if (subTypeAttr != null && "x-preverse".equals(subTypeAttr.getValue()))
                {
                    if ("div".equals(name) || "title".equals(name))
                    {
                        preverse = ele;
                        found = start;
                    }
                }
                else
                {
                    if (typeAttr != null && "psalm".equals(typeAttr.getValue()) && "title".equals(name))
                    {
                        Attribute canonicalAttr = ele.getAttribute("canonical");
                        if (canonicalAttr == null)
                        {
                            ele.setAttribute("canonical", "true");
                        }
                        if (subTypeAttr == null)
                        {
                            ele.setAttribute("subType", "x-preverse");
                            preverse = ele;
                            found = start;
                        }
                    }
                }
            }
            start++;
        }
        if (wrapped)
        {
            super.addOSIS(key, contentList, osisContent);
            return;
        }
        Element everse = OSISUtil.factory().createVerse();
        everse.setAttribute("osisID", key.getOsisID());
        if (preverse == null)
        {
            everse.addContent(osisContent);
        }
        else
        {
            List<Content> sublist = osisContent.subList(found + 1, osisContent.size());
            everse.addContent(sublist);
            sublist.clear();
            super.addOSIS(key, contentList, osisContent);
        }
        contentList.add(everse);
    }

    public Key getScope()
    {
        SwordBookMetaData sbmd = (SwordBookMetaData) getBookMetaData();
        if (sbmd.getProperty("Versification") == null)
        {
            return null;
        }
        Object keyString = sbmd.getProperty("Scope");
        if (keyString != null)
      try
        {
            return getKey((String) keyString);
        }
        catch (NoSuchKeyException ex)
        {
            log.error("Unable to parse scope from book", (Throwable) ex);
            return null;
        }
        Key bookKeys = getGlobalKeyList();
        if (!(bookKeys instanceof org.crosswire.jsword.passage.VerseKey))
        {
            log.error("Global key list isn't a verse key. A very expensive no-op has just occurred.");
            return null;
        }
        return bookKeys;
    }

    protected List<Content> getOsis(Key key, RawTextToXmlProcessor processor) throws BookException
    {
        List<Content> result = getBackend().readToOsis(key, processor);
        assert result != null;
        return result;
    }

    protected SourceFilter getFilter() { return this.filter; }
}
