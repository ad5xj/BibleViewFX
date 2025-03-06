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

import java.util.ArrayList;
import java.util.List;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief Some common implementations of BookFilter.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class BookFilters 
{
    private static boolean commentariesWithBibles;  /// Whether biblesBookFilter includes commentaries. Initially false. */
    private static CustomBookFilter.Test[] tests;
    
 
    private BookFilters() {    }                   /// Ensure we can't be created

    /**
     * @brief A simple default filter that returns everything
     *
     * @return the desired filter
     */
    public static BookFilter getAll() { return new AllBookFilter(); }

    /**
     * @details  
     * A filter that accepts everything that implements Bible or Commentary,
     * when commentaries are listed with Bibles.
     *
     * @return the desired filter
     */
    public static BookFilter getBibles() 
    {
        if ( commentariesWithBibles ) 
        {
            return either(new BookCategoryFilter(BookCategory.BIBLE), new BookCategoryFilter(BookCategory.COMMENTARY));
        }
        return new BookCategoryFilter(BookCategory.BIBLE);
    }

    /**
     * @details
     * A filter that accepts everything that implements Bible.
     *
     * @return the desired filter
     */
    public static BookFilter getOnlyBibles() { return new BookCategoryFilter(BookCategory.BIBLE); }

    /**
     * @details
     * A filter that accepts everything that's not a Bible or a Commentary, when
     * commentaries are listed with Bibles.
     *
     * @return the desired filter
     */
    public static BookFilter getNonBibles() 
    {
        if ( commentariesWithBibles ) 
        {
            return both(new NotBookCategoryFilter(BookCategory.BIBLE), new NotBookCategoryFilter(BookCategory.COMMENTARY));
        }
        return new NotBookCategoryFilter(BookCategory.BIBLE);
    }

    /**
     * @details A filter that accepts everything that implements Dictionary
     *
     * @return the desired filter
     */
    public static BookFilter getDictionaries() 
    {
        return new BookCategoryFilter(BookCategory.DICTIONARY);
    }

    /**
     * @details A filter that accepts everything that implements Dictionary
     *
     * @return the desired filter
     */
    public static BookFilter getGlossaries() 
    {
        return new BookCategoryFilter(BookCategory.GLOSSARY);
    }

    /**
     * @details A filter that accepts everything that implements DailyDevotionals
     *
     * @return the desired filter
     */
    public static BookFilter getDailyDevotionals() 
    {
        return new BookCategoryFilter(BookCategory.DAILY_DEVOTIONS);
    }

    /**
     * @details 
     * A filter that accepts everything that implements Commentary
     *
     * @return the desired filter
     */
    public static BookFilter getCommentaries() 
    {
        return new BookCategoryFilter(BookCategory.COMMENTARY);
    }

    /**
     * @details 
     * A filter that accepts everything that implements GeneralBook
     *
     * @return the desired filter
     */
    public static BookFilter getGeneralBooks() 
    {
        return new BookCategoryFilter(BookCategory.GENERAL_BOOK);
    }

    /**
     * @details 
     * A filter that accepts everything that implements Maps
     *
     * @return the desired filter
     */
    public static BookFilter getMaps() 
    {
        return new BookCategoryFilter(BookCategory.MAPS);
    }

    /**
     * @details 
     * A filter that accepts everything that is a Greek Definition Dictionary
     *
     * @return the desired filter
     */
    public static BookFilter getGreekDefinitions() 
    {
        return new BookFeatureFilter(FeatureType.GREEK_DEFINITIONS);
    }

    /**
     * @details 
     * A filter that accepts everything that is a Greek Parse/Morphology
     * Dictionary
     *
     * @return the desired filter
     */
    public static BookFilter getGreekParse() 
    {
        return new BookFeatureFilter(FeatureType.GREEK_PARSE);
    }

    /**
     * @details 
     * A filter that accepts everything that is a Hebrew Definition Dictionary
     *
     * @return the desired filter
     */
    public static BookFilter getHebrewDefinitions() 
    {
        return new BookFeatureFilter(FeatureType.HEBREW_DEFINITIONS);
    }

    /**
     * @details 
     * A filter that accepts everything that is a Hebrew Parse/Morphology
     * Dictionary
     *
     * @return the desired filter
     */
    public static BookFilter getHebrewParse() 
    {
        return new BookFeatureFilter(FeatureType.HEBREW_PARSE);
    }

    /**
     * @details
     * Determine whether the getBible should return the current Bible or the
     * user's chosen default.
     *
     * @return true if the bible tracks the user's selection
     */
    public static boolean isCommentariesWithBibles() 
    {
        return commentariesWithBibles;
    }

    /**
     * @details
     * Establish whether the getBible should return the current Bible or the
     * user&rsquo;s chosen default.
     *
     * @param current boolean - whether commentaries should be returned together with
     * Bibles
     */
    public static void setCommentariesWithBibles(boolean current) 
    {
        commentariesWithBibles = current;
    }

    /**
     * @details 
     * Filter for all books
     * @see org.crosswire.jsword.book.BookFilter#test(org.crosswire.jsword.book.Book)
     */
    static class AllBookFilter implements BookFilter 
    {
        public boolean test(Book book) {  return true; }
    }

    /**
     * @details 
     * Filter for books by category
     * @see org.crosswire.jsword.book.BookFilter#test(org.crosswire.jsword.book.Book)
     */
    static class BookCategoryFilter implements BookFilter 
    {
        private BookCategory category;

        public boolean test(Book book) { return book.getBookCategory().equals(category) && !book.isLocked(); }

        BookCategoryFilter(BookCategory category) { category = category; }
    }

    /**
     * @details
     * Filter for books by category
     * 
     * @see org.crosswire.jsword.book.BookFilter#test(org.crosswire.jsword.book.Book)
     */
    static class NotBookCategoryFilter implements BookFilter {
        private BookCategory category;

        @Override
        public boolean test(Book book) { return !book.getBookCategory().equals(category) && !book.isLocked(); }

        NotBookCategoryFilter(BookCategory cat) { category = cat; }
    }

    /**
     * @details
     * Filter for books by feature
     * 
     * @see org.crosswire.jsword.book.BookFilter#test(org.crosswire.jsword.book.Book)
     */
    public static class BookFeatureFilter implements BookFilter {
        private FeatureType feature;

        @Override
        public boolean test(Book book) { return book.hasFeature(feature) && !book.isLocked(); }

        public BookFeatureFilter(FeatureType feat) { feature = feat; }
    }

    /**
     * @details 
     * A filter that accepts Books that match two criteria.
     *
     * @param b1 the first filter criteria
     * @param b2 the second filter criteria
     * @return the desired filter
     */
    public static BookFilter both(final BookFilter b1, final BookFilter b2) 
    {
        return new BookFilter() 
        {
            @Override
            public boolean test(Book book) { return b1.test(book) && b2.test(book); }
        };
    }

    /**
     * @details 
     * A filter that accepts Books that match either of two criteria.
     *
     * @param b1 the first filter criteria
     * @param b2 the second filter criteria
     * @return the desired filter
     */
    public static BookFilter either(final BookFilter b1, final BookFilter b2) 
    {
        return (Book book) -> b1.test(book) || b2.test(book);
    }

    /**
     * @details
     * A filter that accepts Books that match by book driver.
     *
     * @param driver the driver to match
     * @return the desired filter
     */
    public static BookFilter getBooksByDriver(final BookDriver driver) 
    {
        return (Book book) -> book.getDriver() == driver;
    }

    /**
     * @details
     * A simple default filter that returns everything. The match parameter is a
     * set of name value pairs like this: <br>
     * <code>initials=ESV;type=Bible;driverName=Sword</code><br>
     * Before the = there must be the name of a property on Book and after the
     * value to match (.toString()) is called on the results of the getter.
     *
     * @param match a ; separated list of properties (of Book) to match
     * @return the desired filter
     * @see Book
     */
    public static BookFilter getCustom(String match) { return new CustomBookFilter(match); }

    /**
     * @details
     * Custom Filter
     * 
     * @param match The match spec.
     * @see BookFilters#getCustom(String)
     */
    static class CustomBookFilter implements BookFilter 
    {
        protected static String property;
        protected static String result;

        CustomBookFilter(String match) 
        {
            List<Test> cache = new ArrayList<Test>();
            String[] filters = match.split(";");
            for (int i = 0; i < filters.length; i++) 
            {
                cache.add(new Test(filters[i]));
            }

            tests = cache.toArray(new Test[cache.size()]);
        }

        /**
         * @see org.crosswire.jsword.book.BookFilter#test(org.crosswire.jsword.book.Book)
         */
        public boolean test(Book book) 
        {
            for (int i = 0; i < tests.length; i++) 
            {
                Test test = tests[i];
                if ( "initials".equalsIgnoreCase(property) ) 
                {
                    if ( !result.equals(book.getInitials()) ) { return false; }
                    continue;
                }
                String result = book.getProperty(property);
                if ( (result == null) || !result.equals(result) ) { return false; }
            }
            return true;
        }

        /**
         * @details 
         * A helper class
         */
        static class Test 
        {

            protected Test(String filter) 
            {
                String[] parts = filter.split("=");
                if ( (parts.length != 2) || 
                     (parts[0].length() == 0) || 
                     (parts[1].length() == 0) 
                   ) 
                {
                    throw new IllegalArgumentException("Filter format is 'property=value', given: " + filter);
                }
                property = parts[0];
                result = parts[1];
            }

            protected Test(String prop, String res) 
            {
                property = prop;
                result = res;
            }
        }
    }
}