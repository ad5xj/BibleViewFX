package org.crosswire.jsword.book.sword;

import java.io.Serializable;

class TreeNode implements Cloneable, Serializable {
    private static final long serialVersionUID = -2472601787934480762L;

    private int offset;
    private int parent;
    private int nextSibling;
    private int firstChild;

    private byte[] userData;

    private String name;

    TreeNode() {
        this(-1);
    }

    TreeNode(int theOffset) {
        this.offset = theOffset;
        this.name = "";
        this.parent = -1;
        this.nextSibling = -1;
        this.firstChild = -1;
        this.userData = new byte[0];
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int newOffset) {
        this.offset = newOffset;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public byte[] getUserData() {
        return (byte[]) this.userData.clone();
    }

    public void setUserData(byte[] theUserData) {
        this.userData = (byte[]) theUserData.clone();
    }

    public int getFirstChild() {
        return this.firstChild;
    }

    public boolean hasChildren() {
        return (this.firstChild != -1);
    }

    public void setFirstChild(int firstChild) {
        this.firstChild = firstChild;
    }

    public int getNextSibling() {
        return this.nextSibling;
    }

    public boolean hasNextSibling() {
        return (this.nextSibling != -1);
    }

    public void setNextSibling(int nextSibling) {
        this.nextSibling = nextSibling;
    }

    public int getParent() {
        return this.parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public TreeNode clone() {
        TreeNode clone = null;
        try {
            clone = (TreeNode) super.clone();
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }
}
