package org.crosswire.jsword.book.sword.state;

import org.crosswire.jsword.book.BookMetaData;

import java.io.Closeable;

public interface OpenFileState extends Closeable {

    void releaseResources();
    void setLastAccess(long paramLong);

    long getLastAccess();

    BookMetaData getBookMetaData();
}
