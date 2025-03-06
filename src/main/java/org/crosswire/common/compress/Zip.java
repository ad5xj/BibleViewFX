package org.crosswire.common.compress;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class Zip extends AbstractCompressor {

    public Zip(InputStream input) {
        super(input);
    }

    public ByteArrayOutputStream compress() throws IOException {
        BufferedInputStream in = new BufferedInputStream(this.input);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DeflaterOutputStream out = new DeflaterOutputStream(bos, new Deflater(), 2048);
        byte[] buf = new byte[2048];
        int count;
        for (count = in.read(buf); count != -1; count = in.read(buf)) {
            out.write(buf, 0, count);
        }
        in.close();
        out.flush();
        out.close();
        return bos;
    }

    public ByteArrayOutputStream uncompress() throws IOException {
        return uncompress(2048);
    }

    public ByteArrayOutputStream uncompress(int expectedLength) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(expectedLength);
        InflaterInputStream in = new InflaterInputStream(this.input, new Inflater(), expectedLength);
        byte[] buf = new byte[expectedLength];
        int count;
        for (count = in.read(buf); count != -1; count = in.read(buf)) {
            out.write(buf, 0, count);
        }
        in.close();
        out.flush();
        out.close();
        return out;
    }
}