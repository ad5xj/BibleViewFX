package org.crosswire.jsword.passage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.icu.NumberShaper;

import org.crosswire.common.util.ItemIterator;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.BibleNames;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Iterator;

public class Verse implements VerseKey<Verse> 
{
    public static final char VERSE_OSIS_DELIM = '.';
    public static final char VERSE_OSIS_SUB_PREFIX = '!';
    public static final char VERSE_PREF_DELIM1 = ' ';
    public static final char VERSE_PREF_DELIM2 = ':';
    
    public static Verse DEFAULT;

    private static final long serialVersionUID = -4033921076023185171L;
    private static final String THISMODULE = "org.crosswire.jsword.passage.Verse";
    private static final Logger lgr = LoggerFactory.getLogger(Verse.class);


    private static NumberShaper shaper = new NumberShaper();

    public static boolean bothNullOrEqual(Object x, Object y)  
    { 
        return ( (x == y) || ((x != null) && x.equals(y)) ); 
    }

    protected static int parseInt(String text)
    {
        int v = -1;
        try
        {
            v = Integer.parseInt(shaper.unshape(text));
        }
        catch ( Exception ex)
        {
            String msg = "Cannot understand " + text + " as a chapter or verse.";
            lgr.error(msg,THISMODULE);
        }
        return v;
    }

    private transient Versification v11n;
    private transient BibleBook book;
    private transient int chapter;
    private transient int verse;

    private int ordinal;

    private String subIdentifier;

