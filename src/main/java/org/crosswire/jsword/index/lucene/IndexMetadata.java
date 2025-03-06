package org.crosswire.jsword.index.lucene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.ResourceUtil;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;

import org.crosswire.jsword.index.IndexManagerFactory;

import java.io.IOException;

public final class IndexMetadata {
    private static final Logger lgr = LoggerFactory.getLogger(IndexMetadata.class);
    private static IndexMetadata myInstance = new IndexMetadata();

    public static final float INDEX_VERSION_1_2 = 1.2F;
    public static final String LATEST_INDEX_VERSION = "Latest.Index.Version";
    public static final String LUCENE_VERSION = "Lucene.Version";
    public static final String PREFIX_LATEST_INDEX_VERSION_BOOK_OVERRIDE = "Latest.Index.Version.Book.";
    @Deprecated
    public static final String INDEX_VERSION = "Installed.Index.Version";

    private PropertyMap props;

    public static IndexMetadata instance()
    {
        return myInstance;
    }

    @Deprecated
    public float getInstalledIndexVersion()
    {
        String value = this.props.get("Installed.Index.Version", "1.1");
        return Float.parseFloat(value);
    }

    public float getLatestIndexVersion()
    {
        String value = this.props.get("Latest.Index.Version", "1.2");
        return Float.parseFloat(value);
    }

    public float getLatestIndexVersion(Book b)
    {
        if (b == null)
        {
            return getLatestIndexVersion();
        }
        String value = this.props.get("Latest.Index.Version.Book." + getBookIdentifierPropSuffix(b.getBookMetaData()), this.props.get("Latest.Index.Version"));
        return Float.parseFloat(value);
    }

    public String getLatestIndexVersionStr()
    {
        String value = this.props.get("Latest.Index.Version", "1.2");
        return value;
    }

    public static String getBookIdentifierPropSuffix(BookMetaData meta)
    {
        String moduleVer = null;
        if (meta.getProperty("Version") != null)
        {
            moduleVer = '[' + meta.getProperty("Version") + ']';
        }
        return meta.getInitials() + moduleVer;
    }

    public float getLuceneVersion()
    {
        return Float.parseFloat(this.props.get("Lucene.Version"));
    }

    private IndexMetadata()
    {
        try
        {
            this.props = ResourceUtil.getProperties(getClass());
        }
        catch (IOException e)
        {
            lgr.error("Property file read error", e);
        }
    }

    public static String generateInstalledBooksIndexVersionReport(BookFilter filter)
    {
        StringBuilder toReturn = new StringBuilder();
        int installedBookCount = 0;
        int searchEnabledBookCount = 0;
        int reindexMandatoryBookCount = 0;
        LuceneIndexManager indexManager = (LuceneIndexManager) IndexManagerFactory.getIndexManager();
        Books myBooks = Books.installed();
        toReturn.append("InstalledBooks:");
        for (Book insBook : myBooks.getBooks(filter))
        {
            installedBookCount++;
            toReturn.append("\n\t").append(insBook.getBookMetaData().getInitials()).append(": ");
            if (indexManager.isIndexed(insBook))
            {
                searchEnabledBookCount++;
                toReturn.append("search enabled, ");
                if (indexManager.needsReindexing(insBook))
                {
                    reindexMandatoryBookCount++;
                    toReturn.append("index outdated, ");
                }
            }
        }
        toReturn.append("\nSummary: installedBooks ").append(installedBookCount).append(", searchEnabledBooks ").append(searchEnabledBookCount).append(", booksWithOutdatedIndex ").append(reindexMandatoryBookCount).append("\n");
        return toReturn.toString();
    }
}