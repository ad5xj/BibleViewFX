package org.crosswire.jsword.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSOtherMsg;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.util.CollectionUtil;
import org.crosswire.common.util.PluginUtil;
import org.crosswire.common.util.Reporter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief This is a List object of Books
 * @extends AbstractBookList
 */
public class Books extends AbstractBookList 
{
    public static Books installed() { return instance; }

    private static final Logger log = LoggerFactory.getLogger(Books.class);
    private static final Books instance = new Books();

    static
    {
        //log.info("Auto-registering start @ {}", Long.toString(System.currentTimeMillis()));
        try
        {
            instance.autoRegister();
        }
        catch ( Exception e )
        {
            log.error("Could not auto-register Book", Books.class);
        }
    }

    private boolean debug_log = false;

    private Map<String, Book> initials = new HashMap<>();
    private Map<String, Book> names = new HashMap<>();
    private Set<BookDriver> drivers = new HashSet<>();
    private Set<Book> books = new TreeSet<>();


    public synchronized List<Book> getBooks() { return CollectionUtil.createList(this.books); }
    public synchronized List<Book> getBooks(BookFilter filter) { return CollectionUtil.createList(new BookFilterIterator(books, filter)); }
    public synchronized Book getBook(String name) 
    {
        if (name == null) { return null; }
    
        Book book = initials.get(name);
        if (book != null) { return book; }

        book = names.get(name);
        if (book != null) { return book; }

        for (Book b : books) 
        {
            if (name.equalsIgnoreCase(b.getInitials()) || name.equalsIgnoreCase(b.getName())) { return b; }
        }
        return null;
    }

    public synchronized void addBook(Book b) 
    {
        if ( (b != null) && books.add(b) ) 
        {
            initials.put(b.getInitials(), b);
            names.put(b.getName(), b);
            fireBooksChanged(instance, b, true);
        }
    }

    public synchronized void removeBook(Book book) throws BookException 
    {
        Activator.deactivate(book);
        boolean removed = books.remove(book);
    
        if (removed) 
        {
            initials.remove(book.getInitials());
            names.remove(book.getName());
            fireBooksChanged(instance, book, false);
        } 
        else 
        {
            String msg = "Could not remove unregistered Book: " + Arrays.toString(new Object[]{book.getName()});
            log.error(msg, Books.class);
            throw new BookException(JSOtherMsg.lookupText("Could not remove unregistered Book: {0}", new Object[]{book.getName()}));
        }
    }

    public synchronized void registerDriver(BookDriver driver) throws Exception 
    {
        if ( driver == null ) { return; }

        int j = 0;

        Book[] bookArray;
        
        if ( debug_log ) { log.debug("begin registering driver: {}", driver.getClass().getName()); }
    
        drivers.add(driver);

        try 
        {
            bookArray = driver.getBooks();

            Set<Book> current = CollectionUtil.createSet(new BookFilterIterator(this.books, BookFilters.getBooksByDriver(driver)));
            for ( j = 0; j < bookArray.length; j++ ) 
            {
                Book b = bookArray[j];
                if (current.contains(b)) 
                {
                    current.remove(b);
                } 
                else 
                {
                    addBook(bookArray[j]);
                }
            }

            for (Book book : current) { removeBook(book); }
        }
        catch ( Exception e )
        {
            String msg = "Could not register driver: " + driver.getDriverName()
                       + "\n    error=" + e.getMessage();
            log.error(msg, Books.class);
            throw e;
        }
        if ( debug_log ) { log.debug("end registering driver: {}", driver.getClass().getName()); }
    }

    public synchronized BookDriver[] getDriversByClass(Class<? extends BookDriver> type) 
    {
        List<BookDriver> matches = new ArrayList<>();
        for (BookDriver driver : drivers) 
        {
            if (driver.getClass() == type) { matches.add(driver); }
        }
        return matches.<BookDriver>toArray(new BookDriver[matches.size()]);
    }

    public synchronized BookDriver[] getDrivers() 
    {
        return drivers.<BookDriver>toArray(new BookDriver[drivers.size()]);
    }

    private void autoRegister() throws Exception
    {
        BookDriver driver = null;
        Method driverInstance = null;
        Class<BookDriver>[] arrayOfClass = PluginUtil.getImplementors(BookDriver.class);
        
        if ( debug_log ) { log.info("begin auto-registering {} drivers:", Integer.toString(arrayOfClass.length)); }
        
        for ( Class<BookDriver> arrayOfClas : arrayOfClass ) 
        {
            try 
            {
                driverInstance = arrayOfClas.getMethod("instance", new Class[0]);
                driver = (BookDriver)driverInstance.invoke(null, new Object[0]);
                registerDriver(driver);
            }
            catch ( Exception e )
            {
                String msg = "Exception getting book driver:" + driver.getDriverName();
                log.error(msg,Books.class);
//                Reporter.informUser(Books.class, e);
                throw e;
            }
        }
    }
}