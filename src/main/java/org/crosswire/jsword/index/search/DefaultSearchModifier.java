package org.crosswire.jsword.index.search;

public class DefaultSearchModifier implements SearchModifier {

    private boolean ranked = false;

    private int maxResults = Integer.MAX_VALUE;

    private static final long serialVersionUID = 0L;

    public boolean isRanked() {
        return this.ranked;
    }

    public void setRanked(boolean newRanked) {
        this.ranked = newRanked;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public void setMaxResults(int newMaxResults) {
        this.maxResults = newMaxResults;
    }
}
