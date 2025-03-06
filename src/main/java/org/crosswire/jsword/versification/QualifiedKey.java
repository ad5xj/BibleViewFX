package org.crosswire.jsword.versification;

import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseKey;
import org.crosswire.jsword.passage.VerseRange;

public final class QualifiedKey {
    private String sectionName;

    private VerseKey qualifiedKey;
    private VerseKey wholeKey;
    private Qualifier absentType;

    enum Qualifier {
        DEFAULT {
            public String getDescription(QualifiedKey q) {
                return "";
            }
        },
        ABSENT_IN_LEFT {
            public String getDescription(QualifiedKey q) {
                return "Absent in Left";
            }
        },
        ABSENT_IN_KJV {
            public String getDescription(QualifiedKey q) {
                return (q != null && q.getSectionName() != null) ? q.getSectionName() : "Missing section name";
            }
        };

        public abstract String getDescription(QualifiedKey param1QualifiedKey);
    }

    protected QualifiedKey(Verse key) {
        setKey(key);
        this.absentType = Qualifier.DEFAULT;
    }

    public QualifiedKey(VerseRange key) {
        setKey(key);
        this.absentType = Qualifier.DEFAULT;
    }

    public QualifiedKey(String sectionName) {
        this.sectionName = sectionName;
        this.absentType = Qualifier.ABSENT_IN_KJV;
    }

    public QualifiedKey() {
        this.absentType = Qualifier.ABSENT_IN_LEFT;
    }

    public static QualifiedKey create(VerseKey k) {
        return (k instanceof Verse) ? new QualifiedKey((Verse) k) : new QualifiedKey((VerseRange) k);
    }

    public VerseKey getKey() {
        return this.wholeKey;
    }

    public Verse getVerse() {
        return (Verse) this.wholeKey;
    }

    public Qualifier getAbsentType() {
        return this.absentType;
    }

    public String getSectionName() {
        return this.sectionName;
    }

    public boolean isWhole() {
        return (this.qualifiedKey == null || this.qualifiedKey.isWhole());
    }

    public QualifiedKey reversify(Versification target) {
        if (this.qualifiedKey == null) {
            return this;
        }
        VerseKey reversifiedKey = this.qualifiedKey.reversify(target);
        if (reversifiedKey != null) {
            return create(reversifiedKey);
        }
        if (target.getName().equals("KJV")) {
            return new QualifiedKey(this.qualifiedKey.getOsisID());
        }
        return new QualifiedKey();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.wholeKey != null) {
            buf.append(this.qualifiedKey.getOsisRef());
        }
        String desc = this.absentType.getDescription(this);
        if (desc.length() > 0) {
            if (buf.length() > 0) {
                buf.append(": ");
            }
            buf.append(this.absentType.getDescription(this));
        }
        return buf.toString();
    }

    public int hashCode() {
        return ((this.qualifiedKey == null) ? 17 : this.qualifiedKey.hashCode()) + ((this.absentType == null) ? 13 : this.absentType.ordinal()) + ((this.sectionName == null) ? 19 : this.sectionName.hashCode());
    }

    public boolean equals(Object obj) {
        if (obj instanceof QualifiedKey) {
            QualifiedKey otherKey = (QualifiedKey) obj;
            return (getAbsentType() == otherKey.getAbsentType() && bothNullOrEqual(this.sectionName, otherKey.sectionName) && bothNullOrEqual(this.qualifiedKey, otherKey.qualifiedKey));
        }
        return false;
    }

    private void setKey(Verse key) {
        this.qualifiedKey = (VerseKey) key;
        this.wholeKey = (VerseKey) key.getWhole();
    }

    private void setKey(VerseRange key) {
        if (key.getCardinality() == 1) {
            this.qualifiedKey = (VerseKey) key.getStart();
        } else {
            this.qualifiedKey = (VerseKey) key;
        }
        this.wholeKey = this.qualifiedKey.getWhole();
    }

    private static boolean bothNullOrEqual(Object x, Object y) {
        return (x == y || (x != null && x.equals(y)));
    }
}
