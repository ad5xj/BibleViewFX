package org.crosswire.jsword.passage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultKeyList extends AbstractKeyList {
  private Key parent;
  
  private List<Key> keys;
  
  private static final long serialVersionUID = -1633375337613230599L;
  
  public DefaultKeyList() {
    super(null);
    this.keys = new ArrayList<Key>();
  }
  
  public DefaultKeyList(Key parent, String name) {
    super(name);
    this.keys = new ArrayList<Key>();
    this.parent = parent;
  }
  
  public boolean canHaveChildren() {
    return false;
  }
  
  public int getChildCount() {
    return 0;
  }
  
  public int getCardinality() {
    return this.keys.size();
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
    this.keys.add(key);
  }
  
  public void removeAll(Key key) {
    this.keys.remove(key);
  }
  
  public void clear() {
    this.keys.clear();
  }
  
  public Key get(int index) {
    return this.keys.get(index);
  }
  
  public int indexOf(Key that) {
    return this.keys.indexOf(that);
  }
  
  public Key getParent() {
    return this.parent;
  }
  
  public void blur(int by, RestrictionType restrict) {
    log.warn("attempt to blur a non-blur-able list");
  }
  
  private static final Logger log = LoggerFactory.getLogger(DefaultKeyList.class);
}
