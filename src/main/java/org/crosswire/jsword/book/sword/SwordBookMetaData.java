package org.crosswire.jsword.book.sword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.Filter;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.IniSection;
import org.crosswire.common.util.Language;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertyMap;

import org.crosswire.common.xml.XMLUtil;

import org.crosswire.jsword.JSMsg;

import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.KeyType;
import org.crosswire.jsword.book.MetaDataLocator;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractBookMetaData;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.book.filter.SourceFilterFactory;
import org.crosswire.jsword.versification.system.Versifications;

import static org.crosswire.jsword.book.MetaDataLocator.FRONTEND;
import static org.crosswire.jsword.book.MetaDataLocator.JSWORD;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import java.net.URI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;

public final class SwordBookMetaData extends AbstractBookMetaData 
{
    public static final String KEY_ABBREVIATION = "Abbreviation";
    public static final String KEY_ABOUT = "About";
    public static final String KEY_BLOCK_COUNT = "BlockCount";
    public static final String KEY_BLOCK_TYPE = "BlockType";
    public static final String KEY_CASE_SENSITIVE_KEYS = "CaseSensitiveKeys";
    public static final String KEY_CIPHER_KEY = "CipherKey";
    public static final String KEY_COMPRESS_TYPE = "CompressType";
    public static final String KEY_COPYRIGHT = "Copyright";
    public static final String KEY_COPYRIGHT_CONTACT_ADDRESS = "CopyrightContactAddress";
    public static final String KEY_COPYRIGHT_CONTACT_EMAIL = "CopyrightContactEmail";
    public static final String KEY_COPYRIGHT_CONTACT_NAME = "CopyrightContactName";
    public static final String KEY_COPYRIGHT_CONTACT_NOTES = "CopyrightContactNotes";
    public static final String KEY_COPYRIGHT_DATE = "CopyrightDate";
    public static final String KEY_COPYRIGHT_HOLDER = "CopyrightHolder";
    public static final String KEY_COPYRIGHT_NOTES = "CopyrightNotes";
    public static final String KEY_DATA_PATH = "DataPath";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_DIRECTION = "Direction";
    public static final String KEY_DISPLAY_LEVEL = "DisplayLevel";
    public static final String KEY_DISTRIBUTION_LICENSE = "DistributionLicense";
    public static final String KEY_DISTRIBUTION_NOTES = "DistributionNotes";
    public static final String KEY_DISTRIBUTION_SOURCE = "DistributionSource";
    public static final String KEY_ENCODING = "Encoding";
    public static final String KEY_FEATURE = "Feature";
    public static final String KEY_GLOBAL_OPTION_FILTER = "GlobalOptionFilter";
    public static final String KEY_SIGLUM1 = "Siglum1";
    public static final String KEY_SIGLUM2 = "Siglum2";
    public static final String KEY_SIGLUM3 = "Siglum3";
    public static final String KEY_SIGLUM4 = "Siglum4";
    public static final String KEY_SIGLUM5 = "Siglum5";
    public static final String KEY_GLOSSARY_FROM = "GlossaryFrom";
    public static final String KEY_GLOSSARY_TO = "GlossaryTo";
    public static final String KEY_HISTORY = "History";
    public static final String KEY_INSTALL_SIZE = "InstallSize";
    public static final String KEY_KEY_TYPE = "KeyType";
    public static final String KEY_LCSH = "LCSH";
    public static final String KEY_LOCAL_STRIP_FILTER = "LocalStripFilter";
    public static final String KEY_MINIMUM_VERSION = "MinimumVersion";
    public static final String KEY_MOD_DRV = "ModDrv";
    public static final String KEY_OBSOLETES = "Obsoletes";
    public static final String KEY_OSIS_Q_TO_TICK = "OSISqToTick";
    public static final String KEY_OSIS_VERSION = "OSISVersion";
    public static final String KEY_PREFERRED_CSS_XHTML = "PreferredCSSXHTML";
    public static final String KEY_SEARCH_OPTION = "SearchOption";
    public static final String KEY_SHORT_COPYRIGHT = "ShortCopyright";
    public static final String KEY_SHORT_PROMO = "ShortPromo";
    public static final String KEY_SOURCE_TYPE = "SourceType";
    public static final String KEY_STRONGS_PADDING = "StrongsPadding";
    public static final String KEY_SWORD_VERSION_DATE = "SwordVersionDate";
    public static final String KEY_TEXT_SOURCE = "TextSource";
    public static final String KEY_UNLOCK_URL = "UnlockURL";
    public static final String KEY_VERSION = "Version";

