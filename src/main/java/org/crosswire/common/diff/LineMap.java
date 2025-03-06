package org.crosswire.common.diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineMap {
    private String sourceMap;
    private String targetMap;

    private List<String> lines;

    public LineMap(String source, String target) {
        this.lines = new ArrayList<String>();
        this.lines.add("");
        Map<String, Integer> linehash = new HashMap<String, Integer>();
        this.sourceMap = linesToCharsMunge(source, this.lines, linehash);
        this.targetMap = linesToCharsMunge(target, this.lines, linehash);
    }

    public void restore(List<?> diffs) {
        StringBuilder text = new StringBuilder();
        for (int x = 0; x < diffs.size(); x++) {
            Difference diff = (Difference) diffs.get(x);
            String chars = diff.getText();
            text.delete(0, text.length());
            for (int y = 0; y < chars.length(); y++) {
                text.append(this.lines.get(chars.charAt(y)));
            }
            diff.setText(text.toString());
        }
    }

    public String getSourceMap() {
        return this.sourceMap;
    }

    public String getTargetMap() {
        return this.targetMap;
    }

    public List<String> getLines() {
        return this.lines;
    }

    private String linesToCharsMunge(String text, List<String> linearray, Map<String, Integer> linehash) {
        StringBuilder buf = new StringBuilder();
        String work = text;
        while (work.length() != 0) {
            int i = work.indexOf('\n');
            if (i == -1) {
                i = work.length() - 1;
            }
            String line = work.substring(0, i + 1);
            work = work.substring(i + 1);
            if (linehash.containsKey(line)) {
                Integer charInt = linehash.get(line);
                buf.append(String.valueOf((char) charInt.intValue()));
                continue;
            }
            linearray.add(line);
            linehash.put(line, Integer.valueOf(linearray.size() - 1));
            buf.append(String.valueOf((char) (linearray.size() - 1)));
        }
        return buf.toString();
    }
}
