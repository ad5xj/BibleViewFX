package org.crosswire.jsword.versification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @ingroup org.crosswire.jsword.versification
 * @brief List object of Bible books
 * 
 * @implements Iterable&lt;&gt;
 * @implements Serializable
 */
class BibleBookList implements Iterable<BibleBook>, Serializable 
{


    private static final long serialVersionUID = -2681289798451902815L;
    private static final String THISMODULE = "org.crosswire.jsword.versification.BibleBookList";
    private static final Logger lgr = LoggerFactory.getLogger(BibleBookList.class);

    protected BibleBook[] books;

    private int[] bookMap;

    BibleBookList(BibleBook[] i_books) 
    {
        books = i_books.clone();
        initialize();
    }

    public boolean contains(BibleBook book) 
    {
        return (book != null && bookMap[book.ordinal()] != -1);
    }

    public int getOrdinal(BibleBook book) { return bookMap[book.ordinal()]; }

    public int getBookCount()             { return books.length; }

    public BibleBook getBook(int ordinal) 
    {
        int ord = ordinal;
        if (ord < 0) {  ord = 0; }
        if (ord >= books.length) { ord = books.length - 1; }
        return books[ord];
    }

    public Iterator<BibleBook> iterator() 
        throws NoSuchElementException, UnsupportedOperationException 
    {
        return new Iterator<BibleBook>() 
        {
            private BibleBook nextBook = BibleBookList.this.books[0];

            public boolean hasNext() { return (nextBook != null); }

            @Override
            public BibleBook next()  
            {
                if (nextBook == null) 
                {
                    String msg = "Error on next when in creating iterator for "
                               + "book list - NoSuchElementException...null";
                    lgr.error(msg,THISMODULE);
                    throw new NoSuchElementException(); 
                }
                BibleBook current = nextBook;
                nextBook = BibleBookList.this.getNextBook(nextBook);
                return current;
            }

            @Override
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }

    public BibleBook getFirstBook() { return books[0]; }

    public BibleBook getLastBook()  { return books[books.length - 1]; }

    public BibleBook getPreviousBook(BibleBook book) 
    {
        int ordinal = book.ordinal();
        int position = bookMap[ordinal];
        if (position > 0) { return books[position - 1]; }
        return null;
    }

    public BibleBook getNextBook(BibleBook book) 
    {
        int ordinal = book.ordinal();
        int position = bookMap[ordinal];
        if (position != -1 && position + 1 < books.length) {
            return books[position + 1];
        }
        return null;
    }

    private void initialize() 
    {
        int cnt = BibleBook.values().length + 1;
        bookMap = new int[cnt];
        for (BibleBook b : BibleBook.values()) 
        {
            bookMap[b.ordinal()] = -1;
        }
        
        for (int i = 0; i < books.length; i++) 
        {
            bookMap[books[i].ordinal()] = i;
        }
    }
}