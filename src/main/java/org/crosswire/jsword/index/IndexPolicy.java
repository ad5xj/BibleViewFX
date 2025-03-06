package org.crosswire.jsword.index;

public interface IndexPolicy {

    boolean isStrongsIndexed();
    boolean isMorphIndexed();
    boolean isNoteIndexed();
    boolean isTitleIndexed();
    boolean isXrefIndexed();
    boolean isSerial();

    int getRAMBufferSize();
}