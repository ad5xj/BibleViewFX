package org.crosswire.jsword.book.sword.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.Reporter;

import org.crosswire.jsword.JSMsg;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.SwordUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.net.URI;

public class GenBookBackendState extends AbstractOpenFileState {

    private static final String EXTENSION_BDT = ".bdt";

    private File bdtFile;

    private RandomAccessFile bdtRaf;

    GenBookBackendState(BookMetaData bookMetaData) {
        super(bookMetaData);
        URI path = null;
        try {
            path = SwordUtil.getExpandedDataPath(bookMetaData);
        } catch (BookException e) {
            Reporter.informUser(this, (LucidException) e);
            return;
        }
        this.bdtFile = new File(path.getPath() + ".bdt");
        if (!this.bdtFile.canRead()) {
            Reporter.informUser(this, (LucidException) new BookException(JSMsg.gettext("Error reading {0}", new Object[]{this.bdtFile.getAbsolutePath()})));
            return;
        }
        try {
            this.bdtRaf = new RandomAccessFile(this.bdtFile, "r");
        } catch (IOException ex) {
            IOUtil.close(this.bdtRaf);
            LOGGER.error("failed to open files", ex);
            this.bdtRaf = null;
        }
    }

    public void releaseResources() {
        IOUtil.close(this.bdtRaf);
        this.bdtRaf = null;
    }

    public RandomAccessFile getBdtRaf() {
        return this.bdtRaf;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GenBookBackendState.class);
}
