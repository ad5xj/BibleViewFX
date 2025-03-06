package org.crosswire.jsword.versification;

import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.internationalisation.LocaleProviderManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class BibleNames {
    private static final BibleNames instance = new BibleNames();

    private static NameList englishBibleNames;

    private transient Map<Locale, NameList> localizedBibleNames;


    public static BibleNames instance() {
        return instance;
    }

    public BookName getBookName(BibleBook book) {
        return getLocalizedBibleNames().getBookName(book);
    }

    public String getPreferredName(BibleBook book) {
        return getLocalizedBibleNames().getPreferredName(book);
    }

    public String getLongName(BibleBook book) {
        return getLocalizedBibleNames().getLongName(book);
    }

    public String getShortName(BibleBook book) {
        return getLocalizedBibleNames().getShortName(book);
    }

    public BibleBook getBook(String find) {
        BibleBook book = null;
        if (containsLetter(find)) {
            book = BibleBook.fromOSIS(find);
            if (book == null) {
                book = getLocalizedBibleNames().getBook(find, false);
            }
            if (book == null) {
                book = englishBibleNames.getBook(find, false);
            }
            if (book == null) {
                book = getLocalizedBibleNames().getBook(find, true);
            }
            if (book == null) {
                book = englishBibleNames.getBook(find, true);
            }
        }
        return book;
    }

    public boolean isBook(String find) {
        return (getBook(find) != null);
    }

    void load(Locale locale) {
        NameList bibleNames = new NameList(locale);
        if (this.localizedBibleNames.get(locale) == null) {
            this.localizedBibleNames.put(locale, bibleNames);
        }
    }

    private BibleNames() {
        this.localizedBibleNames = new HashMap<Locale, NameList>();
        englishBibleNames = getBibleNamesForLocale(Locale.ENGLISH);
        this.localizedBibleNames.put(Locale.ENGLISH, englishBibleNames);
    }

    private NameList getLocalizedBibleNames() {
        return getBibleNamesForLocale(LocaleProviderManager.getLocale());
    }

    private NameList getBibleNamesForLocale(Locale locale) {
        NameList bibleNames = this.localizedBibleNames.get(locale);
        if (bibleNames == null) {
            bibleNames = new NameList(locale);
            this.localizedBibleNames.put(locale, bibleNames);
        }
        return bibleNames;
    }

    private static boolean containsLetter(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isLetter(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private class NameList {
        private static final String FULL_KEY = ".Full";
        private static final String SHORT_KEY = ".Short";
        private static final String ALT_KEY = ".Alt";

        private Locale locale;

        private LinkedHashMap<BibleBook, BookName> books;

        private Map<String, BookName> fullNT;
        private Map<String, BookName> fullOT;
        private Map<String, BookName> fullNC;
        private Map<String, BookName> shortNT;
        private Map<String, BookName> shortOT;
        private Map<String, BookName> shortNC;
        private Map<String, BookName> altNT;
        private Map<String, BookName> altOT;
        private Map<String, BookName> altNC;

        NameList(Locale locale) {
            this.locale = locale;
            initialize();
        }

        BookName getBookName(BibleBook book) {
            return this.books.get(book);
        }

        String getPreferredName(BibleBook book) {
            return getBookName(book).getPreferredName();
        }

        String getLongName(BibleBook book) {
            return getBookName(book).getLongName();
        }

        String getShortName(BibleBook book) {
            return getBookName(book).getShortName();
        }

        BibleBook getBook(String find, boolean fuzzy) {
            String match = BookName.normalize(find, this.locale);
            BookName bookName = this.fullNT.get(match);
            if (bookName != null) {
                return bookName.getBook();
            }
            bookName = this.shortNT.get(match);
            if (bookName != null) {
                return bookName.getBook();
            }
            bookName = this.altNT.get(match);
            if (bookName != null) {
                return bookName.getBook();
            }
            bookName = this.fullOT.get(match);
            if (bookName != null) {
                return bookName.getBook();
            }
            bookName = this.shortOT.get(match);
            if (bookName != null) {
                return bookName.getBook();
            }
            bookName = this.altOT.get(match);
            if (bookName != null) {
                return bookName.getBook();
            }
            bookName = this.fullNC.get(match);
            if (bookName != null) {
                return bookName.getBook();
            }
            bookName = this.shortNC.get(match);
            if (bookName != null) {
                return bookName.getBook();
            }
            bookName = this.altNC.get(match);
            if (bookName != null) {
                return bookName.getBook();
            }
            if (!fuzzy) {
                return null;
            }
            for (BookName aBook : this.books.values()) {
                if (aBook.match(match)) {
                    return aBook.getBook();
                }
            }
            return null;
        }

        private void initialize() {
            int ntCount = 0;
            int otCount = 0;
            int ncCount = 0;
            BibleBook[] bibleBooks = BibleBook.values();
            for (BibleBook book : bibleBooks) {
                int ordinal = book.ordinal();
                if (ordinal > BibleBook.INTRO_OT.ordinal() && ordinal < BibleBook.INTRO_NT.ordinal()) {
                    ntCount++;
                } else if (ordinal > BibleBook.INTRO_NT.ordinal() && ordinal <= BibleBook.REV.ordinal()) {
                    otCount++;
                } else {
                    ncCount++;
                }
            }
            this.books = new LinkedHashMap<BibleBook, BookName>(ntCount + otCount + ncCount);
            String className = BibleNames.class.getName();
            String shortClassName = ClassUtil.getShortClassName(className);
            ResourceBundle resources = ResourceBundle.getBundle(shortClassName, this.locale, (ClassLoader) CWClassLoader.instance(BibleNames.class));
            this.fullNT = new HashMap<String, BookName>(ntCount);
            this.shortNT = new HashMap<String, BookName>(ntCount);
            this.altNT = new HashMap<String, BookName>(ntCount);
            int i;
            for (i = BibleBook.MATT.ordinal(); i <= BibleBook.REV.ordinal(); i++) {
                BibleBook book = bibleBooks[i];
                store(resources, book, this.fullNT, this.shortNT, this.altNT);
            }
            this.fullOT = new HashMap<String, BookName>(otCount);
            this.shortOT = new HashMap<String, BookName>(otCount);
            this.altOT = new HashMap<String, BookName>(otCount);
            for (i = BibleBook.GEN.ordinal(); i <= BibleBook.MAL.ordinal(); i++) {
                BibleBook book = bibleBooks[i];
                store(resources, book, this.fullOT, this.shortOT, this.altOT);
            }
            this.fullNC = new HashMap<String, BookName>(ncCount);
            this.shortNC = new HashMap<String, BookName>(ncCount);
            this.altNC = new HashMap<String, BookName>(ncCount);
            store(resources, BibleBook.INTRO_BIBLE, this.fullNC, this.shortNC, this.altNC);
            store(resources, BibleBook.INTRO_OT, this.fullNC, this.shortNC, this.altNC);
            store(resources, BibleBook.INTRO_NT, this.fullNC, this.shortNC, this.altNC);
            for (i = BibleBook.REV.ordinal() + 1; i < bibleBooks.length; i++) {
                BibleBook book = bibleBooks[i];
                store(resources, book, this.fullNC, this.shortNC, this.altNC);
            }
        }

        private void store(ResourceBundle resources, BibleBook book, Map<String, BookName> fullMap, Map<String, BookName> shortMap, Map<String, BookName> altMap) {
            String osisName = book.getOSIS();
            String fullBook = getString(resources, osisName + ".Full");
            String shortBook = getString(resources, osisName + ".Short");
            if (shortBook.length() == 0) {
                shortBook = fullBook;
            }
            String altBook = getString(resources, osisName + ".Alt");
            BookName bookName = new BookName(this.locale, BibleBook.fromOSIS(osisName), fullBook, shortBook, altBook);
            this.books.put(book, bookName);
            fullMap.put(bookName.getNormalizedLongName(), bookName);
            shortMap.put(bookName.getNormalizedShortName(), bookName);
            String[] alternates = StringUtil.split(BookName.normalize(altBook, this.locale), ',');
            for (int j = 0; j < alternates.length; j++) {
                altMap.put(alternates[j], bookName);
            }
        }

        private String getString(ResourceBundle resources, String key) {
            try {
                return resources.getString(key);
            } catch (MissingResourceException e) {
                assert false;
                return null;
            }
        }
    }
}
