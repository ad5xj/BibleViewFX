package org.crosswire.jsword.passage;

import org.crosswire.common.icu.NumberShaper;

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class VerseRange implements VerseKey<VerseRange> {
  public static final char RANGE_OSIS_DELIM = '-';
  
  public static final char RANGE_PREF_DELIM = '-';
  
  private transient Versification v11n;
  
  private Verse start;
  
  private int verseCount;
  
  private transient Verse end;
  
  private transient NumberShaper shaper;
  
  private transient Key parent;
  
  private transient String originalName;
  
  static final long serialVersionUID = 8307795549869653580L;
  
  public VerseRange(Versification v11n) {
    this(v11n, Verse.DEFAULT, Verse.DEFAULT);
  }
  
  public VerseRange(Versification v11n, Verse start) {
    this(v11n, start, start);
  }
  
  public VerseRange(Versification v11n, Verse start, Verse end) {
    assert v11n != null;
    assert start != null;
    assert end != null;
    this.v11n = v11n;
    this.shaper = new NumberShaper();
    int distance = v11n.distance(start, end);
    if (distance < 0) {
      this.start = end;
      this.end = start;
      this.verseCount = calcVerseCount();
    } else if (distance == 0) {
      this.start = start;
      this.end = start;
      this.verseCount = 1;
    } else {
      this.start = start;
      this.end = end;
      this.verseCount = calcVerseCount();
    } 
  }
  
  public Versification getVersification() {
    return this.v11n;
  }
  
  public VerseRange reversify(Versification newVersification) {
    if (this.v11n.equals(newVersification))
      return this; 
    Verse newStart = this.start.reversify(newVersification);
    if (newStart == null)
      return null; 
    Verse newEnd = this.end.reversify(newVersification);
    if (newEnd == null)
      return null; 
    return new VerseRange(newVersification, newStart, newEnd);
  }
  
  public boolean isWhole() {
    return (this.start.isWhole() && this.end.isWhole());
  }
  
  public VerseRange getWhole() {
    if (isWhole())
      return this; 
    return new VerseRange(this.v11n, this.start.getWhole(), this.end.getWhole());
  }
  
  public VerseRange(VerseRange a, VerseRange b) {
    this.v11n = a.v11n;
    this.shaper = new NumberShaper();
    this.start = this.v11n.min(a.getStart(), b.getStart());
    this.end = this.v11n.max(a.getEnd(), b.getEnd());
    this.verseCount = calcVerseCount();
  }
  
  public String getName() {
    return getName(null);
  }
  
  public String getName(Key base) {
    if (PassageUtil.isPersistentNaming() && this.originalName != null)
      return this.originalName; 
    String rangeName = doGetName(base);
    if (this.shaper.canUnshape())
      return this.shaper.shape(rangeName); 
    return rangeName;
  }
  
  public String getRootName() {
    return this.start.getRootName();
  }
  
  public String getOsisRef() {
    BibleBook startBook = this.start.getBook();
    BibleBook endBook = this.end.getBook();
    int startChapter = this.start.getChapter();
    int endChapter = this.end.getChapter();
    if (startBook != endBook) {
      StringBuilder buf = new StringBuilder();
      if (this.v11n.isStartOfBook(this.start)) {
        buf.append(startBook.getOSIS());
      } else if (this.v11n.isStartOfChapter(this.start)) {
        buf.append(startBook.getOSIS());
        buf.append('.');
        buf.append(startChapter);
      } else {
        buf.append(this.start.getOsisRef());
      } 
      buf.append('-');
      if (this.v11n.isEndOfBook(this.end)) {
        buf.append(endBook.getOSIS());
      } else if (this.v11n.isEndOfChapter(this.end)) {
        buf.append(endBook.getOSIS());
        buf.append('.');
        buf.append(endChapter);
      } else {
        buf.append(this.end.getOsisRef());
      } 
      return buf.toString();
    } 
    if (isWholeBook())
      return startBook.getOSIS(); 
    if (startChapter != endChapter) {
      StringBuilder buf = new StringBuilder();
      if (this.v11n.isStartOfChapter(this.start)) {
        buf.append(startBook.getOSIS());
        buf.append('.');
        buf.append(startChapter);
      } else {
        buf.append(this.start.getOsisRef());
      } 
      buf.append('-');
      if (this.v11n.isEndOfChapter(this.end)) {
        buf.append(endBook.getOSIS());
        buf.append('.');
        buf.append(endChapter);
      } else {
        buf.append(this.end.getOsisRef());
      } 
      return buf.toString();
    } 
    if (isWholeChapter()) {
      StringBuilder buf = new StringBuilder();
      buf.append(startBook.getOSIS());
      buf.append('.');
      buf.append(startChapter);
      return buf.toString();
    } 
    if (this.start.getVerse() != this.end.getVerse()) {
      StringBuilder buf = new StringBuilder();
      buf.append(this.start.getOsisRef());
      buf.append('-');
      buf.append(this.end.getOsisRef());
      return buf.toString();
    } 
    return this.start.getOsisRef();
  }
  
  public String getOsisID() {
    if (isWholeBook())
      return this.start.getBook().getOSIS(); 
    if (isWholeChapter())
      return this.start.getBook().getOSIS() + '.' + this.start.getChapter(); 
    int startOrdinal = this.start.getOrdinal();
    int endOrdinal = this.end.getOrdinal();
    StringBuilder buf = new StringBuilder((endOrdinal - startOrdinal + 1) * 10);
    buf.append(this.start.getOsisID());
    for (int i = startOrdinal + 1; i < endOrdinal; i++) {
      buf.append(" ");
      buf.append(this.v11n.decodeOrdinal(i).getOsisID());
    } 
    if (startOrdinal != endOrdinal) {
      buf.append(" ");
      buf.append(this.end.getOsisID());
    } 
    return buf.toString();
  }
  
  public String toString() {
    return getName();
  }
  
  public Verse getStart() {
    return this.start;
  }
  
  public Verse getEnd() {
    return this.end;
  }
  
  public VerseRange clone() {
    VerseRange copy = null;
    try {
      copy = (VerseRange)super.clone();
      copy.start = this.start;
      copy.end = this.end;
      copy.verseCount = this.verseCount;
      copy.originalName = this.originalName;
      copy.shaper = new NumberShaper();
      copy.v11n = this.v11n;
    } catch (CloneNotSupportedException e) {
      assert false : e;
    } 
    return copy;
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof VerseRange))
      return false; 
    VerseRange vr = (VerseRange)obj;
    return (this.verseCount == vr.verseCount && this.start.equals(vr.start) && this.v11n.equals(vr.v11n));
  }
  
  public int hashCode() {
    int result = this.start.hashCode();
    result = 31 * result + this.verseCount;
    return 31 * result + ((this.v11n == null) ? 0 : this.v11n.hashCode());
  }
  
  public int compareTo(Key obj) {
    VerseRange that = (VerseRange)obj;
    int result = this.start.compareTo(that.start);
    return (result == 0) ? (this.verseCount - that.verseCount) : result;
  }
  
  public boolean adjacentTo(VerseRange that) {
    int thatStart = that.getStart().getOrdinal();
    int thatEnd = that.getEnd().getOrdinal();
    int thisStart = getStart().getOrdinal();
    int thisEnd = getEnd().getOrdinal();
    if (thatStart >= thisStart - 1 && thatStart <= thisEnd + 1)
      return true; 
    if (thisStart >= thatStart - 1 && thisStart <= thatEnd + 1)
      return true; 
    return false;
  }
  
  public boolean overlaps(VerseRange that) {
    int thatStart = that.getStart().getOrdinal();
    int thatEnd = that.getEnd().getOrdinal();
    int thisStart = getStart().getOrdinal();
    int thisEnd = getEnd().getOrdinal();
    if (thatStart >= thisStart && thatStart <= thisEnd)
      return true; 
    if (thisStart >= thatStart && thisStart <= thatEnd)
      return true; 
    return false;
  }
  
  public boolean contains(Verse that) {
    return (this.v11n.distance(this.start, that) >= 0 && this.v11n.distance(that, this.end) >= 0);
  }
  
  public boolean contains(VerseRange that) {
    return (this.v11n.distance(this.start, that.getStart()) >= 0 && this.v11n.distance(that.getEnd(), this.end) >= 0);
  }
  
  public boolean contains(Key key) {
    if (key instanceof VerseRange)
      return contains((VerseRange)key); 
    if (key instanceof Verse)
      return contains((Verse)key); 
    return false;
  }
  
  public boolean isWholeChapter() {
    return (this.v11n.isSameChapter(this.start, this.end) && isWholeChapters());
  }
  
  public boolean isWholeChapters() {
    return (this.v11n.isStartOfChapter(this.start) && this.v11n.isEndOfChapter(this.end));
  }
  
  public boolean isWholeBook() {
    return (this.v11n.isSameBook(this.start, this.end) && isWholeBooks());
  }
  
  public boolean isWholeBooks() {
    return (this.v11n.isStartOfBook(this.start) && this.v11n.isEndOfBook(this.end));
  }
  
  public boolean isMultipleBooks() {
    return (this.start.getBook() != this.end.getBook());
  }
  
  public Verse[] toVerseArray() {
    Verse[] retcode = new Verse[this.verseCount];
    int ord = this.start.getOrdinal();
    for (int i = 0; i < this.verseCount; i++)
      retcode[i] = this.v11n.decodeOrdinal(ord + i); 
    return retcode;
  }
  
  public Iterator<VerseRange> rangeIterator(RestrictionType restrict) {
    return new AbstractPassage.VerseRangeIterator(this.v11n, iterator(), restrict);
  }
  
  public Key getParent() {
    return this.parent;
  }
  
  public void setParent(Key parent) {
    this.parent = parent;
  }
  
  public static VerseRange[] remainder(VerseRange a, VerseRange b) {
    VerseRange rstart = null;
    VerseRange rend = null;
    Versification v11n = a.getVersification();
    if (v11n.distance(a.getStart(), b.getStart()) > 0)
      rstart = new VerseRange(v11n, a.getStart(), v11n.subtract(b.getEnd(), 1)); 
    if (v11n.distance(a.getEnd(), b.getEnd()) < 0)
      rend = new VerseRange(v11n, v11n.add(b.getEnd(), 1), a.getEnd()); 
    if (rstart == null) {
      if (rend == null)
        return new VerseRange[0]; 
      return new VerseRange[] { rend };
    } 
    if (rend == null)
      return new VerseRange[] { rstart }; 
    return new VerseRange[] { rstart, rend };
  }
  
  public static VerseRange intersection(VerseRange a, VerseRange b) {
    Versification v11n = a.getVersification();
    Verse newStart = v11n.max(a.getStart(), b.getStart());
    Verse newEnd = v11n.min(a.getEnd(), b.getEnd());
    if (v11n.distance(newStart, newEnd) >= 0)
      return new VerseRange(a.getVersification(), newStart, newEnd); 
    return null;
  }
  
  private String doGetName(Key base) {
    BibleBook startBook = this.start.getBook();
    int startChapter = this.start.getChapter();
    int startVerse = this.start.getVerse();
    BibleBook endBook = this.end.getBook();
    int endChapter = this.end.getChapter();
    int endVerse = this.end.getVerse();
    if (startBook != endBook) {
      if (isWholeBooks())
        return this.v11n.getPreferredName(startBook) + '-' + this.v11n.getPreferredName(endBook); 
      if (isWholeChapters())
        return this.v11n.getPreferredName(startBook) + ' ' + startChapter + '-' + this.v11n.getPreferredName(endBook) + ' ' + endChapter; 
      if (this.v11n.isChapterIntro(this.start))
        return this.v11n.getPreferredName(startBook) + ' ' + startChapter + '-' + this.end.getName(base); 
      if (this.v11n.isBookIntro(this.start))
        return this.v11n.getPreferredName(startBook) + '-' + this.end.getName(base); 
      return this.start.getName(base) + '-' + this.end.getName(base);
    } 
    if (isWholeBook())
      return this.v11n.getPreferredName(startBook); 
    if (startChapter != endChapter) {
      if (isWholeChapters())
        return this.v11n.getPreferredName(startBook) + ' ' + startChapter + '-' + endChapter; 
      return this.start.getName(base) + '-' + endChapter + ':' + endVerse;
    } 
    if (isWholeChapter())
      return this.v11n.getPreferredName(startBook) + ' ' + startChapter; 
    if (startVerse != endVerse)
      return this.start.getName(base) + '-' + endVerse; 
    return this.start.getName(base);
  }
  
  private Verse calcEnd() {
    if (this.verseCount == 1)
      return this.start; 
    return this.v11n.add(this.start, this.verseCount - 1);
  }
  
  private int calcVerseCount() {
    return this.v11n.distance(this.start, this.end) + 1;
  }
  
  private void verifyData() {
    assert this.verseCount == calcVerseCount() : "start=" + this.start + ", end=" + this.end + ", verseCount=" + this.verseCount;
  }
  
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    this.end = calcEnd();
    this.shaper = new NumberShaper();
    verifyData();
  }
  
  private static final class VerseIterator implements Iterator<Key> {
    private Versification v11n;
    
    private Verse nextVerse;
    
    private int count;
    
    private int total;
    
    protected VerseIterator(VerseRange range) {
      this.v11n = range.getVersification();
      this.nextVerse = range.getStart();
      this.total = range.getCardinality();
      this.count = 0;
    }
    
    public boolean hasNext() {
      return (this.nextVerse != null);
    }
    
    public Key next() throws NoSuchElementException {
      if (this.nextVerse == null)
        throw new NoSuchElementException(); 
      Verse currentVerse = this.nextVerse;
      this.nextVerse = (++this.count < this.total) ? this.v11n.next(this.nextVerse) : null;
      return currentVerse;
    }
    
    public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
    }
  }
  
  public boolean canHaveChildren() {
    return false;
  }
  
  public int getChildCount() {
    return 0;
  }
  
  public int getCardinality() {
    return this.verseCount;
  }
  
  public boolean isEmpty() {
    return (this.verseCount == 0);
  }
  
  public Iterator<Key> iterator() {
    return new VerseIterator(this);
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
  
  public void clear() {}
  
  public Key get(int index) {
    return null;
  }
  
  public int indexOf(Key that) {
    return -1;
  }
  
  public void blur(int by, RestrictionType restrict) {
    VerseRange newRange = restrict.blur(this.v11n, this, by, by);
    this.start = newRange.start;
    this.end = newRange.end;
    this.verseCount = newRange.verseCount;
  }
}
