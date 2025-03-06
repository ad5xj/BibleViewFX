package org.crosswire.jsword.index.lucene.analysis;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.crosswire.jsword.book.Book;

public class XRefAnalyzer extends AbstractBookAnalyzer {

    public XRefAnalyzer()    {    }

    public XRefAnalyzer(Book book) { setBook(book); }

    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return (TokenStream) new KeyFilter(getBook(), (TokenStream) new WhitespaceTokenizer(reader));
    }

    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException
    {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();
        if (streams == null)
        {
            streams = new SavedStreams((Tokenizer) new WhitespaceTokenizer(reader));
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