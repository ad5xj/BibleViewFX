package org.crosswire.jsword.passage;

import org.crosswire.jsword.versification.Versification;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class DistinctPassage extends AbstractPassage {

    private static final long serialVersionUID = 817374460730441662L;

    private transient SortedSet<Key> store;

    public DistinctPassage(Versification v11n) {
        super(v11n);
        this.store = new TreeSet<Key>();
    }

    protected DistinctPassage(Versification v11n, String refs, Key basis) throws NoSuchVerseException {
        super(v11n, refs);
        this.store = new TreeSet<Key>();
        this.store = Collections.synchronizedSortedSet(new TreeSet<Key>());
        addVerses(refs, basis);
    }

    protected DistinctPassage(Versification v11n, String refs) throws NoSuchVerseException {
        this(v11n, refs, (Key) null);
    }

    public DistinctPassage clone() {
        DistinctPassage copy = (DistinctPassage) super.clone();
        copy.store = new TreeSet<Key>();
        copy.store.addAll(this.store);
        return copy;
    }

    public Iterator<Key> iterator() {
        return this.store.iterator();
    }

    public boolean isEmpty() {
        return this.store.isEmpty();
    }

    public int countVerses() {
        return this.store.size();
    }

    public boolean contains(Key obj) {
        for (Key aKey : obj) {
            if (!this.store.contains(aKey)) {
                return false;
            }
        }
        return true;
    }

    public void add(Key obj) {
        optimizeWrites();
        Verse firstVerse = null;
        Verse lastVerse = null;
        for (Key aKey : obj) {
            lastVerse = (Verse) aKey;
            if (firstVerse == null) {
                firstVerse = lastVerse;
            }
            this.store.add(lastVerse);
        }
        if (this.suppressEvents == 0) {
            fireIntervalAdded(this, firstVerse, lastVerse);
        }
    }

    public void remove(Key obj) {
        optimizeWrites();
        Verse firstVerse = null;
        Verse lastVerse = null;
        for (Key aKey : obj) {
            lastVerse = (Verse) aKey;
            if (firstVerse == null) {
                firstVerse = lastVerse;
            }
            this.store.remove(lastVerse);
        }
        if (this.suppressEvents == 0) {
            fireIntervalAdded(this, firstVerse, lastVerse);
        }
    }

    public void clear() {
        optimizeWrites();
        this.store.clear();
        fireIntervalRemoved(this, null, null);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        writeObjectSupport(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        optimizeWrites();
        this.store = new TreeSet<Key>();
        in.defaultReadObject();
        readObjectSupport(in);
    }
}