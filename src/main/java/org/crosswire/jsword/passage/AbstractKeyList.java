package org.crosswire.jsword.passage;

import java.util.Iterator;

public abstract class AbstractKeyList implements Key {
    private static final long serialVersionUID = 3858640507828137034L;

    private String name;

    protected AbstractKeyList(String name) { this.name = name; }

    public boolean isEmpty() { return (getCardinality() == 0); }
    public boolean contains(Key key) { return (indexOf(key) >= 0); }

    public void retainAll(Key key) {
        Key shared = new DefaultKeyList();
        shared.addAll(key);
        retain(this, shared);
    }

    protected static void retain(Key alter, Key base) {
        Iterator<Key> it = alter.iterator();
        while (it.hasNext()) {
            Key sublist = it.next();
            if (sublist.canHaveChildren()) {
                retain(sublist, base);
                if (sublist.isEmpty()) { it.remove(); }
                continue;
            }
            if (!base.contains(sublist)) {
                it.remove();
            }
        }
    }

    public String toString() { return getName(); }

    public void setName(String name) { this.name = name; }

    public String getName() {
        if (this.name != null) { return this.name; }
        DefaultKeyVisitor visitor = new NameVisitor();
        KeyUtil.visit(this, visitor);
        return visitor.toString();
    }

    public String getName(Key base) {  return getName(); }

    public String getRootName()     {  return getName(); }

    public String getOsisRef() {
        DefaultKeyVisitor visitor = new OsisRefVisitor();
        KeyUtil.visit(this, visitor);
        return visitor.toString();
    }

    public String getOsisID() {
        DefaultKeyVisitor visitor = new OsisIDVisitor();
        KeyUtil.visit(this, visitor);
        return visitor.toString();
    }

    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (!obj.getClass().equals(getClass())) { return false; }
        return (compareTo((Key) obj) == 0);
    }

    public int hashCode() { return getName().hashCode(); }

    public int compareTo(Key that) {
        if (this == that) { return 0; }
        if (that == null) { return -1; }
        int ret = getName().compareTo(that.getName());
        if (ret != 0)     { return ret; }
        Iterator<Key> thisIter = iterator();
        Iterator<Key> thatIter = that.iterator();
        Key thisfirst = null;
        Key thatfirst = null;
        if (thisIter.hasNext()) { thisfirst = thisIter.next(); }
        if (thatIter.hasNext()) { thatfirst = thatIter.next(); }
        if (thisfirst == null) {
            if (thatfirst == null) { return 0; }
            return 1;
        }
        if (thatfirst == null)     { return -1; }
        return thisfirst.getName().compareTo(thatfirst.getName());
    }

    public AbstractKeyList clone() {
        AbstractKeyList clone = null;
        try {
            clone = (AbstractKeyList) super.clone();
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }

    static class NameVisitor extends DefaultKeyVisitor {

        protected StringBuilder buffer = new StringBuilder();

        public void visitLeaf(Key key) {
            this.buffer.append(key.getName());
            this.buffer.append(", ");
        }

        public String toString() {
            String reply = this.buffer.toString();
            if (reply.length() > 0) {
                reply = reply.substring(0, reply.length() - ", ".length());
            }
            return reply;
        }
    }

    static class OsisRefVisitor extends NameVisitor {

        public void visitLeaf(Key key) {
            this.buffer.append(key.getOsisRef());
            this.buffer.append(", ");
        }
    }

    static class OsisIDVisitor extends NameVisitor {

        public void visitLeaf(Key key) {
            this.buffer.append(key.getOsisID());
            this.buffer.append(" ");
        }

        public String toString() {
            String reply = super.toString();
            if (reply.length() > 0) {
                reply = reply.substring(0, reply.length() - " ".length());
            }
            return reply;
        }
    }
}