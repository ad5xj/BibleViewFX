package org.crosswire.jsword.book;
/** @ingroup org.crosswire.jsword.book */

import org.crosswire.jsword.internationalisation.LocaleProviderManager;

public enum CaseType 
{
    LOWER 
    {
        public String setCase(String word) 
        {
            return word.toLowerCase(LocaleProviderManager.getLocale());
        }
    },
    SENTENCE 
    {
        public String setCase(String word) 
        {
            int index = word.indexOf('-');
            if (index == -1) { return toSentenceCase(word); }
            if ("maher-shalal-hash-baz".equalsIgnoreCase(word)) { return "Maher-Shalal-Hash-Baz"; }
            if ("no-one".equalsIgnoreCase(word)) { return "No-one"; }
            if ("god-".equalsIgnoreCase(word.substring(0, 4))) { return toSentenceCase(word); }
            return toSentenceCase(word.substring(0, index)) + "-" + toSentenceCase(word.substring(index + 1));
        }
    },
    UPPER 
    {
        public String setCase(String word) 
        {
            return word.toUpperCase(LocaleProviderManager.getLocale());
        }
    };

    public static String toSentenceCase(String word) 
    {
        assert word != null;
        if (word.length() == 0) { return ""; }
        return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase(LocaleProviderManager.getLocale());
    }

    public static CaseType getCase(String word) 
    {
        assert word != null;
        if (word.length() == 0) { return LOWER; }
        if (word.equals(word.toLowerCase(LocaleProviderManager.getLocale()))) { return LOWER; }
        if (word.equals(word.toUpperCase(LocaleProviderManager.getLocale())) && word.length() != 1) 
        {
            return UPPER;
        }
        return SENTENCE;
    }

    public static CaseType fromString(String name) 
    {
        for (CaseType v : values()) 
        {
            if (v.name().equalsIgnoreCase(name)) { return v; }
        }
        assert false;
        return null;
    }

    public static CaseType fromInteger(int i) 
    {
        for (CaseType v : values()) 
        {
            if (v.ordinal() == i) { return v; }
        }
        assert false;
        return null;
    }

    public int toInteger() { return ordinal(); }

    public abstract String setCase(String paramString);
}
