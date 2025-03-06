package org.crosswire.jsword.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.diff.Diff;
import org.crosswire.common.diff.DiffCleanup;
import org.crosswire.common.diff.Difference;
import org.crosswire.common.util.Language;
import org.crosswire.common.xml.JDOMSAXEventProvider;
import org.crosswire.common.xml.SAXEventProvider;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseKey;

import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.VersificationsMapper;
import org.crosswire.jsword.versification.system.Versifications;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Text;

/**
 * @ingroup org.crosswire.jsword
 * @brief Constructor for BookData
 * @implements BookProvider
 */
public class BookData implements BookProvider 
{
    private static final String THISMODULE = "org.crosswire.jsword.versification";
    private static final Logger lgr = LoggerFactory.getLogger(BookData.class);

    private boolean comparingBooks;

    private Key key;
    private Element osis;
    private Element fragment;
    private UnAccenter unaccenter;

    private Book[] books;

    /**
     * @brief Constructor for BookData with params
     * 
     * @param book
     * @param key 
     */
    public BookData(Book book, Key key) 
    {
        assert book != null;
        assert key != null;
        key = key;
        books = new Book[1];
        books[0] = book;
    }

    /**
     * @brief Overloaded constructor for BookData with params
     *
     * @param books
     * @param key
     * @param compare 
     */
    public BookData(Book[] books, Key key, boolean compare) 
    {
        assert books != null && books.length > 0;
        assert key != null;
        books = (Book[]) books.clone();
        key = key;
        comparingBooks = compare;
    }

    /**
     * 
     * @return
     * @throws BookException 
     */
    public Element getOsis() throws BookException 
    {
        if ( osis == null ) 
        {
            osis = OSISUtil.createOsisFramework(getFirstBook().getBookMetaData());
            Element text = osis.getChild("osisText");
            Element div = getOsisFragment();
            text.addContent((Content) div);
        }
        return osis;
    }

    /**
     * 
     * @return
     * @throws BookException 
     */
    public Element getOsisFragment() throws BookException 
    {
        if ( fragment == null ) { fragment = getOsisContent(true); }
        return fragment;
    }

    /**
     * 
     * @param allowGenTitles boolean
     * 
     * @return
     * @throws BookException 
     */
    public Element getOsisFragment(boolean allowGenTitles) throws BookException 
    {
        if ( fragment == null) { fragment = getOsisContent(allowGenTitles); }
        return fragment;
    }

    /**
     * 
     * @return
     * @throws BookException 
     */
    public SAXEventProvider getSAXEventProvider() throws BookException 
    {
        Element frag = getOsisFragment();
        Document doc = frag.getDocument();
        if (doc == null) { doc = new Document(frag); }
        return (SAXEventProvider) new JDOMSAXEventProvider(doc);
    }

    /**
     * 
     * @return Book[]
     */
    public Book[] getBooks() 
    {
        return ( books == null) ? null : (Book[]) books.clone();
    }

    /**
     * 
     * @return Book from Books array
     */
    public Book getFirstBook() 
    {
        return ( books != null && books.length > 0) ? books[0] : null;
    }

    /**
     * 
     * @return key
     */
    public Key getKey()                { return key; }

    /**
     * 
     * @return comparingBooks boolean
     */
    public boolean isComparingBooks() { return comparingBooks; }

