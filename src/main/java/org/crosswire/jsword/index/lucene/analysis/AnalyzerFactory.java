package org.crosswire.jsword.index.lucene.analysis;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.Language;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.ResourceUtil;

import org.crosswire.jsword.book.Book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AnalyzerFactory
{
    public static final String DEFAULT_ID = "Default";

    private static AnalyzerFactory myInstance = new AnalyzerFactory();

    private static final Logger log = LoggerFactory.getLogger(AnalyzerFactory.class);

    public static AnalyzerFactory getInstance() { return myInstance; }

    private PropertyMap myProperties;

    private AnalyzerFactory()  { loadProperties(); }

    public AbstractBookAnalyzer createAnalyzer(Book book)
    {
        AbstractBookAnalyzer newObject = null;
        Language lang = (book == null) ? null : book.getLanguage();
        if ( lang != null )
        {
            String aClass = getAnalyzerValue(lang);
            log.debug("Creating analyzer:{} BookLang:{}", aClass, lang);
            if (aClass != null)
            {
                try
                {
                    Class<?> impl = ClassUtil.forName(aClass);
                    newObject = (AbstractBookAnalyzer) impl.newInstance();
                }
                catch (ClassNotFoundException | IllegalAccessException | InstantiationException e)
                {
                    String msg = "Configuration error in AnalyzerFactory properties" + e.getMessage();
                    log.error(msg,AnalyzerFactory.class);
                }
            }
        }

        if (newObject == null) { newObject = new SimpleLuceneAnalyzer(); }
        newObject.setBook(book);
        newObject.setDoStemming(getDefaultStemmingProperty());
        newObject.setDoStopWords(getDefaultStopWordProperty());
        return newObject;
    }

    public String getAnalyzerValue(Language lang)
    {
        String key = lang.getCode() + ".Analyzer";
        return this.myProperties.get(key);
    }

    public boolean getDefaultStemmingProperty()
    {
        String key = "Default.Stemming";
        return myProperties.get(key).contains("true");
    }

    public boolean getDefaultStopWordProperty()
    {
        String key = "Default.StopWord";
        return myProperties.get(key).contains("true");
    }


    private void loadProperties()
    {
        try
        {
            this.myProperties = ResourceUtil.getProperties(getClass());
        }
        catch (IOException e)
        {
            log.error("AnalyzerFactory property load from file failed", e);
        }
    }
}
