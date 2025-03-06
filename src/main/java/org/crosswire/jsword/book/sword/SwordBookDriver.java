package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.basic.AbstractBookDriver;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.index.IndexStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwordBookDriver extends AbstractBookDriver 
{
    private static final BookDriver INSTANCE = (BookDriver) new SwordBookDriver();
    private static final Logger lgr = LoggerFactory.getLogger(SwordBookDriver.class);

    public static BookDriver instance() { return INSTANCE; }

    public String getDriverName()       { return "Sword"; }

    public Book[] getBooks()
    {
        int j = 0;

        File[] dirs = SwordBookPath.getSwordPath();
        Set<Book> valid = new HashSet<Book>(dirs.length + dirs.length / 3);
        for ( j = 0; j < dirs.length; j++ )
        {
            getBooks(valid, dirs[j]);
        }
        return valid.<Book>toArray(new Book[valid.size()]);
    }

    private void getBooks(Set<Book> valid, File bookDir)
    {
        int i = 0;
        
        String[] bookConfs;
        URI bookDirURI;

        File mods = new File(bookDir, "mods.d");
        if ( !mods.isDirectory() )
        {
            lgr.debug("mods.d directory at {} does not exist", mods);
            return;
        }
        bookConfs = SwordBookPath.getBookList(mods);
        bookDirURI = NetUtil.getURI(bookDir);
        for ( i = 0; i < bookConfs.length; i++ )
        {
            String bookConf = bookConfs[i];
            try
            {
                SwordBookMetaData sbmd = null;
                File configfile = new File(mods, bookConf);
                if ( configfile.exists() ) { sbmd = new SwordBookMetaData(configfile, bookDirURI); }
                if ( sbmd == null ) 
                {
                    lgr.error("The book's configuration files is not supported.",SwordBookDriver.class);
                }
                else if ( !sbmd.isSupported() )
                {
                    String msg = "The book's configuration files is not supported. -> Initials [" 
                               + sbmd.getInitials()
                               + "], Driver=[" + sbmd.getDriver().getDriverName() 
                               + "], Versification=[" + sbmd.getBookType().name()
                               + "], Work type=[" + sbmd.getBookType().name()
                               + "], Work category=[" + sbmd.getProperty("category")
                               + "]";
                    lgr.error(msg,SwordBookDriver.class);
                }
                else
                {
                    sbmd.setDriver((BookDriver) this);
                    Book book = createBook(sbmd);
                    if (!valid.contains(book))
                    {
                        valid.add(book);
                        IndexManager imanager = IndexManagerFactory.getIndexManager();
                        if (imanager.isIndexed(book))
                        {
                            sbmd.setIndexStatus(IndexStatus.DONE);
                        }
                        else
                        {
                            sbmd.setIndexStatus(IndexStatus.UNDONE);
                        }
                    }
                }
            }
            catch ( IOException | BookException e)
            {
                String msg = "Couldn't create SwordBookMetaData - " + e.getMessage();
                lgr.warn("Couldn't create SwordBookMetaData", SwordBookDriver.class);
            }
        }
    }

    public boolean isDeletable(Book dead)
    {
        SwordBookMetaData sbmd = (SwordBookMetaData) dead.getBookMetaData();
        File confFile = sbmd.getConfigFile();
        return (confFile != null && confFile.exists());
    }

    public void delete(Book dead) throws BookException
    {
        SwordBookMetaData sbmd = (SwordBookMetaData) dead.getBookMetaData();
        File confFile = sbmd.getConfigFile();
        if ( confFile == null || !confFile.exists() )
        {
            String msg = "Unable to delete: " + confFile.getAbsolutePath();
            lgr.error(msg,SwordBookDriver.class);
            throw new BookException(JSMsg.gettext("Unable to delete: {0}", new Object[]
            {
                confFile
            }));
        }
        List<File> failures = FileUtil.delete(confFile);
        if (failures.isEmpty())
        {
            URI loc = sbmd.getLocation();
            if (loc != null)
            {
                File bookDir = new File(loc.getPath());
                failures = FileUtil.delete(bookDir);
                Books.installed().removeBook(dead);
            }
        }
        if ( !failures.isEmpty() )
        {
            String msg = "Unable to delete: " + failures.get(0);
            lgr.error(msg, SwordBookDriver.class);
            throw new BookException(JSMsg.gettext("Unable to delete: {0}", new Object[]
            {
                failures.get(0)
            }));
        }
    }

    public static void registerNewBook(SwordBookMetaData sbmd) throws BookException
    {
        int i = 0;
        
        BookDriver[] drivers = Books.installed().getDriversByClass(SwordBookDriver.class);

        try
        {
            for ( i = 0; i < drivers.length; i++ )
            {
                SwordBookDriver sdriver = (SwordBookDriver) drivers[i];
                Book book = sdriver.createBook(sbmd);
                Books.installed().addBook(book);
            }
        }
        catch ( BookException e )
        {
            String msg = "registerNewBook(): error registering new work - " + drivers[i].getDriverName();
            lgr.error(msg,SwordBookDriver.class);
            throw e;
        }
    }

    private Book createBook(SwordBookMetaData sbmd) throws BookException
    {
        BookType modtype = sbmd.getBookType();
        if ( (modtype == null) || (modtype.getBookCategory() == null) )
        {
            String msg = "Unsupported type: " + modtype.toString()
                       + "when reading " + sbmd.getName();
            lgr.error(msg,SwordBookDriver.class);
            throw new BookException(JSOtherMsg.lookupText("Unsupported type: {0} when reading {1}", new Object[0]));
        }
        return modtype.createBook(sbmd);
    }
}