package org.crosswire.jsword.book.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.StringUtil;

import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.book.sword.Backend;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseKey;
import org.crosswire.jsword.passage.VerseRange;

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.VersificationsMapper;
import org.crosswire.jsword.versification.system.Versifications;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Content;
import org.jdom2.Element;

/**
 * @brief Abstract class to extend Abstract Book
 * 
 * @extends AbstractBook
 */
public abstract class AbstractPassageBook extends AbstractBook 
{
    private static final String THISMODULE = "org.crosswire.jsword.book.basic.AbstractPassageBook";
    private static final Logger lgr = LoggerFactory.getLogger(AbstractPassageBook.class);

    private String versification;

    private Versification versificationSystem;

    private PassageKeyFactory keyf;

    private volatile Set<BibleBook> bibleBooks;

    /**
     * @ingroup org.crosswire.jsword.book.basic
     * 
     * @brief Constructor with params
     * 
     * @param bmd
     * @param backend 
     */
    public AbstractPassageBook(BookMetaData bmd, Backend backend) 
    {
        super(bmd, backend);
        this.keyf = PassageKeyFactory.instance();
        this.versification = bmd.getProperty("Versification");
    }

    public Iterator<Content> getOsisIterator(Key key, final boolean allowEmpty, boolean allowGenTitles) throws BookException 
    {
        final SourceFilter filter = getFilter();
        Passage ref = VersificationsMapper.instance().map(KeyUtil.getPassage(key), getVersification());
        final boolean showTitles = (ref.hasRanges(RestrictionType.CHAPTER) || (!allowEmpty && allowGenTitles));
        
        RawTextToXmlProcessor processor = new RawTextToXmlProcessor() 
        {
            private String previousVerseText = "";

            public void preRange(VerseRange range, List<Content> partialDom) 
            {
                if (showTitles) 
                {
                    Element title = OSISUtil.factory().createGeneratedTitle();
                    title.addContent(range.getName());
                    partialDom.add(title);
                }
            }

            public void postVerse(Key verse, List<Content> partialDom, String rawText) 
            {
                if ((allowEmpty || rawText.length() > 0) && !this.previousVerseText.equals(rawText)) {
                    List<Content> osisContent = filter.toOSIS(AbstractPassageBook.this, verse, rawText);
                    AbstractPassageBook.this.addOSIS(verse, partialDom, osisContent);
                }
                this.previousVerseText = rawText;
            }

            public void init(List<Content> partialDom) {
            }
        };
        return getOsis((Key) ref, processor).iterator();
    }

    public void addOSIS(Key key, Element div, List<Content> osisContent) 
    {
        assert key != null;
        div.addContent(osisContent);
    }

    public void addOSIS(Key key, List<Content> content, List<Content> osisContent) 
    {
        assert key != null;
        content.addAll(osisContent);
    }

    protected abstract SourceFilter getFilter();

    public void setDocument(Key key, BookData bdata) throws BookException 
    {
        for (Content nextElem : OSISUtil.getFragment(bdata.getOsisFragment())) 
        {
            if (nextElem instanceof Element) 
            {
                Element div = (Element) nextElem;
                for (Content data : div.getContent()) 
                {
                    if ( data instanceof Element ) 
                    {
                        Element overse = (Element) data;
                        String text = OSISUtil.getPlainText(overse);
                        setRawText(key, text);
                        continue;
                    }
                    else
                    {
                        String msg = "Ignoring non OSIS/Verse content of DIV.";
                        lgr.error(msg,THISMODULE);
                    }
                }
                continue;
            }
            else
            {
                String msg = "Ignoring non OSIS/Verse content of DIV.";
                lgr.error(msg,THISMODULE);
            }
        }
    }

    public boolean isWritable() { return false; }

    public final Key createEmptyKeyList() 
    {
        try
        {
            return keyf.createEmptyKeyList(Versifications.instance().getVersification(versification));
        }
        catch ( Exception e )
        {
                String msg = "Error creating KeyList for Key -  " + e.getMessage();
                lgr.error(msg,THISMODULE);
        }
        return null;
    }

    public Key getValidKey(String name) 
    {
        try 
        {
            return getKey(name);
        } 
        catch (NoSuchKeyException e) 
        {
            return createEmptyKeyList();
        }
    }

    public final Key getKey(String text) throws NoSuchKeyException 
    {
        try
        {
            return (Key) PassageKeyFactory.instance().getKey(Versifications.instance().getVersification(versification), text);
        }
        catch ( Exception e )
        {
            String msg = "Error creating PassageKey in getKey() -  " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        return null;
    }

    public Versification getVersification() 
    {
        if ( versificationSystem == null )
        {
            try
            {
                versificationSystem = Versifications.instance().getVersification(getBookMetaData().getProperty("Versification"));
            }
            catch ( Exception e )
            {
                String msg = "Error creating PassageKey in getKey() -  " + e.getMessage();
                lgr.error(msg,THISMODULE);
            }
        }
        return versificationSystem;
    }

    public Set<BibleBook> getBibleBooks() 
    {
        if (this.bibleBooks == null)
        {
            synchronized (this) 
            {
                if ( bibleBooks == null ) { bibleBooks = getBibleBooksInternal(); }
            }
        }
        return bibleBooks;
    }

    private Set<BibleBook> getBibleBooksInternal() 
    {
        Set<BibleBook> books;
        String list = getBookMetaData().getProperty("BookList");
        if ( list == null ) 
        {
            books = calculateBibleBookList();
            String listOfBooks = toString(books);
            putProperty("BookList", listOfBooks);
        } 
        else 
        {
            books = fromString(list);
        }
        return books;
    }

    private Set<BibleBook> fromString(String list) 
    {
        Set<BibleBook> books = new LinkedHashSet<>(list.length() / 2);
        String[] bookOsis = StringUtil.split(list, ' ');
        for (String s : bookOsis) 
        {
            books.add(BibleBook.fromExactOSIS(s));
        }
        return books;
    }

    private String toString(Set<BibleBook> i_books) 
    {
        String sb = Integer.toString(i_books.size());
        for (Iterator<BibleBook> iterator = i_books.iterator(); iterator.hasNext();) 
        {
            BibleBook b = iterator.next();
            sb += b.getOSIS();
            if (iterator.hasNext()) { sb += ' '; }
        }
        return sb;
    }

    private Set<BibleBook> calculateBibleBookList() 
    {
        Versification v11n;
        Iterator<BibleBook> v11nBookIterator;
        BookMetaData bookMetaData = getBookMetaData();
        VerseKey scope = (VerseKey) getScope();
        if (scope == null) { return new HashSet<BibleBook>(); }
        Set<BibleBook> bookList = new LinkedHashSet<BibleBook>();
        try
        {
            v11n = Versifications.instance().getVersification(bookMetaData.getProperty("Versification"));
            v11nBookIterator = v11n.getBookIterator();
            while (v11nBookIterator.hasNext()) 
            {
                BibleBook bibleBook = v11nBookIterator.next();
                if (scope.contains((Key) new Verse(v11n, bibleBook, 1, 1)) || 
                    scope.contains((Key) new Verse(v11n, bibleBook, 1, 2))
                   ) 
                {
                    bookList.add(bibleBook);
                }
            }
        }
        catch ( Exception e )
        {
            String msg = "Error creating PassageKey in getKey() -  " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        return bookList;
    }
}