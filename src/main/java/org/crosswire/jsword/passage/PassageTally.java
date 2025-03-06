package org.crosswire.jsword.passage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.versification.Versification;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public class PassageTally extends AbstractPassage {

    public static final int MAX_TALLY = 20000;

    private int size;

    private int total;

    protected int[] board;

    private int max;

    private Order order;

    public PassageTally(Versification v11n) {
        super(v11n);
        this.order = Order.BIBLICAL;
        this.board = new int[v11n.maximumOrdinal() + 1];
    }

    protected PassageTally(Versification v11n, String refs, Key basis) throws NoSuchVerseException {
        super(v11n, refs);
        this.order = Order.BIBLICAL;
        this.board = new int[v11n.maximumOrdinal() + 1];
        addVerses(refs, basis);
    }

    protected PassageTally(Versification v11n, String refs) throws NoSuchVerseException {
        this(v11n, refs, (Key) null);
    }

    public boolean isEmpty() {
        return (this.size == 0);
    }

    public int countVerses() {
        return this.size;
    }

    public void setOrdering(Order order) {
        this.order = order;
    }

    public Order getOrdering() {
        return this.order;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public PassageTally clone() {
        PassageTally copy = (PassageTally) super.clone();
        copy.board = (int[]) this.board.clone();
        return copy;
    }

    public String toString() {
        return getName(0);
    }

    public String getName() {
        return getName(0);
    }

    public String getName(int cnt) {
        int maxCount = cnt;
        if (PassageUtil.isPersistentNaming() && this.originalName != null) {
            return this.originalName;
        }
        StringBuilder retcode = new StringBuilder();
        if (this.order == Order.BIBLICAL) {
            Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
            Verse current = null;
            while (it.hasNext()) {
                VerseRange range = it.next();
                retcode.append(range.getName(current));
                if (it.hasNext()) {
                    retcode.append(", ");
                }
                current = range.getStart();
            }
        } else {
            if (maxCount == 0) {
                maxCount = Integer.MAX_VALUE;
            }
            Iterator<Key> it = new OrderedVerseIterator(getVersification(), this.board);
            Key current = null;
            int count = 0;
            while (it.hasNext() && count < maxCount) {
                Key verse = it.next();
                retcode.append(verse.getName(current));
                current = verse;
                count++;
                if (it.hasNext() && count < maxCount) {
                    retcode.append(", ");
                }
            }
        }
        return retcode.toString();
    }

    public String getNameAndTally() {
        return getNameAndTally(0);
    }

    public String getNameAndTally(int cnt) {
        int maxCount = cnt;
        StringBuilder retcode = new StringBuilder();
        if (maxCount == 0) {
            maxCount = Integer.MAX_VALUE;
        }
        OrderedVerseIterator it = new OrderedVerseIterator(getVersification(), this.board);
        int count = 0;
        while (it.hasNext() && count < maxCount) {
            Key verse = it.next();
            retcode.append(verse.getName());
            retcode.append(" (");
            retcode.append(100 * it.lastRank() / this.max);
            retcode.append("%)");
            count++;
            if (it.hasNext() && count < maxCount) {
                retcode.append(", ");
            }
        }
        return retcode.toString();
    }

    public Iterator<Key> iterator() {
        if (this.order == Order.BIBLICAL) {
            return new VerseIterator();
        }
        return new OrderedVerseIterator(getVersification(), this.board);
    }

    public Iterator<VerseRange> rangeIterator(RestrictionType restrict) {
        if (this.order == Order.BIBLICAL) {
            return new AbstractPassage.VerseRangeIterator(getVersification(), iterator(), restrict);
        }
        return new OrderedVerseRangeIterator(getVersification(), iterator(), this.board);
    }

    public boolean contains(Key that) {
        for (Key aKey : that) {
            Verse verse = (Verse) aKey;
            if (this.board[verse.getOrdinal()] == 0) {
                return false;
            }
        }
        return true;
    }

    public int getTallyOf(Verse verse) {
        return this.board[verse.getOrdinal()];
    }

    public int getIndexOf(Verse verse) {
        int pos = verse.getOrdinal();
        int tally = this.board[pos];
        return (tally > 0) ? pos : -1;
    }

    public void add(Key that) {
        optimizeWrites();
        alterVerseBase(that, 1);
        fireIntervalAdded(this, null, null);
    }

    public void add(Key that, int count) {
        optimizeWrites();
        alterVerseBase(that, count);
        fireIntervalAdded(this, null, null);
    }

    public void unAdd(Key that) {
        optimizeWrites();
        alterVerseBase(that, -1);
        fireIntervalRemoved(this, null, null);
    }

    public void remove(Key that) {
        optimizeWrites();
        for (Key aKey : that) {
            Verse verse = (Verse) aKey;
            kill(verse.getOrdinal());
        }
        fireIntervalRemoved(this, null, null);
    }

    public void addAll(Key that) {
        optimizeWrites();
        if (that instanceof PassageTally) {
            PassageTally tally = (PassageTally) that;
            int vib = getVersification().maximumOrdinal();
            for (int i = 0; i <= vib; i++) {
                increment(i, tally.board[i]);
            }
            incrementMax(tally.max);
        } else {
            for (Key aKey : that) {
                Verse verse = (Verse) aKey;
                increment(verse.getOrdinal(), 1);
            }
            incrementMax(1);
        }
        fireIntervalAdded(this, null, null);
    }

    public void unAddAll(Passage that) {
        optimizeWrites();
        if (that instanceof PassageTally) {
            PassageTally tally = (PassageTally) that;
            int vib = getVersification().maximumOrdinal();
            for (int i = 0; i <= vib; i++) {
                increment(i, -tally.board[i]);
            }
        } else {
            for (Key aKey : that) {
                Verse verse = (Verse) aKey;
                increment(verse.getOrdinal(), -1);
            }
        }
        fireIntervalRemoved(this, null, null);
    }

    public void removeAll(Key key) {
        optimizeWrites();
        if (key instanceof PassageTally) {
            PassageTally tally = (PassageTally) key;
            int vib = getVersification().maximumOrdinal();
            for (int i = 0; i <= vib; i++) {
                if (tally.board[i] != 0) {
                    kill(i);
                }
            }
        } else {
            for (Key aKey : key) {
                Verse verse = (Verse) aKey;
                kill(verse.getOrdinal());
            }
        }
        fireIntervalRemoved(this, null, null);
    }

    public void clear() {
        optimizeWrites();
        for (int i = 0; i < this.board.length; i++) {
            this.board[i] = 0;
        }
        this.size = 0;
        fireIntervalRemoved(this, null, null);
    }

    public Passage trimVerses(int count) {
        optimizeWrites();
        int i = 0;
        boolean overflow = false;
        Passage remainder = clone();
        for (Key verse : this) {
            i++;
            if (i > count) {
                remove(verse);
                overflow = true;
                continue;
            }
            remainder.remove(verse);
        }
        if (overflow) {
            return remainder;
        }
        return null;
    }

    public void flatten() {
        optimizeWrites();
        for (int i = 0; i < this.board.length; i++) {
            if (this.board[i] != 0) {
                this.board[i] = 1;
            }
        }
        this.max = 1;
    }

    public void blur(int verses, RestrictionType restrict) {
        assert verses >= 0;
        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();
        if (!restrict.equals(RestrictionType.NONE)) {
            log.warn("Restrict={} is not properly supported.", restrict);
            PassageTally temp = clone();
            Iterator<VerseRange> it = temp.rangeIterator(RestrictionType.NONE);
            while (it.hasNext()) {
                VerseRange range = it.next();
                for (int i = 0; i <= verses; i++) {
                    add(restrict.blur(getVersification(), range, i, i));
                }
            }
        } else {
            int[] newBoard = new int[this.board.length];
            for (int i = 0; i < this.board.length; i++) {
                if (this.board[i] != 0) {
                    int j;
                    for (j = -verses; j < 0; j++) {
                        int k = i + j;
                        if (k >= 0) {
                            newBoard[k] = newBoard[k] + this.board[i] + verses + j;
                        }
                    }
                    newBoard[i] = newBoard[i] + this.board[i] + verses;
                    for (j = 1; j <= verses; j++) {
                        int k = i + j;
                        if (k < this.board.length - 1) {
                            newBoard[k] = newBoard[k] + this.board[i] + verses - j;
                        }
                    }
                }
            }
            this.board = newBoard;
        }
        resetMax();
        lowerNormalizeProtection();
        if (lowerEventSuppressionAndTest()) {
            fireIntervalAdded(this, null, null);
        }
    }

    private void resetMax() {
        optimizeWrites();
        this.max = 0;
        this.size = 0;
        for (int i = 0; i < this.board.length; i++) {
            if (this.board[i] > 0) {
                this.size++;
            }
            if (this.board[i] > this.max) {
                this.max = this.board[i];
            }
        }
    }

    private void alterVerseBase(Key that, int tally) {
        for (Key aKey : that) {
            Verse verse = (Verse) aKey;
            increment(verse.getOrdinal(), tally);
        }
        if (tally > 0) {
            incrementMax(tally);
        }
    }

    private void increment(int ord, int tally) {
        boolean exists = (this.board[ord] > 0);
        this.board[ord] = this.board[ord] + tally;
        if (this.board[ord] > 20000) {
            this.board[ord] = 20000;
        }
        if (this.board[ord] < 0) {
            this.board[ord] = 0;
        }
        if (exists && this.board[ord] == 0) {
            this.size--;
        } else if (!exists && this.board[ord] > 0) {
            this.size++;
        }
    }

    private void incrementMax(int tally) {
        this.max += tally;
        if (this.max > 20000) {
            this.max = 20000;
        }
        if (this.max < 0) {
            this.max = 0;
        }
    }

    private void kill(int ord) {
        if (this.board[ord] > 0) {
            this.size--;
        }
        this.board[ord] = 0;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        writeObjectSupport(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        optimizeWrites();
        in.defaultReadObject();
        readObjectSupport(in);
    }

    public enum Order {
        BIBLICAL, TALLY;
    }

    private static final Logger log = LoggerFactory.getLogger(PassageTally.class);

    private static final long serialVersionUID = 3761128240928274229L;

    private final class VerseIterator implements Iterator<Key> {

        private int next;

        protected VerseIterator() {
            calculateNext();
        }

        public boolean hasNext() {
            return (this.next <= PassageTally.this.board.length - 1);
        }

        public Key next() throws NoSuchElementException {
            if (this.next >= PassageTally.this.board.length) {
                throw new NoSuchElementException();
            }
            Key retcode = PassageTally.this.getVersification().decodeOrdinal(this.next);
            calculateNext();
            return retcode;
        }

        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        private void calculateNext() {
            do {
                this.next++;
            } while (this.next < PassageTally.this.board.length && PassageTally.this.board[this.next] == 0);
        }
    }

    private static final class OrderedVerseIterator implements Iterator<Key> {

        private Versification referenceSystem;

        private PassageTally.TalliedVerse last;

        private Iterator<PassageTally.TalliedVerse> it;

        protected OrderedVerseIterator(Versification v11n, int[] board) {
            this.referenceSystem = v11n;
            TreeSet<PassageTally.TalliedVerse> output = new TreeSet<PassageTally.TalliedVerse>();
            int vib = board.length - 1;
            for (int i = 0; i <= vib; i++) {
                if (board[i] != 0) {
                    output.add(new PassageTally.TalliedVerse(i, board[i]));
                }
            }
            this.it = output.iterator();
            this.last = null;
        }

        public boolean hasNext() {
            return this.it.hasNext();
        }

        public Key next() throws NoSuchElementException {
            this.last = this.it.next();
            return this.referenceSystem.decodeOrdinal(this.last.ord);
        }

        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        public int lastRank() throws NoSuchElementException {
            if (this.last != null) {
                return this.last.tally;
            }
            throw new NoSuchElementException(JSOtherMsg.lookupText("nextElement() has not been called yet.", new Object[0]));
        }
    }

    private static class TalliedVerse implements Comparable<TalliedVerse> {

        protected int ord;

        protected int tally;

        protected TalliedVerse(int ord, int tally) {
            this.ord = ord;
            this.tally = tally;
        }

        public int hashCode() {
            int result = 31 + this.ord;
            return 31 * result + this.tally;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            TalliedVerse other = (TalliedVerse) obj;
            if (this.tally != other.tally) {
                return false;
            }
            if (this.ord != other.ord) {
                return false;
            }
            return true;
        }

        public int compareTo(TalliedVerse that) {
            if (that.tally == this.tally) {
                return this.ord - that.ord;
            }
            return that.tally - this.tally;
        }
    }

    private static final class OrderedVerseRangeIterator implements Iterator<VerseRange> {

        private PassageTally.TalliedVerseRange last;

        private Iterator<PassageTally.TalliedVerseRange> it;

        protected OrderedVerseRangeIterator(Versification v11n, Iterator<Key> vit, int[] board) {
            Set<PassageTally.TalliedVerseRange> output = new TreeSet<PassageTally.TalliedVerseRange>();
            Iterator<VerseRange> rit = new AbstractPassage.VerseRangeIterator(v11n, vit, RestrictionType.NONE);
            while (rit.hasNext()) {
                VerseRange range = rit.next();
                int rank = 0;
                Iterator<Key> iter = range.iterator();
                while (iter.hasNext()) {
                    Verse verse = (Verse) iter.next();
                    int temp = board[verse.getOrdinal()];
                    if (temp > rank) {
                        rank = temp;
                    }
                }
                output.add(new PassageTally.TalliedVerseRange(range, rank));
            }
            this.it = output.iterator();
            this.last = null;
        }

        public boolean hasNext() {
            return this.it.hasNext();
        }

        public VerseRange next() throws NoSuchElementException {
            this.last = this.it.next();
            return this.last.range;
        }

        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }

    private static class TalliedVerseRange implements Comparable<TalliedVerseRange> {

        protected VerseRange range;

        protected int tally;

        protected TalliedVerseRange(VerseRange range, int tally) {
            this.range = range;
            this.tally = tally;
        }

        public int hashCode() {
            int result = 31 + this.tally;
            return 31 * result + ((this.range == null) ? 0 : this.range.hashCode());
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            TalliedVerseRange other = (TalliedVerseRange) obj;
            if (this.tally != other.tally) {
                return false;
            }
            if (this.range == null) {
                if (other.range != null) {
                    return false;
                }
            } else if (!this.range.equals(other.range)) {
                return false;
            }
            return true;
        }

        public int compareTo(TalliedVerseRange that) {
            if (that.tally == this.tally) {
                return this.range.compareTo(that.range);
            }
            return that.tally - this.tally;
        }
    }
}