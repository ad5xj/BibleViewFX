package org.crosswire.jsword.book;

import java.util.Comparator;
import java.util.Locale;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief Book Comparators
 */
public final class BookComparators 
{
    /**
     * @brief getDefault
     * 
     * @return o1.compareTo(o2)
     */
    public static Comparator<Book> getDefault() 
    {
        return new Comparator<Book>() 
        {
            public int compare(Book o1, Book o2) 
            {
                return o1.compareTo(o2);
            }
        };
    }

    /**
     * 
     * @return 
     */
    public static Comparator<Book> getInitialComparator() 
    {
        return new Comparator<Book>() 
        {
            public int compare(Book o1, Book o2) 
            {
                return o1.getInitials().toUpperCase(Locale.ENGLISH).compareTo(o2.getInitials().toUpperCase(Locale.ENGLISH));
            }
        };
    }
}
