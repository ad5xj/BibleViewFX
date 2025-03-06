package org.crosswire.jsword.passage;

import java.io.Serializable;

/**
 * @ingroup org.crosswire.jsword.passage
 * 
 * @brief Inteface defining a Key object
 * details <pre>
 * &lt;b&gt;Inteface defining a Key obejct&lt;b&gt;<br>
 *                           sort based on key,<br>
 *                                   |         all collects are iterable, <br>
 *                                   |              |          similar processes,<br>
 *                                   |              |             |        marker for cloning<br> 
 *                                   |              |             |          |<br>
 * public interface Key extends Comparable<Key>, Iterable<Key>, Cloneable, Serializable</pre>
 * @extends Comparable
 * @extends Iterable
 * @extends Cloneable
 * @extends Serializable
 */
public interface Key extends Comparable<Key>, Iterable<Key>, Cloneable, Serializable 
{
    boolean canHaveChildren();
    boolean isEmpty();
    boolean contains(Key paramKey);
    @Override
    boolean equals(Object paramObject);

    int getChildCount();
    int getCardinality();
    int indexOf(Key paramKey);
    @Override
    int hashCode();

    String getName();
    String getName(Key paramKey);
    String getRootName();
    String getOsisRef();
    String getOsisID();


    void addAll(Key paramKey);
    void removeAll(Key paramKey);
    void retainAll(Key paramKey);
    void clear();
    void blur(int paramInt, RestrictionType paramRestrictionType);

    Key getParent();
    Key get(int paramInt);
    Key clone();
}
