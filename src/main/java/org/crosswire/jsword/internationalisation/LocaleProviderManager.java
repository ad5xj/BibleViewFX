package org.crosswire.jsword.internationalisation;

import java.util.Locale;

public final class LocaleProviderManager {
  public static LocaleProvider getLocaleProvider() {
    return localeProvider;
  }
  
  public static Locale getLocale() {
    return localeProvider.getUserLocale();
  }
  
  public static void setLocaleProvider(LocaleProvider provider) {
    localeProvider = provider;
  }
  
  private static LocaleProvider localeProvider = new DefaultLocaleProvider();
}