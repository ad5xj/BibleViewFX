package org.crosswire.jsword.book;
/** @defgroup org.crosswire.jsword Crosswire JSWORD library */
import java.util.List;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import org.crosswire.common.util.CollectionUtil;

/**
 * @brief Interface to create a list of books
 * 
 * @author ken
 */
public abstract class AbstractBookList implements BookList {

    private List<BooksListener> listeners;

    public AbstractBookList() {
        this.listeners = new CopyOnWriteArrayList<>();
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public List<Book> getBooks(BookFilter filter) {
        List<Book> temp = CollectionUtil.createList(new BookFilterIterator(getBooks(), filter));
        return Collections.unmodifiableList(temp);
    }

    public void addBooksListener(BooksListener li) { this.listeners.add(li); }

    public void removeBooksListener(BooksListener li) { this.listeners.remove(li); }

    protected void fireBooksChanged(Object source, Book book, boolean added) {
        BooksEvent ev = new BooksEvent(source, book, added);
        for (BooksListener listener : this.listeners) {
            if (added) {
                listener.bookAdded(ev);
                continue;
            }
            listener.bookRemoved(ev);
        }
    }
}