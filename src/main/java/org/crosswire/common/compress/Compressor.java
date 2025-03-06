package org.crosswire.common.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface Compressor {
    public static final int BUF_SIZE = 2048;

    ByteArrayOutputStream compress() throws IOException;
    ByteArrayOutputStream uncompress() throws IOException;
    ByteArrayOutputStream uncompress(int paramInt) throws IOException;
}
