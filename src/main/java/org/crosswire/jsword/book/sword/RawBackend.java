package org.crosswire.jsword.book.sword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSMsg;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.RawBackendState;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.RocketPassage;
import org.crosswire.jsword.passage.Verse;

import org.crosswire.jsword.versification.Testament;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @ingroup org.crosswire.jsword.book.sword
 * 
 * @brief Helper class for backend functions
 * 
 * @param <T> 
 * @extends RawBackendState
 */
public class RawBackend<T extends RawBackendState> extends AbstractBackend<RawBackendState> 
{

    protected final int datasize;

    protected final int entrysize;

    protected static final int OFFSETSIZE = 4;

    private static final String THISMODULE = "org.crosswire.jsword.book.sword.RawBackend";
    private static final Logger lgr = LoggerFactory.getLogger(RawBackend.class);

    /**
     * @brief Constructor with params
     * 
     * @param sbmd
     * @param datasize 
     */
    public RawBackend(SwordBookMetaData sbmd, int datasize) 
    {
        super(sbmd);
        this.datasize = datasize;
        this.entrysize = 4 + datasize;
        assert datasize == 2 || datasize == 4;
    }

    public boolean contains(Key key) { return ((getRawTextLength(key) > 0)); }

    @SuppressWarnings("UnusedAssignment")
    public int getRawTextLength(Key key) 
    {
        int index;
        String v11nName;
        Verse verse;
        Versification v11n;
        Testament testament;
        RandomAccessFile idxRaf;
        RawBackendState initState;
        DataIndex dataIndex;
        
        index = -1;
        v11nName = null;
        verse = null;
        v11n = null;
        testament = null;
        idxRaf = null;
        initState = null;
        dataIndex = null;
        
        try
        {
            v11nName = getBookMetaData().getProperty("Versification");
            v11n = Versifications.instance().getVersification(v11nName);
            verse = KeyUtil.getVerse(key);
        }
        catch ( Exception e )
        {
            String msg = "";
            lgr.error(msg,THISMODULE);
        }
        initState = null;
        try 
        {
            index = verse.getOrdinal();
            testament = v11n.getTestament(index);
            index = v11n.getTestamentOrdinal(index);
            initState = initState();
            idxRaf = initState.getIdxRaf(testament);
            if (idxRaf == null) { return 0; }
            dataIndex = getIndex(idxRaf, index);
            return dataIndex.getSize();
        } 
        catch (IOException | BookException ex) 
        {
            return 0;
        } 
        finally 
        {
            OpenFileStateManager.instance().release(initState);
        }
    }

    @Override
    public Key getGlobalKeyList() throws BookException 
    {
        RawBackendState rafBook = null;
        try 
        {
            rafBook = initState();
            String v11nName = getBookMetaData().getProperty("Versification");
            Versification v11n = Versifications.instance().getVersification(v11nName);
            Testament[] testaments = {Testament.OLD, Testament.NEW};
            RocketPassage rocketPassage = new RocketPassage(v11n);
            rocketPassage.raiseEventSuppresion();
            rocketPassage.raiseNormalizeProtection();
            for (Testament currentTestament : testaments) {
                RandomAccessFile idxRaf = rafBook.getIdxRaf(currentTestament);
                if (idxRaf != null) {
                    int maxIndex = v11n.getCount(currentTestament) - 1;
                    byte[] temp = SwordUtil.readRAF(idxRaf, 0L, this.entrysize * maxIndex);
                    if (this.datasize == 2) {
                        int ii;
                        for (ii = 0; ii < temp.length; ii += this.entrysize) {
                            if (temp[ii + 4] != 0 || temp[ii + 5] != 0) {
                                int ordinal = ii / this.entrysize;
                                rocketPassage.addVersifiedOrdinal(v11n.getOrdinal(currentTestament, ordinal));
                            }
                        }
                    } else {
                        int ii;
                        for (ii = 0; ii < temp.length; ii += this.entrysize) {
                            if (temp[ii + 4] != 0 || temp[ii + 5] != 0 || temp[ii + 6] != 0 || temp[ii + 7] != 0) {
                                int ordinal = ii / this.entrysize;
                                rocketPassage.addVersifiedOrdinal(v11n.getOrdinal(currentTestament, ordinal));
                            }
                        }
                    }
                }
            }
            rocketPassage.lowerNormalizeProtection();
            rocketPassage.lowerEventSuppressionAndTest();
            return rocketPassage;
        } 
        catch ( Exception e) 
        {
            String msg = "Unable to read key list from book.";
            lgr.error(msg,THISMODULE);
            throw new BookException(JSMsg.gettext("Unable to read key list from book.", new Object[0]));
        } 
        finally 
        {
            OpenFileStateManager.instance().release(rafBook);
        }
    }

