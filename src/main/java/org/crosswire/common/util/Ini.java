package org.crosswire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

final class Ini {

    private Map<String, IniSection> sectionMap = new TreeMap<String, IniSection>(String.CASE_INSENSITIVE_ORDER);

    private List<String> list = new ArrayList<String>();

    private static final int MAX_BUFF_SIZE = 8192;

    public void clear() {
        this.sectionMap.clear();
        this.list.clear();
    }

    public int size() {
        return this.sectionMap.size();
    }

    public List<String> getSections() {
        return Collections.unmodifiableList(this.list);
    }

    public String getSectionName(int index) {
        return this.list.get(index);
    }

    public String getSectionName() {
        return (size() == 0) ? null : this.list.get(0);
    }

    public int getValueSize(String sectionName, String key) {
        IniSection section = doGetSection(sectionName);
        return (section == null) ? 0 : section.size(key);
    }

    public int getValueSize(String key) {
        IniSection section = getSection();
        return (section == null) ? 0 : section.size(key);
    }

    public String getValue(String sectionName, String key, int index) {
        IniSection section = doGetSection(sectionName);
        return (section == null) ? null : section.get(key, index);
    }

    public String getValue(String sectionName, String key) {
        IniSection section = doGetSection(sectionName);
        return (section == null) ? null : section.get(key, 0);
    }

    public String getValue(String key, int index) {
        IniSection section = getSection();
        return (section == null) ? null : section.get(key, index);
    }

    public String getValue(String key) {
        IniSection section = getSection();
        return (section == null) ? null : section.get(key);
    }

    public boolean add(String sectionName, String key, String value) {
        IniSection section = getOrCreateSection(sectionName);
        return section.add(key, value);
    }

    public boolean replace(String sectionName, String key, String value) {
        IniSection section = getOrCreateSection(sectionName);
        return section.replace(key, value);
    }

    public boolean remove(String sectionName, String key, String value) {
        IniSection section = this.sectionMap.get(sectionName);
        if (section == null) {
            return false;
        }
        boolean changed = section.remove(key, value);
        if (changed
            && section.isEmpty()) {
            this.sectionMap.remove(sectionName);
            this.list.remove(sectionName);
        }
        return changed;
    }

    public boolean remove(String sectionName, String key) {
        IniSection section = this.sectionMap.get(sectionName);
        if (section == null) {
            return false;
        }
        boolean changed = section.remove(key);
        this.sectionMap.remove(sectionName);
        this.list.remove(sectionName);
        return changed;
    }

    public IniSection getSection() {
        return (size() == 0) ? null : this.sectionMap.get(this.list.get(0));
    }

    public Collection<String> getKeys() {
        IniSection section = getSection();
        return (section == null) ? null : section.getKeys();
    }

    public Collection<String> getValues(String key) {
        IniSection section = getSection();
        return (section == null) ? null : section.getValues(key);
    }

    public boolean addValue(String key, String value) {
        IniSection section = getSection();
        return (section == null || section.add(key, value));
    }

    public boolean removeValue(String key, String value) {
        String section = getSectionName();
        return (section == null || remove(section, key, value));
    }

    public boolean removeValue(String key) {
        String section = getSectionName();
        return (section == null || remove(section, key));
    }

    public boolean replaceValue(String key, String value) {
        IniSection section = getSection();
        return (section == null || section.replace(key, value));
    }

    public void load(InputStream is, String encoding) throws IOException {
        Reader in = null;
        try {
            in = new InputStreamReader(is, encoding);
            doLoad(in);
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
        }
    }

    public void load(File file, String encoding) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            load(in, encoding);
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
        }
    }

    public void load(byte[] buffer, String encoding) throws IOException {
        InputStream in = null;
        try {
            in = new ByteArrayInputStream(buffer);
            load(in, encoding);
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
        }
    }

    public void save(File file, String encoding) throws IOException {
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(file), encoding);
            save(out);
        } finally {
            if (out != null) {
                out.close();
                out = null;
            }
        }
    }

    private void save(Writer out) {
        PrintWriter writer = null;
        if (out instanceof PrintWriter) {
            writer = (PrintWriter) out;
        } else {
            writer = new PrintWriter(out);
        }
        for (String sectionName : this.list) {
            IniSection section = doGetSection(sectionName);
            section.save(writer);
        }
    }

    private IniSection doGetSection(String sectionName) {
        return this.sectionMap.get(sectionName);
    }

    private IniSection getOrCreateSection(String sectionName) {
        IniSection section = this.sectionMap.get(sectionName);
        if (section == null) {
            section = new IniSection(sectionName);
            this.sectionMap.put(sectionName, section);
            this.list.add(sectionName);
        }
        return section;
    }

    private void doLoad(Reader in) throws IOException {
        BufferedReader bin = null;
        try {
            if (in instanceof BufferedReader) {
                bin = (BufferedReader) in;
            } else {
                bin = new BufferedReader(in, 8192);
            }
            String sectionName = "";
            StringBuilder buf = new StringBuilder();
            while (true) {
                buf.setLength(0);
                String line = advance(bin);
                if (line == null) {
                    break;
                }
                if (isSectionLine(line)) {
                    sectionName = line.substring(1, line.length() - 1);
                    continue;
                }
                int splitPos = getSplitPos(line);
                if (splitPos < 0) {
                    LOGGER.warn("Expected to see '=' in [{}]: {}", sectionName, line);
                    continue;
                }
                String key = line.substring(0, splitPos).trim();
                if (key.length() == 0) {
                    LOGGER.warn("Empty key in [{}]: {}", sectionName, line);
                }
                String value = more(bin, line.substring(splitPos + 1).trim());
                add(sectionName, key, value);
            }
        } finally {
            if (bin != null) {
                bin.close();
                bin = null;
            }
        }
    }

    private String advance(BufferedReader bin) throws IOException {
        String trimmed = null;
        for (String line = bin.readLine(); line != null; line = bin.readLine()) {
            trimmed = line.trim();
            if (!isCommentLine(trimmed)) {
                return trimmed;
            }
        }
        return null;
    }

    private boolean isCommentLine(String line) {
        if (line == null) {
            return false;
        }
        if (line.length() == 0) {
            return true;
        }
        char firstChar = line.charAt(0);
        return (firstChar == ';' || firstChar == '#');
    }

    private boolean isSectionLine(String line) {
        return (line.charAt(0) == '[' && line.charAt(line.length() - 1) == ']');
    }

    private int getSplitPos(String line) {
        return line.indexOf('=');
    }

    private String more(BufferedReader bin, String value) throws IOException {
        boolean moreCowBell = false;
        String line = value;
        StringBuilder buf = new StringBuilder();
        do {
            moreCowBell = more(line);
            if (moreCowBell) {
                line = line.substring(0, line.length() - 1).trim();
            }
            buf.append(line);
            if (!moreCowBell) {
                continue;
            }
            buf.append('\n');
            line = advance(bin);
        } while (moreCowBell && line != null);
        return buf.toString();
    }

    private static boolean more(String line) {
        int length = line.length();
        return (length > 0 && line.charAt(length - 1) == '\\');
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Ini.class);
}
