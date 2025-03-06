package org.crosswire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.jar.JarEntry;

public final class NetUtil {
    public static final String PROTOCOL_FILE = "file";
    public static final String PROTOCOL_HTTP = "http";
    public static final String PROTOCOL_FTP = "ftp";
    public static final String PROTOCOL_JAR = "jar";
    public static final String INDEX_FILE = "index.txt";
    public static final String SEPARATOR = "/";
    public static final String AUTH_SEPERATOR_USERNAME = "@";
    public static final String AUTH_SEPERATOR_PASSWORD = ":";
    private static final String TEMP_SUFFIX = "tmp";

    private static File cachedir;

    public static URI copy(URI uri) {
        try {
            return new URI(uri.toString());
        } catch (URISyntaxException e) {
            assert false : e;
            return null;
        }
    }

    public static void makeDirectory(URI orig) throws MalformedURLException {
        checkFileURI(orig);
        File file = new File(orig.getPath());
        if (file.isFile()) {
            throw new MalformedURLException(JSMsg.gettext("The URL {0} is a file.", new Object[]{orig}));
        }
        if (!file.isDirectory()) {
            if (!file.mkdirs()) {
                throw new MalformedURLException(JSMsg.gettext("The URL {0} could not be created as a directory.", new Object[]{orig}));
            }
        }
    }

    public static void makeFile(URI orig) throws MalformedURLException, IOException {
        checkFileURI(orig);
        File file = new File(orig.getPath());
        if (file.isDirectory()) {
            throw new MalformedURLException(JSMsg.gettext("The URL {0} is a directory.", new Object[]{orig}));
        }
        if (!file.isFile()) {
            FileOutputStream fout = new FileOutputStream(file);
            fout.close();
            if (!file.isFile()) {
                throw new MalformedURLException(JSMsg.gettext("The URL {0} could not be created as a file.", new Object[]{orig}));
            }
        }
    }

