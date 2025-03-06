package org.crosswire.common.icu;

import org.crosswire.jsword.internationalisation.LocaleProviderManager;

import java.io.Serializable;

import java.util.Locale;

public class NumberShaper implements Serializable 
{
    private static final long serialVersionUID = -8408052851113601251L;

    private char nineShape = Character.MIN_VALUE;

    public boolean canShape() { return (getNine() != '9'); }

    public String shape(String input) 
    {
        if (input == null) { return input; }
        char[] src = input.toCharArray();
        boolean[] transformed = new boolean[1];
        transformed[0] = false;
        char[] dest = shaped(src, transformed);
        if (transformed[0]) { return new String(dest); }
        return input;
    }

    public boolean canUnshape() { return (getNine() != '9'); }

    public String unshape(String input) 
    {
        char[] src = input.toCharArray();
        boolean[] transformed = new boolean[1];
        transformed[0] = false;
        char[] dest = unshaped(src, transformed);
        if (transformed[0]) { return new String(dest); }
        return input;
    }

    private char[] unshaped(char[] src, boolean[] transformed) 
    {
        int nine = getNine();
        if (nine == 57) { return src; }
        int zero = nine - 9;
        return transform(src, zero, nine, 57 - nine, transformed);
    }

    private char[] shaped(char[] src, boolean[] transformed) 
    {
        char nine = getNine();
        if (nine == '9') { return src; }
        return transform(src, 48, 57, nine - 57, transformed);
    }

    private char[] transform(char[] src, int zero, int nine, int offset, boolean[] transformed) 
    {
        char[] text = src;
        int len = src.length;
        int i = 0;
    
        for ( i = 0; i < len; i++) 
        {
            char c = text[i];
            if ( (c >= zero) && (c <= nine) )
            {
                text[i] = (char) (c + offset);
                transformed[0] = true;
            }
        }
        return text;
    }

    private char getNine() 
    {
        if (this.nineShape == '\000') 
        {
            this.nineShape = '9';
            Locale locale = LocaleProviderManager.getLocale();
            if ("fa".equals(locale.getLanguage())) 
            {
                this.nineShape =  ' ';
            } 
            else if ("ar".equals(locale.getLanguage())) 
            {
                this.nineShape = ' ';
        } 
    } 
    return this.nineShape;
    }
}