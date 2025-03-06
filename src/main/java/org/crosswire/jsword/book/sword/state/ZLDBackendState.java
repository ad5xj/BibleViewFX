package org.crosswire.jsword.book.sword.state;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.SwordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZLDBackendState extends RawLDBackendState {

    ZLDBackendState(BookMetaData bookMetaData) throws BookException {
        super(bookMetaData);
        this.zdxFile = null;
        this.zdtFile = null;
        this.zdxRaf = null;
        this.zdtRaf = null;
        this.lastBlockNum = -1L;
        this.lastUncompressed = EMPTY_BYTES;
        URI path = null;
        try {
            path = SwordUtil.getExpandedDataPath(bookMetaData);
        } catch (BookException e) {
            Reporter.informUser(this, (LucidException) e);
            return;
        }
        try {
            this.zdxFile = new File(path.getPath() + ".zdx");
            this.zdtFile = new File(path.getPath() + ".zdt");
            if (!this.zdxFile.canRead()) {
                Reporter.informUser(this, (LucidException) new BookException(JSMsg.gettext("Error reading {0}", new Object[]{this.zdtFile.getAbsolutePath()})));
                return;
            }
            if (!this.zdtFile.canRead()) {
                Reporter.informUser(this, (LucidException) new BookException(JSMsg.gettext("Error reading {0}", new Object[]{this.zdtFile.getAbsolutePath()})));
                return;
            }
            this.zdxRaf = new RandomAccessFile(this.zdxFile, "r");
            this.zdtRaf = new RandomAccessFile(this.zdtFile, "r");
        } catch (IOException ex) {
            IOUtil.close(this.zdxRaf);
            IOUtil.close(this.zdtRaf);
            LOGGER.error("failed to open files", ex);
            this.zdxRaf = null;
            this.zdtRaf = null;
            return;
        }
    }

    public void releaseResources() {
        super.releaseResources();
        this.lastBlockNum = -1L;
        this.lastUncompressed = EMPTY_BYTES;
        IOUtil.close(this.zdxRaf);
        IOUtil.close(this.zdtRaf);
        this.zdxRaf = null;
        this.zdtRaf = null;
    }

    public RandomAccessFile getZdxRaf() {
        return this.zdxRaf;
    }

    public RandomAccessFile getZdtRaf() {
        return this.zdtRaf;
    }

    public long getLastBlockNum() {
        return this.lastBlockNum;
    }

    public byte[] getLastUncompressed() {
        return this.lastUncompressed;
    }

    public void setLastBlockNum(long lastBlockNum) {
        this.lastBlockNum = lastBlockNum;
    }

    public void setLastUncompressed(byte[] lastUncompressed) {
        this.lastUncompressed = lastUncompressed;
    }

    private static final byte[] EMPTY_BYTES = new byte[0];

    private static final String EXTENSION_Z_INDEX = ".zdx";
    private static final String EXTENSION_Z_DATA = ".zdt";
    private static final Logger LOGGER = LoggerFactory.getLogger(ZLDBackendState.class);

    private long lastBlockNum;

    private byte[] lastUncompressed;

    private File zdxFile;
    private File zdtFile;

    private RandomAccessFile zdxRaf;
    private RandomAccessFile zdtRaf;
}
