package org.crosswire.jsword.index;

import java.util.EventListener;

public interface IndexStatusListener extends EventListener 
{
  void statusChanged(IndexStatusEvent paramIndexStatusEvent);
}
