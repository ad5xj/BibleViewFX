/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2009-2012
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.sword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.RawBackendState;
import org.crosswire.jsword.book.sword.state.RawFileBackendState;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;

import org.crosswire.jsword.versification.Testament;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @ingroup org.crosswire.jsword.book.sword
 * @brief Helper class for Backend Fuctions
 * 
 * @extends RawBackend<>
 */
public class RawFileBackend extends RawBackend<RawFileBackendState> 
{
    private static final String THISMODULE = "org.crosswire.jsword.book.sword.RawBackend";
    private static final Logger lgr = LoggerFactory.getLogger(RawFileBackend.class);
    /**
     * @brief Constructor with params
     * @param sbmd
     * @param datasize 
     */
    public RawFileBackend(SwordBookMetaData sbmd, int datasize) 
    {
        super(sbmd, datasize);
    }

    public RawFileBackendState initState() throws BookException 
    {
        return OpenFileStateManager.instance().getRawFileBackendState(getBookMetaData());
    }


    public void setRawText(RawFileBackendState state, Key key, String text) 
        throws BookException, IOException 
    {
        int index;
        String v11nName;
        File dataFile;
        File txtFile;
        DataIndex dataIndex;
        Verse verse;
        Versification v11n;
        Testament testament;
        RandomAccessFile idxRaf;
        RandomAccessFile txtRaf;
        
        index  = -1;
        v11nName = "";
        dataFile = null;
        txtFile = null;
        dataIndex = null;
        verse = null;
        v11n = null;
        testament = null;
        idxRaf = null;
        txtRaf = null;

        v11nName = getBookMetaData().getProperty("Versification");
        try
        {
            v11n = Versifications.instance().getVersification(v11nName);
            verse = KeyUtil.getVerse(key);
            index = verse.getOrdinal();
            testament = v11n.getTestament(index);
            index = v11n.getTestamentOrdinal(index);
        }
        catch ( Exception e )
        {
            String msg = "";
            lgr.error(msg,THISMODULE);
        }
        idxRaf = state.getIdxRaf(testament);
        txtRaf = state.getTextRaf(testament);
        txtFile = state.getTextFile(testament);
        dataIndex = getIndex(idxRaf, index);
        
        if ( dataIndex.getSize() == 0 ) 
        {
            dataFile = createDataTextFile(state.getIncfileValue());
            updateIndexFile(idxRaf, index, txtRaf.length());
            updateDataFile(state.getIncfileValue(), txtFile);
            checkAndIncrementIncfile(state, state.getIncfileValue());
        } 
        else 
        {
            dataFile = getDataTextFile(txtRaf, dataIndex);
        }
        byte[] textData = text.getBytes("UTF-8");
        encipher(textData);
        writeTextDataFile(dataFile, textData);
    }

