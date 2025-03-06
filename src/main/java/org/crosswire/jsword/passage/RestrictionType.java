package org.crosswire.jsword.passage;

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

public enum RestrictionType {
    NONE {
        public boolean isSameScope(Versification v11n, Verse start, Verse end) {
            return true;
        }

        public VerseRange blur(Versification v11n, VerseRange range, int blurDown, int blurUp) {
            Verse start = v11n.subtract(range.getStart(), blurDown);
            Verse end = v11n.add(range.getEnd(), blurUp);
            return new VerseRange(v11n, start, end);
        }

        public VerseRange blur(Versification v11n, Verse verse, int blurDown, int blurUp) {
            Verse start = v11n.subtract(verse, blurDown);
            Verse end = v11n.add(verse, blurUp);
            return new VerseRange(v11n, start, end);
        }

        public VerseRange toRange(Versification v11n, Verse verse, int count) {
            Verse end = verse;
            if (count > 1) {
                end = v11n.add(verse, count - 1);
            }
            return new VerseRange(v11n, verse, end);
        }
    },
    CHAPTER {
        public boolean isSameScope(Versification v11n, Verse start, Verse end) {
            return v11n.isSameChapter(start, end);
        }

        public VerseRange blur(Versification v11n, VerseRange range, int blurDown, int blurUp) {
            Verse start = range.getStart();
            BibleBook startBook = start.getBook();
            int startChapter = start.getChapter();
            int startVerse = start.getVerse() - blurDown;
            Verse end = range.getEnd();
            BibleBook endBook = end.getBook();
            int endChapter = end.getChapter();
            int endVerse = end.getVerse() + blurUp;
            startVerse = Math.max(startVerse, 0);
            endVerse = Math.min(endVerse, v11n.getLastVerse(endBook, endChapter));
            Verse newStart = new Verse(v11n, startBook, startChapter, startVerse);
            Verse newEnd = new Verse(v11n, endBook, endChapter, endVerse);
            return new VerseRange(v11n, newStart, newEnd);
        }

        public VerseRange blur(Versification v11n, Verse verse, int blurDown, int blurUp) {
            BibleBook book = verse.getBook();
            int chapter = verse.getChapter();
            int startVerse = verse.getVerse() - blurDown;
            int endVerse = verse.getVerse() + blurUp;
            startVerse = Math.max(startVerse, 0);
            endVerse = Math.min(endVerse, v11n.getLastVerse(book, chapter));
            Verse start = new Verse(v11n, book, chapter, startVerse);
            Verse end = new Verse(v11n, book, chapter, endVerse);
            return new VerseRange(v11n, start, end);
        }

        public VerseRange toRange(Versification v11n, Verse verse, int count) {
            Verse end = v11n.add(verse, count - 1);
            return new VerseRange(v11n, verse, end);
        }
    };

    private static RestrictionType defaultBlurRestriction;

    public int toInteger() {
        return ordinal();
    }

    public static RestrictionType fromString(String name) {
        for (RestrictionType v : values()) {
            if (v.name().equalsIgnoreCase(name)) {
                return v;
            }
        }
        assert false;
        return null;
    }

    public static RestrictionType fromInteger(int i) {
        for (RestrictionType v : values()) {
            if (v.ordinal() == i) {
                return v;
            }
        }
        assert false;
        return null;
    }

    public static void setBlurRestriction(int value) {
        defaultBlurRestriction = fromInteger(value);
    }

    public static int getBlurRestriction() {
        return getDefaultBlurRestriction().toInteger();
    }

    public static RestrictionType getDefaultBlurRestriction() {
        return defaultBlurRestriction;
    }

    static {
        defaultBlurRestriction = NONE;
    }

    public abstract boolean isSameScope(Versification paramVersification, Verse paramVerse1, Verse paramVerse2);

    public abstract VerseRange blur(Versification paramVersification, VerseRange paramVerseRange, int paramInt1, int paramInt2);

    public abstract VerseRange blur(Versification paramVersification, Verse paramVerse, int paramInt1, int paramInt2);

    public abstract VerseRange toRange(Versification paramVersification, Verse paramVerse, int paramInt);
}
