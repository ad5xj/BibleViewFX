package org.crosswire.jsword.passage;

import org.crosswire.common.util.LucidException;

public class NoSuchKeyException extends LucidException {
    private static final long serialVersionUID = 3257288032582185777L;

    public NoSuchKeyException(String msg) {
        super(msg);
    }

    public NoSuchKeyException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
