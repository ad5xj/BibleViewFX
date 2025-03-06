package org.crosswire.jsword.passage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @ingroup org.crosswire.jsword.passage
 * 
 * @brief Definition of Bitwise Passage
 * 
 * @extends AbstractPassage
 */
public class BitwisePassage extends AbstractPassage 
{
    protected static transient BitSet store;

    private static final String THISMODULE = "org.crosswire.jsword.passage.BitWisePassage";
    private static final long serialVersionUID = -5931560451407396276L;
    private static final Logger lgr = LoggerFactory.getLogger(BitwisePassage.class);

    private transient Versification v11n;
    
    public BitwisePassage(Versification v11n) 
    {
        super(v11n);
        store = new BitSet(v11n.maximumOrdinal() + 1);
    }

    protected BitwisePassage(Versification v11n, String refs, Key basis) throws NoSuchVerseException 
    {
        super(v11n, refs);
        store = new BitSet(v11n.maximumOrdinal() + 1);
        addVerses(refs, basis);
    }

    protected BitwisePassage(Versification v11n, String refs) throws NoSuchVerseException 
    {
        this(v11n, refs, (Key) null);
    }

    public BitwisePassage clone() 
    {
        BitwisePassage copy = (BitwisePassage) super.clone();
        copy.store = (BitSet) store.clone();
        return copy;
    }

    public int countVerses()    { return store.cardinality(); }

    public boolean isEmpty()    { return store.isEmpty(); }

    public Versification getVersification() { return v11n; }

    public Iterator<Key> iterator() { return new VerseIterator(); }

    public boolean contains(Key obj) 
    {
        for (Key aKey : obj) 
        {
            Verse verse = (Verse) aKey;
            if (!store.get(verse.getOrdinal())) { return false; }
        }
        return true;
    }

    public void add(Key obj) 
    {
        optimizeWrites();
        Verse firstVerse = null;
        Verse lastVerse = null;
        for (Key aKey : obj) 
        {
            lastVerse = (Verse) aKey;
            if (firstVerse == null) 
            {
                firstVerse = lastVerse;
            }
            store.set(lastVerse.getOrdinal());
        }
        if (suppressEvents == 0) 
        {
            fireIntervalAdded(this, firstVerse, lastVerse);
        }
    }

    public void addVersifiedOrdinal(int ordinal) 
    {
        optimizeWrites();
        store.set(ordinal);
        if (suppressEvents == 0) 
        {
            Verse verse = getVersification().decodeOrdinal(ordinal);
            fireIntervalAdded(this, verse, verse);
        }
    }

    public void remove(Key obj) 
    {
        optimizeWrites();
        Verse firstVerse = null;
        Verse lastVerse = null;
        for (Key aKey : obj) 
        {
            lastVerse = (Verse) aKey;
            if (firstVerse == null)     { firstVerse = lastVerse; }
            store.clear(lastVerse.getOrdinal());
        }
        if (suppressEvents == 0)   { fireIntervalAdded(this, firstVerse, lastVerse); }
    }

    public void addAll(Key key) 
    {
        if (key.isEmpty()) { return; }

        optimizeWrites();

        if (key instanceof BitwisePassage) 
        {
            BitwisePassage thatRef = (BitwisePassage) key;
            store.or(thatRef.store);
        } 
        else 
        {
            super.addAll(key);
        }
        
        if (suppressEvents == 0 && !key.isEmpty()) 
        {
            if (key instanceof Passage) 
            {
                Passage that = (Passage) key;
                fireIntervalAdded(this, that.getVerseAt(0), that.getVerseAt(that.countVerses() - 1));
            } 
            else if (key instanceof VerseRange) 
            {
                VerseRange that = (VerseRange) key;
                fireIntervalAdded(this, that.getStart(), that.getEnd());
            } 
            else if (key instanceof Verse) 
            {
                Verse that = (Verse) key;
                fireIntervalAdded(this, that, that);
            }
        }
    }

