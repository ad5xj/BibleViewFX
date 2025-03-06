package org.crosswire.jsword.book;

import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.index.IndexStatusListener;
import org.crosswire.jsword.index.search.SearchRequest;

import org.crosswire.common.activate.Activatable;
import org.crosswire.common.util.Language;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;

import org.jdom2.Content;
import org.jdom2.Document;

import java.util.Iterator;
import java.util.Set;

/**
 * @ingroup org.crosswire.jsword
 * @brief Interface to Book data structure
 * 
 * @extends Activatable
 * @extends Comparable<Book>
 * 
 * @author ken
 */
public interface Book extends Activatable, Comparable<Book> 
{

    void setBookMetaData(BookMetaData paramBookMetaData);
    void setRawText(Key paramKey, String paramString) throws BookException;
    void setAliasKey(Key paramKey1, Key paramKey2) throws BookException;
    void setIndexStatus(IndexStatus paramIndexStatus);

    void putProperty(String paramString1, String paramString2);
    void putProperty(String paramString1, String paramString2, boolean paramBoolean);


    void addIndexStatusListener(IndexStatusListener paramIndexStatusListener);
    void removeIndexStatusListener(IndexStatusListener paramIndexStatusListener);


    boolean contains(Key paramKey);
    boolean isWritable();
    boolean isSupported();
    boolean isEnciphered();
    boolean isLocked();
    boolean isQuestionable();
    boolean isLeftToRight();
    boolean hasFeature(FeatureType paramFeatureType);
    boolean match(String paramString);
    boolean unlock(String paramString);

    String getRawText(Key paramKey) throws BookException;
    String getName();
    String getAbbreviation();
    String getInitials();
    String getUnlockKey();
    String getDriverName();
    String getProperty(String paramString);

    String getOsisID();

    Set<String> getPropertyKeys();

    Key getGlobalKeyList();
    Key getScope();
    Key getValidKey(String paramString);
    Key getKey(String paramString) throws NoSuchKeyException;
    Key find(SearchRequest paramSearchRequest) throws BookException;
    Key find(String paramString) throws BookException;
    Key createEmptyKeyList();

    BookMetaData getBookMetaData();
    BookCategory getBookCategory();
    BookDriver getDriver();
    Iterator<Content> getOsisIterator(Key paramKey, boolean paramBoolean1, boolean paramBoolean2) throws BookException;
    Language getLanguage();
    IndexStatus getIndexStatus();
    Document toOSIS();
} // end interface