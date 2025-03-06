package org.crosswire.common.diff;

import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

public final class DiffCleanup {

    private static final int EDIT_COST = 4;

    public static void cleanupSemantic(List<Difference> diffs) {
        boolean changes = false;
        Stack<Difference> equalities = new Stack<Difference>();
        String lastEquality = null;
        int lengthChangesPre = 0;
        int lengthChangesPost = 0;
        ListIterator<Difference> pointer = diffs.listIterator();
        Difference curDiff = pointer.hasNext() ? pointer.next() : null;
        while (curDiff != null) {
            EditType editType = curDiff.getEditType();
            if (EditType.EQUAL.equals(editType)) {
                equalities.push(curDiff);
                lengthChangesPre = lengthChangesPost;
                lengthChangesPost = 0;
                lastEquality = curDiff.getText();
            } else {
                lengthChangesPost += curDiff.getText().length();
                int lastLen = (lastEquality != null) ? lastEquality.length() : 0;
                if (lastEquality != null && lastLen <= lengthChangesPre && lastLen <= lengthChangesPost) {
                    while (curDiff != equalities.lastElement()) {
                        curDiff = pointer.previous();
                    }
                    pointer.next();
                    pointer.set(new Difference(EditType.DELETE, lastEquality));
                    pointer.add(new Difference(EditType.INSERT, lastEquality));
                    equalities.pop();
                    if (!equalities.empty()) {
                        equalities.pop();
                    }
                    if (equalities.empty()) {
                        while (pointer.hasPrevious()) {
                            pointer.previous();
                        }
                    } else {
                        curDiff = equalities.lastElement();
                        while (curDiff != pointer.previous());
                    }
                    lengthChangesPre = 0;
                    lengthChangesPost = 0;
                    lastEquality = null;
                    changes = true;
                }
            }
            curDiff = pointer.hasNext() ? pointer.next() : null;
        }
        if (changes) {
            cleanupMerge(diffs);
        }
    }

    public static void cleanupEfficiency(List<Difference> diffs) {
        if (diffs.isEmpty()) {
            return;
        }
        boolean changes = false;
        Stack<Difference> equalities = new Stack<Difference>();
        String lastEquality = null;
        int preInsert = 0;
        int preDelete = 0;
        int postInsert = 0;
        int postDelete = 0;
        ListIterator<Difference> pointer = diffs.listIterator();
        Difference curDiff = pointer.hasNext() ? pointer.next() : null;
        Difference safeDiff = curDiff;
        while (curDiff != null) {
            EditType editType = curDiff.getEditType();
            if (EditType.EQUAL.equals(editType)) {
                if (curDiff.getText().length() < editCost && postInsert + postDelete > 0) {
                    equalities.push(curDiff);
                    preInsert = postInsert;
                    preDelete = postDelete;
                    lastEquality = curDiff.getText();
                } else {
                    equalities.clear();
                    lastEquality = null;
                    safeDiff = curDiff;
                }
                postInsert = 0;
                postDelete = 0;
            } else {
                if (EditType.DELETE.equals(editType)) {
                    postDelete = 1;
                } else {
                    postInsert = 1;
                }
                if (lastEquality != null && (preInsert + preDelete + postInsert + postDelete > 0 || (lastEquality.length() < editCost / 2 && preInsert + preDelete + postInsert + postDelete == 3))) {
                    while (curDiff != equalities.lastElement()) {
                        curDiff = pointer.previous();
                    }
                    pointer.next();
                    pointer.set(new Difference(EditType.DELETE, lastEquality));
                    curDiff = new Difference(EditType.INSERT, lastEquality);
                    pointer.add(curDiff);
                    equalities.pop();
                    lastEquality = null;
                    if (preInsert == 1 && preDelete == 1) {
                        postInsert = 1;
                        postDelete = 1;
                        equalities.clear();
                        safeDiff = curDiff;
                    } else {
                        if (!equalities.empty()) {
                            equalities.pop();
                        }
                        if (equalities.empty()) {
                            curDiff = safeDiff;
                        } else {
                            curDiff = equalities.lastElement();
                        }
                        while (curDiff != pointer.previous());
                        postInsert = 0;
                        postDelete = 0;
                    }
                    changes = true;
                }
            }
            curDiff = pointer.hasNext() ? pointer.next() : null;
        }
        if (changes) {
            cleanupMerge(diffs);
        }
    }

