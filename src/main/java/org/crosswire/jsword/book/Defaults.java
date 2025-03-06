package org.crosswire.jsword.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

import org.crosswire.common.config.ChoiceFactory;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief Definition of defaults used in book module
 * @author ken
 */
public final class Defaults 
{
    private static boolean trackBible = true;

    private static Book currentBible;
    private static DefaultBook bibleDeft = new DefaultBook(Books.installed(), BookFilters.getOnlyBibles());
    private static DefaultBook commentaryDeft = new DefaultBook(Books.installed(), BookFilters.getCommentaries());
    private static DefaultBook dailyDevotionalDeft = new DefaultBook(Books.installed(), BookFilters.getDailyDevotionals());
    private static DefaultBook dictionaryDeft = new DefaultBook(Books.installed(), BookFilters.getDictionaries());
    private static DefaultBook greekParseDeft = new DefaultBook(Books.installed(), BookFilters.getGreekParse());
    private static DefaultBook hebrewParseDeft = new DefaultBook(Books.installed(), BookFilters.getHebrewParse());
    private static DefaultBook greekDefinitionsDeft = new DefaultBook(Books.installed(), BookFilters.getGreekDefinitions());
    private static DefaultBook hebrewDefinitionsDeft = new DefaultBook(Books.installed(), BookFilters.getHebrewDefinitions());

    static
    {
        Books.installed().addBooksListener(new DefaultsBookListener());
        checkAllPreferable();
    }

    private static final String BIBLE_KEY = "bible-names";
    private static final String COMMENTARY_KEY = "commentary-names";
    private static final String DICTIONARY_KEY = "dictionary-names";
    private static final String DAILY_DEVOTIONALS_KEY = "daily-devotional-names";
    private static final String GREEKDEF_KEY = "greekdef-names";
    private static final String HEBREWDEF_KEY = "hebrewdef-names";
    private static final String GREEKPARSE_KEY = "greekparse-names";
    private static final String HEBREWPARSE_KEY = "hebrewparse-names";

    private static final String THISMODULE = "org.crosswire.jsword.book.Defaults";

    private static final Logger lgr = LoggerFactory.getLogger(Defaults.class);

    public static boolean isCurrentBible() { return trackBible; }

    public static void setCurrentBible(boolean current) { trackBible = current; }
    public static void setCurrentBook(Book book)
    {
        BookCategory type = book.getBookCategory();
        if (type.equals(BookCategory.BIBLE) && isCurrentBible())
        {
            currentBible = book;
        }
    }

    public static Book getCurrentBible()
    {
        if (currentBible == null) { return bibleDeft.getDefault(); }
        return currentBible;
    }

    public static void setBible(Book book)  { bibleDeft.setDefault(book); }

    protected static void unsetBible()      { bibleDeft.unsetDefault(); }

    public static Book getBible()           { return bibleDeft.getDefault(); }
    public static Book getCommentary()      { return commentaryDeft.getDefault(); }
    public static Book getDictionary()      { return dictionaryDeft.getDefault(); }
    public static Book getDailyDevotional() { return dailyDevotionalDeft.getDefault(); }
    public static Book getGreekDefinitions() { return greekDefinitionsDeft.getDefault(); }
    public static Book getHebrewDefinitions() { return hebrewDefinitionsDeft.getDefault(); }
    public static Book getGreekParse()      { return greekParseDeft.getDefault(); }
    public static Book getHebrewParse()     { return hebrewParseDeft.getDefault(); }
    public static String getBibleByName()  { return bibleDeft.getDefaultName(); }
    public static String getCommentaryByName() { return commentaryDeft.getDefaultName(); }
    public static String getDictionaryByName() { return dictionaryDeft.getDefaultName(); }
    public static String getDailyDevotionalByName() { return dailyDevotionalDeft.getDefaultName(); }
    public static String getGreekDefinitionsByName() { return greekDefinitionsDeft.getDefaultName(); }
    public static String getHebrewDefinitionsByName() { return hebrewDefinitionsDeft.getDefaultName(); }
    public static String getGreekParseByName()  { return greekParseDeft.getDefaultName(); }
    public static String getHebrewParseByName() { return hebrewParseDeft.getDefaultName(); }
    public static void setBibleByName(String name) { bibleDeft.setDefaultByName(name); }
    public static void setCommentary(Book book)    { commentaryDeft.setDefault(book); }
    public static void setCommentaryByName(String name) { commentaryDeft.setDefaultByName(name); }
    public static void setDictionary(Book book)    { dictionaryDeft.setDefault(book); }
    public static void setDictionaryByName(String name) { dictionaryDeft.setDefaultByName(name); }
    public static void setDailyDevotional(Book book)    { dictionaryDeft.setDefault(book); }
    public static void setDailyDevotionalByName(String name) { dailyDevotionalDeft.setDefaultByName(name); }
    public static void setGreekDefinitions(Book book)   { greekDefinitionsDeft.setDefault(book); }
    public static void setGreekDefinitionsByName(String name) { greekDefinitionsDeft.setDefaultByName(name); }
    public static void setHebrewDefinitions(Book book)  { hebrewDefinitionsDeft.setDefault(book); }    protected static void unsetCommentary()        { commentaryDeft.unsetDefault(); }
    public static void setHebrewDefinitionsByName(String name) { hebrewDefinitionsDeft.setDefaultByName(name); }    protected static void unsetDictionary()        { dictionaryDeft.unsetDefault(); }
    public static void setGreekParse(Book book)         { greekParseDeft.setDefault(book); }
    public static void setGreekParseByName(String name) { greekParseDeft.setDefaultByName(name); }
    public static void setHebrewParse(Book book)        { hebrewParseDeft.setDefault(book); }
    public static void setHebrewParseByName(String name) { hebrewParseDeft.setDefaultByName(name); }
    protected static void unsetDailyDevotional()   { dailyDevotionalDeft.unsetDefault(); }
    protected static void unsetGreekDefinitions()  { greekDefinitionsDeft.unsetDefault(); }
    protected static void unsetHebrewDefinitions() { hebrewDefinitionsDeft.unsetDefault(); }
    protected static void unsetGreekParse()        { greekParseDeft.unsetDefault(); }
    protected static void unsetHebrewParse()       { hebrewParseDeft.unsetDefault(); }
    protected static DefaultBook getDefaultBible()            { return bibleDeft; }
    protected static DefaultBook getDefaultCommentary()       { return commentaryDeft; }
    protected static DefaultBook getDefaultDictionary()       { return dictionaryDeft; }
    protected static DefaultBook getDefaultDailyDevotional()  { return dailyDevotionalDeft; }
    protected static DefaultBook getDefaultGreekDefinitions() { return greekDefinitionsDeft; }
    protected static DefaultBook getDefaultHebrewDefinitions() { return hebrewDefinitionsDeft; }
    protected static DefaultBook getDefaultGreekParse()       { return greekParseDeft; }
    protected static DefaultBook getDefaultHebrewParse()       { return hebrewParseDeft; }

