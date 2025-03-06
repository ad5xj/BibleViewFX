package org.crosswire.jsword.index.lucene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;

import org.crosswire.jsword.JSMsg;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.OSISUtil;

import org.crosswire.jsword.index.AbstractIndex;
import org.crosswire.jsword.index.IndexPolicy;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.index.lucene.analysis.LuceneAnalyzer;
import org.crosswire.jsword.index.search.SearchModifier;

import org.crosswire.jsword.passage.AbstractPassage;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseFactory;

import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import org.jdom2.Element;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import java.net.URI;

import java.util.ArrayList;
import java.util.List;

/**
 * @ingroup org.crosswire.jsword.index.lucene
 * @brief Helper class to create index
 * 
 * @extends AbstractIndex
 * @implements Closeable
 */
public class LuceneIndex extends AbstractIndex implements Closeable 
{
    public static final String FIELD_KEY = "key";
    public static final String FIELD_BODY = "content";
    public static final String FIELD_STRONG = "strong";
    public static final String FIELD_HEADING = "heading";
    public static final String FIELD_XREF = "xref";
    public static final String FIELD_NOTE = "note";
    public static final String FIELD_MORPHOLOGY = "morph";
    public static final String FIELD_INTRO = "intro";

    private static final int WORK_ESTIMATE = 98;
    private static final String THISMODULE = "org.crosswire.jsword.index.lucene.LuceneIndex";
    private static final Object CREATING = new Object();
    private static final Logger lgr = LoggerFactory.getLogger(LuceneIndex.class);

    private Book book;
    private String path;
    private Directory directory;
    private Searcher searcher;

    public LuceneIndex(Book book, URI storage) throws BookException
    {
        this.book = book;
        try
        {
            this.path = NetUtil.getAsFile(storage).getCanonicalPath();
        }
        catch (IOException ex)
        {
            String msg = "LuceneIndex(): Failed to initialize Lucene search engine. err="+ex.getMessage();
            lgr.error(msg,THISMODULE);
            throw new BookException(JSMsg.gettext("Failed to initialize Lucene search engine.", new Object[0]), ex);
        }
        initDirectoryAndSearcher();
    }

