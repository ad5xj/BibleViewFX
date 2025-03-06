package org.crosswire.jsword.index.lucene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseFactory;
import org.crosswire.jsword.versification.Versification;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Searcher;

public class VerseCollector extends Collector {
    
    private static final String THISMODULE = "VerseCollector";
    private static final Logger lgr = LoggerFactory.getLogger(LuceneIndex.class);

    private int docBase;

    private Versification v11n;

    private Searcher searcher;

    private Key results;

    public VerseCollector(Versification v, Searcher s, Key r)
    {
        v11n = v;
        searcher = s;
        results = r;
    }

    public boolean acceptsDocsOutOfOrder()
    {
        return true;
    }

    public void collect(int docId) throws IOException
    {
        Document doc = this.searcher.doc(this.docBase + docId);
        try
        {
            Verse verse = VerseFactory.fromString(this.v11n, doc.get("key"));
            this.results.addAll((Key) verse);
        }
        catch (NoSuchVerseException e)
        {
            String msg = "collect(): NoSuchVerseException. err=" + e.getMessage();
            lgr.error(msg, THISMODULE);
            IOException ioe = new IOException();
            ioe.initCause((Throwable) e);
            throw ioe;
        }
    }

    public void setNextReader(IndexReader reader, int docBase) throws IOException
    {
        this.docBase = docBase;
    }
    
    public void setScorer(Scorer s) {  }
}