package org.crosswire.jsword.book.readings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.Language;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;

import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.OSISUtil;

import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;

import org.crosswire.jsword.internationalisation.LocaleProviderManager;

import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.PreferredKey;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.SetKeyList;
import org.crosswire.jsword.passage.VerseRange;

import org.crosswire.jsword.versification.system.Versifications;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.jdom2.Content;
import org.jdom2.Element;

/**
 * @brief Abstract class to extend AbstractBook
 * 
 * @extends AbstractBook
 * @implements PreferredKey
 */
public class ReadingsBook extends AbstractBook implements PreferredKey 
{
    private static final String THISMODULE = "org.crosswire.jsword.book.readings.ReadingsBook";
    private static final Logger lgr = LoggerFactory.getLogger(ReadingsBook.class);

    private Key global;
    private Map<Key, String> hashMap;

    public ReadingsBook(ReadingsBookDriver driver, String setname, BookCategory type)
    {
        super(null, null);
        hashMap = new TreeMap<>();
        Locale defaultLocale = LocaleProviderManager.getLocale();
        ResourceBundle prop = ResourceBundle.getBundle(setname, defaultLocale, CWClassLoader.instance(ReadingsBookDriver.class));
        String name = JSMsg.gettext("Readings", new Object[0]);
        DefaultBookMetaData bmd = null;
        Calendar greg = null;

        try
        {
            name = prop.getString("title");
        }
        catch (MissingResourceException e)
        {
            String msg = "Missing resource: 'title' while parsing: " + setname;
            lgr.error(msg,THISMODULE);
        }
        bmd = new DefaultBookMetaData(driver, name, type);
        bmd.setInitials(setname);
        bmd.setLanguage(new Language(defaultLocale.getLanguage()));
        setBookMetaData(bmd);

        greg = new GregorianCalendar();
        greg.set(5, 1);
        greg.set(2, 0);
        int currentYear = greg.get(1);
        while (greg.get(1) == currentYear)
        {
            String internalKey = ReadingsKey.external2internal(greg);
            String readings = "";
            try
            {
                readings = prop.getString(internalKey);
                hashMap.put(new ReadingsKey(greg.getTime()), readings);
            }
            catch (MissingResourceException e)
            {
                String msg = "Missing resource: " + internalKey 
                           + " while parsing: " + setname;
                lgr.error(msg,THISMODULE);
            }
            greg.add(5, 1);
        }
        
        try
        {
            global = new SetKeyList(hashMap.keySet(), getName());
        }
        catch ( Exception e )
        {
                String msg = "Error getting KeyList for global - " + e.getMessage();
                lgr.error(msg,THISMODULE);
        }
    }

    @Override
    public void setRawText(Key key, String rawData) throws BookException
    {
        throw new BookException(JSOtherMsg.lookupText("This Book is read-only.", new Object[0]));
    }

    @Override
    public void setAliasKey(Key alias, Key source) throws BookException
    {
        throw new BookException(JSOtherMsg.lookupText("This Book is read-only.", new Object[0]));
    }

    @Override
    public boolean contains(Key key) { return false; }

    @Override
    public boolean hasFeature(FeatureType feature) { return (feature == FeatureType.DAILY_DEVOTIONS); }

    @Override
    public boolean isWritable() { return false; }

    @Override
    public Key getPreferred()  { return new ReadingsKey(new Date()); }

    @Override
    public String getRawText(Key key) throws BookException { return ""; }

    @Override
    public Iterator<Content> getOsisIterator(Key key, boolean allowEmpty, boolean allowGenTitles) throws BookException
    {
        Passage ref;
        PassageKeyFactory keyf;

        ref = null;
        keyf = null;

        if ( !(key instanceof ReadingsKey) )
        {
            String msg = "Error in OSIS Iterator - Key not found..." + key.getName();
            lgr.error(msg,THISMODULE);
            throw new BookException(JSMsg.gettext("Key not found {0}", new Object[]
            {
                key.getName()
            }));
        }
        List<Content> content = new ArrayList<>();
        Element title = OSISUtil.factory().createTitle();
        title.addContent(key.getName());
        content.add(title);
        String readings = hashMap.get(key);
        if (readings == null)
        {
            String msg = "Error in OSIS Iterator - Readings not found..." + key.getName();
            lgr.error(msg,THISMODULE);
            throw new BookException(JSMsg.gettext("Key not found {0}", new Object[]
            {
                key.getName()
            }));
        }
        try
        {
            keyf = PassageKeyFactory.instance();
            try
            {
                ref = keyf.getKey(Versifications.instance().getVersification("KJV"), readings);
            }
            catch ( Exception e )
            {
                String msg = "Error in OSIS Iterator - Passage not found for KJV" + e.getMessage();
                lgr.error(msg,THISMODULE);
            }
            Element list = OSISUtil.factory().createList();
            content.add(list);
            Iterator<VerseRange> it = ref.rangeIterator(RestrictionType.NONE);
            while (it.hasNext())
            {
                Key range = it.next();
                Element reading = OSISUtil.factory().createReference();
                reading.setAttribute("osisRef", range.getOsisRef());
                reading.addContent(range.getName());
                Element item = OSISUtil.factory().createItem();
                item.addContent(reading);
                list.addContent(item);
            }
        }
        catch ( Exception ex )
        {
            String t = "Failed to parse " + readings;
            lgr.error(t,THISMODULE);
            content.add(OSISUtil.factory().createText(t));
        }
        return content.iterator();
    }

    @Override
    public List<Content> getOsis(Key key, RawTextToXmlProcessor processor) throws BookException
    {
        return Collections.emptyList();
    }


    @Override
    public Key getValidKey(String name)
    {
        try
        {
            return getKey(name);
        }
        catch (NoSuchKeyException e)
        {
            return createEmptyKeyList();
        }
    }

    @Override
    public Key getKey(String name) throws NoSuchKeyException
    {
        DefaultKeyList reply = new DefaultKeyList();
        reply.addAll(new ReadingsKey(name, name, global));
        return reply;
    }

    @Override
    public Key getGlobalKeyList()    { return global; }

    @Override
    public Key createEmptyKeyList()  { return (Key) new DefaultKeyList(); }
}