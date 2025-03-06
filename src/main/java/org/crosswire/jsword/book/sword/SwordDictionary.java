package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.JSOtherMsg;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;

import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.DefaultLeafKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Element;

public class SwordDictionary extends AbstractBook {

    private SourceFilter filter;

    protected SwordDictionary(SwordBookMetaData sbmd, Backend backend) {
        super((BookMetaData) sbmd, backend);
        if (!(backend instanceof AbstractKeyBackend)) {
            throw new IllegalArgumentException("AbstractBackend must be an AbstractKeyBackened");
        }
        this.filter = sbmd.getFilter();
    }

    public Iterator<Content> getOsisIterator(Key key, boolean allowEmpty, boolean allowGenTitles) throws BookException {
        assert key != null;
        List<Content> content = new ArrayList<Content>();
        Element title = OSISUtil.factory().createGeneratedTitle();
        title.addContent(key.getName());
        content.add(title);
        String txt = getBackend().getRawText(key);
        List<Content> osisContent = this.filter.toOSIS((Book) this, key, txt);
        content.addAll(osisContent);
        return content.iterator();
    }

    public String getRawText(Key key) throws BookException {
        return getBackend().getRawText(key);
    }

    public boolean contains(Key key) {
        Backend backend = getBackend();
        return (backend != null && backend.contains(key));
    }

    public List<Content> getOsis(Key key, RawTextToXmlProcessor processor) throws BookException {
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
        return (AbstractKeyBackend) getBackend();
    }

    public Key getValidKey(String name) {
        try {
            return getKey(name);
        } catch (NoSuchKeyException e) {
            return createEmptyKeyList();
        }
    }

    public Key getKey(String text) throws NoSuchKeyException {
        AbstractKeyBackend keyBackend = (AbstractKeyBackend) getBackend();
        int pos = keyBackend.indexOf((Key) new DefaultLeafKeyList(text));
        if (pos < 0) {
            if (keyBackend.getCardinality() > -pos - 1) {
                return keyBackend.get(-pos - 1);
            }
            return keyBackend.get(keyBackend.getCardinality() - 1);
        }
        return keyBackend.get(pos);
    }

    public Key createEmptyKeyList() {
        return (Key) new DefaultKeyList();
    }
}
