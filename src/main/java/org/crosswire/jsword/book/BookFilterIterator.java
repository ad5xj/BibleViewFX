/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief An iterator that filters as it goes.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class BookFilterIterator implements Iterable<Book>, Iterator<Book> 
{
    private Book next;
    private BookFilter filter;

    private Iterator<Book> it;

    /**
     * @brief Simple ctor
     * 
     * @param books an iterator over a set of books
     * @param filter
     *            The filter to use, if null, will iterate over all values
     */
    public BookFilterIterator(Iterable<Book> books, BookFilter filter) 
    {
        it = books.iterator();
        filter = filter;
    }

    /** 
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Book> iterator() { return this; }

    /* 
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() 
    {
        next = findNext();
        return next != null;
    }

    /*
     * @see java.util.Iterator#next()
     */
    public Book next() 
    {
        if (next == null) { throw new NoSuchElementException(); }
        return next;
    }

    /* 
     * @see java.util.Iterator#remove()
     */
    public void remove() { throw new UnsupportedOperationException(); }

    /**
     * @brief Find the next (if there is one)
     * 
     * @return the next book
     */
    private Book findNext() 
    {
        while ( it.hasNext() ) 
        {
            Book book = it.next();
            if (filter == null || filter.test(book)) { return book; }
        }
        return null;
    }
}