    public void setAliasKey(RawFileBackendState state, Key alias, Key source) throws IOException 
    {
        int aliasIndex;
        int sourceOIndex;
        String v11nName;
        Verse aliasVerse;
        Verse sourceVerse;
        Versification v11n;
        Testament testament;
        DataIndex dataIndex;
        
        aliasIndex = -1;
        sourceOIndex = -1;
        v11nName = null;
        aliasVerse = null;
        sourceVerse = null;
        v11n = null;
        testament = null;
        dataIndex = null;
        
        v11nName = getBookMetaData().getProperty("Versification");
        try
        {
            v11n = Versifications.instance().getVersification(v11nName);
        }
        catch ( Exception e )
        {
            String msg = "Error getting versification for " + v11nName
                       + "\n    error=" + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        aliasVerse = KeyUtil.getVerse(alias);
        sourceVerse = KeyUtil.getVerse(source);
        aliasIndex = aliasVerse.getOrdinal();
        testament = v11n.getTestament(aliasIndex);
        aliasIndex = v11n.getTestamentOrdinal(aliasIndex);
        RandomAccessFile idxRaf = state.getIdxRaf(testament);
        sourceOIndex = sourceVerse.getOrdinal();
        sourceOIndex = v11n.getTestamentOrdinal(sourceOIndex);
        dataIndex = getIndex(idxRaf, sourceOIndex);
        updateIndexFile(idxRaf, aliasIndex, dataIndex.getOffset());
    }

    @Override
    public void create() throws IOException, BookException 
    {
        super.create();
        createDataFiles();
        createIndexFiles();
        RawFileBackendState state = null;
        try 
        {
            state = initState();
            createIncfile(state);
            prepopulateIndexFiles(state);
            prepopulateIncfile(state);
        } 
        finally 
        {
            OpenFileStateManager.instance().release(state);
        }
    }

    protected String getEntry(RawBackendState state, String name, Testament testament, long index) 
        throws IOException 
    {
        int size;
        byte[] textBytes;
        File dataFile;
        RandomAccessFile idxRaf;
        RandomAccessFile txtRaf;
        DataIndex dataIndex;
        
        idxRaf = state.getIdxRaf(testament);
        txtRaf = state.getTextRaf(testament);
        dataIndex = getIndex(idxRaf, index);
        size = dataIndex.getSize();
        if ( size == 0 ) { return ""; }
        if ( size < 0 ) 
        {
            String msg = "In " + getBookMetaData().getInitials()
                       + ": Verse " + name
                       + " has a bad index size of "
                       + Integer.toString(size)
                       + "\n    error=Bad Index size";
            lgr.error(msg,THISMODULE);
            return "";
        }

        try 
        {
            dataFile = getDataTextFile(txtRaf, dataIndex);
            textBytes = readTextDataFile(dataFile);
            decipher(textBytes);
            return SwordUtil.decode(name, textBytes, getBookMetaData().getBookCharset());
        } 
        catch (BookException e) 
        {
            String msg = "Error getting and deciphering dataFile." + e.getMessage();
            lgr.error(msg,THISMODULE);
            throw new IOException(e.getMessage());
        }
    }
    protected void updateIndexFile(RandomAccessFile idxRaf, long index, long dataFileStartPosition) throws IOException 
    {
        int dataFileLengthValue;
        long indexFileWriteOffset;
        byte[] startPositionData;
        byte[] lengthValueData;
        byte[] indexFileWriteData;
        
        indexFileWriteOffset = index * this.entrysize;
        dataFileLengthValue = 7;
        startPositionData = littleEndian32BitByteArrayFromInt((int) dataFileStartPosition);
        lengthValueData = littleEndian16BitByteArrayFromShort((short) dataFileLengthValue);
        indexFileWriteData = new byte[6];
        indexFileWriteData[0] = startPositionData[0];
        indexFileWriteData[1] = startPositionData[1];
        indexFileWriteData[2] = startPositionData[2];
        indexFileWriteData[3] = startPositionData[3];
        indexFileWriteData[4] = lengthValueData[0];
        indexFileWriteData[5] = lengthValueData[1];
        SwordUtil.writeRAF(idxRaf, indexFileWriteOffset, indexFileWriteData);
    }

    protected void updateDataFile(long ordinal, File txtFile) throws IOException 
    {
        String fileName = String.format("%07d\r\n", new Object[]{Long.valueOf(ordinal)});
        BufferedOutputStream bos = null;
        try 
        {
            bos = new BufferedOutputStream(new FileOutputStream(txtFile, true));
            bos.write(fileName.getBytes(getBookMetaData().getBookCharset()));
        } 
        finally 
        {
            if (bos != null) { bos.close(); }
        }
    }

    private File createDataTextFile(int index) throws BookException, IOException 
    {
        String dataPath = SwordUtil.getExpandedDataPath(getBookMetaData()).getPath();
        dataPath = dataPath + File.separator + String.format("%07d", new Object[]{Integer.valueOf(index)});
        File dataFile = new File(dataPath);
        if (!dataFile.exists() && !dataFile.createNewFile()) 
        {
            String msg = "Could not create data file in " + THISMODULE;
            lgr.error(msg,THISMODULE);
            throw new IOException("Could not create data file.");
        }
        return dataFile;
    }

    private String getTextFilename(RandomAccessFile txtRaf, DataIndex dataIndex) throws IOException 
    {
        byte[] data = SwordUtil.readRAF(txtRaf, dataIndex.getOffset(), dataIndex.getSize());
        decipher(data);
        if ( data.length == 7 ) 
        { 
            return new String(data, 0, 7); 
        }
        else
        {
            String msg = "Read data is not of appropriate size of 9 bytes in " + THISMODULE;
            lgr.error(msg,THISMODULE);
            throw new IOException("Datalength is not 9 bytes!");
        }
    }

    private File getDataTextFile(RandomAccessFile txtRaf, DataIndex dataIndex) 
        throws IOException, BookException 
    {
        String dataFilename = getTextFilename(txtRaf, dataIndex);
        String dataPath = SwordUtil.getExpandedDataPath(getBookMetaData()).getPath() + File.separator + dataFilename;
        return new File(dataPath);
    }


    private void checkAndIncrementIncfile(RawFileBackendState state, int index) throws IOException 
    {
        if (index >= state.getIncfileValue()) 
        {
            int incValue = index + 1;
            state.setIncfileValue(incValue);
            writeIncfile(state, incValue);
        }
    }


    private void createDataFiles() throws IOException, BookException 
    {
        String path = SwordUtil.getExpandedDataPath(getBookMetaData()).getPath();
        File otTextFile = new File(path + File.separator + "ot");
        if (!otTextFile.exists() && !otTextFile.createNewFile()) 
        {
            String msg = "Could not create ot text file in " + THISMODULE;
            lgr.error(msg,THISMODULE);
            throw new IOException("Could not create ot text file.");
        }

        File ntTextFile = new File(path + File.separator + "nt");
        if (!ntTextFile.exists() && !ntTextFile.createNewFile()) 
        {
            String msg = "Could not create nt text file in " + THISMODULE;
            lgr.error(msg,THISMODULE);
            throw new IOException("Could not create nt text file.");
        }
    }

    private void createIndexFiles() throws IOException, BookException 
    {
        String path = SwordUtil.getExpandedDataPath(getBookMetaData()).getPath();
        File otIndexFile = new File(path + File.separator + "ot" + ".vss");
        if (!otIndexFile.exists() && !otIndexFile.createNewFile()) 
        {
            String msg = "Could not create ot index file in " + THISMODULE;
            lgr.error(msg,THISMODULE);
            throw new IOException("Could not create ot index file.");
        }
        
        File ntIndexFile = new File(path + File.separator + "nt" + ".vss");
        if (!ntIndexFile.exists() && !ntIndexFile.createNewFile()) {
            String msg = "Could not create nt index file in " + THISMODULE;
            lgr.error(msg,THISMODULE);
            throw new IOException("Could not create nt index file.");
        }
    }

    private void prepopulateIndexFiles(RawFileBackendState state) throws IOException 
    {
        int otCount;
        int ntCount;
        String v11nName;
        Versification v11n;
        BufferedOutputStream otIdxBos;
        BufferedOutputStream ntIdxBos;

        otCount = -1;
        ntCount = -1;
        v11nName = null;
        v11n = null;
        otIdxBos = null;
        ntIdxBos = null;
        
        v11nName = getBookMetaData().getProperty("Versification");
        try
        {
            v11n = Versifications.instance().getVersification(v11nName);
            otCount = v11n.getCount(Testament.OLD);
            ntCount = v11n.getCount(Testament.NEW) + 1;
            otIdxBos = new BufferedOutputStream(new FileOutputStream(state.getIdxFile(Testament.OLD), false));
        }
        catch ( Exception e )
        {
            String msg = "Could not repopulate index file in " + THISMODULE;
            lgr.error(msg,THISMODULE);
        }
    
        try 
        {
            for (int i = 0; i < otCount; i++) 
            {
                writeInitialIndex(otIdxBos);
            }
        } 
        finally 
        {
            otIdxBos.close();
        }
        
        ntIdxBos = new BufferedOutputStream(new FileOutputStream(state.getIdxFile(Testament.NEW), false));
        try 
        {
            for (int i = 0; i < ntCount; i++) 
            {
                writeInitialIndex(ntIdxBos);
            }
        } 
        finally 
        {
            ntIdxBos.close();
        }
    }

    private void createIncfile(RawFileBackendState state) throws IOException, BookException 
    {
        File tempIncfile = new File(SwordUtil.getExpandedDataPath(getBookMetaData()).getPath() + File.separator + "incfile");
        if (!tempIncfile.exists() && !tempIncfile.createNewFile()) 
        {
            String msg = "Could not create incfile file in " + THISMODULE;
            lgr.error(msg,THISMODULE);
            throw new IOException("Could not create incfile file.");
        }
        state.setIncfile(tempIncfile);
    }

    private void prepopulateIncfile(RawFileBackendState state) throws IOException { writeIncfile(state, 1); }

    private void writeIncfile(RawFileBackendState state, int value) throws IOException 
    {
        FileOutputStream fos = null;
        try 
        {
            fos = new FileOutputStream(state.getIncfile(), false);
            fos.write(littleEndian32BitByteArrayFromInt(value));
        } 
        catch ( FileNotFoundException e ) 
        {
            String msg = "Error on writing to incfile, file should exist already! - " 
                       + e.getMessage();
            lgr.error(msg, THISMODULE);
        } 
        finally 
        {
            if (fos != null) { fos.close(); }
        }
    }

    private void writeInitialIndex(BufferedOutputStream outStream) throws IOException 
    {
        outStream.write(littleEndian32BitByteArrayFromInt(0));
        outStream.write(littleEndian16BitByteArrayFromShort((short) 0));
    }

    private byte[] readTextDataFile(File dataFile) throws IOException 
    {
        BufferedInputStream inStream = null;
        try 
        {
            int len = (int) dataFile.length();
            byte[] textData = new byte[len];
            inStream = new BufferedInputStream(new FileInputStream(dataFile));
            if (inStream.read(textData) != len) 
            {
                lgr.error("Read data is not of appropriate size of {} bytes!", Integer.toString(len));
                throw new IOException("data is not " + len + " bytes long");
            }
            return textData;
        } 
        catch (FileNotFoundException ex) 
        {
            String msg = "Could not read text data file, file not found: " + dataFile.getName()
                       + "\n    error= " + ex.getMessage();
            lgr.error(msg,THISMODULE);
            throw ex;
        } 
        finally 
        {
            if (inStream != null) { inStream.close(); }
        }
    }

    private void writeTextDataFile(File dataFile, byte[] textData) throws IOException 
    {
        BufferedOutputStream bos = null;
        try 
        {
            bos = new BufferedOutputStream(new FileOutputStream(dataFile, false));
            bos.write(textData);
        } 
        finally 
        {
            if (bos != null) { bos.close(); }
        }
    }

    private byte[] littleEndian32BitByteArrayFromInt(int val) 
    {
        byte[] buffer = new byte[4];
        SwordUtil.encodeLittleEndian32(val, buffer, 0);
        return buffer;
    }

    private byte[] littleEndian16BitByteArrayFromShort(short val) 
    {
        byte[] buffer = new byte[2];
        SwordUtil.encodeLittleEndian16(val, buffer, 0);
        return buffer;
    }
}
