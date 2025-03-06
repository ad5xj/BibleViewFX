package org.crosswire.jsword.versification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;

import org.crosswire.jsword.book.ReferenceSystem;

import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

import org.crosswire.jsword.versification.system.SystemDefault;

import java.io.PrintStream;
import java.io.Serializable;

import java.util.Iterator;

import java.util.Arrays;

/**
 * @ingroup org.crosswire.jsword
 * @brief Base class for Versification
 * 
 * @implements ReferenceSystem
 * @implements Serializable
 */
public class Versification implements ReferenceSystem, Serializable 
{
    private static final long serialVersionUID = -6226916242596368765L;
    private static final String THISMODULE = "org.crosswire.jsword.versification.Versification";
    private static final Logger lgr = LoggerFactory.getLogger(Versification.class);
    
    public static void dump(PrintStream out, String name, BibleBookList bookList, int[][] array)
    {
        String vstr1 = "";
        String vstr2 = "";
        int count = 0;
        
        out.println("    private final int[][] " + name + " =");
        out.println("    {");
        int bookCount = array.length;
        
        for (int bookIndex = 0; bookIndex < bookCount; bookIndex++)
        {
            count = 0;
            out.print("        // ");
            if (bookIndex < bookList.getBookCount())
            {
                BibleBook book = bookList.getBook(bookIndex);
                out.println(book.getOSIS());
            }
            else
            {
                out.println("Sentinel");
            }
            out.print("        { ");
            int numChapters = (array[bookIndex]).length;
            for (int chapterIndex = 0; chapterIndex < numChapters; chapterIndex++)
            {
                if (count++ % 10 == 0) {
                    out.println();
                    out.print("            ");
                }
                vstr1 = "     " + array[bookIndex][chapterIndex];
                vstr2 = vstr1.substring(vstr1.length() - 5);
                out.print(vstr2 + ", ");
            }
            out.println();
            out.println("        },");
        }
        out.println("    };");
    }

    public static void optimize(PrintStream out, BibleBookList bookList, int[][] lastVerse)
    {
        String vstr1 = "";
        String vstr2 = "";
        int count = 0;
        int ordinal = 0;
        
        out.println("    private final int[][] chapterStarts =");
        out.println("    {");
        int bookIndex = 0;
        int ntStartOrdinal = 0;
        
        for (BibleBook book = bookList.getBook(0); book != null; book = bookList.getNextBook(book))
        {
            count = 0;
            out.print("        // ");
            out.println(book.getOSIS());
            out.print("        { ");
            if (book == BibleBook.INTRO_NT) { ntStartOrdinal = ordinal; }
            int numChapters = (lastVerse[bookIndex]).length;
            
            for (int chapterIndex = 0; chapterIndex < numChapters; chapterIndex++)
            {
                if (count++ % 10 == 0)
                {
                    out.println();
                    out.print("            ");
                }
                vstr1 = "     " + ordinal;
                vstr2 = vstr1.substring(vstr1.length() - 5);
                out.print(vstr2 + ", ");
                int versesInChapter = lastVerse[bookIndex][chapterIndex] + 1;
                ordinal += versesInChapter;
            }
            out.println();
            out.println("        },");
            bookIndex++;
        }
        vstr1 = "     " + ordinal;
        vstr2 = vstr1.substring(vstr1.length() - 5);
        out.println("        // Sentinel");
        out.println("        { ");
        out.println("            " + vstr2 + ", ");
        out.println("        },");
        out.println("    };");
        out.println();
        out.println("    /** The last ordinal number of the Old Testament */");
        out.println("    private int otMaxOrdinal = " + (ntStartOrdinal - 1) + ";");
        out.println("    /** The last ordinal number of the New Testament and the maximum ordinal number of this Reference System */");
        out.println("    private int ntMaxOrdinal = " + (ordinal - 1) + ";");
    }

    private int otMaxOrdinal;
    private int ntMaxOrdinal;

    private int[][] lastVerse;
    private int[][] chapterStarts;

    private String name;

    private BibleBookList bookList;

