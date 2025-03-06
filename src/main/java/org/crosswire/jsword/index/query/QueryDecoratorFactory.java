package org.crosswire.jsword.index.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.PluginUtil;

import java.io.IOException;


public final class QueryDecoratorFactory {
    private static final Logger log = LoggerFactory.getLogger(QueryDecoratorFactory.class);
    private static final String THISMODULE = "QueryDecoratorFactory";

    private static QueryDecorator instance;

    public static QueryDecorator getSearchSyntax()  { return instance; }

    static
    {
        try
        {
            instance = (QueryDecorator) PluginUtil.getImplementation(QueryDecorator.class);
        }
        catch ( Exception e)
        {
            log.error("instantiator: create QueryDecorator failed - "+e.getMessage(),THISMODULE);
        }
    }
}
