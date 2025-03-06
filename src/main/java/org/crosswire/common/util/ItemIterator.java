package org.crosswire.common.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ItemIterator<T> implements Iterator<T> {
    private boolean done;

    private T item;

    public ItemIterator(T item) {
        this.item = item;
    }

    public boolean hasNext() {
        return !this.done;
    }

    public T next() {
        if (this.done) {
            throw new NoSuchElementException();
        }
        this.done = true;
        return this.item;
    }

    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
