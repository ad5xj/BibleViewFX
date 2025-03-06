package org.crosswire.jsword.book.sword.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;

import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.SwordUtil;
import org.crosswire.jsword.versification.Testament;

import java.net.URI;

public class RawBackendState extends AbstractOpenFileState {

    protected RandomAccessFile otIdxRaf;

    protected RandomAccessFile ntIdxRaf;

    protected RandomAccessFile otTextRaf;

    protected RandomAccessFile ntTextRaf;

    protected File ntIdxFile;

    protected File ntTextFile;

    protected File otIdxFile;

    protected File otTextFile;

    RawBackendState(BookMetaData bookMetaData) throws BookException {
        super(bookMetaData);
        URI path = SwordUtil.getExpandedDataPath(bookMetaData);
        URI otPath = NetUtil.lengthenURI(path, File.separator + "ot");
        this.otTextFile = new File(otPath.getPath());
        this.otIdxFile = new File(otPath.getPath() + ".vss");
        URI ntPath = NetUtil.lengthenURI(path, File.separator + "nt");
        this.ntTextFile = new File(ntPath.getPath());
        this.ntIdxFile = new File(ntPath.getPath() + ".vss");
        if (!this.otTextFile.canRead() && !this.ntTextFile.canRead()) {
            BookException prob = new BookException(JSOtherMsg.lookupText("Missing data files for old and new testaments in {0}.", new Object[]{path}));
            Reporter.informUser(this, (LucidException) prob);
            throw prob;
        }
        String fileMode = isWritable() ? "rw" : "r";
        if (this.otIdxFile.canRead())
      try {
            this.otIdxRaf = new RandomAccessFile(this.otIdxFile, fileMode);
            this.otTextRaf = new RandomAccessFile(this.otTextFile, fileMode);
        } catch (FileNotFoundException ex) {
            IOUtil.close(this.otIdxRaf);
            IOUtil.close(this.otTextRaf);
            assert false : ex;
            LOGGER.error("Could not open OT", ex);
            this.ntIdxRaf = null;
            this.ntTextRaf = null;
        }
        if (this.ntIdxFile.canRead())
      try {
            this.ntIdxRaf = new RandomAccessFile(this.ntIdxFile, fileMode);
            this.ntTextRaf = new RandomAccessFile(this.ntTextFile, fileMode);
        } catch (FileNotFoundException ex) {
            IOUtil.close(this.ntIdxRaf);
            IOUtil.close(this.ntTextRaf);
            assert false : ex;
            LOGGER.error("Could not open NT", ex);
            this.ntIdxRaf = null;
            this.ntTextRaf = null;
        }
    }

    public boolean isWritable() {
        if (this.otIdxFile.canRead() && (this.otIdxFile.canWrite() || !this.otTextFile.canWrite())) {
            return false;
        }
        if (this.ntIdxFile.canRead() && (this.ntIdxFile.canWrite() || !this.ntTextFile.canWrite())) {
            return false;
        }
        return (this.otIdxFile.canRead() || this.ntIdxFile.canRead());
    }

    public void releaseResources() {
        IOUtil.close(this.ntIdxRaf);
        IOUtil.close(this.ntTextRaf);
        IOUtil.close(this.otIdxRaf);
        IOUtil.close(this.otTextRaf);
        this.ntIdxRaf = null;
        this.ntTextRaf = null;
        this.otIdxRaf = null;
        this.otTextRaf = null;
    }

    public RandomAccessFile getIdxRaf(Testament testament) {
        return (testament == Testament.NEW) ? this.ntIdxRaf : this.otIdxRaf;
    }

    public RandomAccessFile getTextRaf(Testament testament) {
        return (testament == Testament.NEW) ? this.ntTextRaf : this.otTextRaf;
    }

    public RandomAccessFile getOtTextRaf() {
        return this.otTextRaf;
    }

    public RandomAccessFile getNtTextRaf() {
        return this.ntTextRaf;
    }

    public File getTextFile(Testament testament) {
        return (testament == Testament.NEW) ? this.ntTextFile : this.otTextFile;
    }

    public File getIdxFile(Testament testament) {
        return (testament == Testament.NEW) ? this.ntIdxFile : this.otIdxFile;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RawBackendState.class);
}
