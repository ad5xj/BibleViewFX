package org.crosswire.jsword.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief Define the default bible
 */
public class DefaultBook 
{
    private static final Logger lgr = LoggerFactory.getLogger(DefaultBook.class);

    private final BookList books;
    private final BookFilter filter;

    private boolean debug_log = false;

    private Book book;

    /**
     * @brief Constructor with params
     * @param bookList
     * @param bookFilter 
     */
    public DefaultBook(BookList bookList, BookFilter bookFilter) 
    {
        books = bookList;
        filter = bookFilter;
    }

    public void setDefault(Book newBook) 
    {
        if ( filter.test(newBook)) { book = newBook; }
    }

    public void setDefaultConditionally(Book newBook) 
    {
        if ( book == null ) { setDefault(newBook); }
    }

    protected void unsetDefault() 
    {
        book = null;
        checkReplacement();
    }

    protected void unsetDefaultConditionally(Book oldBook) 
    {
        if ( book == oldBook) { unsetDefault(); }
    }

    public Book getDefault() { return this.book; }

    public String getDefaultName() 
    {
        if ( book == null ) { return null; }
        return book.getName();
    }

    public void setDefaultByName(String name) 
    {
        if ( (name == null) || (name.length() == 0) ) 
        {
            if ( debug_log ) { lgr.info("Attempt to set empty book as default. Ignoring"); }
            return;
        }
        
        for (Book aBook : this.books.getBooks(this.filter)) 
        {
            if ( aBook.match(name) ) 
            {
                setDefault(aBook);
                return;
            }
        }
        if ( debug_log ) { lgr.info("Book not found. Ignoring: " + name); }
    }

    protected void checkReplacement() 
    {
        List<Book> bookList = books.getBooks(filter);
        Iterator<Book> it = bookList.iterator();
        if (it.hasNext()) { book = it.next(); }
    }
}