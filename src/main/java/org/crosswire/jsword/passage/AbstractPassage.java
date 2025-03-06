package org.crosswire.jsword.passage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.StringUtil;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Writer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class AbstractPassage implements Passage
{
    public static final String REF_ALLOWED_DELIMS = ",;\n\r\t";
    public static final String REF_PREF_DELIM = ", ";
    public static final String REF_OSIS_DELIM = " ";

    protected static final int BITWISE = 0;
    protected static final int DISTINCT = 1;
    protected static final int RANGED = 2;
    protected static final int METHOD_COUNT = 3;
    
    private static final long serialVersionUID = -5931560451407396276L;
    private static final String THISMODULE = "org.crosswire.jsword.passage.AbstractPassage";
    private static final Logger lgr = LoggerFactory.getLogger(AbstractPassage.class);
    
    protected static VerseRange toVerseRange(Versification i_v11n, Object base) throws ClassCastException
    {
        assert base != null;
        if (base instanceof VerseRange)
        {
            return (VerseRange) base;
        }
        if (base instanceof Verse)
        {
            return new VerseRange(i_v11n, (Verse) base);
        }
        throw new ClassCastException(JSOtherMsg.lookupText("Can only use Verses and VerseRanges in this Collection", new Object[0]));
    }
    public transient Versification v11n;

    protected transient List<PassageListener> listeners;
    protected transient String originalName;
    protected transient int suppressEvents;
    protected transient int skipNormalization;

    private transient Key parent;

    protected AbstractPassage(Versification v11n)
    {
        this(v11n, null);
    }

    protected AbstractPassage(Versification i_v11n, String passageName)
    {
        v11n = i_v11n;
        originalName = passageName;
        listeners = new ArrayList<>();
    }

    @Override
    public boolean isWhole() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Passage getWhole()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(Key obj)
    {
        Passage thatref = (Passage) obj;
        if (thatref.countVerses() == 0)
        {
            if (countVerses() == 0)
            {
                return 0;
            }
            return -1;
        }
        if (countVerses() == 0)
        {
            return 1;
        }
        Verse thatfirst = thatref.getVerseAt(0);
        Verse thisfirst = getVerseAt(0);
        return getVersification().distance(thatfirst, thisfirst);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Passage))
        {
            return false;
        }
        Passage that = (Passage) obj;
        return that.getOsisRef().equals(getOsisRef());
    }

    @Override
    public String getName()
    {
        if (PassageUtil.isPersistentNaming() && originalName != null)
        {
            return originalName;
        }
        StringBuilder retcode = new StringBuilder();
        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
        Verse current = null;
        while (it.hasNext())
        {
            VerseRange range = it.next();
            retcode.append(range.getName(current));
            if (it.hasNext())
            {
                retcode.append(", ");
            }
            current = range.getStart();
        }
        return retcode.toString();
    }

    @Override
    public String getName(Key base)
    {
        return getName();
    }

    @Override
    public String getRootName()
    {
        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
        if (it.hasNext())
        {
            VerseRange range = it.next();
            return range.getRootName();
        }
        return getName();
    }

    @Override
    public String getOsisRef()
    {
        StringBuilder retcode = new StringBuilder();
        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
        boolean hasNext = it.hasNext();
        while (hasNext)
        {
            Key range = it.next();
            retcode.append(range.getOsisRef());
            hasNext = it.hasNext();
            if (hasNext)
            {
                retcode.append(" ");
            }
        }
        return retcode.toString();
    }

    @Override
    public void addAll(Key key)
    {
        if (key.isEmpty())
        {
            return;
        }
        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();
        if (key instanceof RangedPassage)
        {
            Iterator<VerseRange> it = ((RangedPassage) key).rangeIterator(RestrictionType.NONE);
            while (it.hasNext())
            {
                add(it.next());
            }
        }
        else
        {
            for (Key subkey : key)
            {
                add(subkey);
            }
        }
        lowerNormalizeProtection();
        if (lowerEventSuppressionAndTest())
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
        raiseEventSuppresion();
        raiseNormalizeProtection();
        if (key instanceof RangedPassage)
        {
            Iterator<VerseRange> it = ((RangedPassage) key).rangeIterator(RestrictionType.NONE);
            while (it.hasNext())
            {
                remove(it.next());
            }
        }
        else
        {
            Iterator<Key> it = key.iterator();
            while (it.hasNext())
            {
                remove(it.next());
            }
        }

        lowerNormalizeProtection();

        if (lowerEventSuppressionAndTest())
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

    @Override
    public void retainAll(Key key)
    {
        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();
        Passage temp = clone();
        for (Key verse : temp)
        {
            if (!key.contains(verse))
            {
                remove(verse);
            }
        }
        lowerNormalizeProtection();
        if (lowerEventSuppressionAndTest())
        {
            fireIntervalRemoved(this, (Verse) null, (Verse) null);
        }
    }

    @Override
    public void clear()
    {
        optimizeWrites();
        raiseNormalizeProtection();
        remove(getVersification().getAllVerses());
        if (lowerEventSuppressionAndTest())
        {
            fireIntervalRemoved(this, (Verse) null, (Verse) null);
        }
    }

    @Override
    public void blur(int verses, RestrictionType restrict)
    {
        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();
        Passage temp = clone();
        Iterator<VerseRange> it = temp.rangeIterator(RestrictionType.NONE);
        while (it.hasNext())
        {
            VerseRange range = restrict.blur(getVersification(), it.next(), verses, verses);
            add(range);
        }
        lowerNormalizeProtection();
        if (lowerEventSuppressionAndTest())
        {
            fireIntervalAdded(this, null, null);
        }
    }

    @Override
    public void writeDescription(Writer out) throws IOException
    {
        BufferedWriter bout = new BufferedWriter(out);
        bout.write(v11n.getName());
        bout.newLine();
        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
        while (it.hasNext())
        {
            Key range = it.next();
            bout.write(range.getName());
            bout.newLine();
        }
        bout.flush();
    }

    @Override
    public void readDescription(Reader in) throws IOException, NoSuchVerseException
    {
        raiseEventSuppresion();
        raiseNormalizeProtection();
        int count = 0;
        BufferedReader bin = new BufferedReader(in, 8192);
        String v11nName = bin.readLine();
        try
        {
            v11n = Versifications.instance().getVersification(v11nName);
        }
        catch (Exception e)
        {
            String msg = "Error creating instance of Versifications v11 - " + e.getMessage();
            lgr.error(msg, THISMODULE);
        }
        while (true)
        {
            String line = bin.readLine();
            if (line == null)
            {
                break;
            }
            count++;
            addVerses(line, (Key) null);
        }
        if (count == 0)
        {
            return;
        }
        lowerNormalizeProtection();
        if (lowerEventSuppressionAndTest())
        {
            fireIntervalAdded(this, getVerseAt(0), getVerseAt(countVerses() - 1));
        }
    }

    public void optimizeReads()
    {
    }

    public void addPassageListener(PassageListener li)
    {
        synchronized (listeners)
        {
            listeners.add(li);
        }
    }

    public void removePassageListener(PassageListener li)
    {
        synchronized (listeners)
        {
            listeners.remove(li);
        }
    }

    public void setParent(Key parent)
    {
        parent = parent;
    }
    public Key getParent()
    {
        return parent;
    }

    public void raiseNormalizeProtection()
    {
        skipNormalization++;
        if (skipNormalization > 10)
        {
            lgr.warn("skip_normalization={}", Integer.toString(skipNormalization));
        }
    }

    public void lowerNormalizeProtection()
    {
        skipNormalization--;
        if (skipNormalization == 0)
        {
            normalize();
        }
        assert skipNormalization >= 0;
    }

    public void raiseEventSuppresion()
    {
        suppressEvents++;
        if (suppressEvents > 10)
        {
            lgr.warn("suppress_events={}", Integer.toString(suppressEvents));
        }
    }

    public boolean lowerEventSuppressionAndTest()
    {
        suppressEvents--;
        assert suppressEvents >= 0;
        return (suppressEvents == 0);
    }

    @Override
    public boolean isEmpty()
    {
        return !iterator().hasNext();
    }

    @Override
    public boolean hasRanges(RestrictionType restrict)
    {
        int count = 0;
        Iterator<VerseRange> it = rangeIterator(restrict);
        while (it.hasNext())
        {
            it.next();
            count++;
            if (count == 2)
            {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Key key)
    {
        Passage ref = KeyUtil.getPassage(key);
        return containsAll(ref);
    }

    public int hashCode()
    {
        return getOsisRef().hashCode();
    }

    @Override
    public int countVerses()
    {
        int count = 0;
        for (Iterator<?> iter = iterator(); iter.hasNext(); iter.next())
        {
            count++;
        }
        return count;
    }

    @Override
    public int countRanges(RestrictionType restrict)
    {
        int count = 0;
        Iterator<VerseRange> it = rangeIterator(restrict);
        while (it.hasNext())
        {
            it.next();
            count++;
        }
        return count;
    }

    @Override
    public int booksInPassage()
    {
        BibleBook currentBook = null;
        int bookCount = 0;
        for (Key aKey : this)
        {
            Verse verse = (Verse) aKey;
            if (currentBook != verse.getBook())
            {
                currentBook = verse.getBook();
                bookCount++;
            }
        }
        return bookCount;
    }

    public String toString()
    {
        return getName();
    }

    @Override
    public String getOsisID()
    {
        StringBuilder retcode = new StringBuilder();
        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
        boolean hasNext = it.hasNext();
        while (hasNext)
        {
            Key range = it.next();
            retcode.append(range.getOsisID());
            hasNext = it.hasNext();
            if (hasNext)
            {
                retcode.append(" ");
            }
        }
        return retcode.toString();
    }

    @Override
    public String getOverview()
    {
        return JSMsg.gettext("{0,number,integer} {0,choice,0#verses|1#verse|1<verses} in {1,number,integer} {1,choice,0#books|1#book|1<books}", new Object[]
        {
            Integer.valueOf(countVerses()), Integer.valueOf(booksInPassage())
        });
    }

    @Override
    public Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException
    {
        Iterator<Key> it = iterator();
        Object retcode = null;
        for (int i = 0; i <= offset; i++)
        {
            if (!it.hasNext())
            {
                throw new ArrayIndexOutOfBoundsException(JSOtherMsg.lookupText("Index out of range (Given {0,number,integer}, Max {1,number,integer}).", new Object[]
                {
                    Integer.valueOf(offset), Integer.valueOf(countVerses())
                }));
            }
            retcode = it.next();
        }
        return (Verse) retcode;
    }

    @Override
    public VerseRange getRangeAt(int offset, RestrictionType restrict) throws ArrayIndexOutOfBoundsException
    {
        Iterator<VerseRange> it = rangeIterator(restrict);
        Object retcode = null;
        for (int i = 0; i <= offset; i++)
        {
            if (!it.hasNext())
            {
                throw new ArrayIndexOutOfBoundsException(JSOtherMsg.lookupText("Index out of range (Given {0,number,integer}, Max {1,number,integer}).", new Object[]
                {
                    Integer.valueOf(offset), Integer.valueOf(countVerses())
                }));
            }
            retcode = it.next();
        }
        return (VerseRange) retcode;
    }

    @Override
    public Iterator<VerseRange> rangeIterator(RestrictionType restrict)
    {
        return new VerseRangeIterator(getVersification(), iterator(), restrict);
    }

    @Override
    public boolean containsAll(Passage that)
    {
        if (that instanceof RangedPassage)
        {
            Iterator<VerseRange> iter = null;
            iter = ((RangedPassage) that).rangeIterator(RestrictionType.NONE);
            while (iter.hasNext())
            {
                if (!contains(iter.next()))
                {
                    return false;
                }
            }
        }
        else
        {
            Iterator<Key> iter = that.iterator();
            while (iter.hasNext())
            {
                if (!contains(iter.next()))
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Passage trimVerses(int count)
    {
        optimizeWrites();
        raiseNormalizeProtection();
        int i = 0;
        boolean overflow = false;
        Passage remainder = clone();
        for (Key verse : this)
        {
            i++;
            if (i > count)
            {
                remove(verse);
                overflow = true;
                continue;
            }
            remainder.remove(verse);
        }
        lowerNormalizeProtection();
        if (overflow)
        {
            return remainder;
        }
        return null;
    }

    @Override
    public Passage trimRanges(int count, RestrictionType restrict)
    {
        optimizeWrites();
        raiseNormalizeProtection();
        int i = 0;
        boolean overflow = false;
        Passage remainder = clone();
        Iterator<VerseRange> it = rangeIterator(restrict);
        while (it.hasNext())
        {
            i++;
            Key range = it.next();
            if (i > count)
            {
                remove(range);
                overflow = true;
                continue;
            }
            remainder.remove(range);
        }
        lowerNormalizeProtection();
        if (overflow)
        {
            return remainder;
        }
        return null;
    }

    public int getCardinality()
    {
        return countVerses();
    }

    public int indexOf(Key that)
    {
        int index = 0;
        for (Key key : this)
        {
            if (key.equals(that))
            {
                return index;
            }
            index++;
        }
        return -1;
    }

    public boolean canHaveChildren()
    {
        return false;
    }

    public int getChildCount()
    {
        return 0;
    }

    public Key get(int index)
    {
        return getVerseAt(index);
    }


    @Override
    public Versification getVersification()
    {
        return v11n;
    }

    @Override
    public Passage reversify(Versification newVersification) throws UnsupportedOperationException
    {
        if (v11n.equals(newVersification))
        {
            return this;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractPassage clone()
    {
        AbstractPassage copy = null;
        try
        {
            copy = (AbstractPassage) super.clone();
            copy.listeners = new ArrayList<PassageListener>();
            copy.listeners.addAll(listeners);
            copy.originalName = originalName;
        }
        catch (CloneNotSupportedException e)
        {
            assert false : e;
        }
        return copy;
    }
    protected void optimizeWrites()
    {
    }

    protected void fireIntervalAdded(Object source, Verse start, Verse end)
    {
        List<PassageListener> temp;
        if (suppressEvents != 0)
        {
            return;
        }
        PassageEvent ev = new PassageEvent(source, PassageEvent.EventType.ADDED, start, end);
        synchronized (listeners)
        {
            temp = new ArrayList<PassageListener>();
            temp.addAll(listeners);
        }
        for (int i = 0; i < temp.size(); i++)
        {
            PassageListener rl = temp.get(i);
            rl.versesAdded(ev);
        }
    }

    protected void fireIntervalRemoved(Object source, Verse start, Verse end)
    {
        List<PassageListener> temp;
        if (suppressEvents != 0)
        {
            return;
        }
        PassageEvent ev = new PassageEvent(source, PassageEvent.EventType.REMOVED, start, end);
        synchronized (listeners)
        {
            temp = new ArrayList<PassageListener>();
            temp.addAll(listeners);
        }
        for (int i = 0; i < temp.size(); i++)
        {
            PassageListener rl = temp.get(i);
            rl.versesRemoved(ev);
        }
    }

    protected void fireContentsChanged(Object source, Verse start, Verse end)
    {
        List<PassageListener> temp;
        if (suppressEvents != 0)
        {
            return;
        }
        PassageEvent ev = new PassageEvent(source, PassageEvent.EventType.CHANGED, start, end);
        synchronized (listeners)
        {
            temp = new ArrayList<PassageListener>();
            temp.addAll(listeners);
        }
        for (int i = 0; i < temp.size(); i++)
        {
            PassageListener rl = temp.get(i);
            rl.versesChanged(ev);
        }
    }

    protected void addVerses(String refs, Key basis) throws NoSuchVerseException
    {
        optimizeWrites();
        String[] parts = StringUtil.split(refs, ",;\n\r\t");
        if (parts.length == 0)
        {
            return;
        }
        int start = 0;
        VerseRange vrBasis = null;
        if (basis instanceof Verse)
        {
            vrBasis = new VerseRange(v11n, (Verse) basis);
        }
        else if (basis instanceof VerseRange)
        {
            vrBasis = (VerseRange) basis;
        }
        else
        {
            vrBasis = VerseRangeFactory.fromString(v11n, parts[0].trim());
            add(vrBasis);
            start = 1;
        }
        for (int i = start; i < parts.length; i++)
        {
            VerseRange next = VerseRangeFactory.fromString(v11n, parts[i].trim(), vrBasis);
            add(next);
            vrBasis = next;
        }
    }

    protected void writeObjectSupport(ObjectOutputStream out) throws IOException
    {
        out.writeUTF(v11n.getName());
        int bitwiseSize = v11n.maximumOrdinal();
        int rangedSize = 8 * countRanges(RestrictionType.NONE);
        int distinctSize = 4 * countVerses();
        if (bitwiseSize <= rangedSize && bitwiseSize <= distinctSize)
        {
            out.writeInt(0);
            BitSet store = new BitSet(bitwiseSize);
            Iterator<Key> iter = iterator();
            while (iter.hasNext())
            {
                Verse verse = (Verse) iter.next();
                store.set(verse.getOrdinal());
            }
            out.writeObject(store);
        }
        else if (distinctSize <= rangedSize)
        {
            out.writeInt(1);
            out.writeInt(countVerses());
            for (Key aKey : this)
            {
                Verse verse = (Verse) aKey;
                out.writeInt(verse.getOrdinal());
            }
        }
        else
        {
            out.writeInt(2);
            out.writeInt(countRanges(RestrictionType.NONE));
            Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
            while (it.hasNext())
            {
                VerseRange range = it.next();
                out.writeInt(range.getStart().getOrdinal());
                out.writeInt(range.getCardinality());
            }
        }
    }

    protected void readObjectSupport(ObjectInputStream is) throws IOException, ClassNotFoundException
    {
        BitSet store;
        int i, verses, j, ranges, k;
        raiseEventSuppresion();
        raiseNormalizeProtection();
        String v11nName = is.readUTF();
        try
        {
            v11n = Versifications.instance().getVersification(v11nName);
        }
        catch ( Exception e )
        {
            String msg = "";
            lgr.error(msg,THISMODULE);
        }
        int type = is.readInt();
        switch (type)
        {
        case 0:
            store = (BitSet) is.readObject();
            for (i = 0; i < v11n.maximumOrdinal(); i++)
            {
                if (store.get(i))
                {
                    add(v11n.decodeOrdinal(i));
                }
            }
            break;
        case 1:
            verses = is.readInt();
            for (j = 0; j < verses; j++)
            {
                int ord = is.readInt();
                add(v11n.decodeOrdinal(ord));
            }
            break;
        case 2:
            ranges = is.readInt();
            for (k = 0; k < ranges; k++)
            {
                int ord = is.readInt();
                int count = is.readInt();
                add(RestrictionType.NONE.toRange(getVersification(), v11n.decodeOrdinal(ord), count));
            }
            break;
        default:
            throw new ClassCastException(JSOtherMsg.lookupText("Can only use Verses and VerseRanges in this Collection", new Object[0]));
        }
        lowerEventSuppressionAndTest();
        lowerNormalizeProtection();
    }

    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException
    {
        listeners = new ArrayList<PassageListener>();
        originalName = null;
        parent = null;
        skipNormalization = 0;
        suppressEvents = 0;
        is.defaultReadObject();
    }

    private void normalize()
    {
    }

    protected static final class VerseRangeIterator implements Iterator<VerseRange>
    {
        private Versification v11n;

        private Iterator<Key> it;

        private VerseRange nextRange;

        private Verse nextVerse;

        private RestrictionType restrict;

        protected VerseRangeIterator(Versification v11n, Iterator<Key> it, RestrictionType restrict)
        {
            v11n = v11n;
            it = it;
            restrict = restrict;
            if (it.hasNext())
            {
                nextVerse = (Verse) it.next();
            }
            calculateNext();
        }

        public boolean hasNext()
        {
            return (nextRange != null);
        }

        public VerseRange next() throws NoSuchElementException
        {
            VerseRange retcode = nextRange;
            if (retcode == null)
            {
                throw new NoSuchElementException();
            }
            calculateNext();
            return retcode;
        }

        public void remove() throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }

        private void calculateNext()
        {
            if (nextVerse == null)
            {
                nextRange = null;
                return;
            }
            Verse start = nextVerse;
            Verse end = nextVerse;
            while (true)
            {
                if (!it.hasNext())
                {
                    nextVerse = null;
                    break;
                }
                nextVerse = (Verse) it.next();
                if (!v11n.isAdjacentVerse(end, nextVerse))
                {
                    break;
                }
                if (!restrict.isSameScope(v11n, end, nextVerse))
                {
                    break;
                }
                end = nextVerse;
            }
            nextRange = new VerseRange(v11n, start, end);
        }
    }
}
