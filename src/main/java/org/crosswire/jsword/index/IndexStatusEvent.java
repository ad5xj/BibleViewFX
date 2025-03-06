package org.crosswire.jsword.index;

import java.util.EventObject;

public class IndexStatusEvent extends EventObject
{
    private static final long serialVersionUID = 3834876879554819894L;

    private IndexStatus indexStatus;

    public IndexStatusEvent(Object source, IndexStatus status)
    {
        super(source);
        this.indexStatus = status;
    }

    public IndexStatus getIndexStatus()
    {
        return this.indexStatus;
    }
}