package org.crosswire.jsword.passage;

import org.crosswire.common.util.StringUtil;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.versification.Versification;

public final class VerseRangeFactory {

    public static VerseRange fromString(Versification v11n, String orginal) throws NoSuchVerseException {
        return fromString(v11n, orginal, null);
    }

    public static VerseRange fromString(Versification v11n, String original, VerseRange basis) throws NoSuchVerseException {
        String[] parts = StringUtil.splitAll(original, '-');
        switch (parts.length) {
            case 1:
                return fromText(v11n, original, parts[0], parts[0], basis);
            case 2:
                return fromText(v11n, original, parts[0], parts[1], basis);
        }
        throw new NoSuchVerseException(JSMsg.gettext("A verse range cannot have more than 2 parts. (Parts are separated by {0}) Given {1}", new Object[]{Character.valueOf('-'), original}));
    }

    private static VerseRange fromText(Versification v11n, String original, String startVerseDesc, String endVerseDesc, VerseRange basis) throws NoSuchVerseException {
        String[] endParts, startParts = AccuracyType.tokenize(startVerseDesc);
        AccuracyType accuracyStart = AccuracyType.fromText(v11n, original, startParts, basis);
        Verse start = accuracyStart.createStartVerse(v11n, basis, startParts);
        v11n.validate(start.getBook(), start.getChapter(), start.getVerse());
        if (startVerseDesc.equals(endVerseDesc)) {
            endParts = startParts;
        } else {
            endParts = AccuracyType.tokenize(endVerseDesc);
        }
        AccuracyType accuracyEnd = AccuracyType.fromText(v11n, original, endParts, accuracyStart, basis);
        Verse end = accuracyEnd.createEndVerse(v11n, start, endParts);
        v11n.validate(end.getBook(), end.getChapter(), end.getVerse());
        return new VerseRange(v11n, start, end);
    }
}