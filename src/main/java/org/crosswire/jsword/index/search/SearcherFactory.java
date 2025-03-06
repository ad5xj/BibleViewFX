package org.crosswire.jsword.index.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.PluginUtil;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;

import org.crosswire.jsword.index.Index;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;

import java.io.IOException;

public final class SearcherFactory {

    public static Searcher createSearcher(Book book) throws InstantiationException {
        try {
            IndexManager imanager = IndexManagerFactory.getIndexManager();
            Index index = imanager.getIndex(book);
            Searcher parser = (Searcher) PluginUtil.getImplementation(Searcher.class);
            parser.init(index);
            return parser;
        } catch (IOException e) {
            log.error("createSearcher failed", e);
            throw new InstantiationException();
        } catch (BookException e) {
            log.error("createSearcher failed", (Throwable) e);
            throw new InstantiationException();
        } catch (ClassCastException e) {
            log.error("createSearcher failed", e);
            throw new InstantiationException();
        } catch (ClassNotFoundException e) {
            log.error("createSearcher failed", e);
            throw new InstantiationException();
        } catch (IllegalAccessException e) {
            log.error("createSearcher failed", e);
            throw new InstantiationException();
        }
    }

    private static final Logger log = LoggerFactory.getLogger(SearcherFactory.class);
}
