package org.crosswire.jsword.index.lucene.analysis;

import org.crosswire.jsword.book.Book;

import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigurableSnowballAnalyzer extends AbstractBookAnalyzer {

    private final Version matchVersion = Version.LUCENE_29;

    private String stemmerName;

    private Book m_book;

    private static Map<String, String> languageCodeToStemmerLanguageNameMap = new HashMap<String, String>();
    private static HashMap<String, Set<?>> defaultStopWordMap = new HashMap<String, Set<?>>();

    static
    {
        languageCodeToStemmerLanguageNameMap.put("da", "Danish");
        languageCodeToStemmerLanguageNameMap.put("nl", "Dutch");
        languageCodeToStemmerLanguageNameMap.put("en", "English");
        languageCodeToStemmerLanguageNameMap.put("fi", "Finnish");
        languageCodeToStemmerLanguageNameMap.put("fr", "French");
        languageCodeToStemmerLanguageNameMap.put("de", "German");
        languageCodeToStemmerLanguageNameMap.put("it", "Italian");
        languageCodeToStemmerLanguageNameMap.put("no", "Norwegian");
        languageCodeToStemmerLanguageNameMap.put("pt", "Portuguese");
        languageCodeToStemmerLanguageNameMap.put("ru", "Russian");
        languageCodeToStemmerLanguageNameMap.put("es", "Spanish");
        languageCodeToStemmerLanguageNameMap.put("sv", "Swedish");
    }

    static
    {
        defaultStopWordMap.put("fr", FrenchAnalyzer.getDefaultStopSet());
        defaultStopWordMap.put("de", GermanAnalyzer.getDefaultStopSet());
        defaultStopWordMap.put("nl", DutchAnalyzer.getDefaultStopSet());
        defaultStopWordMap.put("en", StopAnalyzer.ENGLISH_STOP_WORDS_SET);
    }

    public final TokenStream tokenStream(String fieldName, Reader reader)
    {
        StopFilter stopFilter = null;
        SnowballFilter snowballFilter = null;
        LowerCaseTokenizer lowerCaseTokenizer = new LowerCaseTokenizer(reader);

        if (doStopWords && (stopSet != null))
        {
            stopFilter = new StopFilter(false, (TokenStream) lowerCaseTokenizer, this.stopSet);
        }

        if (doStemming)
        {
            snowballFilter = new SnowballFilter((TokenStream) stopFilter, this.stemmerName);
        }
        return (TokenStream) snowballFilter;
    }

    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException
    {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();
        if (streams == null)
        {
            streams = new SavedStreams((Tokenizer) new LowerCaseTokenizer(reader));

            if (doStopWords && (stopSet != null))
            {
                streams.setResult((TokenStream) new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(this.matchVersion), streams.getResult(), this.stopSet));
            }

            if (doStemming)
            {
                streams.setResult((TokenStream) new SnowballFilter(streams.getResult(), this.stemmerName));
            }
            setPreviousTokenStream(streams);
        }
        else
        {
            streams.getSource().reset(reader);
        }
        return streams.getResult();
    }

    public void setBook(Book newBook)
    {
        m_book = newBook;
        stemmerName = null;
        if (m_book != null)    { pickStemmer(m_book.getLanguage().getCode()); }
    }

    public void pickStemmer(String languageCode)
    {
        if (languageCode != null)
        {
            if (languageCodeToStemmerLanguageNameMap.containsKey(languageCode))
            {
                this.stemmerName = languageCodeToStemmerLanguageNameMap.get(languageCode);
            }
            else
            {
                throw new IllegalArgumentException("SnowballAnalyzer configured for unavailable stemmer " + this.stemmerName);
            }
            if (defaultStopWordMap.containsKey(languageCode))
            {
                stopSet = defaultStopWordMap.get(languageCode);
            }
        }
    }
}
