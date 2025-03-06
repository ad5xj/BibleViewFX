package org.crosswire.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @ingroup org.crosswire.common.util
 * @brief Helper class to read Windows Style .ini file
 */
public final class IniSection implements Iterable 
{
    private static final int MAX_BUFF_SIZE = 2048;

    private static boolean more(String line)
    {
        int length = line.length();
        return (length > 0 && line.charAt(length - 1) == '\\');
    }

    private String name;
    private String report;
    private String charset;
    private String warnings;

    private File configFile;

    private Map<String, List<String>> section;

    /**
     * @brief Overload constructor with no params
     */
    public IniSection() { this((String) null); }

    /**
     * @brief Overloaded constructor with key param
     * @param n
     */
    public IniSection(String n)
    {
        name = n;
        section = new HashMap<>();
        warnings = "";
    }

    /**
     * @brief Overloaded constructor with IniSection param
     * @param config IniSection
     */
    public IniSection(IniSection config)
    {
        name = config.getName();
        section = new HashMap<>();
        for (String key : config.getKeys())
        {
            for (String value : config.getValues(key))
            {
                add(key, value);
            }
        }
    }

    public void clear()
    {
        section.clear();
        warnings = "";
        report = "";
    }

    public void setName(String n)
    {
        name = n;
    }

    public String getName()
    {
        return name;
    }

    public int size()
    {
        return section.size();
    }

    public int size(String key)
    {
        Collection<String> values = section.get(key);
        return (values == null) ? 0 : values.size();
    }

    public boolean isEmpty()
    {
        return this.section.isEmpty();
    }

    public Iterator iterator()
    {
        return this.section.keySet().iterator();
    }

    public Collection<String> getKeys()
    {
        return Collections.unmodifiableSet(this.section.keySet());
    }

    public boolean containsKey(String key)
    {
        return this.section.containsKey(key);
    }

    public boolean containsValue(String value)
    {
        for (Collection<String> collection : section.values())
        {
            if (collection.contains(value))
            {
                return true;
            }
        }
        return false;
    }

    public boolean containsValue(String key, String value)
    {
        Collection<String> values = section.get(key);
        return (values != null && values.contains(value));
    }

    public boolean add(String key, String value)
    {
        if (!allowed(key, value))
        {
            return false;
        }
        Collection<String> values = getOrCreateValues(key);
        if (values.contains(value))
        {
            warnings += "Duplicate value: " + key + " = " + value + "\n";
            return true;
        }
        return values.add(value);
    }

    public Collection<String> getValues(String key)
    {
        if (this.section.containsKey(key))
        {
            return Collections.unmodifiableCollection(this.section.get(key));
        }
        return null;
    }

    public String get(String key, int index)
    {
        List<String> values = this.section.get(key);
        return (values == null) ? null : values.get(index);
    }

    public String get(String key)
    {
        List<String> values = this.section.get(key);
        return (values == null) ? null : values.get(0);
    }

    public String get(String key, String defaultValue)
    {
        List<String> values = this.section.get(key);
        return (values == null) ? defaultValue : values.get(0);
    }

    public boolean remove(String key, String value)
    {
        Collection<String> values = this.section.get(key);
        if (values == null)
        {
            return false;
        }
        boolean changed = values.remove(value);
        if (changed
            && values.isEmpty())
        {
            this.section.remove(key);
        }
        return changed;
    }

    public boolean remove(String key)
    {
        Collection<String> values = this.section.get(key);
        if (values == null)
        {
            return false;
        }
        this.section.remove(key);
        return true;
    }

    public boolean replace(String key, String value)
    {
        if (!allowed(key, value))
        {
            return false;
        }
        Collection<String> values = getOrCreateValues(key);
        values.clear();
        return values.add(value);
    }

    /**
     * 
     * @param is InputStream
     * @param encoding String
     * 
     * @throws IOException 
     */
    public void load(InputStream is, String encoding) throws IOException
    {
        load(is, encoding, (Filter<String>) null);
    }

    /**
     * @brief Overloaded function with byte array and encoding params
     * 
     * @param buffer
     * @param encoding
     * @throws IOException 
     */
    public void load(byte[] buffer, String encoding) throws IOException
    {
        load(buffer, encoding, (Filter<String>) null);
    }

    /**
     * @brief Overloaded function with file and encoding param
     * 
     * @param file
     * @param encoding
     * @throws IOException 
     */
    public void load(File file, String encoding) throws IOException
    {
        load(file, encoding, (Filter<String>) null);
    }

    /**
     * @brief Overloaded function to include filter param
     * 
     * @param is
     * @param encoding
     * @param filter
     * @throws IOException 
     */
    public void load(InputStream is, String encoding, Filter<String> filter) throws IOException
    {
        Reader in = null;
        try
        {
            in = new InputStreamReader(is, encoding);
            doLoad(in, filter);
        }
        finally
        {
            if (in != null)
            {
                in.close();
                in = null;
            }
        }
    }


    /**
     * @brief Overloaded function to include file, encoding and filter params
     * 
     * @param file
     * @param encoding
     * @param filter
     * @throws IOException 
     */
    public void load(File file, String encoding, Filter<String> filter) throws IOException
    {
        this.configFile = file;
        this.charset = encoding;
        InputStream in = null;
        try
        {
            in = new FileInputStream(file);
            load(in, encoding, filter);
        }
        finally
        {
            if (in != null)
            {
                in.close();
                in = null;
            }
        }
    }

