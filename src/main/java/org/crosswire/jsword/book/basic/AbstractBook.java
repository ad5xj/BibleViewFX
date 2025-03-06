package org.crosswire.jsword.book.basic;

import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.Language;

import org.crosswire.jsword.JSOtherMsg;

import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.index.IndexStatusEvent;
import org.crosswire.jsword.index.IndexStatusListener;

import org.crosswire.jsword.index.search.DefaultSearchRequest;
import org.crosswire.jsword.index.search.SearchRequest;
import org.crosswire.jsword.index.search.Searcher;
import org.crosswire.jsword.index.search.SearcherFactory;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.FeatureType;

import org.crosswire.jsword.book.sword.Backend;
import org.crosswire.jsword.book.sword.processing.NoOpRawTextProcessor;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;

import org.crosswire.jsword.passage.Key;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jdom2.Content;
import org.jdom2.Document;

/**
 * @brief This is the abstract class implemented by the class Book
 *
 */
public abstract class AbstractBook implements Book {

    private Searcher searcher;
    private BookMetaData bmd;
    private Backend backend;

    private List<IndexStatusListener> listeners;

    /**
     * @brief Constructor for AbstractBook
     * @param bmd
     * @param backend 
     */
    public AbstractBook(BookMetaData bmd, Backend backend) {
        this.bmd = bmd;
        this.backend = backend;
        this.listeners = new CopyOnWriteArrayList<IndexStatusListener>();
    }

    protected abstract List<Content> getOsis(Key paramKey, RawTextToXmlProcessor paramRawTextToXmlProcessor) throws BookException;

    protected void firePropertyChange(IndexStatus oldStatus, IndexStatus newStatus) {
        if ( (oldStatus != null) && 
             (newStatus != null) && 
             oldStatus.equals(newStatus) 
           ) { return; }

        IndexStatusEvent ev = new IndexStatusEvent(this, newStatus);

        for (IndexStatusListener listener : this.listeners) 
        {
            listener.statusChanged(ev);
        }
    }

    public final void setBookMetaData(BookMetaData bmd) { this.bmd = bmd; }

    public final BookMetaData getBookMetaData() { return this.bmd; }

    public final Backend getBackend() { return this.backend; }

    public void activate(Lock lock) {    }

    public void deactivate(Lock lock) {    }

    public void setIndexStatus(IndexStatus newStatus) {
        IndexStatus oldStatus = this.bmd.getIndexStatus();
        this.bmd.setIndexStatus(newStatus);
        firePropertyChange(oldStatus, newStatus);
    }

    public void putProperty(String key, String value) { this.bmd.putProperty(key, value, false); }

    public void putProperty(String key, String value, boolean forFrontend) { this.bmd.putProperty(key, value, forFrontend); }

    public void addIndexStatusListener(IndexStatusListener listener) {
        if (this.listeners == null) {
            this.listeners = new CopyOnWriteArrayList<IndexStatusListener>();
        }
        this.listeners.add(listener);
    }

    public void removeIndexStatusListener(IndexStatusListener listener) {
        if (this.listeners == null) {
            return;
        }
        this.listeners.remove(listener);
    }

    public boolean isLeftToRight() { return this.bmd.isLeftToRight(); }

    public boolean isSupported() { return this.bmd.isSupported(); }

    public boolean isEnciphered() { return this.bmd.isEnciphered(); }

    public boolean isLocked() { return this.bmd.isLocked(); }

    public boolean unlock(String unlockKey) {
        boolean state = this.bmd.unlock(unlockKey);
        if (state) {
            return isUnlockKeyValid();
        }
        return state;
    }

    private boolean isUnlockKeyValid() {
        try {
            Key key = getGlobalKeyList();
            if (key == null) {
                return true;
            }
            if (key.getCardinality() > 0) {
                key = key.get(0);
            }
            getOsis(key, (RawTextToXmlProcessor) new NoOpRawTextProcessor());
            return true;
        } catch (BookException ex) {
            return false;
        }
    }

    public boolean match(String name) {
        if (name == null) { return false; }
        if (name.equals(getInitials())) { return true; }
        if (name.equals(getName())) { return true; }
        if (name.equalsIgnoreCase(getInitials())) { return true; }
        if (name.equalsIgnoreCase(getName())) { return true; }
        if (name.startsWith(getInitials())) { return true; }
        if (name.startsWith(getName())) { return true; }
        return false;
    }

    public boolean isQuestionable() { return this.bmd.isQuestionable(); }

    public boolean hasFeature(FeatureType feature) { return this.bmd.hasFeature(feature); }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(getClass())) {
            return false;
        }
        Book that = (Book) obj;
        return this.bmd.equals(that.getBookMetaData());
    }

    public int hashCode() { return this.bmd.hashCode(); }

    public int compareTo(Book obj) { return this.bmd.compareTo(obj.getBookMetaData()); }

    public String toString() { return this.bmd.toString();}
    public String getAbbreviation() { return this.bmd.getAbbreviation(); }
    public String getInitials() { return this.bmd.getInitials(); }
    public String getName() { return this.bmd.getName(); }
    public String getOsisID() { return this.bmd.getOsisID(); }
    public String getProperty(String key) { return this.bmd.getProperty(key); }
    public String getUnlockKey() { return this.bmd.getUnlockKey(); }
    public String getDriverName() { return (this.bmd == null) ? null : this.bmd.getDriverName(); }

    public Key getScope() { return getGlobalKeyList(); }
    public Key find(String request) throws BookException {
        return find((SearchRequest) new DefaultSearchRequest(request));
    }
    public Key find(SearchRequest request) throws BookException {
        if (this.searcher == null)
      try {
            this.searcher = SearcherFactory.createSearcher(this);
        } catch (InstantiationException ex) {
            throw new BookException(JSOtherMsg.lookupText("Failed to initialize the search index", new Object[0]), ex);
        }
        return this.searcher.search(request);
    }

    public Book getBook() { return this; }

    public BookDriver getDriver() { return (this.bmd == null) ? null : this.bmd.getDriver(); }

    public IndexStatus getIndexStatus() { return this.bmd.getIndexStatus(); }

    public Language getLanguage() { return this.bmd.getLanguage(); }

    public Set<String> getPropertyKeys() { return this.bmd.getPropertyKeys(); }

    public BookCategory getBookCategory() { return this.bmd.getBookCategory(); }

    public Document toOSIS() { return this.bmd.toOSIS(); }
}
