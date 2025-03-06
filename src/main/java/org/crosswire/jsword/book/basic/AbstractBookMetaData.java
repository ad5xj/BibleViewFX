package org.crosswire.jsword.book.basic;

import org.crosswire.common.util.Language;

import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.KeyType;

import org.crosswire.jsword.index.IndexStatus;

import org.jdom2.Document;

import java.net.URI;

public abstract class AbstractBookMetaData implements BookMetaData {
    private IndexStatus indexStatus = IndexStatus.UNDONE;
    private Language language;
    private URI library;
    private URI location;
    private BookDriver driver;

    public void setLanguage(Language language) { this.language = language; }

    public void setLibrary(URI library) throws BookException { this.library = library; }

    public void setLocation(URI location) { this.location = location; }

    public void setIndexStatus(IndexStatus newValue) { this.indexStatus = newValue; }

    public void reload() throws BookException {    }

    public void putProperty(String key, String value) { putProperty(key, value, false); }

    public void setDriver(BookDriver driver) { this.driver = driver; }


    public boolean hasFeature(FeatureType feature) { return false; }
    public boolean isSupported() { return true; }
    public boolean isEnciphered() { return false; }
    public boolean isLocked() { return false; }
    public boolean unlock(String unlockKey) { return false; }
    public boolean isQuestionable() { return false; }
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(getClass())) {
            return false;
        }
        BookMetaData that = (BookMetaData) obj;
        return (getBookCategory().equals(that.getBookCategory()) && getName().equals(that.getName()) && getInitials().equals(that.getInitials()));
    }

    public int hashCode() { return getName().hashCode(); }

    public int compareTo(BookMetaData obj) {
        int result = getBookCategory().compareTo(obj.getBookCategory());
        if (result == 0) {
            result = getAbbreviation().compareTo(obj.getAbbreviation());
        }
        if (result == 0) {
            result = getInitials().compareTo(obj.getInitials());
        }
        if (result == 0) {
            result = getName().compareTo(obj.getName());
        }
        return result;
    }

    public String getDriverName() {
        if (getDriver() == null) {
            return null;
        }
        return getDriver().getDriverName();
    }
    public String toString() {
        String internal = getInitials();
        String abbreviation = getAbbreviation();
        if (internal.equals(abbreviation)) {
            return internal;
        }
        StringBuffer buf = new StringBuffer(internal);
        buf.append('(');
        buf.append(abbreviation);
        buf.append(')');
        return buf.toString();
    }
    public String getOsisID() { return getBookCategory().getName() + '.' + getInitials(); }
    public String getUnlockKey() { return null; }

    public KeyType getKeyType() { return KeyType.LIST; }

    public BookDriver getDriver() { return this.driver; }

    public Language getLanguage() { return this.language; }

    public URI getLibrary() { return this.library; }

    public URI getLocation() { return this.location; }

    public IndexStatus getIndexStatus() { return this.indexStatus; }

    public Document toOSIS() {
        throw new UnsupportedOperationException("If you want to use this, implement it.");
    }
}
