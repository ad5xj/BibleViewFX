package org.crosswire.jsword.book.sword.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.NetUtil;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.BlockType;
import org.crosswire.jsword.book.sword.SwordUtil;

import org.crosswire.jsword.versification.Testament;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import java.net.URI;

public class ZVerseBackendState extends AbstractOpenFileState {

    private static final String SUFFIX_COMP = "v";

    private static final String SUFFIX_INDEX = "s";

    private static final String SUFFIX_PART1 = "z";

    private static final String SUFFIX_TEXT = "z";

    private RandomAccessFile otCompRaf;

    private RandomAccessFile ntCompRaf;

    private RandomAccessFile otTextRaf;

    private RandomAccessFile ntTextRaf;

    private RandomAccessFile otIdxRaf;

    private RandomAccessFile ntIdxRaf;

    private Testament lastTestament;

    private long lastBlockNum;

    private byte[] lastUncompressed;

    ZVerseBackendState(BookMetaData bookMetaData, BlockType blockType) throws BookException {
        super(bookMetaData);
        this.lastBlockNum = -1L;
        URI path = SwordUtil.getExpandedDataPath(bookMetaData);
        String otAllButLast = NetUtil.lengthenURI(path, File.separator + "ot" + '.' + blockType.getIndicator() + "z").getPath();
        File otIdxFile = new File(otAllButLast + "s");
        File otTextFile = new File(otAllButLast + "z");
        File otCompFile = new File(otAllButLast + "v");
        String ntAllButLast = NetUtil.lengthenURI(path, File.separator + "nt" + '.' + blockType.getIndicator() + "z").getPath();
        File ntIdxFile = new File(ntAllButLast + "s");
        File ntTextFile = new File(ntAllButLast + "z");
        File ntCompFile = new File(ntAllButLast + "v");
        if (otIdxFile.canRead())
      try {
            this.otCompRaf = new RandomAccessFile(otIdxFile, "r");
            this.otTextRaf = new RandomAccessFile(otTextFile, "r");
            this.otIdxRaf = new RandomAccessFile(otCompFile, "r");
        } catch (FileNotFoundException ex) {
            IOUtil.close(this.otCompRaf);
            IOUtil.close(this.otTextRaf);
            IOUtil.close(this.otIdxRaf);
            assert false : ex;
            LOGGER.error("Could not open OT", ex);
        }
        if (ntIdxFile.canRead())
      try {
            this.ntCompRaf = new RandomAccessFile(ntIdxFile, "r");
            this.ntTextRaf = new RandomAccessFile(ntTextFile, "r");
            this.ntIdxRaf = new RandomAccessFile(ntCompFile, "r");
        } catch (FileNotFoundException ex) {
            IOUtil.close(this.ntCompRaf);
            IOUtil.close(this.ntTextRaf);
            IOUtil.close(this.ntIdxRaf);
            assert false : ex;
            LOGGER.error("Could not open OT", ex);
        }
    }

    public void releaseResources() {
        IOUtil.close(this.ntCompRaf);
        IOUtil.close(this.ntTextRaf);
        IOUtil.close(this.ntIdxRaf);
        IOUtil.close(this.otCompRaf);
        IOUtil.close(this.otTextRaf);
        IOUtil.close(this.otIdxRaf);
        this.ntCompRaf = null;
        this.ntTextRaf = null;
        this.ntIdxRaf = null;
        this.otCompRaf = null;
        this.otTextRaf = null;
        this.otIdxRaf = null;
    }

    public RandomAccessFile getCompRaf(Testament testament) {
        return (testament == Testament.NEW) ? this.ntCompRaf : this.otCompRaf;
    }

    public RandomAccessFile getTextRaf(Testament testament) {
        return (testament == Testament.NEW) ? this.ntTextRaf : this.otTextRaf;
    }

    public RandomAccessFile getIdxRaf(Testament testament) {
        return (testament == Testament.NEW) ? this.ntIdxRaf : this.otIdxRaf;
    }

    public Testament getLastTestament() {
        return this.lastTestament;
    }

    public long getLastBlockNum() {
        return this.lastBlockNum;
    }

    public byte[] getLastUncompressed() {
        return this.lastUncompressed;
    }

    public void setLastTestament(Testament lastTestament) {
        this.lastTestament = lastTestament;
    }

    public void setLastBlockNum(long lastBlockNum) {
        this.lastBlockNum = lastBlockNum;
    }

    public void setLastUncompressed(byte[] lastUncompressed) {
        this.lastUncompressed = lastUncompressed;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ZVerseBackendState.class);
}
