package org.crosswire.jsword.versification;

import org.crosswire.common.util.StringUtil;

import org.crosswire.jsword.book.CaseType;

import java.util.Locale;
import java.util.regex.Pattern;

public final class BookName {
    private static boolean fullBookName = true;
    private static Pattern normPattern = Pattern.compile("[. ]");
    private static CaseType bookCase = CaseType.SENTENCE;


    private String longName;
    private String normalizedLongName;
    private String shortName;
    private String normalizedShortName;
    private String[] alternateNames;

    private Locale locale;

    private BibleBook book;

    public BookName(Locale locale, BibleBook book, String longName, String shortName, String alternateNames) {
        this.locale = locale;
        this.book = book;
        this.longName = longName;
        this.normalizedLongName = normalize(longName, locale);
        this.shortName = shortName;
        this.normalizedShortName = normalize(shortName, locale);
        if (alternateNames != null) {
            this.alternateNames = StringUtil.split(normalize(alternateNames, locale), ',');
        }
    }

    public BibleBook getBook() {
        return this.book;
    }

    public String getPreferredName() {
        if (isFullBookName()) {
            return getLongName();
        }
        return getShortName();
    }

    public String getLongName() {
        CaseType caseType = getDefaultCase();
        if (caseType == CaseType.LOWER) {
            return this.longName.toLowerCase(this.locale);
        }
        if (caseType == CaseType.UPPER) {
            return this.longName.toUpperCase(this.locale);
        }
        return this.longName;
    }

    public String getShortName() {
        CaseType caseType = getDefaultCase();
        if (caseType == CaseType.LOWER) {
            return this.shortName.toLowerCase(this.locale);
        }
        if (caseType == CaseType.UPPER) {
            return this.shortName.toUpperCase(this.locale);
        }
        return this.shortName;
    }

    public String getNormalizedLongName() {
        return this.normalizedLongName;
    }

    public String getNormalizedShortName() {
        return this.normalizedShortName;
    }

    public boolean match(String normalizedName) {
        for (int j = 0; j < this.alternateNames.length; j++) {
            String targetBookName = this.alternateNames[j];
            if (targetBookName.startsWith(normalizedName) || normalizedName.startsWith(targetBookName)) {
                return true;
            }
        }
        if (this.normalizedLongName.startsWith(normalizedName)) {
            return true;
        }
        if (this.normalizedShortName.startsWith(normalizedName) || (this.normalizedShortName.length() > 0 && normalizedName.startsWith(this.normalizedShortName))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.book.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BookName other = (BookName) obj;
        return (this.book == other.book);
    }

    public String toString() {
        return getPreferredName();
    }

    public static String normalize(String str, Locale locale) {
        return normPattern.matcher(str).replaceAll("").toLowerCase(locale);
    }

    public static void setCase(int bookCase) {
        BookName.bookCase = CaseType.fromInteger(bookCase);
    }

    public static void setCase(CaseType newBookCase) {
        bookCase = newBookCase;
    }

    public static int getCase() {
        return bookCase.toInteger();
    }

    public static boolean isFullBookName() {
        return fullBookName;
    }

    public static void setFullBookName(boolean fullName) {
        fullBookName = fullName;
    }

    public static CaseType getDefaultCase() {
        return bookCase;
    }
}
