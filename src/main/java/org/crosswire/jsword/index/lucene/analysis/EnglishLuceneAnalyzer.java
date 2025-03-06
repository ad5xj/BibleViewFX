package org.crosswire.jsword.index.lucene.analysis;

import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;

public class EnglishLuceneAnalyzer extends AbstractBookAnalyzer {
  
  private final Version matchVersion = Version.LUCENE_29;

  public final TokenStream tokenStream(String fieldName, Reader reader) {
    StopFilter stopFilter = null;
    PorterStemFilter porterStemFilter = null;

    LowerCaseTokenizer lowerCaseTokenizer = new LowerCaseTokenizer(reader);
    if (this.doStopWords && this.stopSet != null)
    {
      stopFilter = new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(this.matchVersion), (TokenStream)lowerCaseTokenizer, this.stopSet); 
    }

    if (this.doStemming)
    {
      porterStemFilter = new PorterStemFilter((TokenStream)stopFilter); 
    }
    return (TokenStream)porterStemFilter;
  }
  
  public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
    SavedStreams streams = (SavedStreams)getPreviousTokenStream();
    if (streams == null) 
    {
      streams = new SavedStreams((Tokenizer)new LowerCaseTokenizer(reader));

      if (this.doStopWords && this.stopSet != null)
      {
        streams.setResult((TokenStream)new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(this.matchVersion), streams.getResult(), this.stopSet)); 
      }

      if (this.doStemming)
      {
        streams.setResult((TokenStream)new PorterStemFilter(streams.getResult())); 
      }
      setPreviousTokenStream(streams);
    } 
    else 
    {
      streams.getSource().reset(reader);
    } 
    return streams.getResult();
  }
}