    /**
     * @brief Constructor for this class with simple params
     * 
     * @param i_v11n
     * @param i_ordinal 
     */
    public Verse(Versification i_v11n, int i_ordinal)
    {
        try
        {
            DEFAULT = new Verse(Versifications.instance().getVersification("KJV"), BibleBook.GEN, 1, 1);
        }
        catch ( Exception e )
        {
            String msg = "Error creating instance of Verse - " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        Verse decoded = i_v11n.decodeOrdinal(i_ordinal);
        v11n = i_v11n;
        book = decoded.book;
        chapter = decoded.chapter;
        verse = decoded.verse;
        ordinal = decoded.ordinal;
    }

    /**
     * @brief Constructor with params
     * 
     * @param v11n
     * @param book
     * @param chapter
     * @param verse 
     */
    public Verse(Versification v11n, BibleBook book, int chapter, int verse)
    {
        this(v11n, book, chapter, verse, null);
    }

    /**
     * @brief Overloaded constructor to add subIdentifier param
     * 
     * @param i_v11n Versification
     * @param i_book BibleBook
     * @param i_chapter Integer
     * @param i_verse Integer
     * @param i_subIdentifier String 
     */
    public Verse(Versification i_v11n, BibleBook i_book, int i_chapter, int i_verse, String i_subIdentifier)
    {
        v11n = i_v11n;
        book = i_book;
        chapter = i_chapter;
        verse = i_verse;
        subIdentifier = i_subIdentifier;
        ordinal = i_v11n.getOrdinal(this);
    }

    /**
     * @brief Overloaded constructor to add Boolean patchUp as param
     * @param i_v11n
     * @param i_book
     * @param i_chapter
     * @param i_verse
     * @param i_patchUp 
     */
    public Verse(Versification i_v11n, BibleBook i_book, int i_chapter, int i_verse, boolean i_patchUp)
    {
        Verse patched;

        if ( !i_patchUp )
        {
            String msg = "Use patchUp=true.";
            lgr.error(msg,THISMODULE);
            throw new IllegalArgumentException(JSOtherMsg.lookupText("Use patchUp=true.", new Object[0]));
        }
        v11n = i_v11n;
        patched = v11n.patch(i_book, i_chapter, i_verse);
        book = patched.book;
        chapter = patched.chapter;
        verse = patched.verse;
        ordinal = patched.ordinal;
    }

    public boolean isWhole() { return ( (subIdentifier == null) || (subIdentifier.length() == 0) ); }
    public boolean canHaveChildren()  { return false; }
    public boolean isEmpty()           { return false; }
    public boolean contains(Key key)   { return equals(key); }

    public int getChildCount()        { return 0; }
    public int getCardinality()       { return 1; }


    public Iterator<Key> iterator()    { return (Iterator<Key>) new ItemIterator(this); }

    public Verse getWhole()
    {
        if (isWhole()) { return this; }
        return new Verse(v11n, book, chapter, verse);
    }

    public String toString()            { return getName(); }
    public String getName()             { return getName(null); }
    public String getRootName()         { return BibleNames.instance().getShortName(book); }
    public String getOsisRef()          { return getOsisID(); }
    public String getOsisIDNoSubIdentifier() { return getVerseIdentifier().toString(); }
    public String getName(Key base)
    {
        if ( (base != null) && !(base instanceof Verse) ) { return getName(); }
        String verseName = doGetName((Verse) base);
        if (shaper.canUnshape())  { return shaper.shape(verseName); }
        return verseName;
    }

    public String getOsisID()
    {
        String buf = getVerseIdentifier();
        if ( (subIdentifier != null) && (subIdentifier.length() > 0) )
        {
            buf += "!" + subIdentifier;
        }
        return buf;
    }

    public Verse clone()
    {
        Verse copy = null;
        try
        {
            copy = (Verse) super.clone();
            copy.v11n = v11n;
            copy.book = book;
            copy.chapter = chapter;
            copy.verse = verse;
            copy.ordinal = ordinal;
            copy.subIdentifier = subIdentifier;
        }
        catch (CloneNotSupportedException e)
        {
            assert false : e;
        }
        return copy;
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof Verse)) { return false; }
        Verse that = (Verse) obj;
        return ( (ordinal == that.ordinal) && 
                 (
                    v11n.equals(that.v11n) && 
                    bothNullOrEqual(subIdentifier, that.subIdentifier)
                 )
                );
    }

    public int hashCode()
    {
        int result = 31 + ordinal;
        result = 31 * result + ((v11n == null) ? 0 : v11n.hashCode());
        return 31 * result + ((subIdentifier == null) ? 0 : subIdentifier.hashCode());
    }

    public Key get(int index)
    {
        if ( index == 0 ) { return this; }
        return null;
    }

    public int indexOf(Key that)
    {
        if ( equals(that) ) { return 0; }
        return -1;
    }

    public int compareTo(Key obj) { return (ordinal - ((Verse) obj).ordinal); }

    public Versification getVersification() { return v11n; }

    public Verse reversify(Versification newVersification)
    {
        if ( v11n.equals(newVersification) ) { return this; }
        try
        {
            if (newVersification.validate(book, chapter, verse, true))
            {
                return new Verse(newVersification, book, chapter, verse);
            }
        }
        catch (NoSuchVerseException ex)
        {
            String msg = "Contract for validate was changed to thrown an exception when silent mode is true - " + ex.getMessage();
            lgr.error(msg,THISMODULE);
        }
        return null;
    }

    public int getChapter()       { return chapter; }
    public int getVerse()         { return verse; }
    public int getOrdinal()       { return ordinal; }

    public String getSubIdentifier()  { return subIdentifier; }

    public Key getParent()        {  return null; }

    public BibleBook getBook()    { return book; }

    public Verse[] toVerseArray() { return new Verse[] { this }; }


    public void addAll(Key key)
    {
        throw new UnsupportedOperationException();
    }

    public void removeAll(Key key)
    {
        throw new UnsupportedOperationException();
    }

    public void retainAll(Key key)
    {
        throw new UnsupportedOperationException();
    }

    public void clear()    {    }


    public void blur(int by, RestrictionType restrict) { throw new UnsupportedOperationException(); }

    private String getVerseIdentifier()
    {
        String buf;
        buf = book.getOSIS() + "." + chapter + "." + verse;
        return buf;
    }

    private String doGetName(Verse verseBase)
    {
        String buf;
        
        buf = "";
        if ( book.isShortBook() )
        {
            if ( (verseBase == null) || (verseBase.book != book) )
            {
                buf = BibleNames.instance().getPreferredName(book)
                    + " " + verse;
                return buf;
            }
            return Integer.toString(verse);
        }

        if ( (verseBase == null) || (verseBase.book != book) )
        {
            buf += BibleNames.instance().getPreferredName(book);
            buf += " ";
            buf += chapter;
            buf += ":";
            buf += verse;
            return buf;
        }
        if (verseBase.chapter != chapter)
        {
            buf += chapter;
            buf += ":";
            buf += verse;
            return buf;
        }
        return Integer.toString(verse);
    }
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
        out.writeUTF(v11n.getName());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        String v11nName = in.readUTF();
        try
        {
            v11n = Versifications.instance().getVersification(v11nName);
        }
        catch ( Exception e )
        {
            String msg = "Error reading input stream object - " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        Verse decoded = v11n.decodeOrdinal(ordinal);
        book = decoded.book;
        chapter = decoded.chapter;
        verse = decoded.verse;
    }
}