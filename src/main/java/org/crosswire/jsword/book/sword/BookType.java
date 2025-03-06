/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.sword;
/** @ingroup org.crosswire.jsword.book.sword */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.KeyType;

/**
 * @brief Data about book types.
 *
 * @see gnu.lgpl.License for license details.<br>
 * @copyright The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum BookType 
{
    RAW_TEXT("RawText", BookCategory.BIBLE, KeyType.VERSE) /// Uncompressed Bibles
    {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend) 
        {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException {
            return new RawBackend(sbmd, 2);
        }
    },
     
     
    Z_TEXT("zText", BookCategory.BIBLE, KeyType.VERSE) ///  Compressed Bibles
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend) {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException {
            BlockType blockType = BlockType.fromString((String) sbmd.getProperty(ConfigEntryType.BLOCK_TYPE.toString()));
            return new ZVerseBackend(sbmd, blockType);
        }
    },

    RAW_COM("RawCom", BookCategory.COMMENTARY, KeyType.VERSE) ///  Uncompressed Commentaries
    {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend) 
        {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException 
        {
            return new RawBackend(sbmd, 2);
        }
    },

    RAW_COM4("RawCom4", BookCategory.COMMENTARY, KeyType.VERSE) 
    {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend) 
        {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException 
        {
            return new RawBackend(sbmd, 4);
        }
    },
     
    Z_COM("zCom", BookCategory.COMMENTARY, KeyType.VERSE) ///  Compressed Commentaries
    {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend) 
        {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException 
        {
            BlockType blockType = BlockType.fromString((String) sbmd.getProperty(ConfigEntryType.BLOCK_TYPE.toString()));
            return new ZVerseBackend(sbmd, blockType);
        }
    },
     
    HREF_COM("HREFCom", BookCategory.COMMENTARY, KeyType.VERSE) ///  Uncompresses HREF Commentaries
    {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend) 
        {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException 
        {
            return new RawBackend(sbmd, 2);
        }
    },

    RAW_FILES("RawFiles", BookCategory.COMMENTARY, KeyType.VERSE) ///  Uncompressed Commentaries
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend) 
        {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException 
        {
            return new RawFileBackend(sbmd, 2);
        }
    },

    RAW_LD("RawLD", BookCategory.DICTIONARY, KeyType.LIST) ///  2-Byte Index Uncompressed Dictionaries
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend) 
        {
            if (sbmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS)) 
            {
                return new SwordDailyDevotion(sbmd, backend);
            }
            return new SwordDictionary(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException 
        {
            return new RawLDBackend(sbmd, 2);
        }
    },

    RAW_LD4("RawLD4", BookCategory.DICTIONARY, KeyType.LIST)  ///  4-Byte Index Uncompressed Dictionaries
    {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend) 
        {
            if (sbmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS)) 
            {
                return new SwordDailyDevotion(sbmd, backend);
            }
            return new SwordDictionary(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException 
        {
            return new RawLDBackend(sbmd, 4);
        }
    },

    Z_LD("zLD", BookCategory.DICTIONARY, KeyType.LIST) ///  Compressed Dictionaries
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend) 
        {
            if (sbmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS)) 
            {
                return new SwordDailyDevotion(sbmd, backend);
            }
            return new SwordDictionary(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException 
        {
            return new ZLDBackend(sbmd);
        }
    },

    RAW_GEN_BOOK("RawGenBook", BookCategory.GENERAL_BOOK, KeyType.TREE) ///  Generic Books
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend) 
        {
            return new SwordGenBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException 
        {
            return new GenBookBackend(sbmd);
        }
    };

    private String name;               ///  The name of the BookType

    private BookCategory category;     ///  What category is this book
    private KeyType keyType;           ///  What category is this book

    private static final String THISMODULE = "org.crosswire.jsword.book.sword.BookType";
    private static final Logger lgr = LoggerFactory.getLogger(BookType.class);

    /**
     * @brief Constructor with params
     * 
     * @param i_name
     * @param i_category
     * @param type 
     */
    private BookType(String i_name, BookCategory i_category, KeyType type)
    {
        name = i_name;
        category = i_category;
        keyType = type;
    }

    @Override
    public String toString() { return name; }

    /**
     * @brief Find a BookType from a name.
     *
     * @param name The name of the BookType to look up
     * @return v The found BookType or null if the name is not found
     */
    public static BookType getBookType(String name) 
    {
        for ( BookType v : values() ) 
        {
            try
            {
                if ( v.name().equalsIgnoreCase(name) ) { return v; }
            }
            catch ( IllegalArgumentException e )
            {
                String msg = "BookType " + name + " is not defined!";
                lgr.error(msg,THISMODULE);
            }
        }
//        throw new IllegalArgumentException(JSOtherMsg.lookupText("BookType {0} is not defined!", name));
        return null;
    }

    /**
     * @brief Get the category of this book
     */
    public BookCategory getBookCategory() { return category; }

    /**
     * @brief Get the way this type of Book organizes it's keys.
     *
     * @return the organization of keys for this book
     */
    public KeyType getKeyType() { return keyType; }

    /**
     * @brief is supported property 
     * 
     * @details 
     * Given a SwordBookMetaData determine whether this BookType will work for
     * it.
     *
     * @param sbmd the BookMetaData that this BookType works upon
     * @return true if this is a usable BookType
     */
    public boolean isSupported(SwordBookMetaData sbmd) { return (category != null) && (sbmd != null); }

    /**
     * @brief Create a Book appropriate for the BookMetaData
     *
     * @throws BookException
     */
    public Book createBook(SwordBookMetaData sbmd) throws BookException { return getBook(sbmd, getBackend(sbmd)); }

    public static BookType fromString(String name)   ///  Lookup method to convert from a String
    {
        for (BookType v : values()) 
        {
            try
            {
                if ( v.name.equalsIgnoreCase(name) ) 
                {
                    return v;
                }
            }
            catch ( ClassCastException e )
            {
                String msg = "DataType " + name + " is not defined!";
                lgr.error(msg,THISMODULE);
            }
        }
//        throw new ClassCastException(JSOtherMsg.lookupText("DataType {0} is not defined!", name));
        return null;
    }
    protected abstract Book getBook(SwordBookMetaData sbmd, AbstractBackend backend); /// Create a Book with the given backend
    protected abstract AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException; ///  Create a the appropriate backend for this type of book
}