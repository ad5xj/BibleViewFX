package org.crosswire.jsword.book;

import org.crosswire.common.util.Filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief Defines a set of Book(s) in an array as a Set
 * @extends ArrayList&lt;Book&gt;
 * @implements Set&lt;Book&gt;
 */
public class BookSet extends ArrayList<Book> implements Set<Book> 
{
    private static final long serialVersionUID = 3258688806185154867L;

    /**
     * @brief Default Constructor with no params
     */
    public BookSet() {   }

    /**
     * @brief Overloaded Constructor with params
     */
    public BookSet(Collection<Book> books) 
    {
        this();
        addAll(books);
    }

    /**
     * @brief Get a set of groups without param
     * @return results Set&lt;String&gt;
     */
    public Set<String> getGroups() 
    {
        Set<String> results = new TreeSet<String>();
        for (Book book : this) 
        {
            results.addAll(book.getPropertyKeys());
        }
        return results;
    }

    /**
     * @brief Get a set of groups with param key
     * @return results Set&lt;Object&gt;
     */
    public Set<Object> getGroup(String key) 
    {
        Set<Object> results = new TreeSet();
        for (Book book : this) 
        {
            Object property = book.getProperty(key);
            if (property != null) { results.add(property); }
        }
        return results;
    }

    /**
     * @brief Definition for a filter for the set
     * @param key
     * @param value
     * @return 
     */
    public BookSet filter(String key, Object value) { return filter(new GroupFilter(key, value)); }

    /**
     * @brief Definition for a filter for the set
     * @param filter Filter&lt;Book&gt;
     * @return 
     */
    public BookSet filter(Filter<Book> filter) 
    {
        BookSet listSet = (BookSet) clone();
        Iterator<Book> iter = listSet.iterator();
        while (iter.hasNext()) 
        {
            Book obj = iter.next();
            if (!filter.test(obj)) { iter.remove(); }
        }
        return listSet;
    }

    /**
     * @brief add Book to the set with index
     * @param index
     * @param element 
     */
    public void add(int index, Book element) { add(element); }

    /**
     * @brief add Book to the set with no index
     * @param book Book 
     */
    public final boolean add(Book book) 
    {
        int pos = Collections.binarySearch(this, book);

        if ( pos < 0 ) 
        {
            super.add(-pos - 1, book);
            return true;
        }
        return false;
    }

    /**
     * @brief Add all books in a collection to the set
     * @param c Book
     * @return added boolean 
     */
    public final boolean addAll(Collection<? extends Book> c) 
    {
        boolean added = false;
        for (Book book : c) 
        {
            if (add(book)) { added = true; }
        }
        return added;
    }

    /**
     * @brief Add a specific book to the set indexed by param index
     * @param index
     * @param c
     * @return boolean on success
     */
    public final boolean addAll(int index, Collection<? extends Book> c) { return addAll(c); }

    /**
     * @brief Get a specific book from the set
     * @param index
     * @param element
     * @return item Book
     */
    public Book set(int index, Book element) 
    {
        Book item = remove(index);
        add(element);
        return item;
    }

    /** 
     * @brief Helper class for BookSet Object
     * @implements Filter&lt;Book&gt;
     */
    private static final class GroupFilter implements Filter<Book> 
    {
        private String key;

        private Object value;

        GroupFilter(String aKey, Object aValue) 
        {
            this.key = aKey;
            this.value = aValue;
        }

        public boolean test(Book book) 
        {
            String property = book.getProperty(this.key);
            return (property != null && property.equals(this.value));
        }
    }
}