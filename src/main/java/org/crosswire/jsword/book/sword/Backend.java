package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.passage.Key;

import java.io.IOException;

import java.util.List;

import org.jdom2.Content;

public interface Backend<T extends OpenFileState> {

    BookMetaData getBookMetaData();

    void decipher(byte[] paramArrayOfbyte);

    void encipher(byte[] paramArrayOfbyte);

    @Deprecated
    Key readIndex();

    boolean contains(Key paramKey);

    String getRawText(Key paramKey) throws BookException;

    void setAliasKey(Key paramKey1, Key paramKey2) throws BookException;

    int getRawTextLength(Key paramKey);

    Key getGlobalKeyList() throws BookException;

    List<Content> readToOsis(Key paramKey, RawTextToXmlProcessor paramRawTextToXmlProcessor) throws BookException;

    void create() throws IOException, BookException;

    boolean isSupported();

    boolean isWritable();
}
