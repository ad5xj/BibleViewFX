package org.crosswire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CallContext {
    private static final String THISMODULE = "CallContext";
    private static final Logger LOGR = LoggerFactory.getLogger(CallContext.class);

    private static final int CALL_CONTEXT_OFFSET = 3;

    public static CallContext instance() {
        return resolver;
    }

    public static Class<?> getCallingClass() {
        return getCallingClass(1);
    }

    public static Class<?> getCallingClass(int i) {
        String name = Thread.currentThread().getStackTrace()[3 + i].getClassName();
        try 
        {
            return Class.forName(name);
        } 
        catch ( ClassNotFoundException e ) 
        {
            LOGR.error("getCallingClass(): Class " + name + " not found error - " + e.getMessage(),THISMODULE);
            return CallContext.class;
        }
    }

    private static CallContext resolver = new CallContext();
}
