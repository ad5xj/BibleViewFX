package org.crosswire.jsword.index.search;


import java.io.Serializable;

public interface SearchModifier extends Serializable {
    boolean isRanked();

    int getMaxResults();
}
