package org.crosswire.jsword.index.lucene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;

import org.crosswire.jsword.JSMsg;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;

import org.crosswire.jsword.index.Index;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexPolicy;
import org.crosswire.jsword.index.IndexPolicyAdapter;
import org.crosswire.jsword.index.IndexStatus;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import java.util.HashMap;
import java.util.Map;

public class LuceneIndexManager implements IndexManager {

    public static final String DIR_LUCENE = "lucene";

    private static final Logger lgr = LoggerFactory.getLogger(LuceneIndexManager.class);
    private static final String THISMODULE = "LuceneIndexManager";

    protected static final Map<Book, Index> INDEXES = new HashMap<Book, Index>();

    private IndexPolicy policy = (IndexPolicy) new IndexPolicyAdapter();
    private URI baseFolderURI;

    public LuceneIndexManager()
    {
        try
        {
            this.baseFolderURI = CWProject.instance().getWritableProjectSubdir("lucene", false);
        }
        catch (IOException ex)
        {
            lgr.error("LuceneIndexManager(): Failed to find lucene index storage area.", ex);
        }
    }

    public boolean isIndexed(Book book)
    {
        try
        {
            if (book == null)  {  return false; }
            URI storage = getStorageArea(book);
            return NetUtil.isDirectory(storage);
        }
        catch (IOException ex)
        {
            String msg = "isIndexed(): Failed to find lucene index storage area. err="+ex.getMessage();
            lgr.error(msg, THISMODULE);
            return false;
        }
    }

    public Index getIndex(Book book) throws BookException
    {
        try
        {
            LuceneIndex luceneIndex = null;
            Index reply = INDEXES.get(book);

            if (reply == null)
            {
                URI storage = getStorageArea(book);
                luceneIndex = new LuceneIndex(book, storage);
                INDEXES.put(book, luceneIndex);
            }
            return (Index) luceneIndex;
        }
        catch (IOException ex)
        {
            String msg = "getIndex(): Failed to initialize Lucene search engine. err="+ex.getMessage();
            lgr.error(msg, THISMODULE);
            throw new BookException(JSMsg.gettext("Failed to initialize Lucene search engine.", new Object[0]), ex);
        }
    }

    public boolean needsReindexing(Book book)
    {
        float installedV = InstalledIndex.instance().getInstalledIndexVersion(book);
        if (installedV < IndexMetadata.instance().getLatestIndexVersion(book))
        {
            //lgr.info("{}: needs reindexing, Installed index version @{}", book.getBookMetaData().getInitials(), Float.toString(installedV));
            return true;
        }
        return false;
    }

    public void closeAllIndexes()
    {
        for (Index index : INDEXES.values())
        {
            index.close();
        }
    }

    public void scheduleIndexCreation(Book book)
    {
        book.setIndexStatus(IndexStatus.SCHEDULED);
        IndexStatus finalStatus = IndexStatus.UNDONE;
        try
        {
            URI storage = getStorageArea(book);
            LuceneIndex luceneIndex = new LuceneIndex(book, storage, this.policy);
            if (NetUtil.getAsFile(storage).exists())
            {
                finalStatus = IndexStatus.DONE;
                INDEXES.put(book, luceneIndex);
                InstalledIndex.instance().storeLatestVersionAsInstalledIndexMetadata(book);
            }
        }
        catch (IOException e)
        {
            String msg1 = "scheduleIndexCreation(): I/O exception. err="+e.getMessage();
            lgr.error(msg1, THISMODULE);
            Reporter.informUser(this, e);
        }
        catch (BookException e)
        {
            String msg2 = "scheduleIndexCreation(): BookException. err="+e.getMessage();
            lgr.error(msg2, THISMODULE);
            Reporter.informUser(this, (LucidException) e);
        }
        finally
        {
            book.setIndexStatus(finalStatus);
        }
    }

    public void installDownloadedIndex(Book book, URI tempDest) throws BookException
    {
        try
        {
            URI storage = getStorageArea(book);
            File zip = NetUtil.getAsFile(tempDest);
            IOUtil.unpackZip(zip, NetUtil.getAsFile(storage));
        }
        catch (IOException ex)
        {
            String msg = "installDownloadedIndex(): Installation failed...err="+ex.getMessage();
            lgr.error(msg, THISMODULE);
            throw new BookException(JSMsg.gettext("Installation failed.", new Object[0]), ex);
        }
    }

    public void deleteIndex(Book book) throws BookException
    {
        File tempPath = null;
        try
        {
            Index index = INDEXES.get(book);
            if (index != null)
            {
                index.close();
            }
            File storage = NetUtil.getAsFile(getStorageArea(book));
            String finalCanonicalPath = storage.getCanonicalPath();
            tempPath = new File(finalCanonicalPath + '.' + IndexStatus.CREATING.toString());
            if (tempPath.exists())
            {
                FileUtil.delete(tempPath);
            }
            tempPath = new File(finalCanonicalPath + '.' + IndexStatus.CREATING.toString());
            if (!storage.renameTo(tempPath))
            {
                String msg = "deleteIndex(): Failed to rename index path.";
                lgr.error(msg, THISMODULE);
                throw new BookException(JSMsg.gettext("Failed to delete search index.", new Object[0]));
            }
            book.setIndexStatus(IndexStatus.UNDONE);
            InstalledIndex.instance().removeFromInstalledIndexMetadata(book);
        }
        catch (IOException ex)
        {
            String msg = "deleteIndex(): Failed to delete search index. err="+ex.getMessage();
            lgr.error(msg, THISMODULE);
            throw new BookException(JSMsg.gettext("Failed to delete search index.", new Object[0]), ex);
        }
        FileUtil.delete(tempPath);
    }

    public IndexPolicy getIndexPolicy()   { return this.policy; }

    public void setIndexPolicy(IndexPolicy policy)
    {
        if (policy != null)
        {
            this.policy = policy;
        }
        else
        {
            this.policy = (IndexPolicy) new IndexPolicyAdapter();
        }
    }

    protected URI getStorageArea(Book book) throws IOException
    {
        BookMetaData bmd = book.getBookMetaData();
        String driverName = bmd.getDriverName();
        String bookName = bmd.getInitials();
        assert driverName != null;
        assert bookName != null;
        return NetUtil.lengthenURI(this.baseFolderURI, driverName + "/" + bookName);
    }
}