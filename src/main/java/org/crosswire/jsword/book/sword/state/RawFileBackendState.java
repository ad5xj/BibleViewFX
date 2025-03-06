package org.crosswire.jsword.book.sword.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.IOUtil;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.SwordUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RawFileBackendState extends RawBackendState {

    public static final String INCFILE = "incfile";

    private File incfile;

    private int incfileValue;

    RawFileBackendState(BookMetaData bookMetaData) throws BookException {
        super(bookMetaData);
        this.incfileValue = -1;
    }

    public boolean isWritable() {
        File incFile = getIncfile();
        if (existsAndCanReadAndWrite(this.otTextFile) && existsAndCanReadAndWrite(this.ntTextFile) && existsAndCanReadAndWrite(this.otIdxFile) && existsAndCanReadAndWrite(this.otTextFile) && (incFile == null || existsAndCanReadAndWrite(incFile))) {
            return true;
        }
        return false;
    }

    private boolean existsAndCanReadAndWrite(File file) {
        return (file.exists() && file.canRead() && file.canWrite());
    }

    private int readIncfile() throws IOException {
        int ret = -1;
        if (this.incfile == null) {
            initIncFile();
        }
        if (this.incfile != null) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(this.incfile);
                byte[] buffer = new byte[4];
                if (fis.read(buffer) != 4) {
                    LOGGER.error("Read data is not of appropriate size of 4 bytes!");
                    throw new IOException("Incfile is not 4 bytes long");
                }
                ret = SwordUtil.decodeLittleEndian32(buffer, 0);
                this.incfileValue = ret;
            } catch (FileNotFoundException e) {
                LOGGER.error("Error on writing to incfile, file should exist already!: {}", e.getMessage(), e);
            } finally {
                IOUtil.close(fis);
            }
        }
        return ret;
    }

    private void initIncFile() {
        try {
            File tempIncfile = new File(SwordUtil.getExpandedDataPath(getBookMetaData()).getPath() + File.separator + "incfile");
            if (tempIncfile.exists()) {
                this.incfile = tempIncfile;
            }
        } catch (BookException e) {
            LOGGER.error("Error on checking incfile: {}", e.getMessage(), e);
            this.incfile = null;
        }
    }

    public int getIncfileValue() {
        if (this.incfileValue == -1)
      try {
            readIncfile();
        } catch (IOException e) {
            LOGGER.error("IO Error: {}", e.getMessage(), e);
        }
        return this.incfileValue;
    }

    public void setIncfileValue(int incValue) {
        this.incfileValue = incValue;
    }

    public File getIncfile() {
        if (this.incfile == null) {
            initIncFile();
        }
        return this.incfile;
    }

    public void setIncfile(File incfile) {
        this.incfile = incfile;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RawFileBackendState.class);
}
