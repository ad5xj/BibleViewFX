package org.crosswire.jsword.book;

import org.crosswire.common.util.Language;
import org.crosswire.jsword.index.IndexStatus;

import java.net.URI;
import java.util.Set;

import org.jdom2.Document;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief Interface to Book meta data
 * @extends Comparable&lt;BookMetaData&gt;
 */
public interface BookMetaData extends Comparable<BookMetaData> 
{
    public static final String KEY_CATEGORY = "Category";
    public static final String KEY_BOOK = "Book";
    public static final String KEY_DRIVER = "Driver";
    public static final String KEY_NAME = "Description";
    public static final String KEY_LANG = "Lang";
    public static final String KEY_LANGUAGE = "Language";
    public static final String KEY_FONT = "Font";
    public static final String KEY_VERSIFICATION = "Versification";
    public static final String KEY_BOOKLIST = "BookList";
    public static final String KEY_SCOPE = "Scope";

    void setLanguage(Language paramLanguage);
    void setLibrary(URI paramURI) throws BookException;
    void setLocation(URI paramURI);
    void reload() throws BookException;
    void setProperty(String paramString1, String paramString2);
    void putProperty(String paramString1, String paramString2);
    void putProperty(String paramString1, String paramString2, boolean paramBoolean);
    void setIndexStatus(IndexStatus paramIndexStatus);

    boolean isSupported();
    boolean isEnciphered();
    boolean isLocked();
    boolean unlock(String paramString);
    boolean isQuestionable();
    boolean isLeftToRight();
    boolean hasFeature(FeatureType paramFeatureType);

    String getName();
    String getBookCharset();
    String getAbbreviation();
    String getInitials();
    String getOsisID();
    String getUnlockKey();
    String getDriverName();

    KeyType getKeyType();

    BookCategory getBookCategory();

    BookDriver getDriver();

    Language getLanguage();

    URI getLibrary();

    URI getLocation();

    Set<String> getPropertyKeys();

    String getProperty(String paramString);

    IndexStatus getIndexStatus();

    Document toOSIS();
}
