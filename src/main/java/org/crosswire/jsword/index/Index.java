package org.crosswire.jsword.index;

import org.crosswire.jsword.book.BookException;

import org.crosswire.jsword.index.search.SearchModifier;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;

public interface Index
{

    void setSearchModifier(SearchModifier paramSearchModifier);
    void close();

    Key find(String paramString) throws BookException;
    Key getKey(String paramString) throws NoSuchKeyException;

    SearchModifier getSearchModifier();
}