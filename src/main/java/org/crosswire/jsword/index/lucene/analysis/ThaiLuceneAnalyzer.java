package org.crosswire.jsword.index.lucene.analysis;

import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.th.ThaiWordFilter;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;

public class ThaiLuceneAnalyzer extends AbstractBookAnalyzer {

    private final Version matchVersion = Version.LUCENE_29;

    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        StopFilter stopFilter = null;
        StandardTokenizer standardTokenizer = new StandardTokenizer(this.matchVersion, reader);
        ThaiWordFilter thaiWordFilter = new ThaiWordFilter((TokenStream) standardTokenizer);
    
        if ( doStopWords && (stopSet != null) )
        {
            stopFilter = new StopFilter(false, (TokenStream) thaiWordFilter, stopSet);
        }
        return (TokenStream) stopFilter;
    }

    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException
    {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();
    
        if (streams == null)
        {
            streams = new SavedStreams((Tokenizer) new StandardTokenizer(this.matchVersion, reader));
            streams.setResult((TokenStream) new ThaiWordFilter(streams.getResult()));
            if ( doStopWords && (stopSet != null) )
            {
                streams.setResult((TokenStream) new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(this.matchVersion), streams.getResult(), this.stopSet));
            }
            setPreviousTokenStream(streams);
        }
        else
        {
            streams.getSource().reset(reader);
            streams.getResult().reset();
        }
        return streams.getResult();
    }
}