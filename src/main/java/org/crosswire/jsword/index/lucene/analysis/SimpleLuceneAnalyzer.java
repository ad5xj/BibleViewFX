package org.crosswire.jsword.index.lucene.analysis;

import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;

public class SimpleLuceneAnalyzer extends AbstractBookAnalyzer {
    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        LowerCaseTokenizer lowerCaseTokenizer = new LowerCaseTokenizer(reader);
        return (TokenStream) new ASCIIFoldingFilter((TokenStream) lowerCaseTokenizer);
    }
}
