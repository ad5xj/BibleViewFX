package org.crosswire.jsword.index.search;

public class DefaultSearchRequest implements SearchRequest {
    private static final long serialVersionUID = -5973134101547369187L;

    private String request;

    private SearchModifier modifier;

    public DefaultSearchRequest(String theRequest, SearchModifier theModifier) {
        this.request = theRequest;
        this.modifier = theModifier;
    }

    public DefaultSearchRequest(String theRequest) {
        this(theRequest, null);
    }

    public SearchModifier getSearchModifier() {
        return this.modifier;
    }

    public String getRequest() {
        return this.request;
    }
}