package org.crosswire.jsword.versification;

import org.crosswire.common.config.ConfigException;
import org.crosswire.common.util.KeyValuePair;
import org.crosswire.common.util.ResourceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

public class FileVersificationMapping {

    private List<KeyValuePair> pairs = new ArrayList<KeyValuePair>(16);

    public FileVersificationMapping() {
    }

    public FileVersificationMapping(Versification versification) throws IOException, ConfigException {
        InputStream s = ResourceUtil.getResourceAsStream(getClass(), versification.getName() + ".properties");
        BufferedReader lineReader = new BufferedReader(new InputStreamReader(s));
        String line;
        while ((line = lineReader.readLine()) != null) {
            if (line.length() == 0 || line.charAt(0) == '#') {
                continue;
            }
            int firstEqual = line.indexOf('=');
            if (firstEqual == -1) {
                addProperty(line, null);
                continue;
            }
            addProperty(line.substring(0, firstEqual), line.substring(firstEqual + 1));
        }
    }

    public void addProperty(String key, String value) {
        this.pairs.add(new KeyValuePair(key, value));
    }

    public List<KeyValuePair> getMappings() {
        return this.pairs;
    }
}
