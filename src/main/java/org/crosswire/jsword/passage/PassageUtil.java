package org.crosswire.jsword.passage;

public final class PassageUtil {
  public static void setPersistentNaming(boolean persistentNaming) {
    PassageUtil.persistentNaming = persistentNaming;
  }
  
  public static boolean isPersistentNaming() {
    return persistentNaming;
  }
  
  public static boolean getDefaultPersistentNaming() {
    return false;
  }
  
  private static boolean persistentNaming = getDefaultPersistentNaming();
}