    public void removeAll(Key key) 
    {
        optimizeWrites();
        if ( key instanceof BitwisePassage ) 
        {
            BitwisePassage thatRef = (BitwisePassage) key;
            store.andNot(thatRef.store);
        } 
        else 
        {
            super.removeAll(key);
        }
        if ( (suppressEvents == 0) && !key.isEmpty() ) 
        {
            if (key instanceof Passage) 
            {
                Passage that = (Passage) key;
                fireIntervalRemoved(this, that.getVerseAt(0), that.getVerseAt(that.countVerses() - 1));
            } 
            else if (key instanceof VerseRange) 
            {
                VerseRange that = (VerseRange) key;
                fireIntervalRemoved(this, that.getStart(), that.getEnd());
            } 
            else if (key instanceof Verse) 
            {
                Verse that = (Verse) key;
                fireIntervalRemoved(this, that, that);
            }
        }
    }

    public void retainAll(Key key) 
    {
        optimizeWrites();
        BitSet thatStore = null;
        if (key instanceof BitwisePassage) 
        {
            thatStore = ((BitwisePassage) key).store;
        } 
        else 
        {
            Versification v11n = getVersification();
            thatStore = new BitSet(v11n.maximumOrdinal() + 1);
            for (Key aKey : key) 
            {
                int ord = ((Verse) aKey).getOrdinal();
                if (store.get(ord)) { thatStore.set(ord); }
            }
        }
        store.and(thatStore);
        fireIntervalRemoved(this, null, null);
    }

    public void clear() 
    {
        optimizeWrites();
        store.clear();
        fireIntervalRemoved(this, null, null);
    }

    public void blur(int verses, RestrictionType restrict) 
    {
        int i = -1;
        int j = -1;
        int maximumOrdinal = -1;
        BitSet newStore = null;

        assert verses >= 0;

        optimizeWrites();
        raiseNormalizeProtection();
        if (!restrict.equals(RestrictionType.NONE)) 
        {
            super.blur(verses, restrict);
        } 
        else 
        {
            optimizeWrites();
            raiseEventSuppresion();
            raiseNormalizeProtection();
            maximumOrdinal = getVersification().maximumOrdinal();
            newStore = new BitSet(maximumOrdinal + 1);
            for ( i = store.nextSetBit(0); i >= 0; i = store.nextSetBit(i + 1) ) 
            {
                int start = Math.max(1, i - verses);
                int end = Math.min(maximumOrdinal, i + verses);
                for ( j = start; j <= end; j++ )
                {
                    newStore.set(j);
                }
            }
            store = newStore;
            lowerNormalizeProtection();
            if (lowerEventSuppressionAndTest()) { fireIntervalAdded(this, null, null); }
        }
    }


    private void writeObject(ObjectOutputStream out) throws IOException 
    {
        out.defaultWriteObject();
        out.writeUTF(getVersification().getName());
        writeObjectSupport(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException 
    {
        optimizeWrites();
        in.defaultReadObject();
        String v11nName = in.readUTF();
        try
        {
            Versification v11n = Versifications.instance().getVersification(v11nName);
        }
        catch ( Exception e )
        {
            String msg = "Error getting Versification of " + v11nName
                       + "\n    error=" + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        store = new BitSet(v11n.maximumOrdinal() + 1);
        readObjectSupport(in);
    }

    // ======================================================= //
    // =========== HELPER CLASS VerseIterator ================ //
    // ======================================================= //
    private final class VerseIterator implements Iterator<Key> 
    {
        
        private int next;
        
        VerseIterator() 
        {
            next = -1;
            calculateNext();
        }

        public boolean hasNext() {  return (next >= 0); }

        public Key next() throws NoSuchElementException 
        {
            if (!hasNext()) { throw new NoSuchElementException(); }
            Key retcode = BitwisePassage.this.getVersification().decodeOrdinal(next);
            calculateNext();
            return retcode;
        }

        public void remove() throws UnsupportedOperationException 
        {
            BitwisePassage.store.clear(next); 
        }

        private void calculateNext() { next = BitwisePassage.store.nextSetBit(next + 1); }
    }  // VerseIterator()
    // ======================================================= //
    // ======= END HELPER CLASS VerseIterator ================ //
    // ======================================================= //
}