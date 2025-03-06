package org.crosswire.jsword.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.PluginUtil;

public final class IndexManagerFactory
{
    private static final Logger LGR = LoggerFactory.getLogger(IndexManagerFactory.class);
    private static final String THISMODULE = "IndexManagerFactory";

    private static IndexManager instance;

    static
    {
        try
        {
            instance = (IndexManager)PluginUtil.getImplementation(IndexManager.class);
        }
        catch ( Exception e )
        {
            LGR.error("createIndexManager failed - "+e.getMessage(),THISMODULE);
        }
    }

    public static IndexManager getIndexManager() { return instance; }
}