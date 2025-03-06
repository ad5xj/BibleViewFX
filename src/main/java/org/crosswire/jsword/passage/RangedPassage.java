package org.crosswire.jsword.passage;

import org.crosswire.jsword.versification.Versification;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public class RangedPassage extends AbstractPassage {

    private static final long serialVersionUID = 955115811339960826L;

    private transient Set<VerseRange> store;

    public RangedPassage(Versification refSystem) {
        super(refSystem);
        this.store = new TreeSet<VerseRange>();
    }

    protected RangedPassage(Versification v11n, String refs, Key basis) throws NoSuchVerseException {
        super(v11n, refs);
        this.store = new TreeSet<VerseRange>();
        addVerses(refs, basis);
        normalize();
    }

    protected RangedPassage(Versification v11n, String refs) throws NoSuchVerseException {
        this(v11n, refs, (Key) null);
    }

    public RangedPassage clone() {
        RangedPassage copy = (RangedPassage) super.clone();
        copy.store = new TreeSet<VerseRange>();
        copy.store.addAll(this.store);
        return copy;
    }

    public int countRanges(RestrictionType restrict) {
        if (restrict.equals(RestrictionType.NONE)) {
            return this.store.size();
        }
        return super.countRanges(restrict);
    }

    public int countVerses() {
        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
        int count = 0;
        while (it.hasNext()) {
            VerseRange range = it.next();
            count += range.getCardinality();
        }
        return count;
    }

    public Iterator<Key> iterator() {
        return new VerseIterator(getVersification(), rangeIterator(RestrictionType.NONE));
    }

    public final Iterator<VerseRange> rangeIterator(RestrictionType restrict) {
        if (restrict.equals(RestrictionType.NONE)) {
            return this.store.iterator();
        }
        return new VerseRangeIterator(this.store.iterator(), restrict);
    }

    public boolean isEmpty() {
        return this.store.isEmpty();
    }

    public boolean contains(Key obj) {
        VerseRange thatRange = toVerseRange(getVersification(), obj);
        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
        while (it.hasNext()) {
            VerseRange thisRange = it.next();
            if (thisRange.contains(thatRange)) {
                return true;
            }
        }
        return false;
    }

    public void add(Key obj) {
        optimizeWrites();
        VerseRange thatRange = toVerseRange(getVersification(), obj);
        this.store.add(thatRange);
        normalize();
        if (this.suppressEvents == 0) {
            fireIntervalAdded(this, thatRange.getStart(), thatRange.getEnd());
        }
    }

    public void clear() {
        optimizeWrites();
        this.store.clear();
        fireIntervalRemoved(this, null, null);
    }

    public void remove(Key obj) {
        optimizeWrites();
        VerseRange thatRange = toVerseRange(getVersification(), obj);
        boolean removed = false;
        Set<Key> newStore = new TreeSet<Key>();
        newStore.addAll((Collection) this.store);
        for (Key aKey : newStore) {
            VerseRange thisRange = (VerseRange) aKey;
            if (thisRange.overlaps(thatRange)) {
                this.store.remove(thisRange);
                VerseRange[] vra = VerseRange.remainder(thisRange, thatRange);
                for (int i = 0; i < vra.length; i++) {
                    this.store.add(vra[i]);
                }
                removed = true;
            }
        }
        if (removed) {
            normalize();
        }
        if (this.suppressEvents == 0) {
            fireIntervalRemoved(this, thatRange.getStart(), thatRange.getEnd());
        }
    }

    public void retainAll(Key key) {
        optimizeWrites();
        Set<VerseRange> newStore = new TreeSet<VerseRange>();
        if (key instanceof RangedPassage) {
            Iterator<VerseRange> thatIter = ((RangedPassage) key).rangeIterator(RestrictionType.CHAPTER);
            while (thatIter.hasNext()) {
                VerseRange thatRange = thatIter.next();
                Iterator<VerseRange> thisIter = rangeIterator(RestrictionType.NONE);
                while (thisIter.hasNext()) {
                    VerseRange thisRange = thisIter.next();
                    if (thisRange.overlaps(thatRange)) {
                        VerseRange interstect = VerseRange.intersection(thisRange, thatRange);
                        if (interstect != null) {
                            newStore.add(interstect);
                        }
                    }
                }
            }
        } else {
            Iterator<Key> thatIter = key.iterator();
            while (thatIter.hasNext()) {
                VerseRange thatRange = toVerseRange(getVersification(), thatIter.next());
                Iterator<VerseRange> thisIter = rangeIterator(RestrictionType.NONE);
                while (thisIter.hasNext()) {
                    VerseRange thisRange = thisIter.next();
                    if (thisRange.overlaps(thatRange)) {
                        VerseRange interstect = VerseRange.intersection(thisRange, thatRange);
                        if (interstect != null) {
                            newStore.add(interstect);
                        }
                    }
                }
            }
        }
        this.store = newStore;
        normalize();
        fireIntervalRemoved(this, null, null);
    }

    final void normalize() {
        if (this.skipNormalization != 0) {
            return;
        }
        VerseRange last = null;
        VerseRange next = null;
        Set<VerseRange> newStore = new TreeSet<VerseRange>();
        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
        while (it.hasNext()) {
            next = it.next();
            if (last != null && next.adjacentTo(last)) {
                VerseRange merge = new VerseRange(last, next);
                newStore.remove(last);
                newStore.add(merge);
                last = merge;
                continue;
            }
            newStore.add(next);
            last = next;
        }
        this.store = newStore;
    }

    private static final class VerseIterator implements Iterator<Key> {

        private Iterator<Key> real;

        protected VerseIterator(Versification v11n, Iterator<VerseRange> it) {
            Set<Key> temp = new TreeSet<Key>();
            while (it.hasNext()) {
                VerseRange range = it.next();
                int start = range.getStart().getOrdinal();
                int end = range.getCardinality();
                for (int i = 0; i < end; i++) {
                    temp.add(v11n.decodeOrdinal(start + i));
                }
            }
            this.real = temp.iterator();
        }

        public boolean hasNext() {
            return this.real.hasNext();
        }

        public Key next() throws NoSuchElementException {
            return this.real.next();
        }

        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }

    private static final class VerseRangeIterator implements Iterator<VerseRange> {

        private VerseRange next;

        private RestrictionType restrict;

        private Iterator<VerseRange> real;

        protected VerseRangeIterator(Iterator<VerseRange> it, RestrictionType restrict) {
            this.restrict = restrict;
            this.real = it;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            return (this.next != null || this.real.hasNext());
        }

        public VerseRange next() {
            if (this.next == null) {
                this.next = this.real.next();
            }
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            if (this.restrict.isSameScope(this.next.getVersification(), this.next.getStart(), this.next.getEnd())) {
                return replyNext();
            }
            return splitNext();
        }

        private VerseRange replyNext() {
            VerseRange reply = this.next;
            this.next = null;
            return reply;
        }

        private VerseRange splitNext() {
            Iterator<VerseRange> chop = this.next.rangeIterator(this.restrict);
            VerseRange first = chop.next();
            VerseRange[] ranges = VerseRange.remainder(this.next, first);
            assert ranges.length == 1;
            this.next = ranges[0];
            return first;
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        writeObjectSupport(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        optimizeWrites();
        this.store = new TreeSet<VerseRange>();
        in.defaultReadObject();
        readObjectSupport(in);
    }
}