    public static boolean isFile(URI uri) {
        if (uri.getScheme().equals("file")) {
            return (new File(uri.getPath())).isFile();
        }
        try {
            uri.toURL().openStream().close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static boolean isDirectory(URI orig) {
        if (!orig.getScheme().equals("file")) {
            return false;
        }
        return (new File(orig.getPath())).isDirectory();
    }

    public static boolean canWrite(URI orig) {
        if (!orig.getScheme().equals("file")) {
            return false;
        }
        return (new File(orig.getPath())).canWrite();
    }

    public static boolean canRead(URI orig) {
        if (!orig.getScheme().equals("file")) {
            return false;
        }
        return (new File(orig.getPath())).canRead();
    }

    public static boolean move(URI oldUri, URI newUri) throws IOException {
        checkFileURI(oldUri);
        checkFileURI(newUri);
        File oldFile = new File(oldUri.getPath());
        File newFile = new File(newUri.getPath());
        return oldFile.renameTo(newFile);
    }

    public static boolean delete(URI orig) throws IOException {
        checkFileURI(orig);
        return (new File(orig.getPath())).delete();
    }

    public static File getAsFile(URI uri) throws IOException {
        if (uri.getScheme().equals("file")) {
            return new File(uri.getPath());
        }
        String hashString = (uri.toString().hashCode() + "").replace('-', 'm');
        File workingDir = getURICacheDir();
        File workingFile = null;
        if (workingDir != null && workingDir.isDirectory()) {
            workingFile = new File(workingDir, hashString);
        } else {
            workingFile = File.createTempFile(hashString, "tmp");
        }
        workingFile.deleteOnExit();
        OutputStream output = null;
        InputStream input = null;
        try {
            output = new FileOutputStream(workingFile);
            input = uri.toURL().openStream();
            byte[] data = new byte[512];
            int read;
            for (read = 0; read != -1; read = input.read(data)) {
                output.write(data, 0, read);
            }
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } finally {
                if (output != null) {
                    output.close();
                }
            }
        }
        return workingFile;
    }

    public static URI shortenURI(URI orig, String strip) throws MalformedURLException {
        String file = orig.getPath();
        char lastChar = file.charAt(file.length() - 1);
        if (isSeparator(lastChar)) {
            file = file.substring(0, file.length() - 1);
        }
        String test = file.substring(file.length() - strip.length());
        if (!test.equals(strip)) {
            throw new MalformedURLException(JSOtherMsg.lookupText("The URL {0} does not end in {1}.", new Object[]{orig, strip}));
        }
        String newFile = file.substring(0, file.length() - strip.length());
        try {
            return new URI(orig.getScheme(), orig.getUserInfo(), orig.getHost(), orig.getPort(), newFile, "", "");
        } catch (URISyntaxException e) {
            throw new MalformedURLException(JSOtherMsg.lookupText("The URL {0} does not end in {1}.", new Object[]{orig, strip}));
        }
    }

    public static URI lengthenURI(URI orig, String anExtra) {
        String extra = anExtra;
        try {
            StringBuilder path = new StringBuilder(orig.getPath());
            char lastChar = path.charAt(path.length() - 1);
            char firstChar = extra.charAt(0);
            if (isSeparator(firstChar)) {
                if (isSeparator(lastChar)) {
                    path.append(extra.substring(1));
                } else {
                    path.append(extra);
                }
            } else {
                if (!isSeparator(lastChar)) {
                    path.append("/");
                }
                path.append(extra);
            }
            return new URI(orig.getScheme(), orig.getUserInfo(), orig.getHost(), orig.getPort(), path.toString(), orig.getQuery(), orig.getFragment());
        } catch (URISyntaxException ex) {
            return null;
        }
    }

    private static boolean isSeparator(char c) {
        return (c == '/' || c == '\\');
    }

    public static InputStream getInputStream(URI uri) throws IOException {
        if (uri.getScheme().equals("file")) {
            return new FileInputStream(uri.getPath());
        }
        return uri.toURL().openStream();
    }

    public static OutputStream getOutputStream(URI uri) throws IOException {
        return getOutputStream(uri, false);
    }

    public static OutputStream getOutputStream(URI uri, boolean append) throws IOException {
        if (uri.getScheme().equals("file")) {
            return new FileOutputStream(uri.getPath(), append);
        }
        URLConnection cnx = uri.toURL().openConnection();
        cnx.setDoOutput(true);
        return cnx.getOutputStream();
    }

    public static String[] list(URI uri, URIFilter filter) throws MalformedURLException, IOException {
        String[] reply = new String[0];
        try {
            URI search = lengthenURI(uri, "index.txt");
            reply = listByIndexFile(search, filter);
        } catch (FileNotFoundException ex) {
            LOGGER.warn("index file for " + uri.toString() + " was not found.");
            if (uri.getScheme().equals("file")) {
                return listByFile(uri, filter);
            }
        }
        if (uri.getScheme().equals("file")) {
            String[] files = listByFile(uri, filter);
            if (files.length != reply.length) {
                LOGGER.warn("index file for {} has incorrect number of entries.", uri.toString());
            } else {
                List<String> list = Arrays.asList(files);
                for (int i = 0; i < files.length; i++) {
                    if (!list.contains(files[i])) {
                        LOGGER.warn("file: based index found {} but this was not found using index file.", files[i]);
                    }
                }
            }
        }
        return reply;
    }

    public static String[] listByFile(URI uri, URIFilter filter) throws MalformedURLException {
        File fdir = new File(uri.getPath());
        if (!fdir.isDirectory()) {
            throw new MalformedURLException(JSMsg.gettext("The URL {0} is not a directory", new Object[]{uri.toString()}));
        }
        return fdir.list(new URIFilterFilenameFilter(filter));
    }

    public static String[] listByIndexFile(URI index) throws IOException {
        return listByIndexFile(index, new DefaultURIFilter());
    }

    public static String[] listByIndexFile(URI index, URIFilter filter) throws IOException {
        InputStream in = null;
        BufferedReader din = null;
        try {
            in = getInputStream(index);
            din = new BufferedReader(new InputStreamReader(in), 8192);
            List<String> list = new ArrayList<String>();
            while (true) {
                String line = din.readLine();
                if (line == null) {
                    break;
                }
                String name = line;
                int len = name.length();
                int commentPos;
                for (commentPos = 0; commentPos < len && name.charAt(commentPos) != '#'; commentPos++);
                if (commentPos < len) {
                    name = name.substring(0, commentPos);
                }
                name = name.trim();
                if (name.length() > 0 && !name.equals("index.txt") && filter.accept(name)) {
                    list.add(name);
                }
            }
            return list.<String>toArray(new String[list.size()]);
        } finally {
            IOUtil.close(din);
            IOUtil.close(in);
        }
    }

    public static PropertyMap loadProperties(URI uri) throws IOException {
        InputStream is = null;
        try {
            is = getInputStream(uri);
            PropertyMap prop = new PropertyMap();
            prop.load(is);
            is.close();
            return prop;
        } finally {
            IOUtil.close(is);
        }
    }

    public static void storeProperties(PropertyMap properties, URI uri, String title) throws IOException {
        OutputStream out = null;
        try {
            out = getOutputStream(uri);
            PropertyMap temp = new PropertyMap();
            temp.putAll(properties);
            temp.store(out, title);
        } finally {
            IOUtil.close(out);
        }
    }

    public static int getSize(URI uri) {
        return getSize(uri, null, null);
    }

    public static int getSize(URI uri, String proxyHost) {
        return getSize(uri, proxyHost, null);
    }

    public static int getSize(URI uri, String proxyHost, Integer proxyPort) {
        try {
            if (uri.getScheme().equals("http")) {
                WebResource resource = new WebResource(uri, proxyHost, proxyPort);
                int size = resource.getSize();
                resource.shutdown();
                return size;
            }
            return uri.toURL().openConnection().getContentLength();
        } catch (IOException e) {
            return 0;
        }
    }

    public static long getLastModified(URI uri) {
        return getLastModified(uri, null, null);
    }

    public static long getLastModified(URI uri, String proxyHost) {
        return getLastModified(uri, proxyHost, null);
    }

    public static long getLastModified(URI uri, String proxyHost, Integer proxyPort) {
        try {
            if (uri.getScheme().equals("http")) {
                WebResource resource = new WebResource(uri, proxyHost, proxyPort);
                long l = resource.getLastModified();
                resource.shutdown();
                return l;
            }
            URLConnection urlConnection = uri.toURL().openConnection();
            long time = urlConnection.getLastModified();
            if (urlConnection instanceof JarURLConnection) {
                JarURLConnection jarConnection = (JarURLConnection) urlConnection;
                JarEntry jarEntry = jarConnection.getJarEntry();
                time = jarEntry.getTime();
            }
            return time;
        } catch (IOException ex) {
            LOGGER.warn("Failed to get modified time", ex);
            return (new Date()).getTime();
        }
    }

    public static boolean isNewer(URI left, URI right) {
        return isNewer(left, right, null, null);
    }

    public static boolean isNewer(URI left, URI right, String proxyHost) {
        return isNewer(left, right, proxyHost, null);
    }

    public static boolean isNewer(URI left, URI right, String proxyHost, Integer proxyPort) {
        return (getLastModified(left, proxyHost, proxyPort) > getLastModified(right, proxyHost, proxyPort));
    }

    public static class URIFilterFilenameFilter implements FilenameFilter {

        private URIFilter filter;

        public URIFilterFilenameFilter(URIFilter filter) {
            this.filter = filter;
        }

        public boolean accept(File arg0, String name) {
            return this.filter.accept(name);
        }
    }

    private static void checkFileURI(URI uri) throws MalformedURLException {
        if (!uri.getScheme().equals("file")) {
            throw new MalformedURLException(JSMsg.gettext("The URL {0} is not a file.", new Object[]{uri}));
        }
    }

    public static File getURICacheDir() {
        return cachedir;
    }

    public static void setURICacheDir(File cachedir) {
        NetUtil.cachedir = cachedir;
    }

    public static URI getURI(File file) {
        return file.toURI();
    }

    public static URI getTemporaryURI(String prefix, String suffix) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix);
        return getURI(tempFile);
    }

    public static URI toURI(URL url) {
        try {
            return new URI(url.toExternalForm());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static URL toURL(URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static class IsDirectoryURIFilter implements URIFilter {

        private URI parent;

        public IsDirectoryURIFilter(URI parent) {
            this.parent = parent;
        }

        public boolean accept(String name) {
            return NetUtil.isDirectory(NetUtil.lengthenURI(this.parent, name));
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NetUtil.class);
}
