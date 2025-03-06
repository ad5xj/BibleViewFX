package org.crosswire.jsword.book.readings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.util.URIFilter;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.basic.AbstractBookDriver;

import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

public class ReadingsBookDriver extends AbstractBookDriver 
{
    public static final String DIR_READINGS = "readings";
    public static BookDriver instance() { return INSTANCE; }


    private static final Logger lgr = LoggerFactory.getLogger(ReadingsBookDriver.class);
    private static final String THISMODULE = "ReadingsBookDriver";
    private static final BookDriver INSTANCE = new ReadingsBookDriver();

    private Book[] books;

    public ReadingsBookDriver()
    {
        List<Book> bookList = new ArrayList<Book>();
        String[] installedBooks = getInstalledReadingsSets();
        for ( int i = 0; i < installedBooks.length; i++ )
        {
            try
            {
                bookList.add(new ReadingsBook(this, installedBooks[i], BookCategory.DAILY_DEVOTIONS));
            }
            catch ( Exception e )
            {
                String msg = "Error adding book to list - " + e.getMessage();
                lgr.error(msg,THISMODULE);
            }
        }
        books = bookList.<Book>toArray(new Book[bookList.size()]);
    }

    public Book[] getBooks()       { return ( books == null ) ? null : books.clone(); }
    public String getDriverName() { return "Readings"; }
    public String[] getInstalledReadingsSets()
    {
        URL index = null;

        try
        {
            index = ResourceUtil.getResource(ReadingsBookDriver.class, "readings.txt");
            return NetUtil.listByIndexFile(NetUtil.toURI(index), new ReadingsFilter());
        }
        catch (IOException | MissingResourceException ex)
        {
            String msg = "Error getting resource " + index.toString() 
                       + " - " +  ex.getMessage();
            lgr.error(msg,THISMODULE);
            return new String[0];
        }
    }

    static final class ReadingsFilter implements URIFilter
    {
        public boolean accept(String name) { return true; }
    }
}
