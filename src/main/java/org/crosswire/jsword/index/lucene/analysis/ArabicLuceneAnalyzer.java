package org.crosswire.jsword.index.lucene.analysis;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ar.ArabicLetterTokenizer;
import org.apache.lucene.analysis.ar.ArabicNormalizationFilter;
import org.apache.lucene.analysis.ar.ArabicStemFilter;
import org.apache.lucene.util.Version;

public class ArabicLuceneAnalyzer extends AbstractBookAnalyzer {
    private final Version matchVersion = Version.LUCENE_29;

    public final TokenStream tokenStream(String fieldName, Reader reader)
    {
        StopFilter stopFilter = null;
        ArabicStemFilter arabicStemFilter = null;
        ArabicLetterTokenizer arabicLetterTokenizer = new ArabicLetterTokenizer(reader);
        LowerCaseFilter lowerCaseFilter = new LowerCaseFilter((TokenStream) arabicLetterTokenizer);
        ArabicNormalizationFilter arabicNormalizationFilter = new ArabicNormalizationFilter((TokenStream) lowerCaseFilter);

        if (this.doStopWords && this.stopSet != null)
        {
            stopFilter = new StopFilter(false, (TokenStream) arabicNormalizationFilter, this.stopSet);
        }

        if (this.doStemming)
        {
            arabicStemFilter = new ArabicStemFilter((TokenStream) stopFilter);
        }

        return (TokenStream) arabicStemFilter;
    }

    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException
    {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();

        if (streams == null)
        {
            streams = new SavedStreams((Tokenizer) new ArabicLetterTokenizer(reader));
            streams.setResult((TokenStream) new LowerCaseFilter(streams.getResult()));
            streams.setResult((TokenStream) new ArabicNormalizationFilter(streams.getResult()));
            if (this.doStopWords && this.stopSet != null)
            {
                streams.setResult((TokenStream) new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(this.matchVersion), streams.getResult(), this.stopSet));
            }
            if (this.doStemming)
            {
                streams.setResult((TokenStream) new ArabicStemFilter(streams.getResult()));
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