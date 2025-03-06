package org.crosswire.jsword.passage;

import java.util.EventObject;

public class PassageEvent extends EventObject {

    private EventType type;

    private Verse lower;

    private Verse upper;

    private static final long serialVersionUID = 3906647492467898675L;

    public enum EventType {
        CHANGED, ADDED, REMOVED;
    }

    public PassageEvent(Object source, EventType versesChanged, Verse lower, Verse upper) {
        super(source);
        this.type = versesChanged;
        this.lower = lower;
        this.upper = upper;
    }

    public EventType getType() {
        return this.type;
    }

    public Verse getLowerIndex() {
        return this.lower;
    }

    public Verse getUpperIndex() {
        return this.upper;
    }
}