    public static void cleanupMerge(List<Difference> diffs) {
        diffs.add(new Difference(EditType.EQUAL, ""));
        int countDelete = 0;
        int countInsert = 0;
        StringBuilder textDelete = new StringBuilder();
        StringBuilder textInsert = new StringBuilder();
        int commonLength = 0;
        ListIterator<Difference> pointer = diffs.listIterator();
        Difference curDiff = pointer.hasNext() ? pointer.next() : null;
        Difference prevEqual = null;
        while (curDiff != null) {
            EditType editType = curDiff.getEditType();
            if (EditType.INSERT.equals(editType)) {
                countInsert++;
                textInsert.append(curDiff.getText());
                prevEqual = null;
            } else if (EditType.DELETE.equals(editType)) {
                countDelete++;
                textDelete.append(curDiff.getText());
                prevEqual = null;
            } else if (EditType.EQUAL.equals(editType)) {
                if (countDelete != 0 || countInsert != 0) {
                    pointer.previous();
                    while (countDelete-- > 0) {
                        pointer.previous();
                        pointer.remove();
                    }
                    while (countInsert-- > 0) {
                        pointer.previous();
                        pointer.remove();
                    }
                    if (countDelete != 0 && countInsert != 0) {
                        commonLength = Commonality.prefix(textInsert.toString(), textDelete.toString());
                        if (commonLength > 0) {
                            if (pointer.hasPrevious()) {
                                curDiff = pointer.previous();
                                assert EditType.EQUAL.equals(curDiff.getEditType()) : "Previous diff should have been an equality.";
                                curDiff.appendText(textInsert.substring(0, commonLength));
                                pointer.next();
                            } else {
                                pointer.add(new Difference(EditType.EQUAL, textInsert.substring(0, commonLength)));
                            }
                            textInsert.replace(0, textInsert.length(), textInsert.substring(commonLength));
                            textDelete.replace(0, textDelete.length(), textDelete.substring(commonLength));
                        }
                        commonLength = Commonality.suffix(textInsert.toString(), textDelete.toString());
                        if (commonLength > 0) {
                            curDiff = pointer.next();
                            curDiff.prependText(textInsert.substring(textInsert.length() - commonLength));
                            textInsert.replace(0, textInsert.length(), textInsert.substring(0, textInsert.length() - commonLength));
                            textDelete.replace(0, textDelete.length(), textDelete.substring(0, textDelete.length() - commonLength));
                            pointer.previous();
                        }
                    }
                    if (textDelete.length() != 0) {
                        pointer.add(new Difference(EditType.DELETE, textDelete.toString()));
                    }
                    if (textInsert.length() != 0) {
                        pointer.add(new Difference(EditType.INSERT, textInsert.toString()));
                    }
                    curDiff = pointer.hasNext() ? pointer.next() : null;
                } else if (prevEqual != null) {
                    prevEqual.appendText(curDiff.getText());
                    pointer.remove();
                    curDiff = pointer.previous();
                    pointer.next();
                }
                countInsert = 0;
                countDelete = 0;
                textDelete.delete(0, textDelete.length());
                textInsert.delete(0, textInsert.length());
                prevEqual = curDiff;
            }
            curDiff = pointer.hasNext() ? pointer.next() : null;
        }
        Difference lastDiff = diffs.get(diffs.size() - 1);
        if (lastDiff.getText().length() == 0) {
            diffs.remove(diffs.size() - 1);
        }
    }

    public static void setEditCost(int newEditCost) {
        editCost = newEditCost;
    }

    private static int editCost = 4;
}
