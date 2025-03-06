package org.crosswire.jsword.book.filter.gbf;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

import java.util.LinkedList;

import org.jdom2.Content;

public interface Tag {
  void updateOsisStack(Book paramBook, Key paramKey, LinkedList<Content> paramLinkedList);
}
