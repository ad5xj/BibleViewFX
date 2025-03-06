package org.crosswire.jsword.book.sword;

import org.crosswire.common.icu.DateFormatter;

import org.crosswire.jsword.passage.DefaultLeafKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.PreferredKey;

import java.util.Date;

public class SwordDailyDevotion extends SwordDictionary implements PreferredKey {
  public SwordDailyDevotion(SwordBookMetaData sbmd, Backend backend) {
    super(sbmd, backend);
  }
  
  public Key getPreferred() {
    return (Key)new DefaultLeafKeyList(DateFormatter.getDateInstance().format(new Date()));
  }
}
