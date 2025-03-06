package org.crosswire.jsword.book.sword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.crypt.Sapphire;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.KeyType;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Content;
public abstract class AbstractBackend<T extends OpenFileState> implements StatefulFileBackedBackend<T>, Backend<T> {

    private SwordBookMetaData bmd;

    public AbstractBackend() {
    }

    public AbstractBackend(SwordBookMetaData sbmd) {
        this.bmd = sbmd;
    }

    public BookMetaData getBookMetaData() {
        return (BookMetaData) this.bmd;
    }

    public void decipher(byte[] data) {
        String cipherKeyString = getBookMetaData().getProperty("CipherKey");
        if (cipherKeyString != null) {
            Sapphire cipherEngine = new Sapphire(cipherKeyString.getBytes());
            for (int i = 0; i < data.length; i++) {
                data[i] = cipherEngine.cipher(data[i]);
            }
            cipherEngine.burn();
        }
    }

    public void encipher(byte[] data) {
        decipher(data);
    }

    public Key readIndex() {
        return null;
    }

    public abstract boolean contains(Key paramKey);

    public String getRawText(Key key) throws BookException {
        T state = null;
        try {
            state = initState();
            return readRawContent(state, key);
        } catch (IOException e) {
            throw new BookException("Unable to obtain raw content from backend for key='" + key + '\'', e);
        } finally {
            OpenFileStateManager.instance().release((OpenFileState) state);
        }
    }

    public void setAliasKey(Key alias, Key source) throws BookException {
        T state = null;
        try {
            state = initState();
            setAliasKey(state, alias, source);
        } catch (IOException e) {
            throw new BookException(JSOtherMsg.lookupText("Unable to save {0}.", new Object[]{alias.getOsisID()}));
        } finally {
            OpenFileStateManager.instance().release((OpenFileState) state);
        }
    }

    public int getRawTextLength(Key key) {
        try {
            String raw = getRawText(key);
            return (raw == null) ? 0 : raw.length();
        } catch (BookException e) {
            return 0;
        }
    }

    public Key getGlobalKeyList() throws BookException {
        throw new UnsupportedOperationException("Fast global key list unsupported in this backend");
    }

    public List<Content> readToOsis(Key key, RawTextToXmlProcessor processor) throws BookException {
        List<Content> content = new ArrayList<Content>();
        T openFileState = null;
        try {
            List<Content> list;
            openFileState = initState();
            switch (this.bmd.getKeyType()) {
                case LIST:
                    readNormalOsis(key, processor, content, openFileState);
                    list = content;
                    return list;
                case TREE:
                    readNormalOsisSingleKey(key, processor, content, openFileState);
                    list = content;
                    return list;
                case VERSE:
                    readPassageOsis(key, processor, content, openFileState);
                    list = content;
                    return list;
            }
            throw new BookException("Book has unsupported type of key");
        } finally {
            OpenFileStateManager.instance().release((OpenFileState) openFileState);
        }
    }

    private void readNormalOsis(Key key, RawTextToXmlProcessor processor, List<Content> content, T openFileState) throws BookException {
        Iterator<Key> iterator = key.iterator();
        while (iterator.hasNext()) {
            Key next = iterator.next();
            try {
                String rawText = readRawContent(openFileState, next);
                processor.postVerse(next, content, rawText);
            } catch (IOException e) {
                throwFailedKeyException(key, next, e);
            }
        }
    }

    private void readNormalOsisSingleKey(Key key, RawTextToXmlProcessor processor, List<Content> content, T openFileState) throws BookException {
        try {
            String rawText = readRawContent(openFileState, key);
            processor.postVerse(key, content, rawText);
        } catch (IOException e) {
            throwFailedKeyException(key, key, e);
        }
    }

    private Verse readPassageOsis(Key key, RawTextToXmlProcessor processor, List<Content> content, T openFileState) throws BookException {
        Verse currentVerse = null;
        Passage ref = KeyUtil.getPassage(key);
        Iterator<VerseRange> rit = ref.rangeIterator(RestrictionType.CHAPTER);
        while (rit.hasNext()) {
            VerseRange range = rit.next();
            processor.preRange(range, content);
            for (Key verseInRange : range) {
                currentVerse = KeyUtil.getVerse(verseInRange);
                try {
                    String rawText = readRawContent(openFileState, (Key) currentVerse);
                    processor.postVerse(verseInRange, content, rawText);
                } catch (IOException e) {
                    LOGGER.debug(e.getMessage(), e);
                }
            }
        }
        return currentVerse;
    }

    private void throwFailedKeyException(Key masterKey, Key currentKey, IOException e) throws BookException {
        if (currentKey == null) {
            throw new BookException(JSMsg.gettext("Error reading {0}", new Object[]{masterKey.getName()}), e);
        }
        throw new BookException(JSMsg.gettext("Error reading {0}", new Object[]{currentKey.getName()}), e);
    }

    public void create() throws IOException, BookException {
        File dataPath = new File(SwordUtil.getExpandedDataPath(getBookMetaData()));
        if (!dataPath.exists() && !dataPath.mkdirs()) {
            throw new IOException("Unable to create module data path!");
        }
    }

    public boolean isSupported() {
        return true;
    }

    public boolean isWritable() {
        return false;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBackend.class);
}
