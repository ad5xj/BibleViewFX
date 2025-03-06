package org.crosswire.jsword.index.lucene.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

class SavedStreams {

    private Tokenizer source;
    private TokenStream result;

    SavedStreams(Tokenizer source)
    {
        this.source = source;
        this.result = (TokenStream) source;
    }

    public void setResult(TokenStream result) { this.result = result; }

    public Tokenizer getSource()   { return this.source; }

    public TokenStream getResult() { return this.result; }
}