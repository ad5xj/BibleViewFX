package org.crosswire.jsword.book.sword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.compress.CompressorType;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.book.sword.state.ZVerseBackendState;

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
 * @brief Utility class to help Backend functions
 * 
 * @extends AbstractBackend
 */
public class ZVerseBackend extends AbstractBackend<ZVerseBackendState> 
{
    private static final int IDX_ENTRY_SIZE = 10;
    private static final int COMP_ENTRY_SIZE = 12;

    private static final String THISMODULE = "ZVerseBackend";
    private static final Logger lgr = LoggerFactory.getLogger(ZVerseBackend.class);

    private static boolean debug_log = true;

    private final BlockType blockType;

    public ZVerseBackend(SwordBookMetaData sbmd, BlockType blockType) 
    {
        super(sbmd);
        this.blockType = blockType;
    }

    @Override
    public boolean contains(Key key) { return (getRawTextLength(key) > 0); }

    @Override
    public int getRawTextLength(Key key) 
    {
        int index = 0;
        String v11nName = "";
        Testament testament = null;
        Versification v11n = null;
        Verse verse = null;
        ZVerseBackendState rafBook = null;
        RandomAccessFile idxRaf = null;
    
        try 
        {
            rafBook = initState();
            v11nName = getBookMetaData().getProperty("Versification");
            v11n = Versifications.instance().getVersification(v11nName);
            verse = KeyUtil.getVerse(key);
            index = verse.getOrdinal();
            testament = v11n.getTestament(index);
            index = v11n.getTestamentOrdinal(index);
            idxRaf = rafBook.getIdxRaf(testament);

            if ( idxRaf == null ) { return 0; }

            byte[] temp = SwordUtil.readRAF(idxRaf, 1L * index * 10L, 10);
            if ( (temp == null) || (temp.length == 0) ) { return 0; }
            return SwordUtil.decodeLittleEndian16(temp, 8);
        } 
        catch ( Exception e) 
        {
           String msg = "Unable to ascertain key validity - " + e.getMessage();
            lgr.error(msg, THISMODULE);
            return 0;
        } 
        finally 
        {
            OpenFileStateManager.instance().release(rafBook);
        }
    }

    @Override
    public Key getGlobalKeyList() throws BookException 
    {
        int maxIndex = 0;
        int ii = 0;
        String v11nName = "";
        Versification v11n = null;
        RocketPassage rocketPassage = null;
        ZVerseBackendState rafBook = null;
        RandomAccessFile idxRaf = null;

        try 
        {
            rafBook = initState();
            v11nName = getBookMetaData().getProperty("Versification");
            v11n = Versifications.instance().getVersification(v11nName);
            Testament[] testaments = {Testament.OLD, Testament.NEW};
            rocketPassage = new RocketPassage(v11n);
            rocketPassage.raiseEventSuppresion();
            rocketPassage.raiseNormalizeProtection();

            for (Testament currentTestament : testaments) 
            {
                idxRaf = rafBook.getIdxRaf(currentTestament);
                if ( idxRaf != null ) 
                {
                    maxIndex = v11n.getCount(currentTestament) - 1;
                    byte[] temp = SwordUtil.readRAF(idxRaf, 0L, 10 * maxIndex);
                    
                    for ( ii = 0; ii < temp.length; ii += 10 ) 
                    {
                        if ( (temp[ii + 8] != 0) || (temp[ii + 9] != 0) )
                        {
                            int ordinal = ii / 10;
                            rocketPassage.addVersifiedOrdinal(v11n.getOrdinal(currentTestament, ordinal));
                        }
                    }
                }
            }
            rocketPassage.lowerNormalizeProtection();
            rocketPassage.lowerEventSuppressionAndTest();
            return (Key) rocketPassage;
        } 
        catch ( Exception e) 
        {
            String msg = "Unable to read key list from book. - " + e.getMessage();
            lgr.error(msg,THISMODULE);
            throw new BookException(JSMsg.gettext("Unable to read key list from book.", new Object[0]));
        } 
        finally 
        {
            OpenFileStateManager.instance().release(rafBook);
        }
    }

    @Override
    public ZVerseBackendState initState() throws BookException {
        return OpenFileStateManager.instance().getZVerseBackendState(getBookMetaData(), this.blockType);
    }

