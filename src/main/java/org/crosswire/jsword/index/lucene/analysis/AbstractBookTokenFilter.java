package org.crosswire.jsword.index.lucene.analysis;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.crosswire.jsword.book.Book;

public abstract class AbstractBookTokenFilter extends TokenFilter {

    private Book book;

    public AbstractBookTokenFilter(TokenStream input) { this(null, input); }

    public AbstractBookTokenFilter(Book book, TokenStream input)
    {
        super(input);
        this.book = book;
    }

    public void setBook(Book book) { this.book = book; }

    public boolean equals(Object obj) { return super.equals(obj); }

    public int hashCode()    { return super.hashCode(); }

    public Book getBook()    { return this.book; }
}
