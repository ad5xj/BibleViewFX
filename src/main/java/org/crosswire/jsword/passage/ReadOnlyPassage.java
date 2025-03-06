package org.crosswire.jsword.passage;

import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.versification.Versification;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.util.Iterator;

final class ReadOnlyPassage implements Passage {

    private Passage ref;

    private boolean ignore;

    private static final long serialVersionUID = 3257853173036102193L;

    ReadOnlyPassage(Passage ref, boolean ignore) {
        this.ref = ref;
        this.ignore = ignore;
    }

    public Versification getVersification() {
        return this.ref.getVersification();
    }

    public Passage reversify(Versification newVersification) {
        return this.ref.reversify(newVersification);
    }

    public boolean isWhole() {
        return this.ref.isWhole();
    }

    public Passage getWhole() {
        return this.ref.getWhole();
    }

    public void addAll(Key key) {
        if (this.ignore) {
            return;
        }
        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only passage", new Object[0]));
    }

    public void removeAll(Key key) {
        if (this.ignore) {
            return;
        }
        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only passage", new Object[0]));
    }

    public void retainAll(Key key) {
        if (this.ignore) {
            return;
        }
        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only passage", new Object[0]));
    }

    public boolean contains(Key key) {
        return this.ref.contains(key);
    }

    public boolean canHaveChildren() {
        return this.ref.canHaveChildren();
    }

    public int getChildCount() {
        return this.ref.getChildCount();
    }

    public int getCardinality() {
        return this.ref.getCardinality();
    }

    public Iterator<Key> iterator() {
        return this.ref.iterator();
    }

    public Key get(int index) {
        return this.ref.get(index);
    }

    public int indexOf(Key that) {
        return this.ref.indexOf(that);
    }

    public Key getParent() {
        return this.ref.getParent();
    }

    public String getName() {
        return this.ref.getName();
    }

    public String getName(Key base) {
        return this.ref.getName(base);
    }

    public String getRootName() {
        return this.ref.getRootName();
    }

    public String getOsisRef() {
        return this.ref.getOsisRef();
    }

    public String getOsisID() {
        return this.ref.getOsisID();
    }

    public String getOverview() {
        return this.ref.getOverview();
    }

    public boolean isEmpty() {
        return this.ref.isEmpty();
    }

    public int countVerses() {
        return this.ref.countVerses();
    }

    public boolean hasRanges(RestrictionType restrict) {
        return this.ref.hasRanges(restrict);
    }

    public int countRanges(RestrictionType restrict) {
        return this.ref.countRanges(restrict);
    }

    public Passage trimVerses(int count) {
        return this.ref.trimVerses(count);
    }

    public Passage trimRanges(int count, RestrictionType restrict) {
        return this.ref.trimRanges(count, restrict);
    }

    public int booksInPassage() {
        return this.ref.booksInPassage();
    }

    public Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException {
        return this.ref.getVerseAt(offset);
    }

    public VerseRange getRangeAt(int offset, RestrictionType restrict) throws ArrayIndexOutOfBoundsException {
        return this.ref.getRangeAt(offset, restrict);
    }

    public Iterator<VerseRange> rangeIterator(RestrictionType restrict) {
        return this.ref.rangeIterator(restrict);
    }

    public void add(Key that) {
        if (this.ignore) {
            return;
        }
        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only passage", new Object[0]));
    }

    public void remove(Key that) {
        if (this.ignore) {
            return;
        }
        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only passage", new Object[0]));
    }

    public boolean containsAll(Passage that) {
        return this.ref.containsAll(that);
    }

    public void clear() {
        if (this.ignore) {
            return;
        }
        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only passage", new Object[0]));
    }

    public void blur(int by, RestrictionType restrict) {
        if (this.ignore) {
            return;
        }
        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only passage", new Object[0]));
    }

    public void readDescription(Reader in) {
        if (this.ignore) {
            return;
        }
        throw new IllegalStateException(JSOtherMsg.lookupText("Cannot alter a read-only passage", new Object[0]));
    }

    public void writeDescription(Writer out) throws IOException {
        this.ref.writeDescription(out);
    }

    public void optimizeReads() {
        this.ref.optimizeReads();
    }

    public void addPassageListener(PassageListener li) {
    }

    public void removePassageListener(PassageListener li) {
    }

    public boolean equals(Object obj) {
        return this.ref.equals(obj);
    }

    public int hashCode() {
        return this.ref.hashCode();
    }

    public String toString() {
        return this.ref.toString();
    }

    public ReadOnlyPassage clone() {
        ReadOnlyPassage clone = null;
        try {
            clone = (ReadOnlyPassage) super.clone();
            clone.ref = this.ref;
            clone.ignore = this.ignore;
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }

    public int compareTo(Key o) {
        return this.ref.compareTo(o);
    }
}