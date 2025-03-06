package org.crosswire.jsword.book;

import org.crosswire.common.util.LucidException;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief Custom exception definition
 * @extends LucidException
 * @author ken
 */
public class BookException extends LucidException 
{
    private static final long serialVersionUID = 3977575883768738103L;

    public BookException(String msg)               { super(msg); }

    public BookException(String msg, Throwable ex) { super(msg, ex); }
}