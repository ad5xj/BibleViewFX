package org.crosswire.jsword.book.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.PluginUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class SourceFilterFactory {
    private static final Logger log = LoggerFactory.getLogger(SourceFilterFactory.class);

    private static Map<String, SourceFilter> filters = new HashMap<String, SourceFilter>();

    public static SourceFilter getFilter(String lookup)
    {
        SourceFilter reply = filters.get(lookup.toLowerCase(Locale.ENGLISH));
        if (reply == null)
        {
            reply = deft;
        }
        return reply.clone();
    }

    public static SourceFilter getDefaultFilter()
    {
        return deft.clone();
    }

    public static void addFilter(String name, SourceFilter instance)
    {
        filters.put(name.toLowerCase(Locale.ENGLISH), instance);
    }

    private static volatile SourceFilter deft;

    static
    {
        Map<String, Class<SourceFilter>> map = PluginUtil.getImplementorsMap(SourceFilter.class);
        try
        {
            Class<SourceFilter> cdeft = map.remove("default");
            deft = cdeft.newInstance();
        }
        catch (InstantiationException e)
        {
            log.error("Failed to get default filter, will attempt to use first", e);
        }
        catch (IllegalAccessException e)
        {
            log.error("Failed to get default filter, will attempt to use first", e);
        }
        SourceFilter instance = null;
        for (Map.Entry<String, Class<SourceFilter>> entry : map.entrySet())
        {
            try
            {
                Class<SourceFilter> clazz = entry.getValue();
                instance = clazz.newInstance();
                addFilter(entry.getKey(), instance);
            }
            catch (InstantiationException ex)
            {
                log.error("Failed to add filter", ex);
            }
            catch (IllegalAccessException ex)
            {
                log.error("Failed to add filter", ex);
            }
        }
        if (deft == null)
        {
            deft = instance;
        }
    }
}