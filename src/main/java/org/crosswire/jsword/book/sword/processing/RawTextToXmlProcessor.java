package org.crosswire.jsword.book.sword.processing;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.VerseRange;

import java.util.List;

import org.jdom2.Content;

public interface RawTextToXmlProcessor {
  void init(List<Content> paramList);
  
  void preRange(VerseRange paramVerseRange, List<Content> paramList);
  
  void postVerse(Key paramKey, List<Content> paramList, String paramString);
}
