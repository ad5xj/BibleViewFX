package org.crosswire.jsword.index.lucene.analysis;

import org.crosswire.jsword.book.Book;

import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;

public class XRefFilter extends AbstractBookTokenFilter {
    public XRefFilter(TokenStream in)  { this(null, in); }

    public XRefFilter(Book book, TokenStream in)  { super(book, in); }

    public boolean incrementToken() throws IOException { return this.input.incrementToken(); }
}
