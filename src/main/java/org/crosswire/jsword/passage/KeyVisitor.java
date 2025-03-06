package org.crosswire.jsword.passage;

public interface KeyVisitor {
    void visitLeaf(Key paramKey);
    void visitBranch(Key paramKey);
}