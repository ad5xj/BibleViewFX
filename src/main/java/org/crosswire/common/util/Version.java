package org.crosswire.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {

    public static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)(?:.(\\d+))?(?:.(\\d+))?(?:.(\\d+))?$");

    private final int[] parts;

    private final String original;

    public Version(String version) {
        if (version == null) {
            throw new IllegalArgumentException("Null version not allowed.");
        }
        this.original = version;
        this.parts = new int[]{-1, -1, -1, -1};
        Matcher matcher = VERSION_PATTERN.matcher(this.original);
        if (matcher.matches()) {
            int count = matcher.groupCount();
            for (int i = 1; i <= count; i++) {
                String part = matcher.group(i);
                if (part == null) {
                    break;
                }
                this.parts[i - 1] = Integer.parseInt(part);
            }
        } else {
            throw new IllegalArgumentException("invalid: " + version);
        }
    }

    public String toString() {
        return this.original;
    }

    public int hashCode() {
        return this.original.hashCode();
    }

    public boolean equals(Object object) {
        if (!(object instanceof Version)) {
            return false;
        }
        Version that = (Version) object;
        if (that == this) {
            return true;
        }
        for (int i = 0; i < this.parts.length; i++) {
            if (this.parts[i] != that.parts[i]) {
                return false;
            }
        }
        return true;
    }

    public int compareTo(Version object) {
        if (object == this) {
            return 0;
        }
        for (int i = 0; i < this.parts.length; i++) {
            int result = this.parts[i] - object.parts[i];
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