    /**
     * @brief Get the OSIS content from the Book
     * 
     * @param allowGenTitles
     * @return div Element
     * @throws BookException 
     */
    private Element getOsisContent(boolean allowGenTitles) throws BookException 
    {
        boolean doDiffs;
        int i;
        int j;
        int k;
        int numRangesInMasterPassage;
        int cellCount;
        int rowCount;
        
        String firstText;

        Element table;
        Element row;
        Element cell;
        Element div;
        Book book;
        VerseKey verseKey;
        Key singleKey;
        Language lang;

        boolean[] showDiffs;
        boolean[] ommittedVerses;
        Iterator[] arrayOfIterator;
        Passage[] passages;
        Passage passageOfInterest;
        BookVerseContent[] booksContents;
        
        Iterator<Key> passageKeys;
        List<Content>   xmlContent;
        List<Difference> diffs;
        
        div = OSISUtil.factory().createDiv();
        if ( books.length == 1 ) 
        {
            Iterator<Content> iter = books[0].getOsisIterator(key, false, allowGenTitles);
            while (iter.hasNext()) 
            {
                Content content = iter.next();
                div.addContent(content);
            }
        } 
        else 
        {
            table = OSISUtil.factory().createTable();
            row = OSISUtil.factory().createRow();
            cell = OSISUtil.factory().createCell();
            table.addContent((Content) row);
            arrayOfIterator = new Iterator[books.length];
            passages = new Passage[books.length];
            showDiffs = new boolean[books.length - 1];
            doDiffs = false;
            ommittedVerses = new boolean[books.length];
            numRangesInMasterPassage = 0;
            for ( i = 0; i < books.length; i++ )
            {
                passages[i] = VersificationsMapper.instance().map(KeyUtil.getPassage(key), getVersification(i));
                arrayOfIterator[i] = books[i].getOsisIterator((Key) passages[i], true, true);
                if ( i == 0 ) 
                {
                    ommittedVerses[i] = false;
                    numRangesInMasterPassage = passages[i].countRanges(RestrictionType.NONE);
                } 
                else 
                {
                    ommittedVerses[i] = (passages[i].countRanges(RestrictionType.NONE) > numRangesInMasterPassage);
                }
            }
            booksContents = new BookVerseContent[books.length];
            for ( j = 0; j < books.length; j++ ) 
            {
                doDiffs |= addHeaderAndSetShowDiffsState(row, showDiffs, j, ommittedVerses[j]);
                booksContents[j] = keyIteratorContentByVerse(getVersification(j), arrayOfIterator[j]);
            }
            cellCount = 0;
            rowCount = 0;
            for ( Map.Entry<Verse, List<Content>> verseContent : booksContents[0].entrySet() ) 
            {
                cellCount = 0;
                row = OSISUtil.factory().createRow();
                firstText = "";
                for ( k = 0; k < books.length; k++ ) 
                {
                    book = books[k];
                    cell = OSISUtil.factory().createCell();
                    lang = book.getLanguage();
                    if ( lang != null ) { cell.setAttribute("lang", lang.getCode(), Namespace.XML_NAMESPACE); }
                    row.addContent((Content) cell);
                    StringBuilder newText = new StringBuilder(doDiffs ? 32 : 0);
                    verseKey = VersificationsMapper.instance().mapVerse(verseContent.getKey(), getVersification(k));
                    passageOfInterest = KeyUtil.getPassage((Key) verseKey);
                    passageKeys = passageOfInterest.iterator();
                    while (passageKeys.hasNext()) 
                    {
                        singleKey = passageKeys.next();
                        if (!(singleKey instanceof Verse)) 
                        {
                            throw new UnsupportedOperationException("Iterating through a passage gives non-verses");
                        }
                        xmlContent = booksContents[k].get(singleKey);
                        if ( xmlContent == null ) 
                        {
                            xmlContent = new ArrayList<Content>(0);
                        }
                        addText(doDiffs, newText, xmlContent);
                        if (doDiffs) 
                        {
                            String thisText = newText.toString();
                            if ( unaccenter != null ) { thisText = unaccenter.unaccent(thisText); }
                            if ( (k > 0) && showDiffs[k - 1] ) 
                            {
                                diffs = (new Diff(firstText, thisText, false)).compare();
                                DiffCleanup.cleanupSemantic(diffs);
                                cell.addContent(OSISUtil.diffToOsis(diffs));
                                cell = OSISUtil.factory().createCell();
                                lang = book.getLanguage();
                                cell.setAttribute("lang", lang.getCode(), Namespace.XML_NAMESPACE);
                                row.addContent((Content) cell);
                            }
                            if ( k == 0 ) { firstText = thisText; }
                        }
                        addContentSafely(cell, xmlContent);
                        cellCount++;
                    }
                }
                if ( cellCount == 0 ) { break; }
                table.addContent((Content) row);
                rowCount++;
            }
            if ( rowCount > 0 ) { div.addContent((Content) table); }
        }
        return div;
    }

    /**
     * 
     * @param cell Element 
     * @param xmlContent List
     */
    private void addContentSafely(Element cell, List<Content> xmlContent) 
    {
        Element note = null;
        
        for (Content c : xmlContent) 
        {
            if ( c.getParent() == null ) 
            {
                cell.addContent(c);
                continue;
            }

            if ( note != null ) 
            {
                note.addContent(c.clone());
                continue;
            }
            note = appendVersificationNotice(cell, "duplicate");
            note.addContent(c.clone());
        }
    }

    /**
     * 
     * @param parent Element
     * @param notice String
     * @return note Element
     */
    private Element appendVersificationNotice(Element parent, String notice) 
    {
        Element note = OSISUtil.factory().createDiv();
        note.setAttribute("type", "x-gen");
        note.setAttribute("subType", "x-" + notice);
        parent.addContent((Content) note);
        return note;
    }

