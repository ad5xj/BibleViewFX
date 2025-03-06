package org.crosswire.jsword.index;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;

import java.net.URI;

public interface IndexManager
{

    void scheduleIndexCreation(Book paramBook);
    void installDownloadedIndex(Book paramBook, URI paramURI) throws BookException;
    void deleteIndex(Book paramBook) throws BookException;
    void closeAllIndexes();
    void setIndexPolicy(IndexPolicy paramIndexPolicy);

    boolean isIndexed(Book paramBook);
    boolean needsReindexing(Book paramBook);

    Index getIndex(Book paramBook) throws BookException;
    IndexPolicy getIndexPolicy();
}