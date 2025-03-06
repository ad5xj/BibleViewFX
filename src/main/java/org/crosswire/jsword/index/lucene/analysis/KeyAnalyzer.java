package org.crosswire.jsword.index.lucene.analysis;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.crosswire.jsword.book.Book;

public class KeyAnalyzer extends AbstractBookAnalyzer {
    
    public KeyAnalyzer()           {    }

    public KeyAnalyzer(Book book)  { setBook(book); }

    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return (TokenStream) new KeyFilter(getBook(), (TokenStream) new KeywordTokenizer(reader));
    }

    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException
    {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();
        if (streams == null)
        {
            streams = new SavedStreams((Tokenizer) new KeywordTokenizer(reader));
            streams.setResult((TokenStream) new KeyFilter(getBook(), streams.getResult()));
            setPreviousTokenStream(streams);
        }
        else
        {
            streams.getSource().reset(reader);
        }
        return streams.getResult();
    }
}