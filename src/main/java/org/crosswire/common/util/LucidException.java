package org.crosswire.common.util;

import org.crosswire.jsword.JSMsg;

public class LucidException extends Exception 
{
    private static final long serialVersionUID = 3257846580311963191L;

    public LucidException(String msg) { super(msg); }

    public LucidException(String msg, Throwable cause) { super(msg, cause); }

    public String getDetailedMessage() 
    {
        Throwable cause = getCause();
        if (cause == null) { return getMessage(); }
        String reason = JSMsg.gettext("Reason:", new Object[0]);
        if ( cause instanceof LucidException ) 
        {
            LucidException lex = (LucidException) cause;
            return getMessage() + reason + lex.getDetailedMessage();
        }
        return getMessage() + reason + cause.getMessage();
    }
}