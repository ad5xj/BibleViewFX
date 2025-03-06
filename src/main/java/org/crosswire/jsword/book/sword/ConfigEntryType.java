package org.crosswire.jsword.book.sword;

import org.crosswire.common.util.Language;
import org.crosswire.common.util.Version;
import org.crosswire.jsword.book.BookCategory;
import java.util.regex.Pattern;

enum ConfigEntryType
{
    DATA_PATH("DataPath")
    {
        public boolean isAllowed(String value)  { return true; }
    },
    DESCRIPTION("Description"),
    MOD_DRV("ModDrv", new String[]
    {
        "RawText", "zText", "RawCom", "RawCom4", "zCom", "HREFCom", "RawFiles", "RawLD", "RawLD4", "zLD",
        "RawGenBook"
    }),
    COMPRESS_TYPE("CompressType", new String[]
    {
        "LZSS", "ZIP", "GZIP", "BZIP2", "XZ"
    }),
    BLOCK_TYPE("BlockType", new String[]
    {
        "BOOK", "CHAPTER", "VERSE"
    }),
    BLOCK_COUNT("BlockCount")
    {
        public boolean isText()   { return false; }
        public boolean isAllowed(String aValue)
        {
            try
            {
                Integer.parseInt(aValue);
                return true;
            }
            catch (NumberFormatException e) 
            {
                return false; 
            }
        }

        public Object convert(String input)
        {
            try
            {
                return Integer.valueOf(input);
            }
            catch (NumberFormatException e)
            {
                return getDefault();
            }
        }

    },
    KEY_TYPE("KeyType", new String[]
    {
        "TreeKey", "VerseKey"
    }),
    CASE_SENSITIVE_KEYS("CaseSensitiveKeys", new String[]
    {
        "true", "false"
    })
    {
        public boolean isText()               { return false; }
        public Object convert(String input)   { return Boolean.valueOf(input); }
    },
    CIPHER_KEY("CipherKey"),
    VERSIFICATION("Versification", new String[]
    {
        "Catholic", "Catholic2", "German", "KJV", "KJVA", "LXX", "Leningrad", "Luther", "MT", "NRSV",
        "NRSVA", "Orthodox", "Synodal", "SynodalProt", "Vulg"
    }),
    GLOBAL_OPTION_FILTER("GlobalOptionFilter", new String[]
    {
        "GBFStrongs", "GBFFootnotes", "GBFMorph", "GBFHeadings", "GBFRedLetterWords", "GBFScripref", "ThMLStrongs", "ThMLFootnotes", "ThMLScripref", "ThMLMorph",
        "ThMLHeadings", "ThMLVariants", "ThMLLemma", "UTF8Cantillation", "UTF8GreekAccents", "UTF8HebrewPoints", "UTF8ArabicPoints", "OSISLemma", "OSISMorphSegmentation", "OSISStrongs",
        "OSISFootnotes", "OSISScripref", "OSISMorph", "OSISHeadings", "OSISVariants", "OSISRedLetterWords", "OSISGlosses", "OSISRuby", "OSISXlit", "OSISEnum",
        "OSISReferenceLinks|*", "OSISDictionary"
    })
    {
        public boolean mayRepeat()  { return true; }
    },
    SIGLUM1("Siglum1"),
    SIGLUM2("Siglum2"),
    SIGLUM3("Siglum3"),
    SIGLUM4("Siglum4"),
    SIGLUM5("Siglum5"),
    DIRECTION("Direction", new String[]
    {
        "LtoR", "RtoL", "bidi"
    }),
    SOURCE_TYPE("SourceType", new String[]
    {
        "Plaintext", "GBF", "ThML", "OSIS", "TEI"
    }),
    ENCODING("Encoding", new String[]
    {
        "Latin-1", "UTF-8", "UTF-16", "SCSU"
    }),
    DISPLAY_LEVEL("DisplayLevel")
    {
        public boolean isText()     { return false;}

        public boolean isAllowed(String value)
        {
            try
            {
                Integer.parseInt(value);
                return true;
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }

        public Object convert(String input)
        {
            try
            {
                return Integer.valueOf(input);
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }

    },
    FONT("Font"),
    OSIS_Q_TO_TICK("OSISqToTick", new String[]
    {
        "true", "false"
    })
    {
        public boolean isText()               { return false; }
        public Object convert(String input)   { return Boolean.valueOf(input); }
    },
    FEATURE("Feature", new String[]
    {
        "StrongsNumbers", "GreekDef", "HebrewDef", "GreekParse", "HebrewParse", "DailyDevotion", "Glossary", "Images", "NoParagraphs"
    })
    {
        public boolean mayRepeat()            { return true; }
    },
    GLOSSARY_FROM("GlossaryFrom")
    {
        public boolean isText()               { return false; }
        public Object convert(String input)   { return new Language(input); }
        public String unconvert(Object internal)
        {
            if (internal instanceof Language) { return ((Language) internal).getGivenSpecification(); }
            return super.unconvert(internal);
        }

    },
    GLOSSARY_TO("GlossaryTo")
    {
        public boolean isText()             { return false; }
        public Object convert(String input) { return new Language(input); }
        public String unconvert(Object internal)
        {
            if (internal instanceof Language) { return ((Language) internal).getGivenSpecification(); }
            return super.unconvert(internal);
        }

    },
    PREFERRED_CSS_XHTML("PreferredCSSXHTML"),
    STRONGS_PADDING("StrongsPadding"),
    ABBREVIATION("Abbreviation"),
    ABOUT("About")
    {
        public boolean allowsContinuation() { return true; }
        public boolean allowsRTF() { return true; }

    },
    VERSION("Version")
    {
        public boolean isText()  { return false; }
        public boolean isAllowed(String aValue)
        {
            try
            {
                new Version(aValue);
                return true;
            }
            catch (IllegalArgumentException e)
            {
                return false;
            }
        }

        public Object convert(String input)
        {
            try
            {
                return new Version(input);
            }
            catch (IllegalArgumentException e)
            {
                return null;
            }
        }

    },
    HISTORY("History")
    {
        public boolean mayRepeat() { return true; }
    },
    MINIMUM_VERSION("MinimumVersion"),
    CATEGORY("Category", new String[]
    {
        "Other", "Daily Devotional", "Glossaries", "Cults / Unorthodox / Questionable Material", "Essays", "Maps", "Images", "Biblical Texts", "Commentaries", "Lexicons / Dictionaries",
        "Generic Books"
    })
    {
        public Object convert(String input) { return BookCategory.fromString(input); }
    },
    LCSH("LCSH"),
    LANG("Lang")
    {
        public boolean isText()             { return false; }
        public Object convert(String input) { return new Language(input); }
        public String unconvert(Object internal)
        {
            if (internal instanceof Language) { return ((Language) internal).getGivenSpecification(); }
            return super.unconvert(internal);
        }

    },
    INSTALL_SIZE("InstallSize")
    {
        public boolean isText()   { return false;  }
        public boolean isAllowed(String value)
        {
            try
            {
                Integer.parseInt(value);
                return true;
            }
            catch (NumberFormatException e)
            {
                return false;
            }
        }

        public Object convert(String input)
        {
            try
            {
                return Integer.valueOf(input);
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }

    },
    SWORD_VERSION_DATE("SwordVersionDate")
    {
        public boolean isAllowed(String value)
        {
            return this.validDatePattern.matcher(value).matches();
        }

        private Pattern validDatePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    },
    OBSOLETES("Obsoletes")
    {
        public boolean mayRepeat()            { return true; }
    },
    OSIS_VERSION("OSISVersion"),
    COPYRIGHT("Copyright")
    {
        public boolean allowsContinuation()     { return true; }
    },
    COPYRIGHT_HOLDER("CopyrightHolder"),
    COPYRIGHT_DATE("CopyrightDate")
    {
        public boolean isAllowed(String value) 
        {
            return this.validDatePattern.matcher(value).matches();
        }

        private Pattern validDatePattern = Pattern.compile("\\d{4}(\\s*-\\s*\\d{4})?(\\s*,\\s*\\d{4}(\\s*-\\s*\\d{4})?)*");
    },
    COPYRIGHT_NOTES("CopyrightNotes")
    {
        public boolean allowsContinuation() { return true; }
        public boolean allowsRTF()          { return true;  }

    },
    COPYRIGHT_CONTACT_NAME("CopyrightContactName")
    {
        public boolean allowsContinuation() { return true; }
        public boolean allowsRTF()          {  return true; }

    },
    COPYRIGHT_CONTACT_NOTES("CopyrightContactNotes")
    {
        public boolean allowsContinuation() { return true; }
        public boolean allowsRTF()          { return true; }
    },
    COPYRIGHT_CONTACT_ADDRESS("CopyrightContactAddress")
    {
        public boolean allowsContinuation() { return true; }
        public boolean allowsRTF()          { return true; }
    },
    COPYRIGHT_CONTACT_EMAIL("CopyrightContactEmail"),
    SHORT_PROMO("ShortPromo")
    {
        public boolean allowsHTML()        {  return true; }
    },
    SHORT_COPYRIGHT("ShortCopyright"),
    DISTRIBUTION_LICENSE("DistributionLicense", new String[]
    {
        "Public Domain", "Copyrighted", "Copyrighted; Free non-commercial distribution", "Copyrighted; Permission to distribute granted to *", "Copyrighted; Freely distributable", "Copyrighted; Permission granted to distribute non-commercially in SWORD format", "GFDL", "GPL", "Creative Commons: by-nc-nd*", "Creative Commons: by-nc-sa*",
        "Creative Commons: by-nc*", "Creative Commons: by-nd*", "Creative Commons: by-sa*", "Creative Commons: by*", "Creative Commons: CC0*", "General public license for distribution for any purpose"
    }),
    DISTRIBUTION_NOTES("DistributionNotes")
    {
        public boolean allowsContinuation() { return true; }

    },
    TEXT_SOURCE("TextSource")
    {
        public boolean allowsContinuation() { return true; }

    },
    UNLOCK_URL("UnlockURL"),
    DISTRIBUTION_SOURCE("DistributionSource")
    {
        public boolean allowsContinuation() { return true; }

    },
    LOCAL_STRIP_FILTER("LocalStripFilter"),
    SEARCH_OPTION("SearchOption"),
    SCOPE("Scope"),
    BOOK_LIST("BookList");

    private final String name;
    private final Object defaultValue;
    private final String[] picks;

    public static final String DIRECTION_LTOR = "LtoR";
    public static final String DIRECTION_RTOL = "RtoL";
    public static final String DIRECTION_BIDI = "bidi";

    ConfigEntryType(String name)
    {
        this.name = name;
        this.picks = null;
        String defValue = SwordBookMetaData.DEFAULTS.get(name);
        this.defaultValue = (defValue == null) ? null : convert(defValue);
    }

    ConfigEntryType(String name, String... picks)
    {
        this.name = name;
        this.picks = picks;
        String defValue = SwordBookMetaData.DEFAULTS.get(name);
        this.defaultValue = (defValue == null) ? null : convert(defValue);
    }

    public boolean isText()
    {
        return true;
    }

    public boolean isAllowed(String value)
    {
        if (value == null)
        {
            return false;
        }
        if (hasChoices())
        {
            for (String item : this.picks)
            {
                String val = value;
                String pick = item;
                if (pick.endsWith("*"))
                {
                    int len = pick.length() - 1;
                    pick = pick.substring(0, len);
                    if (val.length() > len)
                    {
                        val = val.substring(0, len);
                    }
                }
                if (pick.equalsIgnoreCase(val))
                {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public String filter(String value)
    {
        if (hasChoices())
        {
            for (String pick : this.picks)
            {
                if (pick.equals(value))
                {
                    return value;
                }
            }
            for (String pick : this.picks)
            {
                if (pick.equalsIgnoreCase(value))
                {
                    return pick;
                }
            }
        }
        return value;
    }

    public boolean allowsRTF()
    {
        return false;
    }

    public boolean allowsHTML()
    {
        return false;
    }

    public boolean allowsContinuation()
    {
        return false;
    }

    public boolean mayRepeat()
    {
        return false;
    }

    protected boolean hasChoices()
    {
        return (this.picks != null);
    }

    public Object getDefault()
    {
        return this.defaultValue;
    }

    public Object convert(String input)
    {
        return input;
    }

    public String unconvert(Object internal)
    {
        if (internal == null)
        {
            return null;
        }
        return internal.toString();
    }

    public static ConfigEntryType fromString(String name)
    {
        if (name != null)
        {
            if (name.startsWith(HISTORY.toString()))
            {
                return HISTORY;
            }
            for (ConfigEntryType o : values())
            {
                if (name.equals(o.name))
                {
                    return o;
                }
            }
        }
        return null;
    }

    public String toString()
    {
        return this.name;
    }

}
