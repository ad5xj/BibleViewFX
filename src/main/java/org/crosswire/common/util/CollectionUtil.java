package org.crosswire.common.util;

import java.io.IOException;

import java.net.URI;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public final class CollectionUtil {

    public static <T> List<T> createList(Iterable<T> it) 
    {
        List<T> reply = new ArrayList<>();
        for (T obj : it) 
        {
            reply.add(obj);
        }
        return reply;
    }

    public static <T> Set<T> createSet(Iterable<T> it) 
    {
        Set<T> reply = new HashSet<T>();
        for (T obj : it) {
            reply.add(obj);
        }
        return reply;
    }

    public static PropertyMap properties2Map(Properties prop) 
    {
        PropertyMap propMap = new PropertyMap();
        for (Enumeration<Object> e = prop.keys(); e.hasMoreElements();) 
        {
            Object k = e.nextElement();
            Object v = prop.get(k);
            if (k instanceof String && v instanceof String) 
            {
                propMap.put((String) k, (String) v);
            }
        }
        return propMap;
    }

    public static PropertyMap properties2Map(URI propUri) throws IOException {
        return NetUtil.loadProperties(propUri);
    }
}