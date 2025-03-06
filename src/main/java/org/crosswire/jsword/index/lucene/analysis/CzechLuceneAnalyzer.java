package org.crosswire.jsword.index.lucene.analysis;

import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;

public class CzechLuceneAnalyzer extends AbstractBookAnalyzer {
    private final Version matchVersion = Version.LUCENE_29;

    public final TokenStream tokenStream(String fieldName, Reader reader)
    {
        StopFilter stopFilter = null;
        LowerCaseTokenizer lowerCaseTokenizer = new LowerCaseTokenizer(reader);

        if (this.doStopWords && this.stopSet != null)
        {
            stopFilter = new StopFilter(false, (TokenStream) lowerCaseTokenizer, this.stopSet);
        }
        return (TokenStream) stopFilter;
    }

    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException
    {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();
        if (streams == null)
        {
            streams = new SavedStreams((Tokenizer) new LowerCaseTokenizer(reader));
            if ( doStopWords && (stopSet != null) )
            {
                streams.setResult((TokenStream) new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(this.matchVersion), streams.getResult(), this.stopSet));
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