package org.crosswire.common.diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DifferenceEngine {

    private static final float TIMEOUT = 1.0F;

    public DifferenceEngine() {
        this("", "");
    }

    public DifferenceEngine(String source, String target) {
        this.source = source;
        this.target = target;
        this.sourceLength = source.length();
        this.targetLength = target.length();
    }

    public List<Difference> generate() {
        long msEnd = System.currentTimeMillis() + (long) (timeout * 1000.0F);
        int maxD = (this.sourceLength + this.targetLength) / 2;
        List<Set<String>> vMap1 = new ArrayList<Set<String>>();
        List<Set<String>> vMap2 = new ArrayList<Set<String>>();
        Map<Integer, Integer> v1 = new HashMap<Integer, Integer>();
        Map<Integer, Integer> v2 = new HashMap<Integer, Integer>();
        v1.put(Integer.valueOf(1), Integer.valueOf(0));
        v2.put(Integer.valueOf(1), Integer.valueOf(0));
        Map<String, Integer> footsteps = new HashMap<String, Integer>();
        boolean done = false;
        boolean front = ((this.sourceLength + this.targetLength) % 2 != 0);
        for (int d = 0; d < maxD; d++) {
            if (timeout > 0.0F && System.currentTimeMillis() > msEnd) {
                return null;
            }
            vMap1.add(new HashSet<String>());
            int k;
            for (k = -d; k <= d; k += 2) {
                int x;
                Integer kPlus1Key = Integer.valueOf(k + 1);
                Integer kPlus1Value = v1.get(kPlus1Key);
                Integer kMinus1Key = Integer.valueOf(k - 1);
                Integer kMinus1Value = v1.get(kMinus1Key);
                if (k == -d || (k != d && kMinus1Value.intValue() < kPlus1Value.intValue())) {
                    x = kPlus1Value.intValue();
                } else {
                    x = kMinus1Value.intValue() + 1;
                }
                int y = x - k;
                String footstep = x + "," + y;
                if (front && footsteps.containsKey(footstep)) {
                    done = true;
                }
                if (!front) {
                    footsteps.put(footstep, Integer.valueOf(d));
                }
                while (!done && x < this.sourceLength && y < this.targetLength && this.source.charAt(x) == this.target.charAt(y)) {
                    x++;
                    y++;
                    footstep = x + "," + y;
                    if (front && footsteps.containsKey(footstep)) {
                        done = true;
                    }
                    if (!front) {
                        footsteps.put(footstep, Integer.valueOf(d));
                    }
                }
                v1.put(Integer.valueOf(k), Integer.valueOf(x));
                Set<String> s = vMap1.get(d);
                s.add(x + "," + y);
                if (done) {
                    Integer footstepValue = footsteps.get(footstep);
                    vMap2 = vMap2.subList(0, footstepValue.intValue() + 1);
                    List<Difference> a = path1(vMap1, this.source.substring(0, x), this.target.substring(0, y));
                    a.addAll(path2(vMap2, this.source.substring(x), this.target.substring(y)));
                    return a;
                }
            }
            vMap2.add(new HashSet<String>());
            for (k = -d; k <= d; k += 2) {
                int x;
                Integer kPlus1Key = Integer.valueOf(k + 1);
                Integer kPlus1Value = v2.get(kPlus1Key);
                Integer kMinus1Key = Integer.valueOf(k - 1);
                Integer kMinus1Value = v2.get(kMinus1Key);
                if (k == -d || (k != d && kMinus1Value.intValue() < kPlus1Value.intValue())) {
                    x = kPlus1Value.intValue();
                } else {
                    x = kMinus1Value.intValue() + 1;
                }
                int y = x - k;
                String footstep = (this.sourceLength - x) + "," + (this.targetLength - y);
                if (!front && footsteps.containsKey(footstep)) {
                    done = true;
                }
                if (front) {
                    footsteps.put(footstep, Integer.valueOf(d));
                }
                while (!done && x < this.sourceLength && y < this.targetLength && this.source.charAt(this.sourceLength - x - 1) == this.target.charAt(this.targetLength - y - 1)) {
                    x++;
                    y++;
                    footstep = (this.sourceLength - x) + "," + (this.targetLength - y);
                    if (!front && footsteps.containsKey(footstep)) {
                        done = true;
                    }
                    if (front) {
                        footsteps.put(footstep, Integer.valueOf(d));
                    }
                }
                v2.put(Integer.valueOf(k), Integer.valueOf(x));
                Set<String> s = vMap2.get(d);
                s.add(x + "," + y);
                if (done) {
                    Integer footstepValue = footsteps.get(footstep);
                    vMap1 = vMap1.subList(0, footstepValue.intValue() + 1);
                    List<Difference> a = path1(vMap1, this.source.substring(0, this.sourceLength - x), this.target.substring(0, this.targetLength - y));
                    a.addAll(path2(vMap2, this.source.substring(this.sourceLength - x), this.target.substring(this.targetLength - y)));
                    return a;
                }
            }
        }
        return null;
    }

    protected List<Difference> path1(List<Set<String>> vMap, String newSource, String newTarget) {
        List<Difference> path = new ArrayList<Difference>();
        int x = newSource.length();
        int y = newTarget.length();
        EditType lastEditType = null;
        for (int d = vMap.size() - 2; d >= 0;) {
            while (true) {
                Set<String> set = vMap.get(d);
                if (set.contains((x - 1) + "," + y)) {
                    x--;
                    if (EditType.DELETE.equals(lastEditType)) {
                        Difference firstDiff = path.get(0);
                        firstDiff.prependText(newSource.charAt(x));
                    } else {
                        path.add(0, new Difference(EditType.DELETE, newSource.substring(x, x + 1)));
                    }
                    lastEditType = EditType.DELETE;
                    break;
                }
                if (set.contains(x + "," + (y - 1))) {
                    y--;
                    if (EditType.INSERT.equals(lastEditType)) {
                        Difference firstDiff = path.get(0);
                        firstDiff.prependText(newTarget.charAt(y));
                    } else {
                        path.add(0, new Difference(EditType.INSERT, newTarget.substring(y, y + 1)));
                    }
                    lastEditType = EditType.INSERT;
                    break;
                }
                x--;
                y--;
                assert newSource.charAt(x) == newTarget.charAt(y) : "No diagonal.  Can't happen. (path1)";
                if (EditType.EQUAL.equals(lastEditType)) {
                    Difference firstDiff = path.get(0);
                    firstDiff.prependText(newSource.charAt(x));
                } else {
                    path.add(0, new Difference(EditType.EQUAL, newSource.substring(x, x + 1)));
                }
                lastEditType = EditType.EQUAL;
            }
            d--;
        }
        return path;
    }

    protected List<Difference> path2(List<Set<String>> vMap, String newSource, String newTarget) {
        List<Difference> path = new ArrayList<Difference>();
        int cachedNewSourceLength = newSource.length();
        int cachedNewTargetLength = newTarget.length();
        int x = cachedNewSourceLength;
        int y = cachedNewTargetLength;
        EditType lastEditType = null;
        for (int d = vMap.size() - 2; d >= 0;) {
            while (true) {
                Set<String> set = vMap.get(d);
                if (set.contains((x - 1) + "," + y)) {
                    x--;
                    if (EditType.DELETE.equals(lastEditType)) {
                        Difference lastDiff = path.get(path.size() - 1);
                        lastDiff.appendText(newSource.charAt(cachedNewSourceLength - x - 1));
                    } else {
                        path.add(new Difference(EditType.DELETE, newSource.substring(cachedNewSourceLength - x - 1, cachedNewSourceLength - x)));
                    }
                    lastEditType = EditType.DELETE;
                    break;
                }
                if (set.contains(x + "," + (y - 1))) {
                    y--;
                    if (EditType.INSERT.equals(lastEditType)) {
                        Difference lastDiff = path.get(path.size() - 1);
                        lastDiff.appendText(newTarget.charAt(cachedNewTargetLength - y - 1));
                    } else {
                        path.add(new Difference(EditType.INSERT, newTarget.substring(cachedNewTargetLength - y - 1, cachedNewTargetLength - y)));
                    }
                    lastEditType = EditType.INSERT;
                    break;
                }
                x--;
                y--;
                assert newSource.charAt(cachedNewSourceLength - x - 1) == newTarget.charAt(cachedNewTargetLength - y - 1) : "No diagonal.  Can't happen. (path2)";
                if (EditType.EQUAL.equals(lastEditType)) {
                    Difference lastDiff = path.get(path.size() - 1);
                    lastDiff.appendText(newSource.charAt(cachedNewSourceLength - x - 1));
                } else {
                    path.add(new Difference(EditType.EQUAL, newSource.substring(cachedNewSourceLength - x - 1, cachedNewSourceLength - x)));
                }
                lastEditType = EditType.EQUAL;
            }
            d--;
        }
        return path;
    }

    public static void setTimeout(float newTimeout) {
        timeout = newTimeout;
    }

    private static float timeout = 1.0F;

    private final String source;
    private final String target;

    private final int targetLength;
    private final int sourceLength;
}