    @Override
    public String readRawContent(ZVerseBackendState rafBook, Key key) throws IOException 
    {
        int index = 0;
        int verseStart = 0;
        int verseSize = 0;
        int blockStart = 0;
        int blockSize = 0;
        int uncompressedSize = 0;
        long blockNum = 0;
        byte[] uncompressed = null;
        String charset = null;
        String compressType = null;
        String v11nName = null;
        Versification v11n = null;
        Verse verse = null;
        BookMetaData bookMetaData = null;
        Testament testament = null;
        RandomAccessFile idxRaf = null;
        RandomAccessFile compRaf = null;
        RandomAccessFile textRaf = null;

        bookMetaData = getBookMetaData();
        charset = bookMetaData.getBookCharset();
        compressType = bookMetaData.getProperty("CompressType");
        try
        {
            v11nName = getBookMetaData().getProperty("Versification");
            v11n = Versifications.instance().getVersification(v11nName);
            verse = KeyUtil.getVerse(key);
            index = verse.getOrdinal();
            testament = v11n.getTestament(index);
            index = v11n.getTestamentOrdinal(index);
            idxRaf = rafBook.getIdxRaf(testament);
            compRaf = rafBook.getCompRaf(testament);
            textRaf = rafBook.getTextRaf(testament);
        }
        catch ( Exception e )
        {
            String msg = "Error reading raw content from book. - " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        if (idxRaf == null) { return ""; }
        byte[] temp = SwordUtil.readRAF(idxRaf, 1L * index * 10L, 10);
        if ( (temp == null) || (temp.length == 0) ) { return ""; }
        blockNum = SwordUtil.decodeLittleEndian32(temp, 0);
        verseStart = SwordUtil.decodeLittleEndian32(temp, 4);
        verseSize = SwordUtil.decodeLittleEndian16(temp, 8);
        if ( (blockNum == rafBook.getLastBlockNum()) && (testament == rafBook.getLastTestament()) )
        {
            uncompressed = rafBook.getLastUncompressed();
        } 
        else 
        {
            temp = SwordUtil.readRAF(compRaf, blockNum * 12L, 12);
            if ( (temp == null) || (temp.length == 0) )  { return ""; }
            blockStart = SwordUtil.decodeLittleEndian32(temp, 0);
            blockSize = SwordUtil.decodeLittleEndian32(temp, 4);
            uncompressedSize = SwordUtil.decodeLittleEndian32(temp, 8);
            byte[] data = SwordUtil.readRAF(textRaf, blockStart, blockSize);
            decipher(data);
            uncompressed = CompressorType.fromString(compressType).getCompressor(data).uncompress(uncompressedSize).toByteArray();
            rafBook.setLastBlockNum(blockNum);
            rafBook.setLastTestament(testament);
            rafBook.setLastUncompressed(uncompressed);
        }
        byte[] chopped = new byte[verseSize];
        System.arraycopy(uncompressed, verseStart, chopped, 0, verseSize);
        return SwordUtil.decode(key.getName(), chopped, charset);
    }

    @Override
    public void setAliasKey(ZVerseBackendState rafBook, Key alias, Key source) throws IOException 
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRawText(ZVerseBackendState rafBook, Key key, String text) throws BookException, IOException 
    {
        throw new UnsupportedOperationException();
    }

    public void dumpIdxRaf(Versification v11n, int ordinalStart, RandomAccessFile raf) 
    {
        int i;
        int verseStart;
        int verseSize;
        long blockNum;
        long end;
        long offset;

        i = 0;
        verseStart = -1;
        verseSize = -1;
        blockNum = -1L;
        end = -1L;
        offset = 0L;

        try 
        {
            end = raf.length();
        } 
        catch (IOException e) 
        {
            String msg = "I/O Exception on dumpIdxRaf - " + e.getMessage();
            lgr.error(msg,THISMODULE);
//            e.printStackTrace();
        }
        i = ordinalStart;
        String buf = "";

        for (offset = 0L; offset < end; offset += 10L) 
        {
            byte[] temp = null;
            try 
            {
                temp = SwordUtil.readRAF(raf, offset, 10);
            } 
            catch (IOException e) 
            {
                String msg = "I/O Exception on dumpIdxRaf - " + e.getMessage();
                lgr.error(msg,THISMODULE);
//                e.printStackTrace();
            }
            if (temp != null && temp.length > 0) {
                blockNum = SwordUtil.decodeLittleEndian32(temp, 0);
                verseStart = SwordUtil.decodeLittleEndian32(temp, 4);
                verseSize = SwordUtil.decodeLittleEndian16(temp, 8);
            }
            buf = v11n.decodeOrdinal(i++).getOsisID() + "\t" + blockNum
                + "\t" + verseStart + "\t" + verseSize;
            if ( debug_log ) { lgr.info(buf,THISMODULE); }
        }
    }

    public void dumpCompRaf(RandomAccessFile raf) 
    {
        int blockNum = 0;
        int blockStart = -1;
        int blockSize = -1;
        int uncompressedSize = -1;
        long end = -1L;
        long offset = 0;
        String buf = null;

        try 
        {
            end = raf.length();
        } 
        catch (IOException e) 
        {
            String msg = "I/O Exception on dumpCompRaf - " + e.getMessage();
            lgr.error(msg,THISMODULE);
//                e.printStackTrace();
        }

        if ( debug_log ) { lgr.info("block\tstart\tsize\tuncompressed"); };

        for (offset = 0L; offset < end; offset += 12L) 
        {
            byte[] temp = null;
            try 
            {
                temp = SwordUtil.readRAF(raf, offset, 12);
            } 
            catch (IOException e) 
            {
                String msg = "I/O Exception on dumpCompRaf - " + e.getMessage();
                lgr.error(msg,THISMODULE);
    //                e.printStackTrace();
            }
            
            if ( (temp != null) && (temp.length > 0) )
            {
                blockStart = SwordUtil.decodeLittleEndian32(temp, 0);
                blockSize = SwordUtil.decodeLittleEndian32(temp, 4);
                uncompressedSize = SwordUtil.decodeLittleEndian32(temp, 8);
            }
            buf = blockNum + "\t" + blockStart + "\t" + blockSize + "\t" + uncompressedSize;
            if ( debug_log ) { lgr.info(buf,THISMODULE); }
        }
    }
}