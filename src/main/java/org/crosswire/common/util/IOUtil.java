package org.crosswire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSMsg;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.MalformedURLException;
import java.net.URI;

import java.util.Enumeration;


public final class IOUtil {
  public static void unpackZip(File file, File destdir) throws IOException {
    unpackZip(file, destdir, true, new String[0]);
  }
  
  public static void unpackZip(File file, File destdir, boolean include, String... includeExcludes) throws IOException {
    byte[] dbuf = new byte[4096];
    ZipFile zf = null;
    try {
      zf = new ZipFile(file);
      Enumeration<ZipArchiveEntry> entries = zf.getEntries();
      while (entries.hasMoreElements()) {
        ZipArchiveEntry entry = entries.nextElement();
        String entrypath = entry.getName();
        if (includeExcludes != null && includeExcludes.length > 0) {
          boolean skip = include;
          for (String filter : includeExcludes) {
            boolean matchesPath = entrypath.toLowerCase().startsWith(filter);
            if (include && matchesPath)
              skip = false; 
            if (!include && matchesPath)
              skip = true; 
          } 
          if (skip)
            continue; 
        } 
        File entryFile = new File(destdir, entrypath);
        File parentDir = entryFile.getParentFile();
        if (!parentDir.isDirectory())
          if (!parentDir.mkdirs())
            throw new MalformedURLException(JSMsg.gettext("The URL {0} could not be created as a directory.", new Object[] { parentDir.toString() }));  
        if (!entry.isDirectory()) {
          URI child = NetUtil.getURI(entryFile);
          OutputStream dataOut = NetUtil.getOutputStream(child);
          InputStream dataIn = zf.getInputStream(entry);
          while (true) {
            int count = dataIn.read(dbuf);
            if (count == -1)
              break; 
            dataOut.write(dbuf, 0, count);
          } 
          dataOut.close();
        } 
      } 
    } finally {
      close((Closeable)zf);
    } 
  }
  
  public static byte[] getZipEntry(String entrySpec) throws IOException {
    byte[] buffer = new byte[0];
    String[] parts = StringUtil.split(entrySpec, '!');
    ZipFile zipFile = null;
    InputStream zin = null;
    try {
      zipFile = new ZipFile(parts[0]);
      ZipArchiveEntry entry = zipFile.getEntry(parts[1]);
      zin = zipFile.getInputStream(entry);
      int size = (int)entry.getSize();
      buffer = new byte[size];
      int offset = 0;
      while (offset < size)
        offset += zin.read(buffer, offset, size - offset); 
      if (offset != size)
        log.error("Error: Could not read {} bytes, instead {}, for {} from {}", new Object[] { Integer.toString(size), Integer.toString(offset), parts[1], parts[0] }); 
    } finally {
      close(zin);
      close((Closeable)zipFile);
    } 
    return buffer;
  }
  
  public static void close(Closeable closeable) {
    if (null != closeable)
      try {
        closeable.close();
      } catch (IOException ex) {
        log.error("close", ex);
      }  
  }
  
  private static final Logger log = LoggerFactory.getLogger(IOUtil.class);
}