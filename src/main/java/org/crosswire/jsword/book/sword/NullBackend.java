package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;

import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;

import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class NullBackend implements Backend {
  public SwordBookMetaData getBookMetaData() {
    return null;
  }
  
  public void decipher(byte[] data) {}
  
  public void encipher(byte[] data) {}
  
  public Key readIndex() {
    return (Key)new DefaultKeyList();
  }
  
  public boolean contains(Key key) {
    return false;
  }
  
  public String getRawText(Key key) throws BookException {
    return "";
  }
  
  public void setAliasKey(Key alias, Key source) throws BookException {}
  
  public int getRawTextLength(Key key) {
    return 0;
  }
  
  public Key getGlobalKeyList() throws BookException {
    return (Key)new DefaultKeyList();
  }
  
  public List readToOsis(Key key, RawTextToXmlProcessor processor) throws BookException {
    return new ArrayList();
  }
  
  public void create() throws IOException, BookException {}
  
  public boolean isSupported() {
    return true;
  }
  
  public boolean isWritable() {
    return false;
  }
}