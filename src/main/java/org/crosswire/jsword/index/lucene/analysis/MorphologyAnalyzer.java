package org.crosswire.jsword.index.lucene.analysis;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;

import java.io.Reader;

public class MorphologyAnalyzer extends AbstractBookAnalyzer {
    
  public TokenStream tokenStream(String fieldName, Reader reader) {
      TokenStream ts = (new WhitespaceAnalyzer()).tokenStream(fieldName, reader);
      return (TokenStream) new LowerCaseFilter(ts);
  }
}