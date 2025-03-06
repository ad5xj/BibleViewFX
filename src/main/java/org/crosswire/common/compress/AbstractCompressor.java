package org.crosswire.common.compress;

import java.io.InputStream;

public abstract class AbstractCompressor implements Compressor {
    protected InputStream input;

    public AbstractCompressor(InputStream input) {
        this.input = input;
    }
}
