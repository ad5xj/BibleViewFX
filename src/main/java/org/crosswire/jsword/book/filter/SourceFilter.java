package org.crosswire.jsword.book.filter;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

import java.util.List;

import org.jdom2.Content;

public interface SourceFilter extends Cloneable {
  List<Content> toOSIS(Book paramBook, Key paramKey, String paramString);
  
  SourceFilter clone();
}
