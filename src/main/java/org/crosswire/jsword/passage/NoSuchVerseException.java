package org.crosswire.jsword.passage;


public class NoSuchVerseException extends NoSuchKeyException {
  private static final long serialVersionUID = 3257572797638129463L;
  
  public NoSuchVerseException(String msg) {
    super(msg);
  }
  
  public NoSuchVerseException(String msg, Throwable ex) {
    super(msg, ex);
  }
}