    public static final Map<String, String> DEFAULTS;

    private static boolean partialLoading;

    private static boolean debug_log = false;


    private static final Logger lgr = LoggerFactory.getLogger(SwordBookMetaData.class);

    private static final String THISMODULE = "org.crosswire.jsword.book.install.sword.SwordBookMetaData";    
    private static final String ENCODING_UTF8 = "UTF-8";
    private static final String ENCODING_LATIN1 = "WINDOWS-1252";
    private static final Pattern RTF_PATTERN = Pattern.compile("\\\\pard|\\\\pa[er]|\\\\qc|\\\\[bi]|\\\\u-?[0-9]{4,6}+");
    private static final Pattern HTML_PATTERN = Pattern.compile("(<[a-zA-Z]|[a-zA-Z]>)");
    private static final PropertyMap ENCODING_JAVA = new PropertyMap();

    private static final String[] REQUIRED = new String[]
    {
        "Abbreviation", "Description", "Lang", "Category", "Version", "Feature", 
        "GlobalOptionFilter", "Siglum1", "Siglum2", "Siglum3", "Siglum4", "Siglum5", 
        "Font", "DataPath", "ModDrv", "SourceType", "BlockType", "BlockCount", 
        "CompressType", "Encoding", "Direction", "KeyType", "DisplayLevel", 
        "Versification", "CaseSensitiveKeys", "LocalStripFilter", "PreferredCSSXHTML", 
        "StrongsPadding", "SearchOption", "InstallSize", "Scope", "BookList", "CipherKey"
    };

    private static final String[] OSIS_INFO = new String[]
    {
        "Abbreviation", "Description", "Lang", "Category", "LCSH", "SwordVersionDate", "Version", "History", "Obsoletes", "GlossaryFrom",
        "GlossaryTo", "About", "ShortPromo", "DistributionLicense", "DistributionNotes", "DistributionSource", "ShortCopyright", "Copyright", "CopyrightDate", "CopyrightHolder",
        "CopyrightContactName", "CopyrightContactAddress", "CopyrightContactEmail", "CopyrightContactNotes", "CopyrightNotes", "TextSource", "Feature", "GlobalOptionFilter", "Siglum1", "Siglum2",
        "Siglum3", "Siglum4", "Siglum5", "Font", "DataPath", "ModDrv", "SourceType", "BlockType", "BlockCount", "CompressType",
        "Encoding", "MinimumVersion", "OSISVersion", "OSISqToTick", "Direction", "KeyType", "DisplayLevel", "Versification", "CaseSensitiveKeys", "LocalStripFilter",
        "PreferredCSSXHTML", "StrongsPadding", "SearchOption", "InstallSize", "Scope", "BookList"
    };

    private static final String[] HIDDEN = new String[] { "CipherKey", "Language" };

    private static Filter keyKeepers;
  
    
    static
    {
        ENCODING_JAVA.put("Latin-1", "WINDOWS-1252");
        ENCODING_JAVA.put("UTF-8", "UTF-8");
    }

    static
    {
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("CompressType", "LZSS");
        tempMap.put("BlockType", "CHAPTER");
        tempMap.put("BlockCount", "200");
        tempMap.put("KeyType", "TreeKey");
        tempMap.put("Versification", "KJV");
        tempMap.put("Direction", "LtoR");
        tempMap.put("SourceType", "Plaintext");
        tempMap.put("Encoding", "Latin-1");
        tempMap.put("DisplayLevel", "1");
        tempMap.put("OSISqToTick", "true");
        tempMap.put("Version", "1.0");
        tempMap.put("MinimumVersion", "1.5.1a");
        tempMap.put("Category", "Other");
        tempMap.put("Lang", "en");
        tempMap.put("DistributionLicense", "Public Domain");
        tempMap.put("CaseSensitiveKeys", "false");
        tempMap.put("StrongsPadding", "true");
        DEFAULTS = Collections.unmodifiableMap(tempMap);
    }

    private boolean installed;
    private boolean filtered;
    private boolean supported;
    private boolean questionable;

    private String bookConf;

