package org.crosswire.jsword.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.passage.Key;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief Definition of utility class to police Book data
 */
public final class DataPolice 
{
    private static boolean reporting;
    private static final String THISMODULE = "org.crosswire.jsword.book.DataPolice";
    private static final Logger lgr = LoggerFactory.getLogger(DataPolice.class);

    public static void report(Book book, Key key, String message) 
    {
        if ( !reporting ) { return; }
        String buf = null;
        BookMetaData bmd = book.getBookMetaData();
        if (bmd != null) { buf += bmd.getInitials(); }
        if (bmd != null && key != null) { buf += ":"; }
        if (key != null) { buf += key.getOsisID(); }
        buf += ": ";
        buf += message;
        lgr.info(buf,THISMODULE);
    }

    public static synchronized boolean isReporting() { return reporting; }

    public static synchronized void setReporting(boolean reporting) { DataPolice.reporting = reporting; }
}
