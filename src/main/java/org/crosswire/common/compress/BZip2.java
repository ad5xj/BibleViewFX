package org.crosswire.common.compress;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class BZip2 extends AbstractCompressor {

    public BZip2(InputStream input) {
        super(input);
    }

    public ByteArrayOutputStream compress() throws IOException {
        BufferedInputStream in = new BufferedInputStream(this.input);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BZip2CompressorOutputStream bZip2CompressorOutputStream = new BZip2CompressorOutputStream(bos);
        IOUtils.copy(in, (OutputStream) bZip2CompressorOutputStream);
        in.close();
        bZip2CompressorOutputStream.flush();
        bZip2CompressorOutputStream.close();
        return bos;
    }

    public ByteArrayOutputStream uncompress() throws IOException {
        return uncompress(2048);
    }

    public ByteArrayOutputStream uncompress(int expectedLength) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(expectedLength);
        BZip2CompressorInputStream bZip2CompressorInputStream = new BZip2CompressorInputStream(this.input);
        IOUtils.copy((InputStream) bZip2CompressorInputStream, out);
        bZip2CompressorInputStream.close();
        out.flush();
        out.close();
        return out;
    }
}
