package org.crosswire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSOtherMsg;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.MissingResourceException;

public final class ResourceUtil 
{
    private static final Logger lgr = LoggerFactory.getLogger(ResourceUtil.class);
    private static final String THISMODULE = "ResourceUtil";


    public static URL getResource(String search) throws MissingResourceException
    {
        return getResource(CallContext.getCallingClass(), search);
    }

    public static <T> URL getResource(Class<T> clazz, String resourceName) throws MissingResourceException
    {
        URL resource = CWClassLoader.instance(clazz).findResource(resourceName);
        if ( resource == null )
        {
            String msg = "getResource(): Cannot find resource: " + resourceName
                       + "\n    class=" + clazz.getName()
                       + "\n    res=" + resourceName;
            lgr.error(msg,THISMODULE);
            throw new MissingResourceException(JSOtherMsg.lookupText("Cannot find resource: {0}", 
                                               new Object[] { resourceName } ), 
                                               clazz.getPackageName() + "." + clazz.getName(), 
                                               resourceName
                                              );
        }
        return resource;
    }

    public static InputStream getResourceAsStream(String search) throws IOException, MissingResourceException
    {
        return getResourceAsStream(CallContext.getCallingClass(), search);
    }

    public static <T> InputStream getResourceAsStream(Class<T> clazz, String search) throws IOException, MissingResourceException
    {
        return getResource(clazz, search).openStream();
    }

    public static PropertyMap getProperties(String subject) throws IOException
    {
        return getProperties(CallContext.getCallingClass(), subject);
    }

    public static <T> PropertyMap getProperties(Class<T> clazz) throws IOException
    {
        return getProperties(clazz, ClassUtil.getShortClassName(clazz));
    }

    private static <T> PropertyMap getProperties(Class<T> clazz, String subject) throws IOException
    {
        String lookup = "";
        InputStream in = null;
        PropertyMap prop = null;
        
        try
        {
            lookup = subject + ".properties";
            in = getResourceAsStream(clazz, lookup);
            prop = new PropertyMap();
            prop.load(in);
            return prop;
        }
        catch (MissingResourceException e)
        {
            String msg = "Missing resource - name=" + subject + ".properties";
            lgr.error(msg,THISMODULE);
            return new PropertyMap();
        }
    }
}