package org.crosswire.jsword.book;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief Interface for BookProvider backend object
 * 
 */
public interface BookProvider 
{
    Book getFirstBook();
    Book[] getBooks();
}