    /**
     * @brief Overload constructor with params for name, book(s) and lastVerse(s)
     *
     *
     * @param name
     * @param booksOT
     * @param booksNT
     * @param lastVerseOT
     * @param lastVerseNT
     */
    public Versification(String name, 
                         BibleBook[] booksOT, 
                         BibleBook[] booksNT, 
                         int[][] lastVerseOT, 
                         int[][] lastVerseNT)
    {
        int bookCount = 1;
        int ntStart = 0;
        int ordinal = 0;
        int bookIndex = 0;

        int[] src = null;
        int[] chapters = null;
        int[] dest = null;

        BibleBook[] books = null;

        this.name = name;
        books = new BibleBook[bookCount];

        if (booksOT.length > 0) { bookCount += booksOT.length + 1; }
        if (booksNT.length > 0) { bookCount += booksNT.length + 1; }

        books[0] = BibleBook.INTRO_BIBLE;

        if (booksOT.length > 0) 
        {
            books[1] = BibleBook.INTRO_OT;
            System.arraycopy(booksOT, 0, books, 2, booksOT.length);
        }

        if (booksNT.length > 0) 
        {
            books[ntStart] = BibleBook.INTRO_NT;
            System.arraycopy(booksNT, 0, books, ntStart + 1, booksNT.length);
        }

        bookList = new BibleBookList(books);
        lastVerse = new int[bookCount][];
        chapters = new int[1];
        chapters[0] = 0;
        lastVerse[bookIndex++] = chapters;
        if (lastVerseOT.length > 0) 
        {
            chapters = new int[1];
            chapters[0] = 0;
            lastVerse[bookIndex++] = chapters;

            for (int i = 0; i < lastVerseOT.length; i++) {
                src = lastVerseOT[i];
                dest = new int[src.length + 1];
                lastVerse[bookIndex++] = dest;
                dest[0] = 0;
                dest = Arrays.copyOf(src, src.length);
            }
        }

        if (lastVerseNT.length > 0) 
        {
            chapters = new int[1];
            chapters[0] = 0;
            lastVerse[bookIndex++] = chapters;
            for (int i = 0; i < lastVerseNT.length; i++) 
            {
                src = lastVerseNT[i];
                dest = new int[src.length + 1];
                lastVerse[bookIndex++] = dest;
                dest[0] = 0;
                dest = Arrays.copyOf(src, src.length);
            }
        }

        chapterStarts = new int[bookCount][];

        for (bookIndex = 0; bookIndex < bookCount; bookIndex++) 
        {
            if (bookList.getBook(bookIndex) == BibleBook.INTRO_NT) { otMaxOrdinal = ordinal - 1; }
            src = lastVerse[bookIndex];
            int numChapters = src.length;
            dest = new int[numChapters];
            chapterStarts[bookIndex] = dest;
            for (int chapterIndex = 0; chapterIndex < numChapters; chapterIndex++) 
            {
                dest[chapterIndex] = ordinal;
                ordinal += src[chapterIndex] + 1;
            }
        }
        this.ntMaxOrdinal = ordinal - 1;
        if (booksNT.length == 0) { this.otMaxOrdinal = this.ntMaxOrdinal; }
    }