    @Override
    public T initState() throws BookException 
    {
        return (T)OpenFileStateManager.instance().getRawBackendState(getBookMetaData());
    }

    public String getRawText(RawBackendState state, Key key) throws IOException 
    {
        return readRawContent(state, key);
    }

    @Override
    public String readRawContent(RawBackendState state, Key key) throws IOException 
    {
        int index;
        String v11nName;
        Verse verse;
        Versification v11n;
        Testament testament;
        
        
        index = -1;
        verse = null;
        testament = null;
        try
        {
            v11nName = getBookMetaData().getProperty("Versification");
            v11n = Versifications.instance().getVersification(v11nName);
            verse = KeyUtil.getVerse(key);
            index = verse.getOrdinal();
            testament = v11n.getTestament(index);
            index = v11n.getTestamentOrdinal(index);
        }
        catch ( Exception e )
        {
            String msg = "Unable to read key list from book.";
            lgr.error(msg,THISMODULE);
        }
        RawBackendState initState = null;
        try 
        {
            initState = initState();
            return getEntry(state, verse.getName(), testament, index);
        } 
        catch ( Exception e ) 
        {
            String msg = "Unable to get BackendState.";
            lgr.error(msg,THISMODULE);
            return "";
        } 
        finally 
        {
            OpenFileStateManager.instance().release(initState);
        }
    }

    @Override
    public void setRawText(RawBackendState state, Key key, String text) throws BookException, IOException 
    {
        //
    }

    @Override
    public boolean isWritable() 
    {
        RawBackendState rawBackendState = null;
        try 
        {
            rawBackendState = initState();
            return rawBackendState.isWritable();
        } 
        catch (BookException e) 
        {
            String msg = "Unable to get BackendState.";
            lgr.error(msg,THISMODULE);
            return false;
        } 
        finally 
        {
            OpenFileStateManager.instance().release((OpenFileState) rawBackendState);
        }
    }

    @Override
    public void setAliasKey(RawBackendState state, Key alias, Key source) throws IOException 
    {
        String msg = "Unable to set AliasKey." 
                   + "\n     state=" + state
                   + "\t alias=" + alias
                   + "\t src=" + source
                   + "\n     error=UnsupportedOperationException";
        lgr.error(msg,THISMODULE);
        throw new UnsupportedOperationException();
    }

    protected DataIndex getIndex(RandomAccessFile raf, long entry) throws IOException 
    {
        int entryOffset;
        int entrySize;
        byte[] buffer;

        entryOffset = -1;        
        entrySize = -1;
        buffer = SwordUtil.readRAF(raf, entry * this.entrysize, this.entrysize);

        if ( (buffer == null) || (buffer.length == 0) ) 
        {
            return new DataIndex(0, 0);
        }
        entryOffset = SwordUtil.decodeLittleEndian32(buffer, 0);

        switch (this.datasize) 
        {
        case 2 ->
            {
                entrySize = SwordUtil.decodeLittleEndian16(buffer, 4);
                return new DataIndex(entryOffset, entrySize);
            }
        case 4 ->
            {
                entrySize = SwordUtil.decodeLittleEndian32(buffer, 4);
                return new DataIndex(entryOffset, entrySize);
            }
        }
        assert false : datasize;
        return new DataIndex(entryOffset, entrySize);
    }

    protected String getEntry(RawBackendState state, String name, Testament testament, long index) 
        throws IOException 
    {
        int size;
        byte[] data;
        RandomAccessFile idxRaf;
        RandomAccessFile txtRaf;
        
        size = -1;
        data = null;
        idxRaf = state.getIdxRaf(testament);
        txtRaf = state.getTextRaf(testament);
        if (idxRaf == null) { return ""; }
        DataIndex dataIndex = getIndex(idxRaf, index);
        size = dataIndex.getSize();
        if ( size <= 0 ) { return ""; }
        if ( size <  0 ) 
        {
            String msg = "In " + getBookMetaData().getInitials() 
                       + ": Verse " + name 
                       + " has a bad index size of " 
                       + Integer.toString(size);
            lgr.error(msg,THISMODULE);
            return "";
        }
        data = SwordUtil.readRAF(txtRaf, dataIndex.getOffset(), size);
        decipher(data);
        return SwordUtil.decode(name, data, getBookMetaData().getBookCharset());
    }
}