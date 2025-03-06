package org.crosswire.jsword.index.lucene.analysis;

import java.util.Set;

import org.apache.lucene.analysis.Analyzer;

import org.crosswire.jsword.book.Book;

public abstract class AbstractBookAnalyzer extends Analyzer 
{
  protected Book book;
  
  protected Set<?> stopSet;
  
  protected boolean doStopWords;
  
  protected boolean doStemming;
  
  public AbstractBookAnalyzer() { this(null); }
  
  public AbstractBookAnalyzer(Book book) 
  {
    this.book = book;
    this.doStopWords = false;
    this.doStemming = true;
  }
  
  public void setBook(Book newBook)           { book = newBook; }
  public void setDoStopWords(boolean doIt)    { doStopWords = doIt; }
  public void setStopWords(Set<?> stopWords)  { stopSet = stopWords; }
  public void setDoStemming(boolean stemming) { doStemming = stemming; }

  public Book getBook()                        { return this.book; }
  public boolean getDoStopWords()             { return this.doStopWords; }
}