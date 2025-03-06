package org.crosswire.jsword.passage;

import org.crosswire.common.icu.NumberShaper;

import org.crosswire.jsword.JSMsg;

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

public enum AccuracyType {
    BOOK_VERSE {
        public boolean isVerse() {
            return true;
        }

        @Override
        public Verse createStartVerse(Versification v11n, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException {
            BibleBook book = v11n.getBook(parts[0]);
            int chapter = 1;
            int verse = 1;
            String subIdentifier = getSubIdentifier(parts);
            boolean hasSub = (subIdentifier != null);
            if ((hasSub && parts.length == 4) || (!hasSub && parts.length == 3)) {
                chapter = getChapter(v11n, book, parts[1]);
                verse = getVerse(v11n, book, chapter, parts[2]);
            } else {
                verse = getVerse(v11n, book, chapter, parts[1]);
            }
            return new Verse(v11n, book, chapter, verse, subIdentifier);
        }

        @Override
        public Verse createEndVerse(Versification v11n, Verse verseBasis, String[] endParts) throws NoSuchVerseException {
            return createStartVerse(v11n, (VerseRange) null, endParts);
        }
    },
    BOOK_CHAPTER {
        public boolean isChapter() {
            return true;
        }

        @Override
        public Verse createStartVerse(Versification v11n, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException {
            BibleBook book = v11n.getBook(parts[0]);
            int chapter = getChapter(v11n, book, parts[1]);
            int verse = 0;
            return new Verse(v11n, book, chapter, verse);
        }

        @Override
        public Verse createEndVerse(Versification v11n, Verse verseBasis, String[] endParts) throws NoSuchVerseException {
            BibleBook book = v11n.getBook(endParts[0]);
            int chapter = getChapter(v11n, book, endParts[1]);
            int verse = v11n.getLastVerse(book, chapter);
            return new Verse(v11n, book, chapter, verse);
        }
    },
    BOOK_ONLY {
        public boolean isBook() {
            return true;
        }

        @Override
        public Verse createStartVerse(Versification v11n, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException {
            BibleBook book = v11n.getBook(parts[0]);
            int chapter = 0;
            int verse = 0;
            return new Verse(v11n, book, chapter, verse);
        }

        @Override
        public Verse createEndVerse(Versification v11n, Verse verseBasis, String[] endParts) throws NoSuchVerseException {
            BibleBook book = v11n.getBook(endParts[0]);
            int chapter = v11n.getLastChapter(book);
            int verse = v11n.getLastVerse(book, chapter);
            return new Verse(v11n, book, chapter, verse);
        }
    },
    CHAPTER_VERSE {
        public boolean isVerse() {
            return true;
        }

        @Override
        public Verse createStartVerse(Versification v11n, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException {
            if (verseRangeBasis == null) {
                throw new NoSuchVerseException(JSMsg.gettext("Book is missing", new Object[0]));
            }
            BibleBook book = verseRangeBasis.getEnd().getBook();
            int chapter = getChapter(v11n, book, parts[0]);
            int verse = getVerse(v11n, book, chapter, parts[1]);
            return new Verse(v11n, book, chapter, verse, getSubIdentifier(parts));
        }

        @Override
        public Verse createEndVerse(Versification v11n, Verse verseBasis, String[] endParts) throws NoSuchVerseException {
            BibleBook book = verseBasis.getBook();
            int chapter = getChapter(v11n, book, endParts[0]);
            int verse = getVerse(v11n, book, chapter, endParts[1]);
            return new Verse(v11n, book, chapter, verse, getSubIdentifier(endParts));
        }
    },
    CHAPTER_ONLY {
        public boolean isChapter() {
            return true;
        }

        @Override
        public Verse createStartVerse(Versification v11n, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException {
            if (verseRangeBasis == null) {
                throw new NoSuchVerseException(JSMsg.gettext("Book is missing", new Object[0]));
            }
            BibleBook book = verseRangeBasis.getEnd().getBook();
            int chapter = getChapter(v11n, book, parts[0]);
            int verse = 0;
            return new Verse(v11n, book, chapter, verse);
        }

        @Override
        public Verse createEndVerse(Versification v11n, Verse verseBasis, String[] endParts) throws NoSuchVerseException {
            BibleBook book = verseBasis.getBook();
            int chapter = getChapter(v11n, book, endParts[0]);
            return new Verse(v11n, book, chapter, v11n.getLastVerse(book, chapter));
        }
    },
    VERSE_ONLY {
        public boolean isVerse() {
            return true;
        }

        @Override
        public Verse createStartVerse(Versification v11n, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException {
            if (verseRangeBasis == null) {
                throw new NoSuchVerseException(JSMsg.gettext("Book and chapter are missing", new Object[0]));
            }
            BibleBook book = verseRangeBasis.getEnd().getBook();
            int chapter = verseRangeBasis.getEnd().getChapter();
            int verse = getVerse(v11n, book, chapter, parts[0]);
            return new Verse(v11n, book, chapter, verse, getSubIdentifier(parts));
        }

        @Override
        public Verse createEndVerse(Versification v11n, Verse verseBasis, String[] endParts) throws NoSuchVerseException {
            BibleBook book = verseBasis.getBook();
            int chapter = verseBasis.getChapter();
            int verse = getVerse(v11n, book, chapter, endParts[0]);
            return new Verse(v11n, book, chapter, verse, getSubIdentifier(endParts));
        }
    };

    public static final String VERSE_ALLOWED_DELIMS = " :.";

    public static final String VERSE_END_MARK1 = "$";

    public static final String VERSE_END_MARK2 = "ff";

    public boolean isBook() {
        return false;
    }

    public boolean isChapter() {
        return false;
    }

    public boolean isVerse() {
        return false;
    }

    public static final int getChapter(Versification v11n, BibleBook lbook, String chapter) throws NoSuchVerseException {
        if (isEndMarker(chapter)) {
            return v11n.getLastChapter(lbook);
        }
        return parseInt(chapter);
    }

    public static final int getVerse(Versification v11n, BibleBook lbook, int lchapter, String verse) throws NoSuchVerseException {
        if (isEndMarker(verse)) {
            return v11n.getLastVerse(lbook, lchapter);
        }
        return parseInt(verse);
    }

    public int toInteger() {
        return ordinal();
    }

    public static AccuracyType fromText(Versification v11n, String original, String[] parts) throws NoSuchVerseException {
        return fromText(v11n, original, parts, (AccuracyType) null, (VerseRange) null);
    }

    public static AccuracyType fromText(Versification v11n, String original, String[] parts, AccuracyType verseAccuracy) throws NoSuchVerseException {
        return fromText(v11n, original, parts, verseAccuracy, (VerseRange) null);
    }

    public static AccuracyType fromText(Versification v11n, String original, String[] parts, VerseRange basis) throws NoSuchVerseException {
        return fromText(v11n, original, parts, (AccuracyType) null, basis);
    }

    public static AccuracyType fromText(Versification v11n, String original, String[] parts, AccuracyType verseAccuracy, VerseRange basis) throws NoSuchVerseException {
        BibleBook pbook;
        int partsLength = parts.length;
        String lastPart = parts[partsLength - 1];
        if (lastPart.length() > 0 && lastPart.charAt(0) == '!') {
            partsLength--;
        }
        switch (partsLength) {
            case 1:
                if (v11n.isBook(parts[0])) {
                    return BOOK_ONLY;
                }
                checkValidChapterOrVerse(parts[0]);
                if (verseAccuracy != null) {
                    if (verseAccuracy.isVerse()) {
                        return VERSE_ONLY;
                    }
                    if (verseAccuracy.isChapter()) {
                        return CHAPTER_ONLY;
                    }
                }
                if (basis != null) {
                    if (basis.isWholeChapter()) {
                        return CHAPTER_ONLY;
                    }
                    return VERSE_ONLY;
                }
                throw buildVersePartsException(original, parts);
            case 2:
                pbook = v11n.getBook(parts[0]);
                if (pbook == null) {
                    checkValidChapterOrVerse(parts[0]);
                    checkValidChapterOrVerse(parts[1]);
                    return CHAPTER_VERSE;
                }
                if (v11n.getLastChapter(pbook) == 1) {
                    return BOOK_VERSE;
                }
                return BOOK_CHAPTER;
            case 3:
                if (v11n.getBook(parts[0]) != null) {
                    checkValidChapterOrVerse(parts[1]);
                    checkValidChapterOrVerse(parts[2]);
                    return BOOK_VERSE;
                }
                throw buildVersePartsException(original, parts);
        }
        throw buildVersePartsException(original, parts);
    }

    private static NoSuchVerseException buildVersePartsException(String original, String[] parts) {
        StringBuilder buffer = new StringBuilder(original);
        for (int i = 0; i < parts.length; i++) {
            buffer.append(", ").append(parts[i]);
        }
        return new NoSuchVerseException(JSMsg.gettext("Too many parts to the Verse. (Parts are separated by any of {0})", new Object[]{buffer.toString()}));
    }

    private static void checkValidChapterOrVerse(String text) throws NoSuchVerseException {
        if (!isEndMarker(text)) {
            parseInt(text);
        }
    }

    private static int parseInt(String text) throws NoSuchVerseException {
        try {
            return Integer.parseInt((new NumberShaper()).unshape(text));
        } catch (NumberFormatException ex) {
            throw new NoSuchVerseException(JSMsg.gettext("Cannot understand {0} as a chapter or verse.", new Object[]{text}));
        }
    }

    private static boolean isEndMarker(String text) {
        if (text.equals("$")) {
            return true;
        }
        if (text.equals("ff")) {
            return true;
        }
        return false;
    }

    private static boolean hasSubIdentifier(String[] parts) {
        String subIdentifier = parts[parts.length - 1];
        return (subIdentifier != null && subIdentifier.length() > 0 && subIdentifier.charAt(0) == '!');
    }

    protected static String getSubIdentifier(String[] parts) {
        String subIdentifier = null;
        if (hasSubIdentifier(parts)) {
            subIdentifier = parts[parts.length - 1].substring(1);
        }
        return subIdentifier;
    }

    public static String[] tokenize(String input) throws NoSuchVerseException {
        String[] args = {null, null, null, null, null, null, null, null};
        int length = input.length();
        char[] normalized = new char[length * 2];
        char lastChar = '0';
        char curChar = ' ';
        int tokenCount = 0;
        int normalizedLength = 0;
        int startIndex = 0;
        String token = null;
        boolean foundBoundary = false;
        boolean foundSubIdentifier = false;
        for (int i = 0; i < length; i++) {
            curChar = input.charAt(i);
            if (curChar == '!') {
                foundSubIdentifier = true;
                token = new String(normalized, startIndex, normalizedLength - startIndex);
                args[tokenCount++] = token;
                normalizedLength = 0;
            }
            if (foundSubIdentifier) {
                normalized[normalizedLength++] = curChar;
            } else {
                boolean charIsDigit = (curChar == '$' || Character.isDigit(curChar) || (curChar == 'f' && ((i + 1 < length) ? input.charAt(i + 1) : 32) == 102 && !Character.isLetter(lastChar)));
                if (charIsDigit || Character.isLetter(curChar)) {
                    foundBoundary = true;
                    boolean charWasDigit = (lastChar == '$' || Character.isDigit(lastChar) || (lastChar == 'f' && ((i > 2) ? input.charAt(i - 2) : 48) == 102));
                    if (charWasDigit || Character.isLetter(lastChar)) {
                        foundBoundary = false;
                        if (normalizedLength > 0 && charWasDigit != charIsDigit) {
                            foundBoundary = true;
                        }
                    }
                    if (foundBoundary) {
                        if (charIsDigit) {
                            if (tokenCount >= args.length) {
                                throw new NoSuchVerseException(JSMsg.gettext("Too many parts to the Verse. (Parts are separated by any of {0})", new Object[]{input}));
                            }
                            token = new String(normalized, startIndex, normalizedLength - startIndex);
                            args[tokenCount++] = token;
                            normalizedLength = 0;
                        } else {
                            normalized[normalizedLength++] = ' ';
                        }
                    }
                    normalized[normalizedLength++] = curChar;
                }
                if (normalizedLength > 0) {
                    lastChar = curChar;
                }
            }
        }
        if (tokenCount >= args.length) {
            throw new NoSuchVerseException(JSMsg.gettext("Too many parts to the Verse. (Parts are separated by any of {0})", new Object[]{input}));
        }
        token = new String(normalized, startIndex, normalizedLength - startIndex);
        args[tokenCount++] = token;
        String[] results = new String[tokenCount];
        System.arraycopy(args, 0, results, 0, tokenCount);
        return results;
    }

    public abstract Verse createStartVerse(Versification paramVersification, VerseRange paramVerseRange, String[] paramArrayOfString) throws NoSuchVerseException;

    public abstract Verse createEndVerse(Versification paramVersification, Verse paramVerse, String[] paramArrayOfString) throws NoSuchVerseException;
}