    private IniSection configAll;
    private IniSection configJSword;
    private IniSection configFrontend;

    private BookCategory bookCat;
    private BookType bookType;

    private File configFile;

    private Set keepers = new HashSet();

    public static void normalize(Writer out, IniSection config, String[] order)
    {
        int pos;
        PrintWriter writer;
        IniSection copy;
        Collection<String> values;
        List<String> knownKeys;
        Iterator<String> iter;
        Iterator<String> keys;
        
        pos = -1;
        writer = null;
        if ( out instanceof PrintWriter )
        {
            writer = (PrintWriter) out;
        }
        else
        {
            writer = new PrintWriter(out);
        }
        copy = new IniSection(config);
        adjustHistory(copy);
        knownKeys = new ArrayList<>(copy.getKeys());
        writer.print("[");
        writer.print(copy.getName());
        writer.print("]");
        writer.println();
        for ( String key : order )
        {
            knownKeys.remove(key);
            if (copy.containsKey(key))
            {
                values = copy.getValues(key);
                iter = values.iterator();
                
                while ( iter.hasNext() )
                {
                    String value = iter.next();
                    String newKey = key;
                    if ( "History".equalsIgnoreCase(key) )
                    {
                        pos = value.indexOf(' ');
                        newKey = newKey + '_' + value.substring(0, pos);
                        value = value.substring(pos + 1);
                    }
                    writer.print(newKey);
                    writer.print("=");
                    writer.print(value.replaceAll("\n", " \\\\\n"));
                    writer.println();
                }
            }
        }
        keys = knownKeys.iterator();
        
        while ( keys.hasNext() )
        {
            String key = keys.next();
            values = copy.getValues(key);
            iter = values.iterator();
            while ( iter.hasNext() )
            {
                String value = iter.next();
                writer.print(key);
                writer.print("=");
                writer.print(value.replaceAll("\n", " \\\\\n"));
                writer.println();
            }
        }
        writer.flush();
    }

    public static void setPartialLoading(boolean partial)
    {
        if (partial != partialLoading)
        {
            if (partial)
            {
                keyKeepers = new KeyFilter(REQUIRED);
            }
            else
            {
                keyKeepers = null;
            }
        }
        partialLoading = partial;
    }

    public static void report(IniSection config)
    {
        int i;
        int count;
        String buf;
        
        i = 0;
        count = 0;
        buf = null;
        
        buf = config.report();
        for (String key : config.getKeys())
        {
            ConfigEntryType type = ConfigEntryType.fromString(key);
            if (type == null
                && key.contains("_"))
            {
                String baseKey = key.substring(0, key.indexOf('_'));
                type = ConfigEntryType.fromString(baseKey);
            }
            count = config.size(key);

            for ( i = 0; i < count; i++ )
            {
                String value = config.get(key, i);
                if ( type == null )
                {
                    buf += "Unknown entry: " + key + "=" + value + "\n";
                }
                else if ( (value.length() == 0) && (type != ConfigEntryType.CIPHER_KEY) )
                {
                    buf += "Unexpected empty entry: " + key + "=" + value + "\n";
                }
                else
                {
                    value = type.filter(value);
                    if ( !type.allowsRTF() && RTF_PATTERN.matcher(value).find() )
                    {
                        buf += "Unexpected RTF: " + key + value + "\n";
                    }
                    
                    if ( !type.allowsHTML() && HTML_PATTERN.matcher(value).find() )
                    {
                        buf += "Unexpected HTML: " + key + value + "\n";
                    }

                    if ( !type.isAllowed(value) )
                    {
                        buf += "Unexpected congfig value: " + key + value + "\n";
                    }

                    if ( (count > 1) && !type.mayRepeat() )
                    {
                        buf += "Unexpected repeat of config key: " + key + value + "\n";
                    }
                }
            }
        }

        if ( !buf.isBlank() && debug_log )
        {
            String msg = "Conf report for [" + config.getName() + "]\n" + buf.toString();
            lgr.info(msg,THISMODULE);
        }
    }

