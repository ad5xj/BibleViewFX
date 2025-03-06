package org.crosswire.jsword.index.lucene.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.study.StrongsNumber;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

public class StrongsNumberFilter extends AbstractBookTokenFilter {
  
  private static final Logger lgr = LoggerFactory.getLogger(StrongsNumberFilter.class);

  private TermAttribute termAtt;
  
  private StrongsNumber number;
  
  public StrongsNumberFilter(TokenStream in) {
    this((Book)null, in);
  }
  
  public StrongsNumberFilter(Book book, TokenStream in) {
    super(book, in);
    this.termAtt = (TermAttribute)addAttribute(TermAttribute.class);
  }
  
  public boolean incrementToken() throws IOException {
    if (this.number == null) {
      while (this.input.incrementToken()) {
        String tokenText = this.termAtt.term();
        this.number = new StrongsNumber(tokenText);
        if (!this.number.isValid()) {
          lgr.warn(JSMsg.gettext("Not a valid Strong's Number \"{0}\"", new Object[] { tokenText }));
          continue;
        } 
        String s = this.number.getStrongsNumber();
        this.termAtt.setTermBuffer(s);
        if (!this.number.isPart())
          this.number = null; 
        return true;
      } 
      return false;
    } 
    this.termAtt.setTermBuffer(this.number.getFullStrongsNumber());
    this.number = null;
    return true;
  }
  
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
  
  public int hashCode() {
    return super.hashCode();
  }
}