    public static void refreshBooks()
    {
        try
        {
            Map<Book, String> bnames = getBookMap(BookFilters.getOnlyBibles());
            ChoiceFactory.getDataMap().put("bible-names", bnames);
            Map<Book, String> cnames = getBookMap(BookFilters.getCommentaries());
            ChoiceFactory.getDataMap().put("commentary-names", cnames);
            Map<Book, String> dnames = getBookMap(BookFilters.getDictionaries());
            ChoiceFactory.getDataMap().put("dictionary-names", dnames);
            Map<Book, String> rnames = getBookMap(BookFilters.getDailyDevotionals());
            ChoiceFactory.getDataMap().put("daily-devotional-names", rnames);
            Map<Book, String> greekDef = getBookMap(BookFilters.getGreekDefinitions());
            ChoiceFactory.getDataMap().put("greekdef-names", greekDef);
            Map<Book, String> hebrewDef = getBookMap(BookFilters.getHebrewDefinitions());
            ChoiceFactory.getDataMap().put("hebrewdef-names", hebrewDef);
            Map<Book, String> greekParse = getBookMap(BookFilters.getGreekParse());
            ChoiceFactory.getDataMap().put("greekparse-names", greekParse);
            Map<Book, String> hebrewParse = getBookMap(BookFilters.getHebrewParse());
            ChoiceFactory.getDataMap().put("hebrewparse-names", hebrewParse);
        }
        catch ( Exception e )
        {
            String msg = "Error creating defaults maps - " + e.getMessage()
                       + "\n    cause=" + e.getCause();
            lgr.error(msg,THISMODULE);
        }
    }

    protected static void checkAllPreferable()
    {
        for (Book book : Books.installed().getBooks())
        {
            checkPreferable(book);
        }
    }

    protected static void checkPreferable(Book book)
    {
        assert book != null;
        bibleDeft.setDefaultConditionally(book);
        commentaryDeft.setDefaultConditionally(book);
        dictionaryDeft.setDefaultConditionally(book);
        dailyDevotionalDeft.setDefaultConditionally(book);
        greekDefinitionsDeft.setDefaultConditionally(book);
        greekParseDeft.setDefaultConditionally(book);
        hebrewDefinitionsDeft.setDefaultConditionally(book);
        hebrewParseDeft.setDefaultConditionally(book);
    }

    private static Map<Book, String> getBookMap(BookFilter filter)
    {
        Map<Book, String> books = new TreeMap<Book, String>(BookComparators.getDefault());
        for (Book book : Books.installed().getBooks(filter))
        {
            books.put(book, book.getName());
        }
        return books;
    }

    static class DefaultsBookListener implements BooksListener
    {
        public void bookAdded(BooksEvent ev)
        {
            Book book = ev.getBook();
            Defaults.checkPreferable(book);
            Defaults.refreshBooks();
        }

        public void bookRemoved(BooksEvent ev)
        {
            Book book = ev.getBook();
            Defaults.getDefaultBible().unsetDefaultConditionally(book);
            Defaults.getDefaultCommentary().unsetDefaultConditionally(book);
            Defaults.getDefaultDailyDevotional().unsetDefaultConditionally(book);
            Defaults.getDefaultDictionary().unsetDefaultConditionally(book);
            Defaults.getDefaultGreekDefinitions().unsetDefaultConditionally(book);
            Defaults.getDefaultGreekParse().unsetDefaultConditionally(book);
            Defaults.getDefaultHebrewDefinitions().unsetDefaultConditionally(book);
            Defaults.getDefaultHebrewParse().unsetDefaultConditionally(book);
        }
    }
}