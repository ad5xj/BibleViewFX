package org.crosswire.jsword.bridge;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;

import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

import java.util.List;
import java.util.Map;

public class BookInstaller
{

    private InstallManager installManager = new InstallManager();

    public void deleteBook(Book book) throws BookException
    {
        book.getDriver().delete(book);
    }

    public Map<String, Installer> getInstallers()
    {
        return this.installManager.getInstallers();
    }

    public static List<Book> getInstalledBooks()
    {
        return Books.installed().getBooks();
    }

    public static List<Book> getInstalledBooks(BookFilter filter)
    {
        return Books.installed().getBooks(filter);
    }

    public static List<Book> getInstalledBooks(String filterSpec)
    {
        return getInstalledBooks(BookFilters.getCustom(filterSpec));
    }

    public static Book getInstalledBook(String bookInitials)
    {
        return Books.installed().getBook(bookInitials);
    }

    public List<Book> getRepositoryBooks(String repositoryName)
    {
        return this.installManager.getInstaller(repositoryName).getBooks();
    }

    public List<Book> getRepositoryBooks(String repositoryName, BookFilter filter)
    {
        return this.installManager.getInstaller(repositoryName).getBooks(filter);
    }

    public List<Book> getRepositoryBooks(String repositoryName, String filterSpec)
    {
        return getRepositoryBooks(repositoryName, BookFilters.getCustom(filterSpec));
    }

    public Book getRepositoryBook(String repositoryName, String bookInitials)
    {
        return this.installManager.getInstaller(repositoryName).getBook(bookInitials);
    }

    public void reloadBookList(String repositoryName) throws InstallException
    {
        this.installManager.getInstaller(repositoryName).reloadBookList();
    }

    public Book getBook(String repositoryName, String bookName)
    {
        return this.installManager.getInstaller(repositoryName).getBook(bookName);
    }

    public void installBook(String repositoryName, Book book) throws BookException, InstallException
    {
        Installer installer = this.installManager.getInstaller(repositoryName);
        if (Books.installed().getBook(book.getInitials()) != null)
        {
            deleteBook(book);
        }
        installer.install(book);
    }

    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            usage();
            return;
        }
        System.err.print("BookInstaller");
        for (int i = 0; i < args.length; i++)
        {
            System.err.print(' ');
            System.err.print(args[i]);
        }
        System.err.print('\n');
        BookInstaller installer = new BookInstaller();
        String operation = args[0];
        if ("uninstall".equalsIgnoreCase(operation))
        {
            if (args.length == 2)
            {
                Book b = Books.installed().getBook(args[1]);
                if (b == null)
                {
                    System.err.println("Book not found");
                    return;
                }
                try
                {
                    installer.deleteBook(b);
                }
                catch (BookException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                usage();
            }
        }
        else
        {
            if ("sources".equalsIgnoreCase(operation))
            {
                Map<String, Installer> installers = installer.getInstallers();
                for (String name : installers.keySet())
                {
                    System.out.println(name);
                }
            }
            else
            {
                if ("list".equalsIgnoreCase(operation))
                {
                    if (args.length == 1)
                    {
                        for (Book book : getInstalledBooks())
                        {
                            System.out.println(book.getInitials());
                        }
                    }
                    else
                    {
                        if (args.length == 2)
                        {
                            for (Book book : installer.getRepositoryBooks(args[1]))
                            {
                                System.out.println(book.getInitials());
                            }
                        }
                        else
                        {
                            usage();
                        }
                    }
                }
                else
                {
                    if ("reload".equalsIgnoreCase(operation))
                    {
                        if (args.length == 2)
                        {
                            try
                            {
                                installer.reloadBookList(args[1]);
                            }
                            catch (InstallException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            usage();
                        }
                    }
                    else
                    {
                        if ("install".equalsIgnoreCase(operation))
                        {
                            if (args.length == 3)
                            {
                                Book b = installer.getBook(args[1], args[2]);
                                if (b == null)
                                {
                                    System.err.println("Book not found");
                                    return;
                                }
                                try
                                {
                                    installer.installBook(args[1], b);
                                }
                                catch (BookException e)
                                {
                                    e.printStackTrace();
                                }
                                catch (InstallException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                usage();
                            }
                        }
                        else
                        {
                            usage();
                        }
                    }
                }
            }
        }
    }

    public static void usage()
    {
        System.err.println("usage: BookInstaller <option>");
        System.err.println("Options:");
        System.err.println("    uninstall bookName                 Uninstall book");
        System.err.println("    sources                            List remote source repositories");
        System.err.println("    list                               List installed books");
        System.err.println("    list      repositoryName           List available books from a repository");
        System.err.println("    reload    repositoryName           Reload local cache for a repository");
        System.err.println("    install   repositoryName bookName  Install a book from a repository");
    }
}