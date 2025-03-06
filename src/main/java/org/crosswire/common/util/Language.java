package org.crosswire.common.util;

import java.util.Locale;

public class Language implements Comparable<Language> {

    public static final String DEFAULT_LANG_CODE = "en";
    public static final String UNKNOWN_LANG_CODE = "und";
    public static final Language DEFAULT_LANG = new Language("en");

    private boolean valid;
    private boolean knowsDirection;
    private boolean ltor;

    private String given;
    private String found;
    private String code;
    private String script;
    private String country;
    private String name;


    public Language(String specification) {
        this.given = specification;
        parse(this.given);
    }

    public String getGivenSpecification() {
        return this.given;
    }

    public String getFoundSpecification() {
        getName();
        return this.found;
    }

    public boolean isValidLanguage() {
        getName();
        return this.valid;
    }

    public String getCode() {
        return this.code;
    }

    public String getScript() {
        return this.script;
    }

    public String getCountry() {
        return this.country;
    }

    public String getName() {
        if (this.name == null) {
            boolean more = true;
            String result = this.code;
            String lookup = this.code;
            StringBuilder sb = new StringBuilder();
            if (this.script != null && this.country != null) {
                sb.append(this.code);
                sb.append('-');
                sb.append(this.script);
                sb.append('-');
                sb.append(this.country);
                lookup = sb.toString();
                result = Languages.getName(lookup);
                more = lookup.equals(result);
            }
            if (more && this.script != null) {
                sb.setLength(0);
                sb.append(this.code);
                sb.append('-');
                sb.append(this.script);
                lookup = sb.toString();
                result = Languages.getName(lookup);
                more = lookup.equals(result);
            }
            if (more && this.country != null) {
                sb.setLength(0);
                sb.append(this.code);
                sb.append('-');
                sb.append(this.country);
                lookup = sb.toString();
                result = Languages.getName(lookup);
                more = lookup.equals(result);
            }
            if (more) {
                lookup = this.code;
                result = Languages.getName(lookup);
                more = lookup.equals(result);
            }
            if (more) {
                lookup = this.code;
                result = (new Locale(lookup)).getDisplayLanguage();
                more = lookup.equals(result);
            }
            if (more) {
                lookup = this.code;
                result = Languages.AllLanguages.getName(lookup);
                more = lookup.equals(result);
            }
            if (more) {
                this.valid = false;
            }
            this.found = lookup;
            this.name = result;
        }
        return this.name;
    }

    public boolean isLeftToRight() {
        if (!this.knowsDirection) {
            this.ltor = !Languages.RtoL.isRtoL(this.script, this.code);
            this.knowsDirection = true;
        }
        return this.ltor;
    }

    public int hashCode() {
        if (this.found == null) {
            getName();
        }
        return this.found.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Language other = (Language) obj;
        return (this.code.equals(other.code) && compareStrings(this.script, other.script) && compareStrings(this.country, other.country));
    }

    public String toString() {
        return getName();
    }

    public int compareTo(Language o) {
        return getName().compareTo(o.getName());
    }

    private void parse(String spec) {
        String specification = spec;
        if (specification == null) {
            specification = "en";
        }
        int len = specification.length();
        if (len < 2 || specification.charAt(0) == '-' || specification.charAt(1) == '-') {
            this.valid = false;
            this.code = "und";
            return;
        }
        if (len <= 3) {
            this.code = CanonicalUtils.getLanguage(specification, 0, len);
        }
        int partLen = 0;
        int start = 0;
        int split;
        for (split = 2; split < len; split++) {
            char c = specification.charAt(split);
            if (c == '-') {
                break;
            }
        }
        this.code = CanonicalUtils.getLanguage(specification, start, split);
        partLen = split - start;
        this.valid = (partLen == 2 || partLen == 3);
        start = split + 1;
        if (split < len) {
            for (split = start; split < len; split++) {
                char c = specification.charAt(split);
                if (c == '-') {
                    break;
                }
            }
            partLen = split - start;
            if (partLen == 4) {
                this.script = CanonicalUtils.getScript(specification, start, split);
            } else if (partLen == 2) {
                this.country = CanonicalUtils.getCountry(specification, start, split);
            } else {
                this.valid = false;
            }
            start = split + 1;
        }
        if (this.country == null && split < len) {
            for (split = start; split < len; split++) {
                char c = specification.charAt(split);
                if (c == '-') {
                    break;
                }
            }
            partLen = split - start;
            if (partLen == 2) {
                this.country = CanonicalUtils.getCountry(specification, start, split);
            } else {
                this.valid = false;
            }
            start = split + 1;
        }
        if (start <= len) {
            this.valid = false;
        }
    }

    private boolean compareStrings(String a, String b) {
        return ((a == null && b == null) || (a != null && a.equals(b)));
    }

    private static final class CanonicalUtils {

        public static String getLanguage(String specification, int start, int end) {
            if (start == end) {
                return null;
            }
            int first;
            for (first = start; first < end && isLowerASCII(specification.charAt(first)); first++);
            if (first == end) {
                return specification.substring(start, end);
            }
            int len = end - start;
            char[] buf = new char[len];
            int i = 0;
            for (int j = start; j < end; j++) {
                buf[i++] = (j < first) ? specification.charAt(j) : toLowerASCII(specification.charAt(j));
            }
            return new String(buf);
        }

        public static String getCountry(String specification, int start, int end) {
            if (start == end) {
                return null;
            }
            int first;
            for (first = start; first < end && isUpperASCII(specification.charAt(first)); first++);
            if (first == end) {
                return specification.substring(start, end);
            }
            int len = end - start;
            char[] buf = new char[len];
            int i = 0;
            for (int j = start; j < end; j++) {
                buf[i++] = (j < first) ? specification.charAt(j) : toUpperASCII(specification.charAt(j));
            }
            return new String(buf);
        }

        public static String getScript(String specification, int start, int end) {
            if (start == end) {
                return null;
            }
            int first = start;
            if (isUpperASCII(specification.charAt(start))) {
                for (first = start + 1; first < end && isLowerASCII(specification.charAt(first)); first++);
                if (first == end) {
                    return specification.substring(start, end);
                }
            }
            int len = end - start;
            char[] buf = new char[len];
            buf[0] = (first == start) ? toUpperASCII(specification.charAt(first)) : specification.charAt(first);
            int i = 1;
            for (int j = start + 1; j < end; j++) {
                buf[i++] = (j < first) ? specification.charAt(j) : toLowerASCII(specification.charAt(j));
            }
            return new String(buf);
        }

        private static boolean isUpperASCII(char c) {
            return (c >= 'A' && c <= 'Z');
        }

        private static boolean isLowerASCII(char c) {
            return (c >= 'a' && c <= 'z');
        }

        private static char toUpperASCII(char c) {
            return isLowerASCII(c) ? (char) (c - 32) : c;
        }

        private static char toLowerASCII(char c) {
            return isUpperASCII(c) ? (char) (c + 32) : c;
        }
    }
}