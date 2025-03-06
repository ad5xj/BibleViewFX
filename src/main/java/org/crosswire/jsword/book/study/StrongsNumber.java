package org.crosswire.jsword.book.study;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrongsNumber {

    private static final Pattern STRONGS_PATTERN = Pattern.compile("([GgHh])([0-9]*)!?([A-Za-z]+)?");
    private static final DecimalFormat ZERO_PAD = new DecimalFormat("0000");

    private boolean valid;

    private int strongsNumber;

    private char language;

    private String part;

    public StrongsNumber(String input)    { this.valid = parse(input); }

    public StrongsNumber(char language, short strongsNumber) { this(language, strongsNumber, null); }

    public StrongsNumber(char lang, short strongs, String p)
    {
        language = lang;
        strongsNumber = strongs;
        part = p;
        valid = isValid();
    }

    public String getStrongsNumber()
    {
        StringBuilder buf = new StringBuilder(5);
        buf.append(this.language);
        buf.append(ZERO_PAD.format(this.strongsNumber));
        return buf.toString();
    }

    public String getFullStrongsNumber()
    {
        StringBuilder buf = new StringBuilder(5);
        buf.append(language);
        buf.append(ZERO_PAD.format(strongsNumber));
        if (part != null)  { buf.append(part); }
        return buf.toString();
    }

    public boolean isGreek()  { return (language == 'G'); }

    public boolean isHebrew() { return (this.language == 'H'); }

    public boolean isPart()   { return (this.part != null); }

    public boolean isValid()
    {
        if (!this.valid)   { return false; }
        if (   (this.language == 'H')
            && (strongsNumber >= 1)
            && (strongsNumber <= 8674)
           )  { return true; }

        if (   (language == 'G')
            && ( (strongsNumber >= 1) 
                 && (strongsNumber < 1418)
               )
            || (   (strongsNumber > 1418)
                && (strongsNumber < 2717)
               )
            || (    (strongsNumber > 2717)
                &&  (strongsNumber < 3203)
               )
            || (    (strongsNumber > 3302) 
                &&  (strongsNumber <= 5624)
               )
           )   {  return true; }
        valid = false;
        return false;
    }

    public int hashCode()
    {
        int result = 31 + language;
        return 31 * result + strongsNumber;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)       { return true; }
        if ((obj == null) || (getClass() != obj.getClass()))  { return false; }
        StrongsNumber other = (StrongsNumber) obj;
        return (this.language == other.language && this.strongsNumber == other.strongsNumber);
    }

    public String toString()    {  return getStrongsNumber(); }

    private boolean parse(String input)
    {
        String text = input;
        language = 'U';
        strongsNumber = 9999;
        part = "";
        Matcher m = STRONGS_PATTERN.matcher(text);
        if (!m.lookingAt())  { return false; }

        String lang = m.group(1);
        language = lang.charAt(0);
        switch (this.language)
        {
        case 'g' -> this.language = 'G';
        case 'h' -> this.language = 'H';
        }

        try
        {
            this.strongsNumber = Integer.parseInt(m.group(2));
        }
        catch (NumberFormatException e)
        {
            this.strongsNumber = 0;
            return false;
        }
        this.part = m.group(3);
        return true;
    }
}