package org.crosswire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.internationalisation.LocaleProviderManager;

import java.io.IOException;

import java.net.URL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

public final class Languages {
    protected static final Logger log = LoggerFactory.getLogger(Books.class);
    protected static final String THISMODULE = "Languages";

    private static Map<Locale, ResourceBundle> localisedCommonLanguages = new HashMap<Locale, ResourceBundle>();

    public static String getName(String code)
    {
        String name = code;
        try
        {
            ResourceBundle langs = getLocalisedCommonLanguages();
            if (langs != null)
            {
                name = langs.getString(code);
            }
        }
        catch (MissingResourceException e)
        {
            String msg = "getName(): Missing Resource - " + code;
            log.error(msg, THISMODULE);
        }
        return name;
    }

    private static ResourceBundle getLocalisedCommonLanguages()
    {
        Locale locale = LocaleProviderManager.getLocale();
        ResourceBundle langs = localisedCommonLanguages.get(locale);
        if (langs == null)
        {
            synchronized (Languages.class)
            {
                langs = localisedCommonLanguages.get(locale);
                if (langs == null)
                {
                    langs = initLanguages(locale);
                    if (langs != null)
                    {
                        localisedCommonLanguages.put(locale, langs);
                    }
                }
            }
        }
        return langs;
    }

    private static ResourceBundle initLanguages(Locale locale)
    {
        try
        {
            return ResourceBundle.getBundle("iso639", locale, CWClassLoader.instance());
        }
        catch (MissingResourceException e)
        {
            log.info("Unable to find language in iso639 bundle", e);
            return null;
        }
    }

    public static final class AllLanguages
    {
        private static PropertyMap instance;

        public static String getName(String languageCode)
        {
            if (instance != null)
            {
                String name = instance.get(languageCode);
                if (name != null) {  return name;  }
            }
            return languageCode;
        }

        static
        {
            try
            {
                instance = ResourceUtil.getProperties("iso639full");
                Languages.log.debug("Loading iso639full.properties file");
            }
            catch (IOException e)
            {
                Languages.log.info("Unable to load iso639full.properties", e);
            }
        }

    }

    public static final class RtoL
    {

        public static boolean isRtoL(String script, String lang)
        {
            if (script != null) { return rtol.contains(script); }
            if (lang != null)   { return rtol.contains(lang);   }
            return false;
        }

        private static Set rtol = new HashSet();

        static
        {
            try
            {
                URL index = ResourceUtil.getResource(Translations.class, "rtol.txt");
                String[] list = NetUtil.listByIndexFile(NetUtil.toURI(index));
                Languages.log.debug("Loading iso639full.properties file");
                for (int i = 0; i < list.length; i++)
                {
                    rtol.add(list[i]);
                }
            }
            catch (IOException ex)
            {
                Languages.log.error("RtoL(): Unable to load rtol.txt", ex);
            }
        }

    }
}