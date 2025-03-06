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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.EventObject;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief Event definition for Books object
 * @details A BooksEvent is fired whenever a Bible is added or removed from the system.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class BooksEvent extends EventObject 
{
    private static final long serialVersionUID = 3834876879554819894L; /// ID For serialization

    private transient Book book;   /// The name of the changed Bible

    private boolean added;         /// Is this an addition event? 

    /**
     * @brief Basic constructor
     * 
     * @param source the source of this BookEvent
     * @param i_book The book of the changed Bible, or 
     * null if there is more than one change.
     * @param added True if the changed Bible is an addition.
     */
    public BooksEvent(Object source, Book i_book, boolean i_added) 
    {
        super(source);

        book = i_book;
        added = i_added;
    }

    /**
     * @brief Get the the changed Book
     * 
     * @return book Book
     */
    public Book getBook() { return book; }

    /**
     * @brief Is this an addition event?
     * 
     * @return true if the book is being added
     */
    public boolean isAddition() { return added; }

    /**
     * @brief Serialization support.
     * 
     * @param is the input stream
     * @throws IOException if an I/O error occurred
     * @throws ClassNotFoundException this class cannot be recreated.
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException 
    {
        // Broken but we don't serialize events
        book = null;
        is.defaultReadObject();
    }
}