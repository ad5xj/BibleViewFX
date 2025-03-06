package org.crosswire.jsword.passage;

import org.crosswire.jsword.versification.Versification;

import java.io.IOException;
import java.io.ObjectInputStream;

import java.util.Iterator;

public class RocketPassage extends BitwisePassage {
  private transient DistinctPassage distinct;
  
  private transient RangedPassage ranged;
  
  private static final long serialVersionUID = 3258125864771401268L;
  
  public RocketPassage(Versification v11n) {
    super(v11n);
  }
  
  protected RocketPassage(Versification v11n, String refs, Key basis) throws NoSuchVerseException {
    super(v11n, refs, basis);
  }
  
  protected RocketPassage(Versification v11n, String refs) throws NoSuchVerseException {
    this(v11n, refs, (Key)null);
  }
  
  public void optimizeReads() {
    raiseEventSuppresion();
    DistinctPassage dtemp = new DistinctPassage(getVersification());
    dtemp.raiseEventSuppresion();
    dtemp.addAll(this);
    dtemp.lowerEventSuppressionAndTest();
    RangedPassage rtemp = new RangedPassage(getVersification());
    rtemp.raiseEventSuppresion();
    rtemp.addAll(this);
    rtemp.lowerEventSuppressionAndTest();
    this.distinct = dtemp;
    this.ranged = rtemp;
    lowerEventSuppressionAndTest();
  }
  
  protected void optimizeWrites() {
    this.distinct = null;
    this.ranged = null;
  }
  
  public int countRanges(RestrictionType restrict) {
    if (this.ranged != null)
      return this.ranged.countRanges(restrict); 
    return super.countRanges(restrict);
  }
  
  public int countVerses() {
    if (this.distinct != null)
      return this.distinct.countVerses(); 
    return super.countVerses();
  }
  
  public Iterator<Key> iterator() {
    if (this.distinct != null)
      return this.distinct.iterator(); 
    return super.iterator();
  }
  
  public Iterator<VerseRange> rangeIterator(RestrictionType restrict) {
    if (this.ranged != null)
      return this.ranged.rangeIterator(restrict); 
    return super.rangeIterator(restrict);
  }
  
  public boolean isEmpty() {
    if (this.distinct != null)
      return this.distinct.isEmpty(); 
    return super.isEmpty();
  }
  
  public Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException {
    if (this.distinct != null)
      return this.distinct.getVerseAt(offset); 
    return super.getVerseAt(offset);
  }
  
  public VerseRange getRangeAt(int offset, RestrictionType restrict) throws ArrayIndexOutOfBoundsException {
    if (this.ranged != null)
      return this.ranged.getRangeAt(offset, restrict); 
    return super.getRangeAt(offset, restrict);
  }
  
  public int booksInPassage() {
    if (this.distinct != null)
      return this.distinct.booksInPassage(); 
    return super.booksInPassage();
  }
  
  public boolean containsAll(Passage that) {
    if (this.ranged != null)
      return this.ranged.containsAll(that); 
    return super.containsAll(that);
  }
  
  private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
    optimizeWrites();
    is.defaultReadObject();
  }
}