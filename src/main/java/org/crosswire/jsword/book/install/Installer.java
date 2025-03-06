package org.crosswire.jsword.book.install;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookList;

import java.net.URI;

import java.util.List;

public interface Installer extends BookList
{

    void reloadBookList() throws InstallException;
    void install(Book paramBook) throws InstallException;
    void downloadSearchIndex(Book paramBook, URI paramURI) throws InstallException;
    void close();

    boolean isNewer(Book paramBook);

    int getSize(Book paramBook);

    String getType();
    String getInstallerDefinition();

    URI toRemoteURI(Book paramBook);

    List<Book> getBooks();

    Book getBook(String paramString);
}