    private static void adjustHistory(IniSection config)
    {
        int pos;
        String value;
        ConfigEntryType type;
        List<String> keys;

        pos = -1;
        value = null;
        type = null;
        keys = new ArrayList<String>(config.getKeys());

        for (String key : keys)
        {
            value = config.get(key);
            type = ConfigEntryType.fromString(key);
            if ( ConfigEntryType.HISTORY.equals(type) )
            {
                config.remove(key);
                pos = key.indexOf('_');
                value = key.substring(pos + 1) + ' ' + value;
                config.add("History", value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public SwordBookMetaData(File file, URI bookRootPath) throws IOException, BookException
    {
        installed = true;
        configFile = file;
        bookConf = file.getName();
        if ( debug_log ) { lgr.info("config file="+bookConf); }
        setLibrary(bookRootPath);
        configAll = new IniSection();
        filtered = true;
        try
        {
            reload(keyKeepers);
        }
        catch ( BookException e )
        {
            String msg = "reload(): error reloading meta data - " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
    }

    @SuppressWarnings("unchecked")
    public SwordBookMetaData(byte[] buffer, String bc) throws IOException, BookException
    {
        installed = false;
        bookConf = bc;
        supported = true;
        configAll = new IniSection();
        filtered = true;
        loadBuffer(buffer, keyKeepers);
        adjustConfig();
        report(configAll);
    }

    public void reload() throws BookException    { reload(null); }

    /**
     * @brief Overloaded reload function to include keepers param
     * 
     * @param keepers
     * @throws BookException 
     */
    public void reload(Filter<String> keepers) throws BookException
    {
        if ( !filtered && (keepers == null) ) { return; }
        
        supported = true;
        
        if ( configJSword != null )         { configJSword.clear(); }
        if ( configFrontend != null )       { configFrontend.clear(); }
        try
        {
            if ( installed )
            {
                loadFile(keepers);
            }
            else
            {
                byte[] buffer = IOUtil.getZipEntry(bookConf);
                loadBuffer(buffer, keepers);
            }
            adjustConfig();
            report(configAll);
            configJSword = addConfig(MetaDataLocator.JSWORD);
            configFrontend = addConfig(MetaDataLocator.FRONTEND);
        }
        catch (IOException ex)
        {
            String msg = "I/O error and unable to load conf " + keepers + " - " + ex.getMessage();
            lgr.error(msg,THISMODULE);
            throw new BookException("unable to load conf", ex);
        }
    }

    public void setProperty(String key, String value)  { configAll.replace(key, value); }

    public void putProperty(String key, String value, boolean forFrontend)
    {
        MetaDataLocator mdl = forFrontend ? MetaDataLocator.FRONTEND : MetaDataLocator.JSWORD;
        putProperty(key, value, mdl);
    }

    /**
     * @brief Overloaded version of putProperty with different params
     * 
     * @param key
     * @param value
     * @param metaDataLocator 
     */
    public void putProperty(String key, String value, MetaDataLocator metaDataLocator)
    {
        setProperty(key, value);
        if ( !installed )   { return; }
        File writeLocation = metaDataLocator.getWriteLocation();
        if ( writeLocation == null )  { return; }
        IniSection config = null;
        switch (metaDataLocator)
        {
        case FRONTEND ->
            {
                if (configFrontend == null)
                {
                    configFrontend = new IniSection(configAll.getName());
                }
                config = configFrontend;
            }
        case JSWORD ->
            {
                if (configJSword == null)
                {
                    configJSword = new IniSection(configAll.getName());
                }
                config = configJSword;
            }
        }
        
        if (config != null)
        {
            config.replace(key, value);
            try
            {
                config.save( new File(writeLocation, bookConf ), getBookCharset());
            }
            catch (IOException ex)
            {
                String msg  =  "Unable to save ="  + key
                            +  "conf file for [" + value + configAll.getName()
                            + "\n    Error="+ ex.getMessage();
                lgr.error(msg,SwordBookMetaData.class);
            }
        }
    }

    public boolean isQuestionable()  { return questionable; }
    public boolean isSupported()     { return supported;  }
    public boolean isEnciphered()    
    {
        String cipher = getProperty("CipherKey");
        return (cipher != null);
    }

    public boolean isLocked()
    {
        String cipher = getProperty("CipherKey");
        return (cipher != null && cipher.length() == 0);
    }

    public boolean unlock(String unlockKey)
    {
        putProperty("CipherKey", unlockKey, false);
        return true;
    }

    public boolean isLeftToRight()
    {
        String dir = getProperty("Direction");
        if ( "bidi".equalsIgnoreCase(dir) )
        {
            Language lang = getLanguage();
            return lang.isLeftToRight();
        }
        return "LtoR".equalsIgnoreCase(dir);
    }

    public boolean hasFeature(FeatureType feature)
    {
        String name = feature.toString();
        String buff = "";

        if ( configAll.containsValue("Feature", name) ) { return true; }
        buff = getProperty("SourceType") + name;
        if ( configAll.containsValue("GlobalOptionFilter", buff) ) { return true; }
        String alias = feature.getAlias();
        buff = "";
        buff = getProperty("SourceType") + alias;
        return ( configAll.containsValue("GlobalOptionFilter", name) || configAll.containsValue("GlobalOptionFilter", buff));
    }

    public String getUnlockKey()    { return getProperty("CipherKey"); }
    public String getName()         { return getProperty("Description"); }
    public String getBookCharset()  { return ENCODING_JAVA.get(getProperty("Encoding")); }
    public String getInitials()     { return configAll.getName(); }
    public String getAbbreviation()
    {
        String abbreviation = getProperty("Abbreviation");
        if (abbreviation != null && abbreviation.length() > 0) { return abbreviation; }
        return getInitials();
    }

    public String getProperty(String key)
    {
        if ("Language".equals(key))    { return getLanguage().getName(); }
        return configAll.get(key, DEFAULTS.get(key));
    }

    public KeyType getKeyType()
    {
        BookType bt = getBookType();
        if (bt == null)
        {
            return null;
        }
        return bt.getKeyType();
    }

    public BookType getBookType()      { return bookType; }

    public SourceFilter getFilter()
    {
        String sourcetype = getProperty("SourceType");
        return SourceFilterFactory.getFilter(sourcetype);
    }

    public File getConfigFile()    { return configFile; }

    public BookCategory getBookCategory()
    {
        if ( bookCat == null )
        {
            bookCat = (BookCategory) getValue("Category");
            if ( bookCat == BookCategory.OTHER )
            {
                BookType bt = getBookType();
                if ( bt == null )      { return bookCat; }
                bookCat = bt.getBookCategory();
            }
        }
        return bookCat;
    }

    public Document toOSIS()
    {
        List<String> knownKeys = new ArrayList<>(configAll.getKeys());
        OSISUtil.OSISFactory factory = OSISUtil.factory();
        Element table = factory.createTable();
        Element row = toRow(factory, "Initials", getInitials());

        table.addContent(row);

        for ( String key : OSIS_INFO )
        {
            knownKeys.remove(key);
            row = toRow(factory, key);
            if ( row != null )
            {
                table.addContent((Content) row);
            }
        }

        List<String> hide = Arrays.asList(HIDDEN);

        for ( String key : knownKeys )
        {
            if ( hide.contains(key) )
            {
                continue;
            }
            row = toRow(factory, key);
            if ( row != null )  { table.addContent((Content) row); }
        }
        return new Document(table);
    }

    public Set<String> getPropertyKeys()   { return null; }

    private void loadFile(Filter<String> keepers) throws IOException
    {
        filtered = (keepers != null);
        configAll.clear();
        configAll.load(configFile, "UTF-8", keepers);
        String encoding = configAll.get("Encoding");
        if ( !"UTF-8".equalsIgnoreCase(encoding) )
        {
            configAll.clear();
            configAll.load(configFile, "WINDOWS-1252", keepers);
        }
    }

    private void loadBuffer(byte[] buffer, Filter<String> keepers) throws IOException
    {
        filtered = (keepers != null);
        configAll.clear();
        configAll.load(buffer, "UTF-8", keepers);
        String encoding = configAll.get("Encoding");

        if ( !"UTF-8".equalsIgnoreCase(encoding) )
        {
            configAll.clear();
            configAll.load(buffer, "WINDOWS-1252", keepers);
        }
    }

    private void mergeConfig(IniSection config)
    {
        for (String key : config.getKeys())
        {
            ConfigEntryType type = ConfigEntryType.fromString(key);
            for (String value : config.getValues(key))
            {
                if (type != null && type.mayRepeat())
                {
                    if (!configAll.containsValue(key, value))
                    {
                        configAll.add(key, value);
                    }
                    continue;
                }
                setProperty(key, value);
            }
        }
    }

    private void adjustConfig() throws BookException
    {
        adjustLocation();
        adjustLanguage();
        adjustBookType();
        adjustName();
        adjustHistory(configAll);
    }

    private void adjustLanguage()
    {
        String lang = getProperty("Lang");
        String langFrom = configAll.get("GlossaryFrom");
        String langTo   = configAll.get("GlossaryTo");


        testLanguage("Lang", lang);
        if (langFrom   != null || langTo != null)
        {
            if (langFrom == null)
            {
                langFrom = lang;
                setProperty("GlossaryFrom", langFrom);
                if ( debug_log ) 
                {
                    String msg = "Missing data for [" + configAll.getName() + "]. "
                               + "Assuming " + langFrom + " = " + langTo;
                    lgr.warn(msg,THISMODULE);
                }
            }
            testLanguage("GlossaryFrom", langFrom);

            if (langTo == null)
            {
                langTo = Language.DEFAULT_LANG.getGivenSpecification();
                setProperty("GlossaryTo", langTo);
                if ( debug_log ) 
                {
                    String msg = "Missing data for [" + configAll.getName()
                               + "]. Assuming GlossaryTo=" + langTo;
                    lgr.info(msg,THISMODULE);
                }
            }

            testLanguage("GlossaryTo", langTo);

            if ( !langFrom.equals(lang) && !langTo.equals(lang) )
            {
                String msg = "Data error in [" + configAll.getName()
                           + "]. Neither GlossaryFrom or GlossaryTo match Lang";
                lgr.error(msg,THISMODULE);
            }
        }
        setLanguage((Language) getValue("Lang"));
    }

    private void testLanguage(String key, String lang)
    {
        Language language = new Language(lang);
        if (!language.isValidLanguage())
        {
            if ( debug_log )
            {
                String msg = "Unknown language [" + configAll.getName()
                           + "] " + key + " = " + lang;
                lgr.info(msg,THISMODULE);
            }
        }
    }

    private void adjustBookType()
    {
        String modTypeName = getProperty("ModDrv");
        BookCategory focusedCategory = (BookCategory) getValue("Category");

        questionable = (focusedCategory == BookCategory.QUESTIONABLE);

        if ( modTypeName == null )
        {
            String msg = "Null detected in ModDrv type"; 
            if ( debug_log ) { lgr.info(msg); }
            supported = false;
            return;
        }

        String v11n = getProperty("Versification");

        if (!Versifications.instance().isDefined(v11n))
        {
            String msg = "Book not supported: Unknown versification for [" + configAll.getName()
                       + "] Versification=" + v11n;
            lgr.error(msg,THISMODULE);
            supported = false;
            return;
        }

        bookType = BookType.fromString(modTypeName);

        if ( bookType == null )
        {
            String msg = "Book not supported: malformed conf file for [" + configAll.getName()
                       + "] no book type found";
            if ( debug_log ) { lgr.info(msg); }
            supported = false;
            return;
        }
        if ( focusedCategory == BookCategory.OTHER ) { focusedCategory = bookType.getBookCategory(); }
        setProperty("Category", focusedCategory.getName());
    }

    private void adjustName()
    {
        String desc;
        String msg;
        
        desc = null;
        msg  = null;
        
        desc = configAll.get("Description");
        if ( desc == null )
        {
            msg = "Conf description missing [\'Description\'" 
                       + "].";
            if ( debug_log ) { lgr.info(msg); }
            setProperty("Description", configAll.getName());
        }
    }

    @SuppressWarnings("UnusedAssignment")
    private void adjustLocation() throws BookException
    {
        // declare local vars
        boolean isDirectoryPath;
        boolean doesexist;
        int     lastSlash;
        String  msg;
        String  datapath;
        File    bookDir;
        URI     library;
        URI     location;

        // init local vars for use
        library = getLibrary();
        if (library == null) { return; }
        if ( debug_log ) { lgr.info("library="+library.toString()); }

        isDirectoryPath = false;
        doesexist = false;
        lastSlash = 0;
        bookDir = null;
        location = null;
        datapath = null;
        msg = "";
        datapath = getProperty("DataPath");
        if ( debug_log ) { lgr.info("DataPath property="+datapath); }
        lastSlash = datapath.lastIndexOf('/');
        // test certain vars to continue
        if (lastSlash == -1)  { return; }
        // more testing
        if (lastSlash == datapath.length() - 1)
        {
            isDirectoryPath = true;
            if ( debug_log ) { lgr.info("datapath="+datapath); }
            datapath = datapath.substring(0, lastSlash);
        }
        location = NetUtil.lengthenURI(library, datapath);
        bookDir = new File(location.getPath());
        // all testing done and vars set 

        if ( !bookDir.isDirectory() ) // is this location a folder name
        {
            if ( isDirectoryPath ) // does it end with path separator char
            {
                // yes so the asssumption is there are no files here
                msg = bookDir.getAbsolutePath() + " folder is empty";
                lgr.error(msg,THISMODULE);
                throw new BookException(
                  JSMsg.gettext("The book {0} is missing its data files", new Object[] { configAll.getName() })
                );
            }

            lastSlash = datapath.lastIndexOf('/');
            datapath = datapath.substring(0, lastSlash);
            location = NetUtil.lengthenURI(library, datapath);

            // good pathname so find the file
            File bookFile = new File(bookDir.getAbsolutePath() + ".dat");

            doesexist = bookFile.exists();  // does it exist
            if ( !doesexist )
            {
                msg = bookFile.getAbsolutePath() + " file not found - "
                    + "\n    bookfile=" + bookDir.getAbsolutePath();
                lgr.error(msg,THISMODULE);
                throw new BookException(JSMsg.gettext("The book {0} is missing its data files", new Object[]
                {
                    configAll.getName()
                }));
            }
        }
        setLocation(location);
    }

    private String getInternalName() { return configAll.getName(); }

    private Object getValue(String key)
    {
        ConfigEntryType type = ConfigEntryType.fromString(key);
        String ce = getProperty(key);
        if (type == null)
        {
            return ce;
        }
        return (ce == null) ? null : type.convert(ce);
    }

    private IniSection addConfig(MetaDataLocator locator)
    {
        File conf = new File(locator.getWriteLocation(), bookConf);

        if (!conf.exists()) { conf = new File(locator.getReadLocation(), bookConf); }

        if (conf.exists())
        {
            String encoding = getProperty("Encoding");
            try
            {
                IniSection config = new IniSection();
                config.load(conf, encoding);
                mergeConfig(config);
                return config;
            }
            catch (IOException e)
            {
                String msg = "Unable to load conf " + conf
                           + "Error=" + e.getMessage();
                lgr.error(msg,THISMODULE);
            }
        }
        return null;
    }

    private Element toRow(OSISUtil.OSISFactory factory, String key, String value)
    {
        Element nameEle = toKeyCell(factory, key);
        Element valueElement = factory.createCell();
        valueElement.addContent(value);
        Element rowEle = factory.createRow();
        rowEle.addContent((Content) nameEle);
        rowEle.addContent((Content) valueElement);
        return rowEle;
    }

    private Element toRow(OSISUtil.OSISFactory factory, String key)
    {
        int size = configAll.size(key);
        if (size == 0)
        {
            return null;
        }
        ConfigEntryType type = ConfigEntryType.fromString(key);
        Element nameEle = toKeyCell(factory, key);
        Element valueElement = factory.createCell();
        for (int j = 0; j < size; j++)
        {
            if (j > 0) { valueElement.addContent((Content) factory.createLB()); }
            String text = configAll.get(key, j);
            if (type != null && !type.isText() && type.isAllowed(text))
            {
                text = type.convert(text).toString();
            }
            text = XMLUtil.escape(text);
            if (type != null && type.allowsRTF())
            {
                valueElement.addContent(OSISUtil.rtfToOsis(text));
            }
            else
            {
                valueElement.addContent(text);
            }
        }
        Element rowEle = factory.createRow();
        rowEle.addContent((Content) nameEle);
        rowEle.addContent((Content) valueElement);
        return rowEle;
    }

    private Element toKeyCell(OSISUtil.OSISFactory factory, String key)
    {
        Element nameEle = factory.createCell();
        Element hiEle = factory.createHI();
        hiEle.setAttribute("type", "bold");
        nameEle.addContent((Content) hiEle);
        hiEle.addContent(key);
        return nameEle;
    }

    private static final class KeyFilter implements Filter<String>
    {
        private Set kprs = new HashSet();

        KeyFilter(String[] keepers)
        {
            for (String key : keepers)
            {
                kprs.add(key);
            }
        }

        public boolean test(String key)
        {
            return kprs.contains(key);
        }
    }
}