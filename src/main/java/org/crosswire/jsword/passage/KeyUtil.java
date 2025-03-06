package org.crosswire.jsword.passage;

import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

public final class KeyUtil 
{

    public static void visit(Key key, KeyVisitor visitor)
    {
        for (Key subkey : key)
        {
            if (subkey.canHaveChildren())
            {
                visitor.visitBranch(subkey);
                visit(subkey, visitor);
                continue;
            }
            visitor.visitLeaf(subkey);
        }
    }

    public static Verse getVerse(Key key)
    {
        if (key instanceof Verse)
        {
            return (Verse) key;
        }
        if (key instanceof VerseRange)
        {
            VerseRange range = (VerseRange) key;
            return range.getStart();
        }
        if (key instanceof Passage)
        {
            Passage ref = (Passage) key;
            return ref.getVerseAt(0);
        }
        throw new ClassCastException("Expected key to be a Verse, VerseRange or Passage");
    }

    public static Passage getPassage(Key key)
    {
        if (key == null)
        {
            return null;
        }
        if (key instanceof Passage)
        {
            return (Passage) key;
        }
        if (key instanceof VerseKey)
        {
            VerseKey verseKey = (VerseKey) key;
            Key ref = PassageKeyFactory.instance().createEmptyKeyList(verseKey.getVersification());
            ref.addAll(verseKey);
            return (Passage) ref;
        }
        throw new ClassCastException("Expected key to be a Verse, VerseRange or Passage");
    }

    public static Versification getVersification(Key key)
    {
        if (key instanceof VerseKey)
        {
            return ((VerseKey) key).getVersification();
        }
        try
        {
            return Versifications.instance().getVersification("KJV");
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}