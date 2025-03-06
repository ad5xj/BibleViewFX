package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.state.OpenFileState;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.RestrictionType;

import java.io.IOException;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractKeyBackend<T extends OpenFileState> extends AbstractBackend<T> implements Key {

    private static final long serialVersionUID = -2782112117361556089L;

    public AbstractKeyBackend(SwordBookMetaData sbmd) {
        super(sbmd);
    }

    public boolean canHaveChildren() {
        return false;
    }

    public int getChildCount() {
        return 0;
    }

    public boolean isEmpty() {
        return (getCardinality() == 0);
    }

    public boolean contains(Key key) {
        return (indexOf(key) >= 0);
    }

    public Iterator<Key> iterator() {
        return new Iterator<Key>() {
            private int here;

            public boolean hasNext() {
                return (this.here < this.count);
            }

            public Key next() throws NoSuchElementException {
                if (this.here >= this.count) {
                    throw new NoSuchElementException();
                }
                return AbstractKeyBackend.this.get(this.here++);
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            private int count = AbstractKeyBackend.this.getCardinality();
        };
    }

    public void addAll(Key key) {
        throw new UnsupportedOperationException();
    }

    public void removeAll(Key key) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public void setAliasKey(T state, Key alias, Key source) throws IOException {
        throw new UnsupportedOperationException();
    }

    public void setRawText(T state, Key key, String text) throws BookException, IOException {
        throw new UnsupportedOperationException();
    }

    public Key getParent() {
        return null;
    }

    public AbstractKeyBackend<T> clone() {
        AbstractKeyBackend<T> clone = null;
        try {
            clone = (AbstractKeyBackend<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }

    public String getName() {
        return getBookMetaData().getInitials();
    }

    public String getName(Key base) {
        return getName();
    }

    public String getOsisID() {
        return getName();
    }

    public String getOsisRef() {
        return getName();
    }

    public String getRootName() {
        return getName();
    }

    public void retainAll(Key key) {
        throw new UnsupportedOperationException();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(getClass())) {
            return false;
        }
        return (compareTo((Key) obj) == 0);
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public int compareTo(Key that) {
        if (this == that) {
            return 0;
        }
        if (that == null) {
            return -1;
        }
        int ret = getName().compareTo(that.getName());
        if (ret != 0) {
            return ret;
        }
        Iterator<Key> thisIter = iterator();
        Iterator<Key> thatIter = that.iterator();
        Key thisfirst = null;
        Key thatfirst = null;
        if (thisIter.hasNext()) {
            thisfirst = thisIter.next();
        }
        if (thatIter.hasNext()) {
            thatfirst = thatIter.next();
        }
        if (thisfirst == null) {
            if (thatfirst == null) {
                return 0;
            }
            return 1;
        }
        if (thatfirst == null) {
            return -1;
        }
        return thisfirst.getName().compareTo(thatfirst.getName());
    }

    public void blur(int by, RestrictionType restrict) {
    }
}
