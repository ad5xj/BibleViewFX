package org.crosswire.jsword.index.lucene.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;

public class ChineseLuceneAnalyzer extends AbstractBookAnalyzer {

    private ChineseAnalyzer myAnalyzer = new ChineseAnalyzer();

    public final TokenStream tokenStream(String fieldName, Reader reader)
    {
        return this.myAnalyzer.tokenStream(fieldName, reader);
    }

    public final TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException
    {
        return this.myAnalyzer.reusableTokenStream(fieldName, reader);
    }
}
