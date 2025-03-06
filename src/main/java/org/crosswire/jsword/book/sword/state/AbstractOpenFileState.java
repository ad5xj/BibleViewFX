package org.crosswire.jsword.book.sword.state;

import org.crosswire.jsword.book.BookMetaData;

public abstract class AbstractOpenFileState implements OpenFileState {

    private BookMetaData bookMetaData;

    private long lastAccess;

    public AbstractOpenFileState(BookMetaData bmd) {
        this.bookMetaData = bmd;
        this.lastAccess = System.currentTimeMillis();
    }

    public void close() {
        OpenFileStateManager.instance().release(this);
    }

    public BookMetaData getBookMetaData() {
        return this.bookMetaData;
    }

    public long getLastAccess() {
        return this.lastAccess;
    }

    public void setLastAccess(long lastAccess) {
        this.lastAccess = lastAccess;
    }
}
