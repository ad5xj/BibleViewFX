package org.crosswire.common.diff;

public final class Commonality {

    public static int prefix(String source, String target) {
        int pointermin = 0;
        int pointermax = Math.min(source.length(), target.length());
        int pointermid = pointermax;
        while (pointermin < pointermid) {
            if (source.regionMatches(0, target, 0, pointermid)) {
                pointermin = pointermid;
            } else {
                pointermax = pointermid;
            }
            pointermid = (pointermax - pointermin) / 2 + pointermin;
        }
        return pointermid;
    }

    public static int suffix(String source, String target) {
        int pointermin = 0;
        int pointermax = Math.min(source.length(), target.length());
        int pointermid = pointermax;
        while (pointermin < pointermid) {
            if (source.regionMatches(source.length() - pointermid, target, target.length() - pointermid, pointermid)) {
                pointermin = pointermid;
            } else {
                pointermax = pointermid;
            }
            pointermid = (pointermax - pointermin) / 2 + pointermin;
        }
        return pointermid;
    }

    public static CommonMiddle halfMatch(String source, String target) {
        int sourceLength = source.length();
        int targetLength = target.length();
        String longText = (sourceLength > targetLength) ? source : target;
        String shortText = (sourceLength > targetLength) ? target : source;
        int longTextLength = Math.max(sourceLength, targetLength);
        if (longTextLength < 10 || shortText.length() < 1) {
            return null;
        }
        CommonMiddle hm1 = halfMatch(longText, shortText, ceil(longTextLength, 4));
        CommonMiddle hm2 = halfMatch(longText, shortText, ceil(longTextLength, 2));
        CommonMiddle hm = null;
        if (hm1 == null && hm2 == null) {
            return null;
        }
        if (hm2 == null) {
            hm = hm1;
        } else if (hm1 == null) {
            hm = hm2;
        } else {
            hm = (hm1.getCommonality().length() > hm2.getCommonality().length()) ? hm1 : hm2;
        }
        if (sourceLength > targetLength) {
            return hm;
        }
        if (hm != null) {
            return new CommonMiddle(hm.getTargetPrefix(), hm.getTargetSuffix(), hm.getSourcePrefix(), hm.getSourceSuffix(), hm.getCommonality());
        }
        return null;
    }

    private static CommonMiddle halfMatch(String longText, String shortText, int startIndex) {
        String seed = longText.substring(startIndex, startIndex + longText.length() / 4);
        int j = -1;
        String common = "";
        String longTextPrefix = "";
        String longTextSuffix = "";
        String shortTextPrefix = "";
        String shortTextSuffix = "";
        while ((j = shortText.indexOf(seed, j + 1)) != -1) {
            int prefixLength = prefix(longText.substring(startIndex), shortText.substring(j));
            int suffixLength = suffix(longText.substring(0, startIndex), shortText.substring(0, j));
            if (common.length() < prefixLength + suffixLength) {
                common = shortText.substring(j - suffixLength, j) + shortText.substring(j, j + prefixLength);
                longTextPrefix = longText.substring(0, startIndex - suffixLength);
                longTextSuffix = longText.substring(startIndex + prefixLength);
                shortTextPrefix = shortText.substring(0, j - suffixLength);
                shortTextSuffix = shortText.substring(j + prefixLength);
            }
        }
        if (common.length() >= longText.length() / 2) {
            return new CommonMiddle(longTextPrefix, longTextSuffix, shortTextPrefix, shortTextSuffix, common);
        }
        return null;
    }

    private static int ceil(int number, int divisor) {
        assert divisor > 0;
        int result = number / divisor + ((number % divisor > 0) ? 1 : 0);
        return result;
    }
}
