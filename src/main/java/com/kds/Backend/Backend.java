/*
 * Copyright (C) 2025 org.kds
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kds.Backend;
/** @ingroup Application */

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;

import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;

import org.crosswire.jsword.passage.Key;

import java.io.IOException;

import java.util.List;

import org.jdom2.Content;

public interface Backend<T extends org.crosswire.jsword.book.sword.state.OpenFileState> 
{
    BookMetaData getWorkMetaData();

    void decipher(byte[] paramArrayOfbyte);
    void encipher(byte[] paramArrayOfbyte);
    void setAliasKey(Key paramKey1, Key paramKey2) throws BookException;
    void create() throws IOException, BookException;

    boolean contains(Key paramKey);
    boolean isSupported();
    boolean isWritable();

    int getRawTextLength(Key paramKey);

    String getRawText(Key paramKey) throws BookException;

    Key getGlobalKeyList() throws BookException;
    Key readIndex();

    List<Content> readToOsis(Key paramKey, RawTextToXmlProcessor paramRawTextToXmlProcessor) throws BookException;
}