    /**
     * 
     * @param i integer
     * @return
     */
    private Versification getVersification(int i) 
    {
        try
        {
            return Versifications.instance().getVersification(books[i].getBookMetaData().getProperty("Versification"));
        }
        catch ( Exception e )
        {
            String msg = "Error getting versification of BookData - " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        return null;
    }

    /**
     * 
     * @param v11n
     * @param iter
     * @return contentsByOsisID BookVerseContent
     * @throws BookException 
     */
    private BookVerseContent keyIteratorContentByVerse(Versification v11n, Iterator<Content> iter) throws BookException {
        BookVerseContent contentsByOsisID = new BookVerseContent();
        Verse currentVerse = null;
        List<Content> contents = new ArrayList<Content>();
        while ( iter.hasNext() ) 
        {
            Content content = iter.next();
            if ( (content instanceof Element) && 
                 ("verse".equals(((Element) content).getName())) 
               ) 
            {
                if ( currentVerse != null ) 
                {
                    contentsByOsisID.put(currentVerse, contents);
                    contents = new ArrayList<Content>();
                }
                currentVerse = OSISUtil.getVerse(v11n, (Element) content);
                if ( contents.size() > 0 ) 
                {
                    Verse previousVerse = new Verse(currentVerse.getVersification(), currentVerse.getOrdinal() - 1);
                    contentsByOsisID.put(previousVerse, contents);
                    contents = new ArrayList<Content>();
                }
            }
            contents.add(content);
        }
        if (currentVerse != null) { contentsByOsisID.put(currentVerse, contents); }
        return contentsByOsisID;
    }

    /**
     * 
     * @param row
     * @param showDiffs
     * @param i_i
     * @param ommittedVerse
     * @return doDiffs  boolean
     */
    private boolean addHeaderAndSetShowDiffsState(Element row, boolean[] showDiffs, int i_i, boolean ommittedVerse) {
        boolean doDiffs = false;

        String prevName;
        String buf;

        Text text;

        Book firstBook;
        BookCategory category;
        BookCategory prevCategory;
        Book book;
        Element cell = OSISUtil.factory().createHeaderCell();


        buf = "";
        book = null;

        if ( i_i > 0 ) 
        {
            firstBook = books[0];
            book = books[i_i];
            category = book.getBookCategory();
            prevCategory = firstBook.getBookCategory();
            prevName = firstBook.getInitials();
            showDiffs[i_i - 1] = (comparingBooks && BookCategory.BIBLE.equals(category) && category.equals(prevCategory) && book.getLanguage().equals(firstBook.getLanguage()) && !book.getInitials().equals(prevName));
            
            if ( showDiffs[i_i - 1] ) 
            {
                doDiffs = true;
                buf = firstBook.getInitials() + " ==> " + book.getInitials() 
                    + cell.addContent((Content)OSISUtil.factory().createText(buf))
                    + row.addContent((Content)cell);
                cell = OSISUtil.factory().createHeaderCell();
            }
        }
        text = OSISUtil.factory().createText(book.getInitials());
        
        if ( ommittedVerse ) 
        {
            Element notice = appendVersificationNotice(cell, "omitted-verses");
            notice.addContent((Content) text);
        } 
        else 
        {
            cell.addContent((Content) text);
        }
        row.addContent((Content) cell);
        return doDiffs;
    }

    /**
     * 
     * @param doDiffs
     * @param newText
     * @param contents 
     */
    private void addText(boolean doDiffs, StringBuilder newText, List<Content> contents) 
    {
        for (Content c : contents) 
        {
            addText(doDiffs, newText, c);
        }
    }

    /**
     * 
     * @param doDiffs
     * @param newText
     * @param content 
     */
    private void addText(boolean doDiffs, StringBuilder newText, Content content) 
    {
        if ( doDiffs ) 
        {
            if ( newText.length() != 0 ) { newText.append(' '); }
            if (content instanceof Element) 
            {
                newText.append(OSISUtil.getCanonicalText((Element) content));
            } 
            else if (content instanceof Text) 
            {
                newText.append(((Text) content).getText());
            }
        }
    }

    public void setUnaccenter(UnAccenter unaccenter) { unaccenter = unaccenter; }

    class BookVerseContent extends TreeMap<Verse, List<Content>> 
    {
        private static final long serialVersionUID = -6508118172314227362L;
    }
}