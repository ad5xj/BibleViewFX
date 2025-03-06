package org.crosswire.jsword.passage;

import java.util.EventListener;

public interface PassageListener extends EventListener {
  void versesAdded(PassageEvent paramPassageEvent);
  
  void versesRemoved(PassageEvent paramPassageEvent);
  
  void versesChanged(PassageEvent paramPassageEvent);
}
