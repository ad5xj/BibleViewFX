package org.crosswire.jsword.book.sword.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.Reporter;

import org.crosswire.jsword.JSMsg;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.RawLDBackend;
import org.crosswire.jsword.book.sword.SwordUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.net.URI;

public class RawLDBackendState extends AbstractOpenFileState {

    private int size;

    private File idxFile;

    private RandomAccessFile idxRaf;

    private File datFile;

    private RandomAccessFile datRaf;

    RawLDBackendState(BookMetaData bookMetaData) throws BookException {
        super(bookMetaData);
        this.size = -1;
        URI path = null;
        try {
            path = SwordUtil.getExpandedDataPath(bookMetaData);
        } catch (BookException e) {
            Reporter.informUser(this, (LucidException) e);
            throw e;
        }
        try {
            this.idxFile = new File(path.getPath() + ".idx");
            this.datFile = new File(path.getPath() + ".dat");
            if (!this.idxFile.canRead()) {
                Reporter.informUser(this, (LucidException) new BookException(JSMsg.gettext("Error reading {0}", new Object[]{this.idxFile.getAbsolutePath()})));
                return;
            }
            if (!this.datFile.canRead()) {
                BookException prob = new BookException(JSMsg.gettext("Error reading {0}", new Object[]{this.datFile.getAbsolutePath()}));
                Reporter.informUser(this, (LucidException) prob);
                throw prob;
            }
            this.idxRaf = new RandomAccessFile(this.idxFile, "r");
            this.datRaf = new RandomAccessFile(this.datFile, "r");
        } catch (IOException ex) {
            IOUtil.close(this.idxRaf);
            IOUtil.close(this.datRaf);
            LOGGER.error("failed to open files", ex);
            this.idxRaf = null;
            this.datRaf = null;
            throw new BookException(JSMsg.gettext("Error reading {0}", new Object[]{this.datFile.getAbsolutePath()}), ex);
        }
    }

    public void releaseResources() {
        this.size = -1;
        IOUtil.close(this.idxRaf);
        IOUtil.close(this.datRaf);
        this.idxRaf = null;
        this.datRaf = null;
    }

    public int getSize() {
        return this.size;
    }

    public File getIdxFile() {
        return this.idxFile;
    }

    public RandomAccessFile getIdxRaf() {
        return this.idxRaf;
    }

    public RandomAccessFile getDatRaf() {
        return this.datRaf;
    }

    public void setSize(int size) {
        this.size = size;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RawLDBackend.class);
}
