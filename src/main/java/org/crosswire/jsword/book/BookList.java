package org.crosswire.jsword.book;

import java.util.List;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief Interface for List&lt;&gt; of books
 */
public interface BookList 
{
    List<Book> getBooks();
    List<Book> getBooks(BookFilter paramBookFilter);

    void addBooksListener(BooksListener paramBooksListener);
    void removeBooksListener(BooksListener paramBooksListener);
}
