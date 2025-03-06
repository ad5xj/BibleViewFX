package org.crosswire.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public final class StringUtil {

    public static final String NEWLINE = System.getProperty("line.separator", "\r\n");

    public static boolean equals(String string1, String string2) {
        if (string1 == null) {
            return (string2 == null);
        }
        return string1.equals(string2);
    }

    public static String read(Reader in) throws IOException {
        StringBuilder retcode = new StringBuilder();
        BufferedReader din = new BufferedReader(in, 8192);
        while (true) {
            String line = din.readLine();
            if (line == null) {
                break;
            }
            retcode.append(line);
            retcode.append(NEWLINE);
        }
        return retcode.toString();
    }

    public static String createTitle(String variable) {
        StringBuilder retcode = new StringBuilder();
        boolean lastlower = false;
        boolean lastspace = true;
        for (int i = 0; i < variable.length(); i++) {
            char c = variable.charAt(i);
            if (lastlower && Character.isUpperCase(c) && !lastspace) {
                retcode.append(' ');
            }
            lastlower = !Character.isUpperCase(c);
            if (lastspace) {
                c = Character.toUpperCase(c);
            }
            if (c == '_') {
                c = ' ';
            }
            if (!lastspace || c != ' ') {
                retcode.append(c);
            }
            lastspace = (c == ' ');
        }
        return retcode.toString();
    }

    public static String getInitials(String sentence) {
        String[] words = split(sentence);
        StringBuilder retcode = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            char first = Character.MIN_VALUE;
            for (int j = 0; first == '\000' && j < word.length(); j++) {
                char c = word.charAt(j);
                if (Character.isLetter(c)) {
                    first = c;
                }
            }
            if (first != '\000') {
                retcode.append(first);
            }
        }
        return retcode.toString();
    }

    public static String[] split(String str) {
        if (str == null) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int len = str.length();
        if (len == 0) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        char[] cstr = str.toCharArray();
        int count = 0;
        int start = 0;
        int i = 0;
        while ((i = indexOfWhitespace(cstr, start)) != -1) {
            if (i > start) {
                count++;
            }
            start = i + 1;
        }
        if (start < len) {
            count++;
        }
        String[] list = new String[count];
        if (start == 0) {
            list[0] = str;
            return list;
        }
        start = 0;
        i = 0;
        int x = 0;
        while ((i = indexOfWhitespace(cstr, start)) != -1) {
            if (i > start) {
                list[x++] = str.substring(start, i);
            }
            start = i + 1;
        }
        if (start < len) {
            list[x++] = str.substring(start);
        }
        return list;
    }

    public static String[] split(String str, int max) {
        if (str == null) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int len = str.length();
        if (len == 0) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        char[] cstr = str.toCharArray();
        int count = 0;
        int start = 0;
        int i = 0;
        while ((i = indexOfWhitespace(cstr, start)) != -1) {
            if (i > start) {
                count++;
            }
            start = i + 1;
        }
        if (start < len) {
            count++;
        }
        if (start == 0) {
            String[] arrayOfString = new String[count];
            arrayOfString[0] = str;
            return arrayOfString;
        }
        if (max > 0 && count > max) {
            count = max;
        }
        String[] list = new String[count];
        start = 0;
        i = 0;
        int x = 0;
        while ((i = indexOfWhitespace(cstr, start)) != -1) {
            if (i > start && x < count) {
                list[x++] = str.substring(start, i);
            }
            start = i + 1;
        }
        if (start < len && x < count) {
            list[x++] = str.substring(start);
        }
        return list;
    }

    public static String[] split(String str, char separatorChar) {
        if (str == null) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int len = str.length();
        if (len == 0) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int count = 0;
        int start = 0;
        int i = 0;
        while ((i = str.indexOf(separatorChar, start)) != -1) {
            if (i > start && i < len) {
                count++;
            }
            start = i + 1;
        }
        if (start < len) {
            count++;
        }
        String[] list = new String[count];
        if (count == 1) {
            list[0] = str;
            return list;
        }
        start = 0;
        i = 0;
        int x = 0;
        while ((i = str.indexOf(separatorChar, start)) != -1) {
            if (i > start) {
                list[x++] = str.substring(start, i);
            }
            start = i + 1;
        }
        if (start < len) {
            list[x++] = str.substring(start, len);
        }
        return list;
    }

    public static String[] split(String str, char separatorChar, int max) {
        if (str == null) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int len = str.length();
        if (len == 0) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int count = 0;
        int start = 0;
        int i = 0;
        while ((i = str.indexOf(separatorChar, start)) != -1) {
            if (i > start) {
                count++;
            }
            start = i + 1;
        }
        if (start < len) {
            count++;
        }
        if (count == 1) {
            String[] arrayOfString = new String[count];
            arrayOfString[0] = str;
            return arrayOfString;
        }
        if (max > 0 && count > max) {
            count = max;
        }
        String[] list = new String[count];
        start = 0;
        i = 0;
        int x = 0;
        while ((i = str.indexOf(separatorChar, start)) != -1) {
            if (i > start && x < count) {
                list[x++] = str.substring(start, i);
            }
            start = i + 1;
        }
        if (start < len && x < count) {
            list[x++] = str.substring(start);
        }
        return list;
    }

    public static String[] split(String str, String separatorChars) {
        return split(str, separatorChars, -1);
    }

    public static String[] split(String str, String separatorStr, int max) {
        if (separatorStr == null) {
            return split(str, max);
        }
        if (separatorStr.length() == 1) {
            return split(str, separatorStr.charAt(0), max);
        }
        if (str == null) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int len = str.length();
        if (len == 0) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        char[] cstr = str.toCharArray();
        char[] separatorChars = separatorStr.toCharArray();
        int count = 0;
        int start = 0;
        int i = 0;
        while ((i = indexOfAny(cstr, separatorChars, start)) != -1) {
            if (i > start) {
                count++;
            }
            start = i + 1;
        }
        if (start < len) {
            count++;
        }
        if (count == 1) {
            String[] arrayOfString = new String[count];
            arrayOfString[0] = str;
            return arrayOfString;
        }
        if (max > 0 && count > max) {
            count = max;
        }
        String[] list = new String[count];
        start = 0;
        i = 0;
        int x = 0;
        while ((i = indexOfAny(cstr, separatorChars, start)) != -1) {
            if (i > start && x < count) {
                list[x++] = str.substring(start, i);
            }
            start = i + 1;
        }
        if (start < len && x < count) {
            list[x++] = str.substring(start);
        }
        return list;
    }

    public static String[] splitAll(String str, char separatorChar) {
        if (str == null) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int len = str.length();
        if (len == 0) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int count = 1;
        int start = 0;
        int i = 0;
        while ((i = str.indexOf(separatorChar, start)) != -1) {
            count++;
            start = i + 1;
        }
        String[] list = new String[count];
        if (count == 1) {
            list[0] = str;
            return list;
        }
        start = 0;
        i = 0;
        for (int x = 0; x < count; x++) {
            i = str.indexOf(separatorChar, start);
            if (i != -1) {
                list[x] = str.substring(start, i);
            } else {
                list[x] = str.substring(start);
            }
            start = i + 1;
        }
        return list;
    }

    public static String[] splitAll(String str, char separatorChar, int max) {
        if (str == null) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int len = str.length();
        if (len == 0) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int count = 1;
        int start = 0;
        int i = 0;
        while ((i = str.indexOf(separatorChar, start)) != -1) {
            count++;
            start = i + 1;
        }
        if (count == 1) {
            String[] arrayOfString = new String[count];
            arrayOfString[0] = str;
            return arrayOfString;
        }
        if (max > 0 && count > max) {
            count = max;
        }
        String[] list = new String[count];
        start = 0;
        i = 0;
        for (int x = 0; x < count; x++) {
            i = str.indexOf(separatorChar, start);
            if (i != -1) {
                list[x] = str.substring(start, i);
            } else {
                list[x] = str.substring(start, len);
            }
            start = i + 1;
        }
        return list;
    }

    public static String join(Object[] array, String aSeparator) {
        String separator = aSeparator;
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        int arraySize = array.length;
        int bufSize = (arraySize == 0) ? 0 : (arraySize * (((array[0] == null) ? 16 : array[0].toString().length()) + separator.length()));
        StringBuilder buf = new StringBuilder(bufSize);
        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static int indexOfAny(char[] str, char[] separatorChars, int offset) {
        int strlen = str.length;
        int seplen = separatorChars.length;
        for (int i = offset; i < strlen; i++) {
            char ch = str[i];
            for (int j = 0; j < seplen; j++) {
                if (separatorChars[j] == ch) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int indexOfWhitespace(char[] str, int offset) {
        int strlen = str.length;
        for (int i = offset; i < strlen; i++) {
            if (Character.isWhitespace(str[i])) {
                return i;
            }
        }
        return -1;
    }

    public static final String[] EMPTY_STRING_ARRAY = new String[0];
}