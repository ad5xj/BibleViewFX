package org.crosswire.jsword.book.install.sword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.CollectionUtil;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.StringUtil;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;

import org.crosswire.jsword.book.AbstractBookList;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilterIterator;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookSet;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.sword.Backend;
import org.crosswire.jsword.book.sword.NullBackend;
import org.crosswire.jsword.book.sword.SwordBook;
import org.crosswire.jsword.book.sword.SwordBookDriver;
import org.crosswire.jsword.book.sword.SwordBookMetaData;
import org.crosswire.jsword.book.sword.SwordBookPath;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSwordInstaller extends AbstractBookList implements Installer, Comparable<AbstractSwordInstaller> 
{
    protected boolean loaded;

    protected Integer proxyPort;

    protected String host;
    protected String proxyHost;
    protected String packageDirectory = "";
    protected String catalogDirectory = "";
    protected String indexDirectory = "";

    protected Map<String, Book> entries = new HashMap<String, Book>();

    protected static final String FILE_LIST_GZ = "mods.d.tar.gz";
    protected static final String CONF_DIR = "mods.d";
    protected static final String ZIP_SUFFIX = ".zip";
    protected static final String SEARCH_DIR = "search/jsword/L1";
    protected static final String DOWNLOAD_PREFIX = "download-";
    
    protected abstract void download(Progress paramProgress, String paramString1, String paramString2, URI paramURI) throws InstallException;

    private static final String THISMODULE = "org.crosswire.jsword.book.install.sword.AbstractSwordInstaller";
    private static final Logger lgr = LoggerFactory.getLogger(AbstractSwordInstaller.class);

    private boolean debug_log = false; 

    public String getInstallerDefinition() 
    {
        String buf = host + "," 
                   + packageDirectory + "," 
                   + catalogDirectory + "," 
                   + indexDirectory + ",";
        if (proxyHost != null) { buf += proxyHost; }
        buf += ",";
        if ( proxyPort != null ) { buf += proxyPort; }
        return buf;
    }

    public boolean isNewer(Book book) 
    {
        URI configURI = null;
        URI remote = null;
        SwordBookMetaData sbmd = (SwordBookMetaData) book.getBookMetaData();
        File conf = sbmd.getConfigFile();

        if ( (conf == null) || !conf.exists() ) {  return false; }
        try
        {
            configURI = NetUtil.getURI(conf);
            remote = toRemoteURI(book);
        }
        catch ( Exception e )
        {
            String msg = "Error getting URI - " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        return NetUtil.isNewer(remote, configURI, proxyHost, proxyPort);
    }

    public List<Book> getBooks() 
    {
        try {
            if ( !loaded) { loadCachedIndex(); }
            return new ArrayList<Book>(this.entries.values());
        } 
        catch (InstallException | IOException | BookException ex) 
        {
            String msg = "Failed to reload cached index file - " + ex.getMessage();
            lgr.error(msg, THISMODULE);
            return new ArrayList<>();
        }
    }

    public List<Book> getBooks(BookFilter filter) 
    {
        List<Book> books = null;
        synchronized(this) { books = getBooks(); }
        List<Book> temp = CollectionUtil.createList((Iterable) new BookFilterIterator(books, filter));
        return (List<Book>) new BookSet(temp);
    }

    public Book getBook(String name) 
    {
        List<Book> books = null;
        synchronized (this) { books = getBooks(); }

        for (Book book : books) 
        {
            if (name.equals(book.getName())) { return book; }
        }

        for (Book book : books) 
        {
            if (name.equalsIgnoreCase(book.getName())) { return book; }
        }

        for (Book book : books) 
        {
            BookMetaData bmd = book.getBookMetaData();
            if (name.equals(bmd.getInitials())) { return book; }
        }

        for (Book book : books) 
        {
            BookMetaData bmd = book.getBookMetaData();
            if (name.equalsIgnoreCase(bmd.getInitials())) { return book; }
        }
        return null;
    }

    public void install(Book book) 
    {
        if ( book == null ) { return; }
        
        String jobName;
        SwordBookMetaData sbmd;
        Progress job;
        URI temp;
        File dldir;
        File jswordHome;
        
        temp = null;
        dldir = null;
        job = null;
        jobName = "";
        sbmd = (SwordBookMetaData)book.getBookMetaData();
        try
        {
            String msg = "Installing book: " + sbmd.getName();
            jobName = JSMsg.gettext(msg);
            job = JobManager.createJob(String.format("INSTALL_BOOK-%s", new Object[]{book.getInitials()}), jobName, Thread.currentThread());
            job.beginJob(jobName);
            Thread.yield();
            if ( debug_log ) 
            {
                lgr.info("Initializing...");
                job.setSectionName(JSMsg.gettext("Initializing", new Object[0])); 
            }
            temp = NetUtil.getTemporaryURI("swd", ".zip");
            download(job, packageDirectory, sbmd.getInitials() + ".zip", temp);
            job.setCancelable(false);

            if ( !job.isFinished() ) 
            {
                dldir = SwordBookPath.getSwordDownloadDir();
                IOUtil.unpackZip(NetUtil.getAsFile(temp), dldir, true, new String[]{"mods.d", "modules"});
                jswordHome = NetUtil.getAsFile(CWProject.instance().getWritableProjectDir());
                IOUtil.unpackZip(NetUtil.getAsFile(temp), jswordHome, false, new String[] {"mods.d", "modules"} );
                if ( debug_log )
                {
                    msg = "Copying config file " + dldir + temp;
                    lgr.info(msg, THISMODULE);
                    job.setSectionName(JSMsg.gettext("Copying config file", new Object[0])); 
                }
                sbmd.setLibrary(NetUtil.getURI(dldir));
                SwordBookDriver.registerNewBook(sbmd);
            }
        } 
        catch ( Exception e) 
        {
            String msg = "Exception downloading file for Sword Meta Data " + sbmd.getName()
                       + "\n    error=" + e.getMessage() + "\n    Job canceled";
            lgr.error(msg, THISMODULE);
            Reporter.informUser(this, (LucidException) e);
            job.cancel();
        }
        finally 
        {
            job.done();
            if (temp != null)
            {
                try 
                {
                    NetUtil.delete(temp);
                } 
                catch (IOException e)
                {
                    String msg = "I/O Exception Error deleting temp download file. - " + e.getMessage();
                    lgr.error(msg,THISMODULE);
                }
            }
        }
    }

    public void reloadBookList() throws InstallException 
    {
        // declare local vars
        String jobName;
        String confDirPath;
        String confDirPathOld;
        String msg;
        Progress job;
        List<File> errors;
        URI cacheDir;
        URI confDir;
        URI cache;
        File dirConf;
        File dirConfOld;
        // init local vars for use
        jobName = JSMsg.gettext("Downloading files", new Object[0]);
        confDirPath = "";
        confDirPathOld = "";
        msg = "";
        job = JobManager.createJob("RELOAD_BOOK_LIST", jobName, Thread.currentThread());
        job.beginJob(jobName);
        errors = null;
        cacheDir = null;
        confDir  = null;
        cache    = null;
        dirConf = null;
        dirConfOld = null;
        
        try 
        {
            cacheDir = getCachedIndexDir();
            confDir = NetUtil.lengthenURI(cacheDir, "mods.d.zip");
            cache = getCachedIndexFile();
            download(job, this.catalogDirectory, "mods.d.tar.gz", cache);
            job.setCancelable(false);
            
            if (NetUtil.isFile(confDir)) 
            {
                confDirPath = confDir.getPath();
                confDirPathOld = confDirPath + ".old";
                dirConf = new File(confDirPath);
                dirConfOld = new File(confDirPathOld);
                if (dirConfOld.exists()) { FileUtil.delete(dirConfOld); }
                if (!dirConf.renameTo(dirConfOld)) { errors = FileUtil.delete(new File(confDirPath)); }
                if ( (errors != null) && !errors.isEmpty()) 
                {
                    msg = "Unable to delete: " + cacheDir + "\n    error=" + errors.get(0);
                    lgr.error(msg, THISMODULE);
                    throw new InstallException(JSMsg.gettext("Unable to delete: {0}", new Object[]{errors.get(0)}));
                }
                try
                {
                    unpack(cacheDir, cache);
                }
                catch ( IOException e )
                {
                    msg = "I/O Error on reloadBookList(): Error - " + e.getMessage();
                    lgr.error(msg,THISMODULE);
                }
                FileUtil.delete(dirConfOld);
            }
            loaded = false;
        } 
        catch (InstallException ex) 
        {
            msg = "Error on reloadBookList(): Error - " + ex.getMessage();
            lgr.error(msg,THISMODULE);
            job.cancel();
            throw ex;
        }
        finally 
        {
            job.done();
        }
    }

    public void downloadSearchIndex(Book book, URI localDest) throws InstallException 
    {
        String jobName = JSMsg.gettext("Downloading files", new Object[0]);
        Progress job = JobManager.createJob(String.format("DOWNLOAD_SEARCH_INDEX-%s", new Object[]{book.getInitials()}), jobName, Thread.currentThread());
        job.beginJob(jobName);

        try 
        {
            download(job, packageDirectory + '/' + "search/jsword/L1", book.getInitials() + ".zip", localDest);
        } 
        catch (InstallException ex) 
        {
            job.cancel();
            String msg = "Error on reloadBookList(): Error - " + ex.getMessage()
                       + "\n    book=" + book.getInitials() + ".zip";
            lgr.error(msg,THISMODULE);
            throw ex;
        } 
        finally 
        {
            job.done();
        }
    }

    protected void unpack(URI cacheDir, URI cache) throws IOException 
    {
        int size = 0;
        int n = 0;

        TarArchiveInputStream tarArchiveInputStream = null;
        ZipArchiveOutputStream zipArchiveOutputStream = null;
        InputStream fin = null;
        GzipCompressorInputStream gin = null;
        ArchiveInputStream tin = null;
        ArchiveOutputStream zout = null;
        
        try 
        {
            zipArchiveOutputStream = new ZipArchiveOutputStream(new File(cacheDir.getPath(), "mods.d.zip"));
            fin = NetUtil.getInputStream(cache);
            gin = new GzipCompressorInputStream(fin);
            tarArchiveInputStream = new TarArchiveInputStream((InputStream) gin);
            while (true) 
            {
                ArchiveEntry entry = tarArchiveInputStream.getNextEntry();
                if (entry == null)   { break; }
                if (entry.isDirectory()) { continue; }
                
                String path = entry.getName();
                
                if (!path.endsWith(".conf")) 
                {
                   String msg = "Not a SWORD config file: " + path;
                    lgr.error(msg,THISMODULE);
                    continue;
                }
                
                size = (int) entry.getSize();
                if ( size == 0 ) 
                {
                    String msg = "Empty entry: " + path;
                    lgr.error(msg, THISMODULE);
                    continue;
                }
                ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(path);
                zipArchiveOutputStream.putArchiveEntry((ArchiveEntry) zipArchiveEntry);
                byte[] buffer = new byte[size];
                n = tarArchiveInputStream.read(buffer);
                if ( n != size ) 
                {
                    String msg = "Error: Could not read " + Integer.toString(size)
                               + " bytes for " + path 
                               + "from " + cache.getPath();
                    lgr.error(msg, THISMODULE);
                }
                zipArchiveOutputStream.write(buffer, 0, n);
                zipArchiveOutputStream.closeArchiveEntry();
            }
        } 
        finally 
        {
            IOUtil.close((Closeable) tarArchiveInputStream);
            IOUtil.close((Closeable) gin);
            IOUtil.close(fin);
            IOUtil.close((Closeable) zipArchiveOutputStream);
        }
    }

    public void close() 
    {
        this.entries.clear();
        this.loaded = false;
    }

    public String getCatalogDirectory() { return catalogDirectory; }

    public void setCatalogDirectory(String catologDirectory) { catalogDirectory = catologDirectory; }

    public String getPackageDirectory() { return packageDirectory; }

    public void setPackageDirectory(String newDirectory) 
    {
        if ( (packageDirectory == null) || !packageDirectory.equals(newDirectory) ) 
        {
            packageDirectory = newDirectory;
            loaded = false;
        }
    }

    public String getIndexDirectory() { return indexDirectory; }

    public void setIndexDirectory(String indexDirectory) { indexDirectory = indexDirectory; }

    public String getHost() { return this.host; }

    public void setHost(String newHost) 
    {
        if ( (host == null ) || !this.host.equals(newHost) ) 
        {
            this.host = newHost;
            this.loaded = false;
        }
    }

    public String getProxyHost() { return this.proxyHost; }

    public void setProxyHost(String newProxyHost) 
    {
        String pHost = null;

        if ( (newProxyHost != null) && (newProxyHost.length() > 0) ) { pHost = newProxyHost; }
        if ( (proxyHost == null) || !this.proxyHost.equals(pHost) ) 
        {
            proxyHost = pHost;
            loaded = false;
        }
    }

    public Integer getProxyPort() { return this.proxyPort; }

    public void setProxyPort(Integer newProxyPort) 
    {
        if ( (proxyPort == null) || !this.proxyPort.equals(newProxyPort) ) 
        {
            proxyPort = newProxyPort;
            loaded = false;
        }
    }

    public boolean equals(Object object) 
    {
        if ( !(object instanceof AbstractSwordInstaller) ) { return false; }

        AbstractSwordInstaller that = (AbstractSwordInstaller) object;

        if ( !StringUtil.equals(host, that.host) ) { return false; }
        if ( !StringUtil.equals(this.packageDirectory, that.packageDirectory)) {
            return false;
        }
        return true;
    }

    public int compareTo(AbstractSwordInstaller myClass) 
    {
        int ret = this.host.compareTo(myClass.host);
        if ( ret != 0 ) { ret = this.packageDirectory.compareTo(myClass.packageDirectory); }
        return ret;
    }

    public int hashCode() { return this.host.hashCode() + this.packageDirectory.hashCode(); }

    protected URI getCachedIndexDir() throws InstallException 
    {
        try 
        {
            return CWProject.instance().getWritableProjectSubdir(getTempFileExtension(this.host, this.catalogDirectory), true);
        } 
        catch (IOException ex) 
        {
            throw new InstallException(JSOtherMsg.lookupText("URL manipulation failed", new Object[0]), ex);
        }
    }

    protected URI getCachedIndexFile() throws InstallException { return NetUtil.lengthenURI(getCachedIndexDir(), "mods.d.tar.gz"); }

    protected void loadCachedIndex() throws IOException, InstallException, BookException 
    {
        int size = 0;
        int offset = 0;
        byte[] buffer;
        String path = "";
        BookDriver fake = SwordBookDriver.instance();
        NullBackend nullBackend = new NullBackend();
        SwordBookMetaData sbmd = null;
        SwordBook swordBook = null;
        entries.clear();
        URI cacheDir = getCachedIndexDir();
        URI confDir = NetUtil.lengthenURI(cacheDir, "mods.d.zip");
        URI cache = getCachedIndexFile();
        
        if (!NetUtil.isFile(cache))   { reloadBookList(); }
        if (!NetUtil.isFile(confDir)) { unpack(cacheDir, cache); }

        InputStream fin = null;

        ZipArchiveInputStream zin = null;

        try 
        {
            fin = NetUtil.getInputStream(confDir);
            zin = new ZipArchiveInputStream(fin);
            while (true) 
            {
                ZipArchiveEntry zipArchiveEntry = zin.getNextZipEntry();
                if (zipArchiveEntry == null)       { break; }
                if (zipArchiveEntry.isDirectory()) { continue; }

                path = zipArchiveEntry.getName();

                if ( !path.endsWith(".conf") ) 
                {
                    String msg = "Not a SWORD config file: " + path;
                    lgr.error(msg,THISMODULE);
                    continue;
                }

                size = (int) zipArchiveEntry.getSize();

                if ( size == 0 ) 
                {
                    String msg = "Empty entry: " + path;
                    lgr.error(msg, THISMODULE);
                    continue;
                }

                buffer = new byte[size];
                
                while (offset < size) 
                {
                    offset += zin.read(buffer, offset, size - offset);
                }
                
                if ( offset != size ) 
                {
                    String msg = "Error: Could not read " + Integer.toString(size)
                               + " bytes, instead got " + Integer.toString(offset)
                               + ", for " + path
                               + " from " + cache.getPath();
                    if ( debug_log ) { lgr.info(msg); }
                }
                
                sbmd = new SwordBookMetaData(buffer, confDir.getPath() + '!' + path);
                sbmd.setDriver(fake);
                
                if ( !sbmd.isSupported()) { continue; }

                swordBook = new SwordBook(sbmd, (Backend) nullBackend);
                entries.put(swordBook.getInitials() + swordBook.getName(), swordBook);
            }
        } 
        finally 
        {
            IOUtil.close(fin);
            IOUtil.close((Closeable) zin);
        }
        this.loaded = true;
    }

    private static String getTempFileExtension(String host, String catalogDir) { return "download-" + host + catalogDir.replace('/', '_'); }
}