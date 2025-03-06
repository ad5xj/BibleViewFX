package org.crosswire.jsword.index;

public class IndexPolicyAdapter implements IndexPolicy
{

    public boolean isStrongsIndexed() { return true; }
    public boolean isMorphIndexed()   { return true; }
    public boolean isNoteIndexed()    { return true; }
    public boolean isTitleIndexed()   { return true; }
    public boolean isXrefIndexed()    { return true; }
    public boolean isSerial()         { return false; }

    public int getRAMBufferSize()     { return 16; }
}
