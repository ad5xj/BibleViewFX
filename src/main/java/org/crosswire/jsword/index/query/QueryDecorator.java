package org.crosswire.jsword.index.query;

public interface QueryDecorator {
  String decoratePhrase(String paramString);
  String decorateAllWords(String paramString);
  String decorateAnyWords(String paramString);
  String decorateNotWords(String paramString);
  String decorateStartWords(String paramString);
  String decorateSpellWords(String paramString);
  String decorateRange(String paramString);
}
