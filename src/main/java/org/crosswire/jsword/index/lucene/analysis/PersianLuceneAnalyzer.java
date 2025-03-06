package org.crosswire.jsword.index.lucene.analysis;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ar.ArabicLetterTokenizer;
import org.apache.lucene.analysis.ar.ArabicNormalizationFilter;
import org.apache.lucene.analysis.fa.PersianNormalizationFilter;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;

public class PersianLuceneAnalyzer extends AbstractBookAnalyzer {
  
  private final Version matchVersion = Version.LUCENE_29;

  public final TokenStream tokenStream(String fieldName, Reader reader) {
    StopFilter stopFilter = null;
    ArabicLetterTokenizer arabicLetterTokenizer = new ArabicLetterTokenizer(reader);
    LowerCaseFilter lowerCaseFilter = new LowerCaseFilter((TokenStream)arabicLetterTokenizer);
    ArabicNormalizationFilter arabicNormalizationFilter = new ArabicNormalizationFilter((TokenStream)lowerCaseFilter);
    PersianNormalizationFilter persianNormalizationFilter = new PersianNormalizationFilter((TokenStream)arabicNormalizationFilter);

    if (this.doStopWords && this.stopSet != null)
    {
      stopFilter = new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(this.matchVersion), (TokenStream)persianNormalizationFilter, this.stopSet); 
    }
    return (TokenStream)stopFilter;
  }
  
  public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
    SavedStreams streams = (SavedStreams)getPreviousTokenStream();
    if (streams == null) {
      streams = new SavedStreams((Tokenizer)new ArabicLetterTokenizer(reader));
      streams.setResult((TokenStream)new LowerCaseFilter(streams.getResult()));
      streams.setResult((TokenStream)new ArabicNormalizationFilter(streams.getResult()));
      streams.setResult((TokenStream)new PersianNormalizationFilter(streams.getResult()));
      if (this.doStopWords && this.stopSet != null)
        streams.setResult((TokenStream)new StopFilter(false, streams.getResult(), this.stopSet)); 
      setPreviousTokenStream(streams);
    } else {
      streams.getSource().reset(reader);
    } 
    return streams.getResult();
  }
}
