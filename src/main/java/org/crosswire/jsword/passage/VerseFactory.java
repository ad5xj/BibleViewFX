package org.crosswire.jsword.passage;

import org.crosswire.jsword.versification.Versification;

public final class VerseFactory {

    public static Verse fromString(Versification v11n, String original) throws NoSuchVerseException {
        if ("".equals(original)) {
            return null;
        }
        String[] parts = AccuracyType.tokenize(original);
        AccuracyType accuracy = AccuracyType.fromText(v11n, original, parts);
        assert accuracy != null;
        return accuracy.createStartVerse(v11n, null, parts);
    }

    public static Verse fromString(Versification v11n, String original, VerseRange verseRangeBasis) throws NoSuchVerseException {
        if ("".equals(original)) {
            return null;
        }
        String[] parts = AccuracyType.tokenize(original);
        AccuracyType accuracy = AccuracyType.fromText(v11n, original, parts, null, verseRangeBasis);
        assert accuracy != null;
        return accuracy.createStartVerse(v11n, verseRangeBasis, parts);
    }
}