    public LuceneIndex(Book book, URI storage, IndexPolicy policy) throws BookException
    {
        this.book = book;
        File finalPath = null;
        File tempPath = null;
        IndexStatus finalStatus = IndexStatus.INVALID;
        List<Key> errors = null;
        Progress job = null;
        String jobName = "";

        try
        {
            finalPath = NetUtil.getAsFile(storage);
            this.path = finalPath.getCanonicalPath();
        }
        catch (IOException ex)
        {
            String msg = "LuceneIndex(): Failed to initialize Lucene search engine. err="+ex.getMessage();
            lgr.error(msg,THISMODULE);
            throw new BookException(JSMsg.gettext("Failed to initialize Lucene search engine.", new Object[0]), ex);
        }

        jobName = JSMsg.gettext("Creating index. Processing {0}", new Object[]
        {
            book.getInitials()
        });

        job = JobManager.createJob(String.format("CREATE_INDEX-%s", new Object[]
        {
            book.getInitials()
        }), jobName, Thread.currentThread());
        
        job.beginJob(jobName);

        finalStatus = IndexStatus.UNDONE;

        errors = new ArrayList<>();
        tempPath = new File(this.path + '.' + IndexStatus.CREATING.toString());
        
        if (tempPath.exists()) { FileUtil.delete(tempPath); }
        try
        {
            LuceneAnalyzer luceneAnalyzer = new LuceneAnalyzer(book);
            Object mutex = policy.isSerial() ? CREATING : book.getBookMetaData();
            synchronized (mutex)
            {
                book.setIndexStatus(IndexStatus.CREATING);
                IndexWriter writer = null;
                try
                {
                    FSDirectory fSDirectory = FSDirectory.open(new File(tempPath.getCanonicalPath()));
                    writer = new IndexWriter((Directory) fSDirectory, (Analyzer) luceneAnalyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
                    writer.setRAMBufferSizeMB(policy.getRAMBufferSize());
                    generateSearchIndexImpl(job, errors, writer, book.getGlobalKeyList(), 0, policy);
                }
                catch ( Exception e )
                {
                    String msg = "LuceneIndex(): Failed on Dir open. err="+e.getMessage();
                    lgr.error(msg,THISMODULE);
                }
                finally
                {
                    if (writer != null)  {  writer.close();  }
                }

                job.setCancelable(false);

                if (!job.isFinished() && !tempPath.renameTo(finalPath))
                {
                    String msg = "LuceneIndex(): Failed on rename of path. . . ";
                    lgr.error(msg,THISMODULE);
                    throw new BookException(JSMsg.gettext("Installation failed.", new Object[0]));
                }

                if (finalPath.exists())    { finalStatus = IndexStatus.DONE; }

                if (!errors.isEmpty())
                {
                    StringBuilder buf = new StringBuilder();
                    for (Key error : errors)
                    {
                        buf.append(error);
                        buf.append('\n');
                    }
                    Reporter.informUser(this, JSMsg.gettext("The following verses have errors and could not be indexed\n{0}", new Object[]
                    {
                        buf
                    }));
                }
                initDirectoryAndSearcher();
            }
        }
        catch (IOException ex)
        {
            job.cancel();
            String msg = "LuceneIndex(): Failed to initialize Lucene search engine. err="+ex.getMessage();
            lgr.error(msg,THISMODULE);
            throw new BookException(JSMsg.gettext("Failed to initialize Lucene search engine.", new Object[0]), ex);
        }
        finally
        {
            book.setIndexStatus(finalStatus);
            job.done();
            if (tempPath.exists()) { FileUtil.delete(tempPath); }
        }
    }

    private void initDirectoryAndSearcher()
    {
        try
        {
            this.directory = (Directory) FSDirectory.open(new File(this.path));
            this.searcher = (Searcher) new IndexSearcher(this.directory, true);
        }
        catch (IOException ex)
        {
            String msg = "initDirectoryAndSearcher(): second load failure. err="+ex.getLocalizedMessage();
            lgr.error(msg, THISMODULE);
        }
    }

    public Key find(String search) throws BookException
    {
        int i;
        int docId;
        int score;
        String v11nName;
        Versification v11n;
        SearchModifier modifier;
        Key results;
        ParseException parseException;
        Throwable theCause;
        PassageTally tally;
        PassageTally passageTally1; 
        Document doc;
        Verse verse;

        i = 0;
        docId = 0;
        score = -1;
        modifier = getSearchModifier();
        results = null;
        parseException = null;
        theCause = null;
        tally = null;
        passageTally1 = null; 
        doc = null;
        verse = null;
        v11nName = null;
        v11n = null;

        try
        {
            v11nName = book.getBookMetaData().getProperty("Versification").toString();
            v11n = Versifications.instance().getVersification(v11nName);
        }
        catch ( Exception e )
        {
            String msg = "Versification error on find() " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }

        if ( search != null )
        {
            try
            {
                LuceneAnalyzer luceneAnalyzer = new LuceneAnalyzer(book);
                QueryParser parser = new QueryParser(Version.LUCENE_29, "content", (Analyzer) luceneAnalyzer);
                parser.setAllowLeadingWildcard(true);
                Query query = parser.parse(search);
                String msg = "find(): ParsedQuery- " + query.toString();
                //lgr.info(msg, THISMODULE);
                if ( (modifier != null) && (modifier.isRanked()) )
                {
                    tally = new PassageTally(v11n);
                    tally.raiseEventSuppresion();
                    tally.raiseNormalizeProtection();
                    passageTally1 = tally;
                    TopScoreDocCollector collector = TopScoreDocCollector.create(modifier.getMaxResults(), false);
                    this.searcher.search(query, (Collector) collector);
                    tally.setTotal(collector.getTotalHits());
                    ScoreDoc[] hits = (collector.topDocs()).scoreDocs;

                    for (i = 0; i < hits.length; i++)
                    {
                        docId = (hits[i]).doc;
                        doc = this.searcher.doc(docId);
                        verse = VerseFactory.fromString(v11n, doc.get("key"));
                        score = (int) ((hits[i]).score * 100.0F + 1.0F);
                        tally.add((Key) verse, score);
                    }
                    tally.lowerNormalizeProtection();
                    tally.lowerEventSuppressionAndTest();
                }
                else
                {
                    results = this.book.createEmptyKeyList();
                    AbstractPassage passage = null;
                    if (results instanceof AbstractPassage)
                    {
                        passage = (AbstractPassage) results;
                        passage.raiseEventSuppresion();
                        passage.raiseNormalizeProtection();
                    }
                    this.searcher.search(query, new VerseCollector(v11n, this.searcher, results));
                    if (passage != null)
                    {
                        passage.lowerNormalizeProtection();
                        passage.lowerEventSuppressionAndTest();
                    }
                }
            }
            catch (IOException e)
            {
                String msg = "find(): I/O Exception. cause="+e.getCause();
                lgr.error(msg,THISMODULE);
                Throwable cause = e.getCause();
                theCause = (cause instanceof NoSuchVerseException) ? cause : e;
            }
            catch (NoSuchVerseException e)
            {
                String msg = "find(): No such Verse Exception err="+e.getMessage();
                lgr.error(msg,THISMODULE);
                NoSuchVerseException noSuchVerseException1 = e;
            }
            catch (ParseException e)
            {
                String msg = "find(): Parse Exception err="+e.getMessage();
                lgr.error(msg,THISMODULE);
                parseException = e;
            }

            if (parseException != null)
            {
                String msg = "find(): Book Exception err=Search failed on parse exception.";
                lgr.error(msg,THISMODULE);
                throw new BookException(JSMsg.gettext("Search failed.", new Object[0]), parseException);
            }
        }
        if (results == null)
        {
            PassageTally passageTally = null;

            if ( (modifier != null) && (modifier.isRanked()) )
            {
                passageTally = new PassageTally(v11n);
            }
            else
            {
                results = this.book.createEmptyKeyList();
            }
        }
        return results;
    }

    public Key getKey(String name) throws NoSuchKeyException { return this.book.getKey(name); }

    public final void close()
    {
        IOUtil.close((Closeable) this.searcher);
        this.searcher = null;
        IOUtil.close((Closeable) this.directory);
        this.directory = null;
    }

    public Searcher getSearcher() {  return this.searcher; }

    private void generateSearchIndexImpl(Progress job, List<Key> errors, IndexWriter writer, Key key, int count, IndexPolicy policy) throws BookException, IOException
    {
        boolean includeStrongs = false;
        boolean includeXrefs = false;
        boolean includeNotes = false;
        boolean includeHeadings = false;
        boolean includeMorphology = false;
        String oldRootName = "";
        int percent = 0;
        int size = 0;
        int subCount = 0;
        int oldPercent = 0;
        String rootName = "";
        String v11nName = "";
        BookData data = null;
        Element osis = null;
        Document doc = new Document();
        Versification v11n = null;

        Field keyField = new Field("key", "", Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.NO);
        Field bodyField = new Field("content", "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field introField = new Field("intro", "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field strongField = new Field("strong", "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.YES);
        Field xrefField = new Field("xref", "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field noteField = new Field("note", "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field headingField = new Field("heading", "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);
        Field morphologyField = new Field("morph", "", Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.NO);

        try
        {
            if (book.getBookMetaData().getProperty("Versification") != null)
            {
                v11nName = book.getBookMetaData().getProperty("Versification").toString();
            }

             v11n = Versifications.instance().getVersification(v11nName);
        }
        catch ( Exception e )
        {
            String msg = "Versicfication Error on generating Index. err="+e.getMessage();
            lgr.error(msg,THISMODULE);
        }

        includeStrongs = (this.book.getBookMetaData().hasFeature(FeatureType.STRONGS_NUMBERS) && policy.isStrongsIndexed());
        includeXrefs = (this.book.getBookMetaData().hasFeature(FeatureType.SCRIPTURE_REFERENCES) && policy.isXrefIndexed());
        includeNotes = (this.book.getBookMetaData().hasFeature(FeatureType.FOOTNOTES) && policy.isNoteIndexed());
        includeHeadings = (this.book.getBookMetaData().hasFeature(FeatureType.HEADINGS) && policy.isTitleIndexed());
        includeMorphology = (this.book.getBookMetaData().hasFeature(FeatureType.MORPHOLOGY) && policy.isMorphIndexed());

        size = key.getCardinality();
        subCount = count;

        for (Key subkey : key)
        {
            if (subkey.canHaveChildren())
            {
                generateSearchIndexImpl(job, errors, writer, subkey, subCount, policy);
                continue;
            }
            data = new BookData(this.book, subkey);
            osis = null;
            try
            {
                osis = data.getOsisFragment(false);
            }
            catch (BookException e)
            {
                String msg = "generateSearchIndexImpl(): Book Exception - "+e.getDetailedMessage();
                lgr.error(msg,THISMODULE);
                errors.add(subkey);
                continue;
            }
            doc.getFields().clear();
            keyField.setValue(subkey.getOsisRef());
            doc.add((Fieldable) keyField);

            if (subkey instanceof Verse && ((Verse) subkey).getVerse() == 0)
            {
                addField(doc, introField, OSISUtil.getCanonicalText(osis));
            }
            else
            {
                addField(doc, bodyField, OSISUtil.getCanonicalText(osis));
            }

            if (includeStrongs) { addField(doc, strongField, OSISUtil.getStrongsNumbers(osis)); }

            if (includeXrefs)   { addField(doc, xrefField, OSISUtil.getReferences(this.book, subkey, v11n, osis)); }

            if (includeNotes)   { addField(doc, noteField, OSISUtil.getNotes(osis)); }

            if (includeHeadings)
            {
                String heading = OSISUtil.getHeadings(osis);
                addField(doc, headingField, heading);
            }

            if (includeMorphology) { addField(doc, morphologyField, OSISUtil.getMorphologiesWithStrong(osis)); }

            if (doc.getFields().size() > 1) { writer.addDocument(doc); }

            rootName = subkey.getRootName();

            if (!rootName.equals(oldRootName))
            {
                oldRootName = rootName;
                job.setSectionName(rootName);
            }

            subCount++;
            oldPercent = percent;
            percent = 98 * subCount / size;
            if (oldPercent != percent) { job.setWork(percent); }
            Thread.yield();
            if (Thread.currentThread().isInterrupted())  { break; }
        }
    }

    private void addField(Document doc, Field field, String text)
    {
        if ( !text.isBlank() && (text.length() > 0) )
        {
            field.setValue(text);
            doc.add((Fieldable) field);
        }
    }
}