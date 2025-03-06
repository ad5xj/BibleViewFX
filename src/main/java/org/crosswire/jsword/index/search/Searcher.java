package org.crosswire.jsword.index.search;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.index.Index;
import org.crosswire.jsword.index.query.Query;
import org.crosswire.jsword.passage.Key;

public interface Searcher {
  void init(Index paramIndex);
  
  Key search(SearchRequest paramSearchRequest) throws BookException;
  
  Key search(Query paramQuery) throws BookException;
}
