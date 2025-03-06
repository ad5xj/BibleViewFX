package org.crosswire.jsword.versification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.config.ConfigException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.RangedPassage;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseKey;
import org.crosswire.jsword.versification.system.Versifications;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * 
 * @brief Utility class to help Versification functions
 */
public class VersificationsMapper 
{

    private static volatile VersificationsMapper instance;
    private static final String THISMODULE = "org.crosswire.jsword.versification.VersificationMapper";
    private static final Map<Versification, VersificationToKJVMapper> MAPPERS = new HashMap<Versification, VersificationToKJVMapper>();
    private static final Logger LOGGER = LoggerFactory.getLogger(VersificationsMapper.class);

    private static Versification KJV;

    public static VersificationsMapper instance() 
    {
        if (instance == null)
        {
            synchronized (VersificationsMapper.class) 
            {
                  if (instance == null) { instance = new VersificationsMapper(); }
            }
        }
        return instance;
    }
    /**
     * @brief Default constructor with no params
     */
    private VersificationsMapper() 
    {
        try
        {
            KJV = Versifications.instance().getVersification("KJV");
        }
        catch ( Exception e )
        {
            String msg = "Failed to create versification maper error=" + e.getMessage();
            LOGGER.error(msg, THISMODULE);
        }
        MAPPERS.put(KJV, null); 
    }

    public Passage map(Passage key, Versification target) 
    {
        if (key.getVersification().equals(target)) { return key; }
        
        RangedPassage rangedPassage = new RangedPassage(target);
        Iterator<Key> verses = key.iterator();
        while (verses.hasNext()) 
        {
            Verse verse = (Verse) verses.next();
            rangedPassage.addAll(mapVerse(verse, target));
        }
        return rangedPassage;
    }

    public VerseKey mapVerse(Verse v, Versification targetVersification) 
    {
        List<QualifiedKey> kjvVerses;

        if (v.getVersification().equals(targetVersification)) { return v; }
        ensure(v.getVersification());
        ensure(targetVersification);
        VersificationToKJVMapper mapper = MAPPERS.get(v.getVersification());
        if (mapper == null) 
        {
            kjvVerses = new ArrayList<QualifiedKey>();
            Verse reversifiedVerse = v.reversify(KJV);
            if (reversifiedVerse != null) 
            {
                kjvVerses.add(new QualifiedKey(reversifiedVerse));
            }
        } 
        else 
        {
            kjvVerses = mapper.map(new QualifiedKey(v));
        }
        if (KJV.equals(targetVersification)) 
        {
            return getKeyFromQualifiedKeys(KJV, kjvVerses);
        }
        VersificationToKJVMapper targetMapper = MAPPERS.get(targetVersification);
        if (targetMapper == null) 
        {
            return guessKeyFromKjvVerses(targetVersification, kjvVerses);
        }
        RangedPassage rangedPassage = new RangedPassage(targetVersification);
        for (QualifiedKey qualifiedKey : kjvVerses) 
        {
            VerseKey verseKey = targetMapper.unmap(qualifiedKey);
            if (verseKey != null) { rangedPassage.addAll(verseKey); }
        }
        return (VerseKey) rangedPassage;
    }
    public void ensureMappingDataLoaded(Versification ver) { ensure(ver); }

    private VerseKey guessKeyFromKjvVerses(Versification targetVersification, List<QualifiedKey> kjvVerses) 
    {
        RangedPassage rangedPassage = new RangedPassage(targetVersification);
        for (QualifiedKey qualifiedKey : kjvVerses) 
        {
            if (qualifiedKey.getKey() != null) 
            {
                VerseKey key = qualifiedKey.reversify(targetVersification).getKey();
                if (key != null) { rangedPassage.addAll((Key) key); }
            }
        }
        return rangedPassage;
    }

    private VerseKey getKeyFromQualifiedKeys(Versification versification, List<QualifiedKey> kjvVerses) 
    {
        RangedPassage rangedPassage = new RangedPassage(versification);
        for (QualifiedKey k : kjvVerses) 
        {
            if (k.getKey() != null) { rangedPassage.addAll((Key) k.getKey()); }
        }
        return (VerseKey) rangedPassage;
    }


    private void ensure(Versification versification) 
    {
        if (MAPPERS.containsKey(versification)) { return; }

        try 
        {
            MAPPERS.put(versification, new VersificationToKJVMapper(versification, new FileVersificationMapping(versification)));
        } 
        catch (IOException | ConfigException | MissingResourceException e) 
        {
            String msg = "Failed to load versification mappings for versification [" + versification
                       + "]\n    error=" + e.getMessage();
            LOGGER.error(msg, THISMODULE);
            MAPPERS.put(versification, null);
        }
    }
}
