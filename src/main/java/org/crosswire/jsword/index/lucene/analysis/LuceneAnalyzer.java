package org.crosswire.jsword.index.lucene.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.index.lucene.InstalledIndex;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;

public class LuceneAnalyzer extends Analyzer {
  
  private static final Logger lgr = LoggerFactory.getLogger(LuceneAnalyzer.class);

  private PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper((Analyzer)new SimpleAnalyzer());
  
  public LuceneAnalyzer(Book book) {
    if (InstalledIndex.instance().getInstalledIndexDefaultVersion() > 1.1F) {
      Analyzer myNaturalLanguageAnalyzer = AnalyzerFactory.getInstance().createAnalyzer(book);
      this.analyzer.addAnalyzer("content", myNaturalLanguageAnalyzer);
      lgr.debug("{}: Using languageAnalyzer: {}", book.getBookMetaData().getInitials(), myNaturalLanguageAnalyzer.getClass().getName());
    } 
    this.analyzer.addAnalyzer("key", new KeyAnalyzer());
    this.analyzer.addAnalyzer("strong", new StrongsNumberAnalyzer());
    this.analyzer.addAnalyzer("morph", new MorphologyAnalyzer());
    this.analyzer.addAnalyzer("xref", new XRefAnalyzer());
  }
  
  public TokenStream tokenStream(String fieldName, Reader reader) {
    return this.analyzer.tokenStream(fieldName, reader);
  }
}