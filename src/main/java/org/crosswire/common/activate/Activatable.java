package org.crosswire.common.activate;

public interface Activatable {

    void activate(Lock paramLock);

    void deactivate(Lock paramLock);
}
