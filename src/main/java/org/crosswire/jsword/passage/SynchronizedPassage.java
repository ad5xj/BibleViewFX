package org.crosswire.jsword.passage;

import org.crosswire.jsword.versification.Versification;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.util.Iterator;

final class SynchronizedPassage implements Passage {

    private Passage ref;

    private static final long serialVersionUID = 3833181441264531251L;

    SynchronizedPassage(Passage ref) {
        this.ref = ref;
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

    public synchronized void addAll(Key key) {
        this.ref.addAll(key);
    }

    public synchronized void removeAll(Key key) {
        this.ref.removeAll(key);
    }

    public synchronized void retainAll(Key key) {
        this.ref.retainAll(key);
    }

    public synchronized boolean contains(Key key) {
        return this.ref.contains(key);
    }

    public synchronized int getChildCount() {
        return this.ref.getChildCount();
    }

    public synchronized int getCardinality() {
        return this.ref.getCardinality();
    }

    public synchronized boolean canHaveChildren() {
        return this.ref.canHaveChildren();
    }

    public synchronized Iterator<Key> iterator() {
        return this.ref.iterator();
    }

    public synchronized Key get(int index) {
        return this.ref.get(index);
    }

    public synchronized int indexOf(Key that) {
        return this.ref.indexOf(that);
    }

    public synchronized Key getParent() {
        return this.ref.getParent();
    }

    public synchronized String getName() {
        return this.ref.getName();
    }

    public synchronized String getName(Key base) {
        return this.ref.getName(base);
    }

    public synchronized String getRootName() {
        return this.ref.getRootName();
    }

    public synchronized String getOsisRef() {
        return this.ref.getOsisRef();
    }

    public synchronized String getOsisID() {
        return this.ref.getOsisID();
    }

    public synchronized String getOverview() {
        return this.ref.getOverview();
    }

    public synchronized boolean isEmpty() {
        return this.ref.isEmpty();
    }

    public synchronized int countVerses() {
        return this.ref.countVerses();
    }

    public synchronized boolean hasRanges(RestrictionType restrict) {
        return this.ref.hasRanges(restrict);
    }

    public synchronized int countRanges(RestrictionType restrict) {
        return this.ref.countRanges(restrict);
    }

    public synchronized Passage trimVerses(int count) {
        return this.ref.trimVerses(count);
    }

    public synchronized Passage trimRanges(int count, RestrictionType restrict) {
        return this.ref.trimRanges(count, restrict);
    }

    public synchronized int booksInPassage() {
        return this.ref.booksInPassage();
    }

    public synchronized Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException {
        return this.ref.getVerseAt(offset);
    }

    public synchronized VerseRange getRangeAt(int offset, RestrictionType restrict) throws ArrayIndexOutOfBoundsException {
        return this.ref.getRangeAt(offset, restrict);
    }

    public synchronized Iterator<VerseRange> rangeIterator(RestrictionType restrict) {
        return this.ref.rangeIterator(restrict);
    }

    public synchronized void add(Key that) {
        this.ref.add(that);
    }

    public synchronized void remove(Key that) {
        this.ref.remove(that);
    }

    public synchronized boolean containsAll(Passage that) {
        return this.ref.containsAll(that);
    }

    public synchronized void clear() {
        this.ref.clear();
    }

    public synchronized void blur(int by, RestrictionType restrict) {
        this.ref.blur(by, restrict);
    }

    public synchronized void readDescription(Reader in) throws IOException, NoSuchVerseException {
        this.ref.readDescription(in);
    }

    public synchronized void writeDescription(Writer out) throws IOException {
        this.ref.writeDescription(out);
    }

    public synchronized void optimizeReads() {
        this.ref.optimizeReads();
    }

    public synchronized void addPassageListener(PassageListener li) {
        this.ref.addPassageListener(li);
    }

    public synchronized void removePassageListener(PassageListener li) {
        this.ref.removePassageListener(li);
    }

    public synchronized SynchronizedPassage clone() {
        SynchronizedPassage clone = null;
        try {
            clone = (SynchronizedPassage) super.clone();
            synchronized (clone) {
                clone.ref = (Passage) this.ref.clone();
            }
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }

    public synchronized int hashCode() {
        return this.ref.hashCode();
    }

    public synchronized boolean equals(Object obj) {
        return this.ref.equals(obj);
    }

    public synchronized int compareTo(Key o) {
        return this.ref.compareTo(o);
    }
}