    public boolean containsBook(BibleBook book) { return this.bookList.contains(book); }
    public boolean isBook(String find)          { return (getBook(find) != null); }
    public boolean isBookIntro(Verse verse)     { return (0 == verse.getChapter() && isIntro(verse)); }
    public boolean isChapterIntro(Verse verse)  { return (0 != verse.getChapter() && isIntro(verse)); }
    public boolean isSameBook(Verse first, Verse second) { return (first.getBook() == second.getBook()); }
    public boolean isIntro(Verse verse) 
    {
        int v = verse.getVerse();
        return (v == 0);
    }
    public boolean isStartOfChapter(Verse verse) 
    {
        int v = verse.getVerse();
        return (v <= 1);
    }
    public boolean isEndOfChapter(Verse verse) 
    {
        int v = 0;
        int c = 0;
        BibleBook b = null;

        b = verse.getBook();
        v = verse.getVerse();
        c = verse.getChapter();
        return (v == getLastVerse(b, c));
    }
    public boolean isStartOfBook(Verse verse) 
    {
        int v = 0;
        int c = 0;

        v = verse.getVerse();
        c = verse.getChapter();
        return (v <= 1 && c <= 1);
    }
    public boolean isEndOfBook(Verse verse) 
    {
        int v = 0;
        int c = 0;
        BibleBook b = null;
        
        b = verse.getBook();
        v = verse.getVerse();
        c = verse.getChapter();

        return (v == getLastVerse(b, c) && c == getLastChapter(b));
    }
    public boolean isSameChapter(Verse first, Verse second) 
    {
        return (first.getBook() == second.getBook() && first.getChapter() == second.getChapter());
    }
    public boolean isAdjacentChapter(Verse first, Verse second) 
    {
        Verse before = null;
        Verse after  = null;
        
        before = min(first, second);
        after  = max(first, second);

        if ( isSameBook(first, second) ) { return ((after.getChapter() - before.getChapter()) == 1); }

        return (isAdjacentBook(before, after) && getLastChapter(before.getBook()) == before.getChapter() && after.getChapter() <= 1);
    }
    public boolean isAdjacentBook(Verse first, Verse second) 
    {
        return (Math.abs(this.bookList.getOrdinal(second.getBook()) - this.bookList.getOrdinal(first.getBook())) == 1);
    }
    public boolean isAdjacentVerse(Verse first, Verse second) 
    {
        Verse before = min(first, second);
        Verse after = max(first, second);
        if (isSameChapter(first, second)) { return (after.getVerse() - before.getVerse() == 1); }
        return (isAdjacentChapter(before, after) && getLastVerse(before.getBook(), before.getChapter()) == before.getVerse() && after.getVerse() <= 1);
    }
    public boolean validate(BibleBook book, int chapter, int verse, boolean silent) throws NoSuchVerseException {
        if (book == null) 
        {
            if (silent) { return false; }
            String msg = "Book must not be null"  
                       + "\n    Chapter=" + chapter
                       + "\t silent=" + silent;
            lgr.error(msg,THISMODULE);
            throw new NoSuchVerseException(JSOtherMsg.lookupText("Book must not be null", new Object[0]));
        }
        int maxChapter = getLastChapter(book);
        if (chapter < 0 || chapter > maxChapter) 
        {
            if (silent) { return false; }
            String msg = "Chapter should be between 0 and " 
                       + Integer.valueOf(maxChapter) 
                       + " for " + getPreferredName(book) + " "
                       + Integer.valueOf(chapter);
            lgr.error(msg,THISMODULE);
            throw new NoSuchVerseException(JSMsg.gettext("Chapter should be between {0} and {1,number,integer} for {2} (given {3,number,integer}).", new Object[]{Integer.valueOf(0), Integer.valueOf(maxChapter), getPreferredName(book), Integer.valueOf(chapter)}));
        }
        int maxVerse = getLastVerse(book, chapter);
        if (verse < 0 || verse > maxVerse) {
            if (silent) {
                return false;
            }
            throw new NoSuchVerseException(JSMsg.gettext("Verse should be between {0} and {1,number,integer} for {2} {3,number,integer} (given {4,number,integer}).", new Object[]{Integer.valueOf(0), Integer.valueOf(maxVerse), getPreferredName(book), Integer.valueOf(chapter), Integer.valueOf(verse)}));
        }
        return true;
    }

    public int distance(Verse start, Verse end) { return end.getOrdinal() - start.getOrdinal(); }
    public int getBookCount()                   { return this.bookList.getBookCount(); }
    public int getLastChapter(BibleBook book) 
    {
        try 
        {
            return (this.lastVerse[this.bookList.getOrdinal(book)]).length - 1;
        } 
        catch ( NullPointerException | ArrayIndexOutOfBoundsException ex ) 
        {
            return 0;
        }
    }

    public int getLastVerse(BibleBook book, int chapter) 
    {
        try 
        {
            return this.lastVerse[this.bookList.getOrdinal(book)][chapter];
        } 
        catch ( NullPointerException | ArrayIndexOutOfBoundsException ex ) 
        {
            return 0;
        }
    }

    public int getBookCount(Verse start, Verse end) 
    {
        int startBook = this.bookList.getOrdinal(start.getBook());
        int endBook = this.bookList.getOrdinal(end.getBook());
        return endBook - startBook + 1;
    }

    public int getChapterCount(Verse start, Verse end) 
    {
        int startChap = 1;
        int endChap   = 1;
        
        startChap = start.getChapter();
        endChap = end.getChapter();
        BibleBook startBook = start.getBook();
        BibleBook endBook = end.getBook();

        if (startBook == endBook) { return endChap - startChap + 1; }
        int total = getLastChapter(startBook) - startChap;
        startBook = bookList.getNextBook(startBook);
        endBook = bookList.getPreviousBook(endBook);
        for (BibleBook b = startBook; b != endBook; b = this.bookList.getNextBook(b)) 
        {
            total += getLastChapter(b);
        }
        total += endChap;
        return total;
    }

    public int maximumOrdinal() { return this.ntMaxOrdinal; }

