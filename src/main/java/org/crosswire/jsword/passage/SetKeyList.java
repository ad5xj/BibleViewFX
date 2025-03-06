package org.crosswire.jsword.passage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SetKeyList extends AbstractKeyList {

    private static final long serialVersionUID = -1460162676283475117L;
    private static final Logger log = LoggerFactory.getLogger(SetKeyList.class);
    private Key parent;
    private List<Key> list;

    public void addAll(Key key) { this.list.add(key); }
    public void clear()         { this.list.clear();  }
    public void removeAll(Key key) { this.list.remove(key); }
    public void blur(int by, RestrictionType restrict) { log.warn("attempt to blur a non-blur-able list"); }

    public boolean contains(Key key) { return this.list.contains(key); }
    public boolean isEmpty()         { return this.list.isEmpty(); }
    public boolean canHaveChildren() { return false; }
    public boolean equals(Object obj)
    {
        if ( obj instanceof SetKeyList )
        {
            SetKeyList that = (SetKeyList) obj;
            return this.list.equals(that.list);
        }
        return false;
    }

    public int hashCode()        { return this.list.hashCode(); }
    public int getCardinality()  { return this.list.size(); }
    public int getChildCount()   { return 0; }
    public int indexOf(Key that) { return this.list.indexOf(that); }

    public SetKeyList(Set<Key> set) { this(set, null, null); }
    public SetKeyList(Set<Key> set, String name) { this(set, null, name); }
    public SetKeyList(Set<Key> set, Key parent)  { this(set, parent, null); }
    public SetKeyList(Set<Key> set, Key parent, String name) 
    {
        super(name);
        this.list = new ArrayList<Key>();
        this.parent = parent;
        this.list.addAll(set);
    }

    public Key get(int index)  { return this.list.get(index); }
    public Key getParent()     { return this.parent; }

    public Iterator<Key> iterator() { return this.list.iterator(); }
}