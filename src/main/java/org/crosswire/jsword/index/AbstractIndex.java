package org.crosswire.jsword.index;

import org.crosswire.jsword.index.search.SearchModifier;

public abstract class AbstractIndex implements Index {

    private SearchModifier modifier;

    public void setSearchModifier(SearchModifier theModifier) { modifier = theModifier; }

    public SearchModifier getSearchModifier() { return modifier; }
}