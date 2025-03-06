package org.crosswire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.icu.NumberShaper;

import org.crosswire.jsword.internationalisation.LocaleProviderManager;

import java.text.MessageFormat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MsgBase 
{
    private static boolean debug_log = true;
    private static Map<Locale, Map<String, ResourceBundle>> localeToResourceMap = new HashMap<>();

    private static final String THISMODULE = "MsgBase";
    private static final Logger log = LoggerFactory.getLogger(MsgBase.class);

    private NumberShaper shaper = new NumberShaper();

    public String lookup(String key, Object... params)
    {
        String rawMessage = "";
        
        if (params.length == 0)
        {
            return shaper.shape(rawMessage);
        }

        try
        {
            rawMessage = obtainString(key);
        }
        catch ( MissingResourceException e )
        {
            String msg = "Missing resource: Locale=" + LocaleProviderManager.getLocale()
                       + "\n     name=" + key
                       + "\n     params=" + params.toString();
            log.error(msg, THISMODULE);
        }

        rawMessage = rawMessage.replaceAll("'", "''");
        return shaper.shape(MessageFormat.format(rawMessage, params));
    }

    private String obtainString(String key) throws MissingResourceException
    {
        try
        {
            if ( !(getLocalisedResources() == null) ) { return getLocalisedResources().getString(key); }
        }
        catch ( MissingResourceException ex )
        {
            String msg = "Missing resource: Locale=" + LocaleProviderManager.getLocale()
                       + " name=" + key;
            log.error(msg, THISMODULE);
            ex.printStackTrace();
            throw ex;
        }
        return key;
    }

    private ResourceBundle getLocalisedResources()
    {
        String className;
        String shortClassName;

        ResourceBundle resourceBundle;
        Locale currentUserLocale;

        Class<? extends MsgBase> implementingClass;
        Map<String, ResourceBundle> localisedResourceMap;
        
        currentUserLocale = LocaleProviderManager.getLocale();
        localisedResourceMap = getLazyLocalisedResourceMap(currentUserLocale);

        implementingClass = getClass();
        className = implementingClass.getName();
        shortClassName = ClassUtil.getShortClassName(className);
        resourceBundle = localisedResourceMap.get(className);

        if ( resourceBundle == null )
        {
            String msg = "Unable to find the language resources, null. Locale=" + currentUserLocale
                       + " class=" + className;
            if ( debug_log ) { log.info(msg,THISMODULE); }
            throw new MissingResourceException("Unable to find the language resources.", className, shortClassName);
        }

        return resourceBundle;
    }

    private ResourceBundle getResourceBundleForClass(Class<? extends MsgBase> implementingClass, String className, String shortClassName, Locale currentUserLocale, Map<String, ResourceBundle> localisedResourceMap)
    {
        ResourceBundle resourceBundle;

        synchronized (MsgBase.class)
        {
            resourceBundle = localisedResourceMap.get(className);

            if ( resourceBundle == null )
            {
                try
                {
                    resourceBundle = ResourceBundle.getBundle(shortClassName, currentUserLocale, CWClassLoader.instance(implementingClass));
                    localisedResourceMap.put(className, resourceBundle);
                }
                catch (MissingResourceException ex)
                {
                    String msg = "Assuming key is the default message - " + className
                               + "\n    error=" + ex.getMessage();
                    log.error(msg,THISMODULE);
                }
            }
        }
        return resourceBundle;
    }

    private Map<String, ResourceBundle> getLazyLocalisedResourceMap(Locale currentUserLocale)
    {
        Map<String, ResourceBundle> localisedResourceMap = localeToResourceMap.get(currentUserLocale);
        if (localisedResourceMap == null)
        synchronized (MsgBase.class)
        {
            localisedResourceMap = localeToResourceMap.get(currentUserLocale);
            if ( localisedResourceMap == null )
            {
                localisedResourceMap = new HashMap<>(512);
                localeToResourceMap.put(currentUserLocale, localisedResourceMap);
            }
        }
        return localisedResourceMap;
    }
}