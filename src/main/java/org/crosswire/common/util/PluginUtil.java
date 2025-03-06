package org.crosswire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSOtherMsg;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

public final class PluginUtil {

    private static final Logger log = LoggerFactory.getLogger(PluginUtil.class);

    private static final String THISMODULE = "PluginUtil";
    private static final String DEFAULT = "default";

    public static final String EXTENSION_PLUGIN = ".plugin";

    public static <T> Class<T>[] getImplementors(Class<T> clazz) {
        String name = null;
        try {
            List<Class<T>> matches = new ArrayList<Class<T>>();
            PropertyMap props = getPlugin(clazz);
            for (String key : props.keySet()) 
            {
                name = props.get(key);
            
                try 
                {
                    Class<T> impl = (Class) ClassUtil.forName(name);
                    if (clazz.isAssignableFrom(impl)) 
                    {
                        matches.add(impl);
                        continue;
                    }
                    log.warn("Class {} does not implement {}. Ignoring.", impl.getName(), clazz.getName());
                } 
                catch (ClassNotFoundException ex) 
                {
                    log.warn("Failed to add class to list: {}"+name+" err="+ex.getMessage(), THISMODULE);
                }
            }
            //log.debug("Found {} implementors of {}", Integer.toString(matches.size()), clazz.getName());
            return matches.<Class<T>>toArray((Class<T>[]) new Class[matches.size()]);
        } 
        catch (IOException ex) 
        {
            log.error("Failed to get any classes.", ex);
            return (Class<T>[]) new Class[0];
        }
    }

    public static <T> Map<String, Class<T>> getImplementorsMap(Class<T> clazz) {
        Map<String, Class<T>> matches = new HashMap<String, Class<T>>();
        try {
            PropertyMap props = getPlugin(clazz);
            for (String key : props.keySet()) {
                try {
                    String value = props.get(key);
                    Class<T> impl = (Class)ClassUtil.forName(value);
                    if (clazz.isAssignableFrom(impl)) {
                        matches.put(key, impl);
                        continue;
                    }
                    log.warn("Class {} does not implement {}. Ignoring.", impl.getName(), clazz.getName());
                } catch (ClassNotFoundException ex) {
                    log.warn("Failed to add class to list: "+clazz.getName()+"\terr="+ex,THISMODULE);
                }
            }
            //log.debug("Found {} implementors of {}", Integer.toString(matches.size()), clazz.getName());
        } 
        catch (IOException ex) 
        {
            log.error("Failed to get any classes.\terr="+ex.getMessage(),THISMODULE);
        }
        return matches;
    }

    public static <T> Class<T> getImplementor(Class<T> clazz) throws IOException, ClassNotFoundException, ClassCastException {
        PropertyMap props = getPlugin(clazz);
        String name = props.get("default");
        Class<T> impl = (Class) ClassUtil.forName(name);
        if (!clazz.isAssignableFrom(impl)) {
            throw new ClassCastException(JSOtherMsg.lookupText("Class {0} does not implement {1}.", new Object[]{impl.getName(), clazz.getName()}));
        }
        return impl;
    }

    public static <T> T getImplementation(Class<T> clazz) throws MalformedURLException, ClassCastException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        return getImplementor(clazz).newInstance();
    }

    public static <T> PropertyMap getPlugin(Class<T> clazz) throws IOException {
        String subject = ClassUtil.getShortClassName(clazz);
        try {
            String lookup = subject + ".plugin";
            InputStream in = ResourceUtil.getResourceAsStream(clazz, lookup);
            PropertyMap prop = new PropertyMap();
            prop.load(in);
            return prop;
        } catch (MissingResourceException e) {
            return new PropertyMap();
        }
    }
}
