package org.crosswire.jsword.book.basic;

import org.crosswire.jsword.JSOtherMsg;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;

public abstract class AbstractBookDriver implements BookDriver {

    public boolean isWritable() { return false;  }
    public boolean isDeletable(Book dead) { return false; }


    public Book create(Book source) throws BookException {
        throw new BookException(JSOtherMsg.lookupText("This Book is read-only.", new Object[0]));
    }

    public void delete(Book dead) throws BookException {
        throw new BookException(JSOtherMsg.lookupText("This Book is read-only.", new Object[0]));
    }

    public Book getFirstBook() {
        Book[] books = getBooks();
        return (books == null || books.length == 0) ? null : books[0];
    }
}