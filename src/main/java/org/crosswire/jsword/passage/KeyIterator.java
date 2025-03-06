package org.crosswire.jsword.passage;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class KeyIterator implements Iterator<Key> {

    private Stack<Locator> stack;

    public KeyIterator(Key key) {
        this.stack = new Stack<Locator>();
        this.stack.push(new Locator(key));
    }

    protected void prepare() {
        if (this.stack.size() == 0) {
            return;
        }
        Locator peek = this.stack.peek();
        if (peek.getParent().getChildCount() > peek.getPosition()) {
            return;
        }
        this.stack.pop();
        prepare();
    }

    public boolean hasNext() {
        prepare();
        return (this.stack.size() != 0);
    }

    public Key next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Locator peek = this.stack.peek();
        int childNum = peek.getPosition();
        peek.setPosition(childNum + 1);
        if (childNum == -1) {
            return peek.getParent();
        }
        this.stack.push(new Locator(peek.getParent().get(childNum)));
        return next();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public static class Locator {

        private Key parent;

        private int position;

        public Locator(Key parent) {
            this.parent = parent;
            this.position = -1;
        }

        public Key getParent() {
            return this.parent;
        }

        public void setParent(Key parent) {
            this.parent = parent;
        }

        public int getPosition() {
            return this.position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
