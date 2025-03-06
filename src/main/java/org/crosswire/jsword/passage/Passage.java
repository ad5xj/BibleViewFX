package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.util.Iterator;

public interface Passage extends VerseKey<Passage> 
{
    void add(Key paramKey);
    void remove(Key paramKey);
    void readDescription(Reader paramReader) throws IOException, NoSuchVerseException;
    void writeDescription(Writer paramWriter) throws IOException;
    void optimizeReads();
    void addPassageListener(PassageListener paramPassageListener);
    void removePassageListener(PassageListener paramPassageListener);

    boolean hasRanges(RestrictionType paramRestrictionType);
    boolean contains(Key paramKey);
    boolean containsAll(Passage paramPassage);

    int countVerses();
    int countRanges(RestrictionType paramRestrictionType);
    int booksInPassage();

    String getOverview();

    Passage trimVerses(int paramInt);
    Passage trimRanges(int paramInt, RestrictionType paramRestrictionType);


    Verse getVerseAt(int paramInt) throws ArrayIndexOutOfBoundsException;
    VerseRange getRangeAt(int paramInt, RestrictionType paramRestrictionType) throws ArrayIndexOutOfBoundsException;

    Iterator<VerseRange> rangeIterator(RestrictionType paramRestrictionType);
}
