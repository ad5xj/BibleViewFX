package org.crosswire.jsword.passage;

import org.crosswire.jsword.versification.Versification;

public enum PassageType {
    SPEED {
        @Override
        public Passage createPassage(Versification v11n, String passage, Key basis) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage(v11n);
            }
            return new RocketPassage(v11n, passage, basis);
        }

        @Override
        public Passage createEmptyPassage(Versification v11n) {
            return new RocketPassage(v11n);
        }
    },
    WRITE_SPEED {
        @Override
        public Passage createPassage(Versification v11n, String passage, Key basis) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage(v11n);
            }
            return new BitwisePassage(v11n, passage, basis);
        }

        @Override
        public Passage createEmptyPassage(Versification v11n) {
            return new BitwisePassage(v11n);
        }
    },
    SIZE {
        private static final long serialVersionUID = -1959355535575121168L;

        @Override
        public Passage createPassage(Versification v11n, String passage, Key basis) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage(v11n);
            }
            return new DistinctPassage(v11n, passage, basis);
        }

        @Override
        public Passage createEmptyPassage(Versification v11n) {
            return new DistinctPassage(v11n);
        }
    },
    MIX {
        @Override
        public Passage createPassage(Versification v11n, String passage, Key basis) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage(v11n);
            }
            return new RangedPassage(v11n, passage, basis);
        }

        @Override
        public Passage createEmptyPassage(Versification v11n) {
            return new RangedPassage(v11n);
        }
    },
    TALLY {
        @Override
        public Passage createPassage(Versification v11n, String passage, Key basis) throws NoSuchVerseException {
            if (passage == null || passage.length() == 0) {
                return createEmptyPassage(v11n);
            }
            return new PassageTally(v11n, passage, basis);
        }

        @Override
        public Passage createEmptyPassage(Versification v11n) {
            return new PassageTally(v11n);
        }
    };

    public Passage createPassage(Versification v11n, String passage) throws NoSuchVerseException {
        return createPassage(v11n, passage, (Key) null);
    }

    public static PassageType fromString(String name) {
        for (PassageType v : values()) {
            if (v.name().equalsIgnoreCase(name)) {
                return v;
            }
        }
        assert false;
        return null;
    }

    public static PassageType fromInteger(int i) {
        for (PassageType v : values()) {
            if (v.ordinal() == i) {
                return v;
            }
        }
        return SPEED;
    }

    public static int toInteger(PassageType type) {
        return type.ordinal();
    }

    public abstract Passage createPassage(Versification paramVersification, String paramString, Key paramKey) throws NoSuchVerseException;

    public abstract Passage createEmptyPassage(Versification paramVersification);
}