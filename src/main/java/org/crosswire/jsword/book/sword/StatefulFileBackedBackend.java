package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.book.sword.state.OpenFileState;

import java.io.IOException;

public interface StatefulFileBackedBackend<T extends OpenFileState> {
  T initState() throws BookException;
  
  String readRawContent(T paramT, Key paramKey) throws BookException, IOException;
  
  void setRawText(T paramT, Key paramKey, String paramString) throws BookException, IOException;
  
  void setAliasKey(T paramT, Key paramKey1, Key paramKey2) throws IOException;
}
