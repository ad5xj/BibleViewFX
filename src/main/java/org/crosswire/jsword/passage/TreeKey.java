package org.crosswire.jsword.passage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TreeKey extends AbstractKeyList {

    private Key parent;

    private List<Key> children;

    public TreeKey(String name, Key parent) {
        super(name);
        this.parent = parent;
        this.children = new ArrayList<Key>();
    }

    public TreeKey(String text) {
        this(text, null);
    }

    public boolean canHaveChildren() {
        return true;
    }

    public int getChildCount() {
        return this.children.size();
    }

    public int getCardinality() {
        int cardinality = 1;
        for (Key child : this.children) {
            cardinality += child.getCardinality();
        }
        return cardinality;
    }

    public boolean isEmpty() {
        return this.children.isEmpty();
    }

    public boolean contains(Key key) {
        if (this.children.contains(key)) {
            return true;
        }
        for (Key child : this.children) {
            if (child.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public Iterator<Key> iterator() {
        return new KeyIterator(this);
    }

    public void addAll(Key key) {
        this.children.add(key);
    }

    public void removeAll(Key key) {
        this.children.remove(key);
    }

    public void clear() {
        this.children.clear();
    }

    public Key get(int index) {
        return this.children.get(index);
    }

    public int indexOf(Key that) {
        return this.children.indexOf(that);
    }

    public Key getParent() {
        return this.parent;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(getClass())) {
            return false;
        }
        TreeKey otherTreeKey = (TreeKey) obj;
        if (!getName().equals(otherTreeKey.getName())) {
            return false;
        }
        if (getParent() == null) {
            return (otherTreeKey.getParent() == null);
        }
        return getParent().equals(otherTreeKey.getParent());
    }

    public void blur(int by, RestrictionType restrict) {
        log.warn("attempt to blur a non-blur-able list");
    }

    public TreeKey clone() {
        return (TreeKey) super.clone();
    }

    public String getRootName() {
        String rootName = getName();
        for (Key parentKey = this; parentKey != null && parentKey.getName().length() > 0; parentKey = parentKey.getParent()) {
            rootName = parentKey.getName();
        }
        return rootName;
    }

    public String getOsisRef() {
        return getOsisID();
    }

    public String getOsisID() {
        StringBuilder b = new StringBuilder(100);
        b.append(getName());
        for (Key parentKey = getParent(); parentKey != null && parentKey.getName().length() > 0; parentKey = parentKey.getParent()) {
            b.insert(0, "/");
            b.insert(0, parentKey.getName());
        }
        return b.toString();
    }

    private static final Logger log = LoggerFactory.getLogger(TreeKey.class);

    private static final long serialVersionUID = -6560408145705717977L;
}
