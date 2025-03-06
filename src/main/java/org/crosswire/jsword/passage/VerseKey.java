package org.crosswire.jsword.passage;

import org.crosswire.jsword.versification.Versification;

public interface VerseKey<T extends VerseKey> extends Key {

    Versification getVersification();

    T reversify(Versification paramVersification);

    boolean isWhole();

    T getWhole();
}