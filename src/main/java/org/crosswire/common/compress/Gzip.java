package org.crosswire.common.compress;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class Gzip extends AbstractCompressor {
  public Gzip(InputStream input) {
    super(input);
  }
  
  public ByteArrayOutputStream compress() throws IOException {
    BufferedInputStream in = new BufferedInputStream(this.input);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    GzipCompressorOutputStream gzipCompressorOutputStream = new GzipCompressorOutputStream(bos);
    IOUtils.copy(in, (OutputStream)gzipCompressorOutputStream);
    in.close();
    gzipCompressorOutputStream.flush();
    gzipCompressorOutputStream.close();
    return bos;
  }
  
  public ByteArrayOutputStream uncompress() throws IOException {
    return uncompress(2048);
  }
  
  public ByteArrayOutputStream uncompress(int expectedLength) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream(expectedLength);
    GzipCompressorInputStream gzipCompressorInputStream = new GzipCompressorInputStream(this.input);
    IOUtils.copy((InputStream)gzipCompressorInputStream, out);
    gzipCompressorInputStream.close();
    out.flush();
    out.close();
    return out;
  }
}