    public int getOrdinal(Verse verse) 
    {
        try 
        {
            return this.chapterStarts[this.bookList.getOrdinal(verse.getBook())][verse.getChapter()] + verse.getVerse();
        } 
        catch (ArrayIndexOutOfBoundsException e) 
        {
            String msg = "OutOfBoundsException getting ordinal for verse " + verse.getName();
            lgr.error(msg,THISMODULE);
            return 0;
        }
    }

    public int getOrdinal(Testament testament, int testamentOrdinal) 
    {
        try
        {
            if ( testamentOrdinal <= 0 ) { return 0; }
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            String msg = "Testament Ordinal value exception";
            lgr.error(msg,THISMODULE);
            return 0;
        }
        int ordinal = (testamentOrdinal >= 0) ? testamentOrdinal : 0;

        if (Testament.NEW == testament) 
        {
            ordinal = this.otMaxOrdinal + testamentOrdinal;
            return (ordinal <= this.ntMaxOrdinal) ? ordinal : this.ntMaxOrdinal;
        }
        return (ordinal <= this.otMaxOrdinal) ? ordinal : this.otMaxOrdinal;
    }

    public int getTestamentOrdinal(int ordinal) 
    {
        try
        {
            if ( ordinal <= 0 ) { return 0; }
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            String msg = "Ordinal value exception";
            lgr.error(msg,THISMODULE);
            return 0;
        }
        int ntOrdinal = this.otMaxOrdinal + 1;
        if (ordinal >= ntOrdinal) { return ordinal - ntOrdinal + 1; }
        return ordinal;
    }

    public int getCount(Testament testament) 
    {
        int total = this.ntMaxOrdinal + 1;
        if (testament == null) { return total; }
        int otCount = this.otMaxOrdinal + 1;
        if (testament == Testament.OLD) { return otCount; }
        return total - otCount;
    }


    public String getName()                     { return this.name; }
    public String getPreferredName(BibleBook book) 
    {
        if (containsBook(book)) { return BibleNames.instance().getPreferredName(book); }
        return null;
    }

    public String getLongName(BibleBook book) 
    {
        if (containsBook(book)) { return BibleNames.instance().getLongName(book); }
        return null;
    }

    public String getShortName(BibleBook book) 
    {
        if (containsBook(book)) { return BibleNames.instance().getShortName(book); }
        return null;
    }

    public BibleBook getBook(int ordinal)       { return this.bookList.getBook(ordinal); }
    public BibleBook getBook(String find) 
    {
        BibleBook book = BibleNames.instance().getBook(find);
        if (containsBook(book)) {  return book; }
        return null;
    }

    public BibleBook getFirstBook() { return this.bookList.getFirstBook(); }
    public BibleBook getLastBook()  { return this.bookList.getLastBook(); }
    public BibleBook getNextBook(BibleBook book) { return this.bookList.getNextBook(book); }
    public BibleBook getPreviousBook(BibleBook book) { return this.bookList.getPreviousBook(book); }
    public BookName getBookName(BibleBook book) {
        if (containsBook(book)) { return BibleNames.instance().getBookName(book); }
        return null;
    }

    public Iterator<BibleBook> getBookIterator() { return this.bookList.iterator(); }


    public VerseRange getAllVerses() 
    {
        Verse first = new Verse(this, this.bookList.getFirstBook(), 0, 0);
        BibleBook book = this.bookList.getLastBook();
        int chapter = getLastChapter(book);
        Verse last = new Verse(this, book, chapter, getLastVerse(book, chapter));
        return new VerseRange(this, first, last);
    }

    public Verse min(Verse first, Verse second) 
    {
        return (first.getOrdinal() <= second.getOrdinal()) ? first : second;
    }

    public Verse max(Verse first, Verse second) 
    {
        return (first.getOrdinal() > second.getOrdinal()) ? first : second;
    }

    public Verse subtract(Verse verse, int n) 
    {
        int newVerse = verse.getVerse() - n;
        if (newVerse >= 0) 
        {
            return new Verse(verse.getVersification(), verse.getBook(), verse.getChapter(), newVerse);
        }
        return decodeOrdinal(verse.getOrdinal() - n);
    }

