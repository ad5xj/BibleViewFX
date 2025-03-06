package org.crosswire.common.util;

public interface Filter<T> {
    boolean test(T paramT);
}