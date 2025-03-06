package org.crosswire.common.compress;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class XZ extends AbstractCompressor {
  public XZ(InputStream input) {
    super(input);
  }
  
  public ByteArrayOutputStream compress() throws IOException {
    BufferedInputStream in = new BufferedInputStream(this.input);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XZCompressorOutputStream xZCompressorOutputStream = new XZCompressorOutputStream(bos);
    IOUtils.copy(in, (OutputStream)xZCompressorOutputStream);
    in.close();
    xZCompressorOutputStream.flush();
    xZCompressorOutputStream.close();
    return bos;
  }
  
  public ByteArrayOutputStream uncompress() throws IOException {
    return uncompress(2048);
  }
  
  public ByteArrayOutputStream uncompress(int expectedLength) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream(expectedLength);
    XZCompressorInputStream xZCompressorInputStream = new XZCompressorInputStream(this.input);
    IOUtils.copy((InputStream)xZCompressorInputStream, out);
    xZCompressorInputStream.close();
    out.flush();
    out.close();
    return out;
  }
}
