package org.crosswire.jsword.passage;

import org.crosswire.jsword.JSOtherMsg;

import java.util.Iterator;

public class ReadOnlyKeyList implements Key {
    private static final long serialVersionUID = -7947159638198641657L;

    private boolean ignore;

    private Key keys;

    public ReadOnlyKeyList(Key keys, boolean ignore) {
        this.keys = keys;
        this.ignore = ignore;
    }

    public int getCardinality() {
        return this.keys.getCardinality();
    }

    public boolean canHaveChildren() {
        return this.keys.canHaveChildren();
    }

    public int getChildCount() {
        return this.keys.getChildCount();
    }

    public boolean isEmpty() {
        return this.keys.isEmpty();
    }

    public boolean contains(Key key) {
        return this.keys.contains(key);
    }

    public Iterator<Key> iterator() {
        return this.keys.iterator();
    }

    public void addAll(Key key) {
        if (this.ignore) {
            return;
        }
        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only key list", new Object[0]));
    }

    public void removeAll(Key key) {
        if (this.ignore) {
            return;
        }
        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only key list", new Object[0]));
    }

    public void retainAll(Key key) {
        if (this.ignore) {
            return;
        }
        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only key list", new Object[0]));
    }

    public void clear() {
        if (this.ignore) {
            return;
        }
        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only key list", new Object[0]));
    }

    public String getName() {
        return this.keys.getName();
    }

    public String getName(Key base) {
        return this.keys.getName(base);
    }

    public String getRootName() {
        return this.keys.getRootName();
    }

    public String getOsisRef() {
        return this.keys.getOsisRef();
    }

    public String getOsisID() {
        return this.keys.getOsisID();
    }

    public int hashCode() {
        return this.keys.hashCode();
    }

    public boolean equals(Object obj) {
        return this.keys.equals(obj);
    }

    public int compareTo(Key o) {
        return this.keys.compareTo(o);
    }

    public Key get(int index) {
        return this.keys.get(index);
    }

    public int indexOf(Key that) {
        return this.keys.indexOf(that);
    }

    public Key getParent() {
        return this.keys.getParent();
    }

    public void blur(int by, RestrictionType restrict) {
        if (this.ignore) {
            return;
        }
        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only key list", new Object[0]));
    }

    public ReadOnlyKeyList clone() {
        ReadOnlyKeyList clone = null;
        try {
            clone = (ReadOnlyKeyList) super.clone();
            clone.keys = this.keys.clone();
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }
}
