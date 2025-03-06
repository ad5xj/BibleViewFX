package org.crosswire.jsword.book.sword;

import org.crosswire.common.activate.Activatable;
import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;

import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.ReadOnlyKeyList;
import org.crosswire.jsword.passage.VerseRange;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Content;

public class SwordGenBook extends AbstractBook {

    private Key global;

    private boolean active;

    private Map<String, Key> map;

    private Key set;

    protected SourceFilter filter;

    protected SwordGenBook(SwordBookMetaData sbmd, Backend backend) {
        super((BookMetaData) sbmd, backend);
        if (backend == null) {
            throw new IllegalArgumentException("AbstractBackend must not be null.");
        }
        this.filter = sbmd.getFilter();
        this.map = null;
        this.set = null;
        this.global = null;
        this.active = false;
    }

    public final void activate(Lock lock) {
        super.activate(lock);
        this.set = getBackend().readIndex();
        this.map = new HashMap<String, Key>();
        for (Key key : this.set) {
            this.map.put(key.getOsisRef(), key);
        }
        this.global = (Key) new ReadOnlyKeyList(this.set, false);
        this.active = true;
    }

    public final void deactivate(Lock lock) {
        super.deactivate(lock);
        this.map = null;
        this.set = null;
        this.global = null;
        this.active = false;
    }

    public Iterator<Content> getOsisIterator(Key key, boolean allowEmpty, boolean allowGenTitle) throws BookException {
        checkActive();
        assert key != null;
        return getBackend().readToOsis(key, new RawTextToXmlProcessor() {
            public void preRange(VerseRange range, List<Content> partialDom) {
            }

            public void postVerse(Key verse, List<Content> partialDom, String rawText) {
                partialDom.addAll(SwordGenBook.this.filter.toOSIS((Book) SwordGenBook.this, verse, rawText));
            }

            public void init(List<Content> partialDom) {
            }
        }).iterator();
    }

    public String getRawText(Key key) throws BookException {
        return getBackend().getRawText(key);
    }

    public boolean contains(Key key) {
        return getBackend().contains(key);
    }

    public List<Content> getOsis(Key key, RawTextToXmlProcessor processor) throws BookException {
        checkActive();
        assert key != null;
        return getBackend().readToOsis(key, processor);
    }

    public boolean isWritable() {
        return getBackend().isWritable();
    }

    public void setRawText(Key key, String rawData) throws BookException {
        throw new BookException(JSOtherMsg.lookupText("This Book is read-only.", new Object[0]));
    }

    public void setAliasKey(Key alias, Key source) throws BookException {
        throw new BookException(JSOtherMsg.lookupText("This Book is read-only.", new Object[0]));
    }

    public Key getGlobalKeyList() {
        checkActive();
        return this.global;
    }

    public Key getValidKey(String name) {
        try {
            return getKey(name);
        } catch (NoSuchKeyException e) {
            return createEmptyKeyList();
        }
    }

    public Key getKey(String text) throws NoSuchKeyException {
        checkActive();
        Key key = this.map.get(text);
        if (key != null) {
            return key;
        }
        for (String keyName : this.map.keySet()) {
            if (keyName.equalsIgnoreCase(text)) {
                return this.map.get(keyName);
            }
        }
        for (String keyName : this.map.keySet()) {
            if (keyName.startsWith(text)) {
                return this.map.get(keyName);
            }
        }
        for (String keyName : this.map.keySet()) {
            if (keyName.indexOf(text) != -1) {
                return this.map.get(keyName);
            }
        }
        throw new NoSuchKeyException(JSMsg.gettext("No entry for '{0}' in {1}.", new Object[]{text, getInitials()}));
    }

    public Key createEmptyKeyList() {
        return (Key) new DefaultKeyList();
    }

    private void checkActive() {
        if (!this.active) {
            Activator.activate((Activatable) this);
        }
    }
}
