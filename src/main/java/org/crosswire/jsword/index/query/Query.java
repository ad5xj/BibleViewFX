package org.crosswire.jsword.index.query;

import org.crosswire.jsword.book.BookException;

import org.crosswire.jsword.index.Index;

import org.crosswire.jsword.passage.Key;

public interface Query {
    Key find(Index paramIndex) throws BookException;
}