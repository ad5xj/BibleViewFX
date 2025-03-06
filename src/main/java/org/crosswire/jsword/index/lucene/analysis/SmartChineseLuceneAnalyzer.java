package org.crosswire.jsword.index.lucene.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;

public class SmartChineseLuceneAnalyzer extends AbstractBookAnalyzer {
  private SmartChineseAnalyzer myAnalyzer = new SmartChineseAnalyzer(Version.LUCENE_29);
  
  public final TokenStream tokenStream(String fieldName, Reader reader) {
    return this.myAnalyzer.tokenStream(fieldName, reader);
  }
  
  public final TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
    return this.myAnalyzer.reusableTokenStream(fieldName, reader);
  }
}