    public Verse next(Verse verse) 
    {
        if (verse.getOrdinal() == this.ntMaxOrdinal) { return null; }

        BibleBook nextBook = verse.getBook();
        int nextChapter = verse.getChapter();
        int nextVerse = verse.getVerse() + 1;

        if (nextVerse > getLastVerse(nextBook, nextChapter)) 
        {
            nextVerse = 0;
            nextChapter++;
            if (nextChapter > getLastChapter(nextBook)) 
            {
                nextChapter = 0;
                nextBook = this.bookList.getNextBook(verse.getBook());
            }
        }

        if (nextBook == null) 
        {
            assert false;
            return null;
        }

        return new Verse(this, nextBook, nextChapter, nextVerse);
    }

    public Verse add(Verse verse, int n) 
    {
        int newVerse = 0;
        
        newVerse = verse.getVerse() + n;
        if (newVerse <= getLastVerse(verse.getBook(), verse.getChapter())) 
        {
            return new Verse(verse.getVersification(), verse.getBook(), verse.getChapter(), newVerse);
        }
        return decodeOrdinal(verse.getOrdinal() + n);
    }

    public Testament getTestament(int ordinal) 
    {
        if (ordinal > this.otMaxOrdinal)  { return Testament.NEW; }
        return Testament.OLD;
    }

    public Verse decodeOrdinal(int ordinal) 
    {
        int ord = ordinal;
        if (ord < 0) 
        {
            ord = 0;
        } 
        else if (ord > this.ntMaxOrdinal) 
        {
            ord = this.ntMaxOrdinal;
        }
        
        if (ord == 0) { return new Verse(this, BibleBook.INTRO_BIBLE, 0, 0); }
        if (ord == 1) { return new Verse(this, BibleBook.INTRO_OT, 0, 0); }
        if (ord == this.otMaxOrdinal + 1) { return new Verse(this, BibleBook.INTRO_NT, 0, 0); }
        int low = 0;
        int high = this.chapterStarts.length;
        int match = -1;
        while ( (high - low) > 1) 
        {
            int mid = low + high >>> 1;
            int cmp = this.chapterStarts[mid][0] - ord;
            if (cmp < 0) 
            {
                low = mid;
                continue;
            }

            if (cmp > 0) 
            {
                high = mid;
                continue;
            }
            match = mid;
        }
        int bookIndex = (match >= 0) ? match : low;
        BibleBook book = this.bookList.getBook(bookIndex);
        low = 0;
        high = (this.chapterStarts[bookIndex]).length;
        match = -1;
        while ( (high - low) > 1) 
        {
            int mid = low + high >>> 1;
            int cmp = this.chapterStarts[bookIndex][mid] - ord;
            if (cmp < 0) 
            {
                low = mid;
                continue;
            }
            
            if (cmp > 0) 
            {
                high = mid;
                continue;
            }
            match = mid;
        }
        int chapterIndex = (match >= 0) ? match : low;
        int verse = (chapterIndex == 0) ? 0 : (ord - this.chapterStarts[bookIndex][chapterIndex]);
        return new Verse(this, book, chapterIndex, verse);
    }

    public void validate(BibleBook book, int chapter, int verse) throws NoSuchVerseException 
    {
        validate(book, chapter, verse, false);
    }


    public Verse patch(BibleBook book, int chapter, int verse) 
    {
        BibleBook patchedBook = book;
        int patchedChapter = chapter;
        int patchedVerse = verse;
        if (patchedBook == null) { patchedBook = this.bookList.getFirstBook(); }
        if (patchedChapter < 0)  { patchedChapter = 0; }
        if (patchedVerse < 0)    { patchedVerse = 0; }
        
        while ( (patchedBook != null) && (patchedChapter > getLastChapter(patchedBook)) ) 
        {
            patchedChapter -= getLastChapter(patchedBook) + 1;
            patchedBook = this.bookList.getNextBook(patchedBook);
        }

        while ( (patchedBook != null) && (patchedVerse > getLastVerse(patchedBook, patchedChapter)) ) 
        {
            patchedVerse -= getLastVerse(patchedBook, patchedChapter) + 1;
            patchedChapter++;
            if (patchedChapter > getLastChapter(patchedBook)) 
            {
                patchedChapter -= getLastChapter(patchedBook) + 1;
                patchedBook = this.bookList.getNextBook(patchedBook);
            }
        }

        if (patchedBook == null) 
        {
            patchedBook = this.bookList.getLastBook();
            patchedChapter = getLastChapter(patchedBook);
            patchedVerse = getLastVerse(patchedBook, patchedChapter);
        }
        return new Verse(this, patchedBook, patchedChapter, patchedVerse);
    }
}