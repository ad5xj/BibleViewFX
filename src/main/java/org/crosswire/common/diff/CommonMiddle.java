package org.crosswire.common.diff;

public class CommonMiddle {
    private String sourcePrefix;
    private String sourceSuffix;
    private String targetPrefix;
    private String targetSuffix;
    private String commonality;

    public CommonMiddle(String sourcePrefix, String sourceSuffix, String targetPrefix, String targetSuffix, String commonality) {
        this.sourcePrefix = sourcePrefix;
        this.sourceSuffix = sourceSuffix;
        this.targetPrefix = targetPrefix;
        this.targetSuffix = targetSuffix;
        this.commonality = commonality;
    }

    public String getSourcePrefix() {
        return this.sourcePrefix;
    }

    public String getTargetPrefix() {
        return this.targetPrefix;
    }

    public String getCommonality() {
        return this.commonality;
    }

    public String getSourceSuffix() {
        return this.sourceSuffix;
    }

    public String getTargetSuffix() {
        return this.targetSuffix;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.sourcePrefix);
        buf.append(',');
        buf.append(this.sourceSuffix);
        buf.append(',');
        buf.append(this.targetPrefix);
        buf.append(',');
        buf.append(this.targetSuffix);
        buf.append(',');
        buf.append(this.commonality);
        return buf.toString();
    }

    public int hashCode() {
        int result = 31 + ((this.sourcePrefix == null) ? 0 : this.sourcePrefix.hashCode());
        result = 31 * result + ((this.sourceSuffix == null) ? 0 : this.sourceSuffix.hashCode());
        result = 31 * result + ((this.targetPrefix == null) ? 0 : this.targetPrefix.hashCode());
        result = 31 * result + ((this.targetSuffix == null) ? 0 : this.targetSuffix.hashCode());
        return 31 * result + ((this.commonality == null) ? 0 : this.commonality.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CommonMiddle other = (CommonMiddle) obj;
        return (this.sourcePrefix.equals(other.sourcePrefix) && this.sourceSuffix.equals(other.sourceSuffix) && this.targetPrefix.equals(other.targetPrefix) && this.targetSuffix.equals(other.targetSuffix) && this.commonality.equals(other.commonality));
    }
}