    /**
     * @brief Overloaded function to include byte array, encoding and filter params
     * 
     * @param buffer
     * @param encoding
     * @param filter
     * @throws IOException 
     */
    public void load(byte[] buffer, String encoding, Filter<String> filter) throws IOException
    {
        InputStream in = null;
        try
        {
            in = new ByteArrayInputStream(buffer);
            load(in, encoding, filter);
        }
        finally
        {
            if (in != null)
            {
                in.close();
                in = null;
            }
        }
    }

    public void save() throws IOException
    {
        assert this.configFile != null;
        assert this.charset != null;
        if (this.configFile != null && this.charset != null)
        {
            save(this.configFile, this.charset);
        }
    }

    public void save(File file, String encoding) throws IOException
    {
        this.configFile = file;
        this.charset = encoding;
        Writer out = null;
        try
        {
            out = new OutputStreamWriter(new FileOutputStream(file), encoding);
            save(out);
        }
        finally
        {
            if (out != null)
            {
                out.close();
                out = null;
            }
        }
    }

    public void save(Writer out)
    {
        PrintWriter writer = null;
        if (out instanceof PrintWriter)
        {
            writer = (PrintWriter) out;
        }
        else
        {
            writer = new PrintWriter(out);
        }
        writer.print("[");
        writer.print(this.name);
        writer.print("]");
        writer.println();
        boolean first = true;
        Iterator<String> keys = this.section.keySet().iterator();
        while (keys.hasNext())
        {
            String key = keys.next();
            Collection<String> values = this.section.get(key);
            Iterator<String> iter = values.iterator();
            while (iter.hasNext())
            {
                if (!first)
                {
                    writer.println();
                    first = false;
                }
                String value = iter.next();
                writer.print(key);
                writer.print(" = ");
                writer.print(format(value));
                writer.println();
            }
        }
        writer.flush();
    }

    public String report()
    {
        String str = this.report;
        this.report = "";
        return str;
    }

    private String format(String value)
    {
        return value.replaceAll("\n", " \\\\\n\t");
    }

    private Collection<String> getOrCreateValues(String key)
    {
        List<String> values = this.section.get(key);
        if (values == null)
        {
            values = new ArrayList<String>();
            this.section.put(key, values);
        }
        return values;
    }

    private void doLoad(Reader in, Filter<String> filter) throws IOException
    {
        BufferedReader bin = null;
        try
        {
            if (in instanceof BufferedReader)
            {
                bin = (BufferedReader) in;
            }
            else
            {
                bin = new BufferedReader(in, 2048);
            }
            while (true)
            {
                String line = advance(bin);
                if (line == null)
                {
                    break;
                }
                if (isSectionLine(line))
                {
                    this.name = line.substring(1, line.length() - 1);
                    continue;
                }
                int splitPos = getSplitPos(line);
                if (splitPos < 0)
                {
                    warnings += "Skipping: Expected to see '=' in: " + line + "\n";
                    continue;
                }
                String key = line.substring(0, splitPos).trim();
                String value = more(bin, line.substring(splitPos + 1).trim());
                if (filter == null || filter.test(key))
                {
                    add(key, value);
                }
            }
            report = warnings;
            warnings = "";
        }
        finally
        {
            if (bin != null)
            {
                bin.close();
                bin = null;
            }
        }
    }

    private String advance(BufferedReader bin) throws IOException
    {
        String trimmed = null;
        for (String line = bin.readLine(); line != null; line = bin.readLine())
        {
            trimmed = line.trim();
            if (!isCommentLine(trimmed))
            {
                return trimmed;
            }
        }
        return null;
    }

    private boolean isCommentLine(String line)
    {
        if (line == null)
        {
            return false;
        }
        if (line.length() == 0)
        {
            return true;
        }
        char firstChar = line.charAt(0);
        return (firstChar == ';' || firstChar == '#');
    }

    private boolean isSectionLine(String line)
    {
        return (line.charAt(0) == '[' && line.charAt(line.length() - 1) == ']');
    }

    private int getSplitPos(String line)
    {
        return line.indexOf('=');
    }

    private String more(BufferedReader bin, String value) throws IOException
    {
        boolean moreCowBell = false;
        String line = value;
        String buf = "";
        do
        {
            moreCowBell = more(line);
            if (moreCowBell)
            {
                line = line.substring(0, line.length() - 1).trim();
            }
            buf += line;
            if (!moreCowBell)
            {
                continue;
            }

            buf += "\n";
            line = advance(bin);
            int splitPos = getSplitPos(line);
            if (splitPos < 0)
            {
                continue;
            }
            warnings += "Possible trailing continuation on previous line. Found: " + line + "\n";
        }
        while (moreCowBell && line != null);
        String cowBell = buf;
        buf = null;
        line = null;
        return cowBell;
    }

    private boolean allowed(String key, String value)
    {
        if ((key == null) || (key.length() == 0) || (value == null))
        {
            if (key == null)
            {
                warnings += "Null keys not allowed: " + " = " + value + "\n";
            }
            else if (key.length() == 0)
            {
                warnings += "Empty keys not allowed: " + " = " + value + "\n";
            }
            if (value == null)
            {
                warnings += "Null values not allowed: " + " = " + value + "\n";
            }
            return false;
        }
        return true;
    }
}