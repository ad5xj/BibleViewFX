package org.crosswire.jsword.book;

/** 
 * @ingroup org.crosswire.jsword.book
 * @extends BookProvider
 */
public interface BookDriver extends BookProvider 
{

    void delete(Book paramBook) throws BookException;

    boolean isWritable();
    boolean isDeletable(Book paramBook);
  
    String getDriverName();
    
    Book create(Book paramBook) throws BookException;
}