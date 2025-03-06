package org.crosswire.jsword.passage;

import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class OsisParser {

    private static final String START_CHAPTER_OR_VERSE = "1";

    public VerseRange parseOsisRef(Versification v11n, String osisRef) {
        String endOsisID, osisIDs[] = StringUtil.splitAll(osisRef, '-');
        if (osisIDs.length < 1 || osisIDs.length > 2) {
            return null;
        }
        if (osisIDs[0].length() == 0 || (osisIDs.length == 2 && osisIDs[1].length() == 0)) {
            return null;
        }
        String startOsisID = osisIDs[0];
        if (osisIDs.length == 1) {
            endOsisID = startOsisID;
        } else {
            endOsisID = osisIDs[1];
        }
        List<String> startOsisIDParts = splitOsisId(startOsisID);
        if (isAnEmptyPart(startOsisIDParts) || startOsisIDParts.size() > 3) {
            return null;
        }
        while (startOsisIDParts.size() < 3) {
            startOsisIDParts.add("1");
        }
        List<String> endOsisIDParts = splitOsisId(endOsisID);
        if (isAnEmptyPart(endOsisIDParts)) {
            return null;
        }
        int endOsisIDPartCount = endOsisIDParts.size();
        if (endOsisIDPartCount < 3) {
            int chapter;
            String bookName = endOsisIDParts.get(0);
            BibleBook book = BibleBook.fromExactOSIS(bookName);
            if (endOsisIDPartCount == 1) {
                chapter = v11n.getLastChapter(book);
                endOsisIDParts.add(Integer.toString(chapter));
            } else {
                chapter = Integer.parseInt(endOsisIDParts.get(1));
            }
            if (endOsisIDPartCount < 3) {
                int verse = v11n.getLastVerse(book, chapter);
                endOsisIDParts.add(Integer.toString(verse));
            }
        }
        Verse start = parseOsisID(v11n, startOsisIDParts);
        if (start == null) {
            return null;
        }
        Verse end = parseOsisID(v11n, endOsisIDParts);
        if (end == null) {
            return null;
        }
        return new VerseRange(v11n, start, end);
    }

    public Verse parseOsisID(Versification v11n, String osisID) {
        if (osisID == null) {
            return null;
        }
        List<String> osisIDParts = splitOsisId(osisID);
        if (osisIDParts.size() != 3 || isAnEmptyPart(osisIDParts)) {
            return null;
        }
        return parseOsisID(v11n, osisIDParts);
    }

    private Verse parseOsisID(Versification v11n, List<String> osisIDParts) {
        BibleBook b = BibleBook.fromExactOSIS(osisIDParts.get(0));
        if (b == null) {
            return null;
        }
        String[] endParts = StringUtil.splitAll(osisIDParts.get(2), '!');
        String subIdentifier = null;
        if (endParts.length == 2 && endParts[1].length() > 0) {
            subIdentifier = endParts[1];
        }
        return new Verse(v11n, b, Integer.parseInt(osisIDParts.get(1)), Integer.parseInt(endParts[0]), subIdentifier);
    }

    private List<String> splitOsisId(String osisID1) {
        String[] partArray = StringUtil.splitAll(osisID1, '.');
        List<String> list = new ArrayList<String>(3);
        list.addAll(Arrays.asList(partArray));
        return list;
    }

    private static boolean isAnEmptyPart(List<String> parts) {
        for (String part : parts) {
            if (part.length() == 0) {
                return true;
            }
        }
        return false;
    }
}
