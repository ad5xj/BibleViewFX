package org.crosswire.common.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Diff {

    private String source;

    private String target;

    private boolean checkLines;

    public Diff(String source, String target) {
        this(source, target, true);
    }

    public Diff(String source, String target, boolean checkLines) {
        this.source = source;
        this.target = target;
        this.checkLines = checkLines;
    }

    public List<Difference> compare() {
        if (this.source.equals(this.target)) {
            List<Difference> list = new ArrayList<Difference>();
            list.add(new Difference(EditType.EQUAL, this.source));
            return list;
        }
        int commonLength = Commonality.prefix(this.source, this.target);
        String commonPrefix = this.source.substring(0, commonLength);
        this.source = this.source.substring(commonLength);
        this.target = this.target.substring(commonLength);
        commonLength = Commonality.suffix(this.source, this.target);
        String commonSuffix = this.source.substring(this.source.length() - commonLength);
        this.source = this.source.substring(0, this.source.length() - commonLength);
        this.target = this.target.substring(0, this.target.length() - commonLength);
        List<Difference> diffs = compute();
        if (!"".equals(commonPrefix)) {
            diffs.add(0, new Difference(EditType.EQUAL, commonPrefix));
        }
        if (!"".equals(commonSuffix)) {
            diffs.add(new Difference(EditType.EQUAL, commonSuffix));
        }
        DiffCleanup.cleanupMerge(diffs);
        return diffs;
    }

    private List<Difference> compute() {
        List<Difference> diffs = new ArrayList<Difference>();
        if ("".equals(this.source)) {
            diffs.add(new Difference(EditType.INSERT, this.target));
            return diffs;
        }
        if ("".equals(this.target)) {
            diffs.add(new Difference(EditType.DELETE, this.source));
            return diffs;
        }
        String longText = (this.source.length() > this.target.length()) ? this.source : this.target;
        String shortText = (this.source.length() > this.target.length()) ? this.target : this.source;
        int i = longText.indexOf(shortText);
        if (i != -1) {
            EditType editType = (this.source.length() > this.target.length()) ? EditType.DELETE : EditType.INSERT;
            diffs.add(new Difference(editType, longText.substring(0, i)));
            diffs.add(new Difference(EditType.EQUAL, shortText));
            diffs.add(new Difference(editType, longText.substring(i + shortText.length())));
            return diffs;
        }
        CommonMiddle middleMatch = Commonality.halfMatch(this.source, this.target);
        if (middleMatch != null) {
            Diff startDiff = new Diff(middleMatch.getSourcePrefix(), middleMatch.getTargetPrefix(), this.checkLines);
            Diff endDiff = new Diff(middleMatch.getSourceSuffix(), middleMatch.getTargetSuffix(), this.checkLines);
            diffs = startDiff.compare();
            diffs.add(new Difference(EditType.EQUAL, middleMatch.getCommonality()));
            diffs.addAll(endDiff.compare());
            return diffs;
        }
        if (this.checkLines && this.source.length() + this.target.length() < 250) {
            this.checkLines = false;
        }
        LineMap lineMap = null;
        if (this.checkLines) {
            lineMap = new LineMap(this.source, this.target);
            this.source = lineMap.getSourceMap();
            this.target = lineMap.getTargetMap();
        }
        diffs = (new DifferenceEngine(this.source, this.target)).generate();
        if (diffs == null) {
            diffs = new ArrayList<Difference>();
            diffs.add(new Difference(EditType.DELETE, this.source));
            diffs.add(new Difference(EditType.INSERT, this.target));
        }
        if (this.checkLines && lineMap != null) {
            lineMap.restore(diffs);
            DiffCleanup.cleanupSemantic(diffs);
            diffs.add(new Difference(EditType.EQUAL, ""));
            int countDeletes = 0;
            int countInserts = 0;
            StringBuilder textDelete = new StringBuilder();
            StringBuilder textInsert = new StringBuilder();
            ListIterator<Difference> pointer = diffs.listIterator();
            Difference curDiff = pointer.next();
            while (curDiff != null) {
                EditType editType = curDiff.getEditType();
                if (EditType.INSERT.equals(editType)) {
                    countInserts++;
                    textInsert.append(curDiff.getText());
                } else if (EditType.DELETE.equals(editType)) {
                    countDeletes++;
                    textDelete.append(curDiff.getText());
                } else {
                    if (countDeletes >= 1 && countInserts >= 1) {
                        pointer.previous();
                        for (int j = 0; j < countDeletes + countInserts; j++) {
                            pointer.previous();
                            pointer.remove();
                        }
                        Diff newDiff = new Diff(textDelete.toString(), textInsert.toString(), false);
                        for (Difference diff : newDiff.compare()) {
                            pointer.add(diff);
                        }
                    }
                    countInserts = 0;
                    countDeletes = 0;
                    textDelete.delete(0, textDelete.length());
                    textInsert.delete(0, textInsert.length());
                }
                curDiff = pointer.hasNext() ? pointer.next() : null;
            }
            diffs.remove(diffs.size() - 1);
        }
        return diffs;
    }

    public int xIndex(List<Difference> diffs, int loc) {
        int chars1 = 0;
        int chars2 = 0;
        int lastChars1 = 0;
        int lastChars2 = 0;
        Difference lastDiff = null;
        for (Difference diff : diffs) {
            EditType editType = diff.getEditType();
            if (!EditType.INSERT.equals(editType)) {
                chars1 += diff.getText().length();
            }
            if (!EditType.DELETE.equals(editType)) {
                chars2 += diff.getText().length();
            }
            if (chars1 > loc) {
                lastDiff = diff;
                break;
            }
            lastChars1 = chars1;
            lastChars2 = chars2;
        }
        if (lastDiff != null && EditType.DELETE.equals(lastDiff.getEditType())) {
            return lastChars2;
        }
        return lastChars2 + loc - lastChars1;
    }

    public String prettyHtml(List<Difference> diffs) {
        StringBuilder buf = new StringBuilder();
        for (int x = 0; x < diffs.size(); x++) {
            Difference diff = diffs.get(x);
            EditType editType = diff.getEditType();
            String text = diff.getText();
            if (EditType.DELETE.equals(editType)) {
                buf.append("<del style=\"background:#FFE6E6;\">");
                buf.append(text);
                buf.append("</del>");
            } else if (EditType.INSERT.equals(editType)) {
                buf.append("<ins style=\"background:#E6FFE6;\">");
                buf.append(text);
                buf.append("</ins>");
            } else {
                buf.append("<span>");
                buf.append(text);
                buf.append("</span>");
            }
        }
        return buf.toString();
    }
}
