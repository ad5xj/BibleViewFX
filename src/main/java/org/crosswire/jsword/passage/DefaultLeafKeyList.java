package org.crosswire.jsword.passage;

import org.crosswire.common.util.ItemIterator;

import java.util.Iterator;

public class DefaultLeafKeyList implements Key {

    private String name;
    private String osisName;

    private Key parent;

    private static final long serialVersionUID = -7462556005744186622L;

    public DefaultLeafKeyList(String name) {
        this(name, name, null);
    }

    public DefaultLeafKeyList(String name, String osisName) {
        this(name, osisName, null);
    }

    public DefaultLeafKeyList(String name, String osisName, Key parent) {
        this.name = name;
        this.parent = parent;
        this.osisName = osisName;
    }

    public boolean canHaveChildren() {
        return false;
    }

    public int getChildCount() {
        return 0;
    }

    public String getName() {
        return this.name;
    }

    public String getName(Key base) {
        return getName();
    }

    public String getRootName() {
        return getName();
    }

    public String getOsisRef() {
        return this.osisName;
    }

    public String getOsisID() {
        return getOsisRef();
    }

    public Key getParent() {
        return this.parent;
    }

    public int getCardinality() {
        return 1;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean contains(Key key) {
        return equals(key);
    }

    public Iterator<Key> iterator() {
        return (Iterator<Key>) new ItemIterator(this);
    }

    public void addAll(Key key) {
        throw new UnsupportedOperationException();
    }

    public void removeAll(Key key) {
        throw new UnsupportedOperationException();
    }

    public void retainAll(Key key) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
    }

    public Key get(int index) {
        if (index == 0) {
            return this;
        }
        return null;
    }

    public int indexOf(Key that) {
        if (equals(that)) {
            return 0;
        }
        return -1;
    }

    public void blur(int by, RestrictionType restrict) {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return getName();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(getClass())) {
            return false;
        }
        DefaultLeafKeyList that = (DefaultLeafKeyList) obj;
        return this.name.equals(that.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public int compareTo(Key obj) {
        DefaultLeafKeyList that = (DefaultLeafKeyList) obj;
        return this.name.compareTo(that.name);
    }

    public DefaultLeafKeyList clone() {
        DefaultLeafKeyList clone = null;
        try {
            clone = (DefaultLeafKeyList) super.clone();
            if (this.parent != null) {
                clone.parent = this.parent.clone();
            }
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }
}