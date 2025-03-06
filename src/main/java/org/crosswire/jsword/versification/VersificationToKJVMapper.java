package org.crosswire.jsword.versification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.KeyValuePair;
import org.crosswire.common.util.LucidRuntimeException;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.OsisParser;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.RangedPassage;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseKey;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.system.Versifications;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @ingroup org.crosswire.jsword.versicfication
 * @brief Helper class for versicfication of KJV
 */
public class VersificationToKJVMapper 
{
    private static final Logger lgr = LoggerFactory.getLogger(VersificationToKJVMapper.class);
    private static final String THISMODULE = "org.crosswire.jsword.versicfication.VersificationToKJVMapper";

    private static Versification KJV;

    private boolean hasErrors;

    private Versification nonKjv;
    private Passage absentVerses;
    private OsisParser osisParser;
    private Map<VerseKey, List<QualifiedKey>> toKJVMappings;
    private Map<QualifiedKey, Passage> fromKJVMappings;

    public VersificationToKJVMapper(Versification nonKjv, FileVersificationMapping mapping) 
    {
        try
        {
            KJV = Versifications.instance().getVersification("KJV");
        }
        catch ( Exception e )
        {
            String msg = "Error in versification of KJV bible. error-" + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        osisParser = new OsisParser();
        absentVerses = createEmptyPassage(KJV);
        toKJVMappings = new HashMap<VerseKey, List<QualifiedKey>>();
        fromKJVMappings = new HashMap<QualifiedKey, Passage>();
        nonKjv = nonKjv;
        processMappings(mapping);
        trace();
    }

    private void processMappings(FileVersificationMapping mappings) 
    {
        List<KeyValuePair> entries = mappings.getMappings();
        for (KeyValuePair entry : entries) 
        {
            try 
            {
                processEntry(entry);
            } 
            catch (NoSuchKeyException ex) 
            {
                String msg = "Unable to process entry [" + entry.getKey()
                           + "] with value [" + entry.getValue() + "]"
                           + "\n    error=" + ex.getMessage();
                lgr.error(msg,THISMODULE);
                hasErrors = true;
            }
        }
    }

    private void processEntry(KeyValuePair entry) throws NoSuchKeyException 
    {
        String leftHand = entry.getKey();
        String kjvHand = entry.getValue();
        if (leftHand == null || leftHand.length() == 0) 
        {
            lgr.error("Left hand must have content");
            return;
        }
        if ("!zerosUnmapped".equals(leftHand)) {
            return;
        }
        QualifiedKey left = getRange(nonKjv, leftHand, null);
        QualifiedKey kjv = getRange(KJV, kjvHand, left.getKey());
        addMappings(left, kjv);
    }

    private void addMappings(QualifiedKey leftHand, QualifiedKey kjvVerses) throws NoSuchVerseException 
    {
        if (leftHand.getAbsentType() == QualifiedKey.Qualifier.ABSENT_IN_LEFT) 
        {
            absentVerses.addAll((Key) kjvVerses.getKey());
        } 
        else if (leftHand.getKey().getCardinality() == 1) 
        {
            add1ToManyMappings(leftHand.getVerse(), kjvVerses);
        } 
        else 
        {
            addManyToMany(leftHand, kjvVerses);
        }
    }

    private void addManyToMany(QualifiedKey leftHand, QualifiedKey kjvVerses) 
    {
        VerseKey leftKeys = leftHand.getKey();
        VerseKey kjvKeys = kjvVerses.getKey();
        Iterator<Key> leftIter = leftKeys.iterator();
        if ( (kjvKeys != null) && (kjvKeys.getCardinality() != 1) ) 
        {
            int diff = Math.abs(leftKeys.getCardinality() - kjvKeys.getCardinality());
            if (diff > 1) { reportCardinalityError(leftKeys, kjvKeys); }
            boolean skipVerse0 = (diff == 1);
            Iterator<Key> kjvIter = kjvKeys.iterator();
            while (leftIter.hasNext()) 
            {
                Verse leftVerse = (Verse) leftIter.next();
                if (!kjvIter.hasNext()) { reportCardinalityError(leftKeys, kjvKeys); }
                Verse rightVerse = (Verse) kjvIter.next();
                QualifiedKey kjvKey = new QualifiedKey(rightVerse);
                if (skipVerse0 && leftVerse.getVerse() == 0) 
                {
                    addForwardMappingFromSingleKeyToRange(leftVerse, kjvKey);
                    addKJVToMapping(kjvKey, leftVerse);
                    if (!leftIter.hasNext()) { reportCardinalityError(leftKeys, kjvKeys); }
                    leftVerse = (Verse) leftIter.next();
                }

                if (skipVerse0 && (rightVerse.getVerse() == 0) ) 
                {
                    addForwardMappingFromSingleKeyToRange(leftVerse, kjvKey);
                    addKJVToMapping(kjvKey, leftVerse);
                    if (!kjvIter.hasNext()) {
                        reportCardinalityError(leftKeys, kjvKeys);
                    }
                    rightVerse = (Verse) kjvIter.next();
                    kjvKey = new QualifiedKey(rightVerse);
                }
                addForwardMappingFromSingleKeyToRange(leftVerse, kjvKey);
                addKJVToMapping(kjvKey, leftVerse);
            }

            if (kjvIter.hasNext()) { reportCardinalityError(leftKeys, kjvKeys); }
        } 
        else 
        {
            while (leftIter.hasNext()) 
            {
                Verse leftKey = (Verse) leftIter.next();
                addForwardMappingFromSingleKeyToRange(leftKey, kjvVerses);
                addKJVToMapping(kjvVerses, leftKey);
            }
        }
    }

    private void reportCardinalityError(VerseKey leftKeys, VerseKey kjvKeys) 
    {
        throw new LucidRuntimeException(String.format("%s has a cardinality of %s whilst %s has a cardinality of %s.", new Object[]{leftKeys, Integer.toString(leftKeys.getCardinality()), kjvKeys, Integer.toString(kjvKeys.getCardinality())}));
    }

    private void addKJVToMapping(QualifiedKey kjvVerses, Verse leftKey) 
    {
        if ( leftKey != null ) 
        {
            getNonEmptyKey(fromKJVMappings, kjvVerses).addAll((Key) leftKey);
            if ( !kjvVerses.isWhole() ) 
            {
                getNonEmptyKey(fromKJVMappings, QualifiedKey.create(kjvVerses.getKey().getWhole())).addAll((Key) leftKey);
            }
        }
    }

    private void add1ToManyMappings(Verse leftHand, QualifiedKey kjvHand) throws NoSuchVerseException 
    {
        addForwardMappingFromSingleKeyToRange(leftHand, kjvHand);
        addReverse1ToManyMappings(leftHand, kjvHand);
    }

    private void addReverse1ToManyMappings(Verse leftHand, QualifiedKey kjvHand) 
    {
        Iterator<Key> kjvKeys;
        
        kjvKeys = null;

        if (kjvHand.getAbsentType() == QualifiedKey.Qualifier.ABSENT_IN_KJV || kjvHand.getKey().getCardinality() == 1) 
        {
            addKJVToMapping(kjvHand, leftHand);
        } 
        else 
        {
            kjvKeys = kjvHand.getKey().iterator();

            if (kjvKeys == null) { return; }

            while (kjvKeys.hasNext()) 
            {
                addKJVToMapping(new QualifiedKey(KeyUtil.getVerse(kjvKeys.next())), leftHand);
            }
        }
    }

    private void addForwardMappingFromSingleKeyToRange(Verse leftHand, QualifiedKey kjvHand) 
    {
        if (leftHand == null) { return; }
        getNonEmptyMappings(toKJVMappings, leftHand).add(kjvHand);
    }

    private void trace() 
    {
        if (lgr.isTraceEnabled()) 
        {
            PrintStream ps = null;
            try 
            {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ps = new PrintStream(os);
                dump(ps);
                String output = os.toString("UTF8");
                lgr.trace(output);
            } 
            catch ( UnsupportedEncodingException e ) 
            {
                String msg = "Encoding UTF8 not supported." + e.getMessage();
                lgr.error(msg,THISMODULE);
            } 
            finally 
            {
                IOUtil.close(ps);
            }
        }
    }

    public void dump(PrintStream out) 
    {
        String nonKjvName = nonKjv.getName();
        out.println("##############################");
        out.print(String.format("Mapping between KJV and %s", new Object[]{nonKjvName}));
        out.println("##############################");
        out.println("    ******************************");
        out.println("    Forward mappings towards KJV");
        out.println("    ******************************");
        for (Map.Entry<VerseKey, List<QualifiedKey>> entry : toKJVMappings.entrySet()) {
            List<QualifiedKey> kjvVerses = entry.getValue();
            String osisRef = ((VerseKey) entry.getKey()).getOsisRef();
            for (QualifiedKey q : kjvVerses) {
                out.println(String.format("\t(%s) %s => (KJV) %s", new Object[]{nonKjvName, osisRef, q.toString()}));
            }
        }
        out.println("    ******************************");
        out.println("    Absent verses in left versification:");
        out.println(String.format("\t[%s]", new Object[]{absentVerses.getOsisRef()}));
        out.println("    ******************************");
        out.println("    Backwards mappings from KJV");
        out.println("    ******************************");
        for (Map.Entry<QualifiedKey, Passage> entry : fromKJVMappings.entrySet()) {
            out.println(String.format("\t(KJV): %s => (%s) %s", new Object[]{((QualifiedKey) entry.getKey()).toString(), nonKjvName, ((Passage) entry.getValue()).getOsisRef()}));
        }
    }

    private boolean hasErrors() { return hasErrors; }

    private VerseKey getNonEmptyKey(Map<QualifiedKey, Passage> mappings, QualifiedKey key) 
    {
        Passage matchingVerses = mappings.get(key);
        if (matchingVerses == null) 
        {
            matchingVerses = createEmptyPassage(nonKjv);
            mappings.put(key, matchingVerses);
        }
        return (VerseKey) matchingVerses;
    }

    private <T, S> List<S> getNonEmptyMappings(Map<T, List<S>> mappings, T key) 
    {
        List<S> matchingVerses = mappings.get(key);
        if (matchingVerses == null) 
        {
            matchingVerses = new ArrayList<S>();
            mappings.put(key, matchingVerses);
        }
        return matchingVerses;
    }

    private QualifiedKey getRange(Versification versification, String versesKey, VerseKey offsetBasis) throws NoSuchKeyException 
    {
        char firstChar;
        
        if (versesKey == null || versesKey.length() == 0) 
        {
            String msg = "Cannot understand [" + versesKey + "] as a chapter or verse.";
            lgr.error(msg,THISMODULE);
            throw new NoSuchKeyException(JSMsg.gettext("Cannot understand [{0}] as a chapter or verse.", new Object[]{versesKey}));
        }
        firstChar = versesKey.charAt(0);
        switch (firstChar) 
        {
            case '?' ->
            {
                return getAbsentQualifiedKey(versification, versesKey);
            }
            case '+', '-' ->
            {
                return getOffsetQualifiedKey(versification, versesKey, offsetBasis);
            }
        }
        return getExistingQualifiedKey(versification, versesKey);
    }

    private QualifiedKey getOffsetQualifiedKey(Versification versification, String versesKey, VerseKey offsetBasis) 
        throws NoSuchKeyException 
    {
        int offset;
        VerseRange vr;
        Verse start;
        Verse end;
        Verse vrStart;

        
        offset = -1;
        vr = null;
        start = null;
        vrStart = null;
        end = null;

        if (offsetBasis == null || offsetBasis.getCardinality() == 0) 
        {
            String msg = "Unable to offset the given key [" + offsetBasis + "]";
            lgr.error(msg,THISMODULE);
            throw new NoSuchKeyException(JSMsg.gettext("Unable to offset the given key [{0}]", new Object[]{offsetBasis}));
        }
        
        offset = Integer.parseInt(versesKey.substring(1));

        if (offsetBasis instanceof VerseRange) 
        {
            vr = (VerseRange) offsetBasis;
        } 
        else if (offsetBasis instanceof Passage) 
        {
            Iterator<VerseRange> iter = ((Passage) offsetBasis).rangeIterator(RestrictionType.NONE);
            if (iter.hasNext()) { vr = iter.next(); }
        }
        if ( vr == null ) 
        {
            throw new NoSuchKeyException(JSMsg.gettext("Unable to offset the given key [{0}]", new Object[]{offsetBasis}));
        }
        vrStart = vr.getStart();
        start = vrStart.reversify(versification);
        if (offset < 0) 
        {
            start = versification.subtract(start, -offset);
        } 
        else if (offset > 0) 
        {
            start = versification.add(start, offset);
        }
        end = start;
        
        if (vr.getCardinality() > 1) 
        {
            end = versification.add(start, vr.getCardinality() - 1);
        }
        
        if ( (start == null) || (end == null) ) 
        {
            String msg = "Verse range with offset did not map to correct range"
                       + "in target versification. This mapping will be set to "
                       + "an empty unmapped key.";
            lgr.error(msg,THISMODULE);
            hasErrors = true;
        }
        return ((start != null && end != null) ? new QualifiedKey(new VerseRange(versification, start, end)) : new QualifiedKey(versesKey));
    }

    private QualifiedKey getExistingQualifiedKey(Versification versification, String versesKey) 
    {
        return new QualifiedKey(osisParser.parseOsisRef(versification, versesKey));
    }

    private QualifiedKey getAbsentQualifiedKey(Versification versification, String versesKey) 
    {
        if (versification.equals(nonKjv)) 
        {
            return new QualifiedKey();
        }
        return new QualifiedKey(versesKey);
    }

    private List<QualifiedKey> getQualifiedKeys(Key leftKey) { return toKJVMappings.get(leftKey); }

    public List<QualifiedKey> map(QualifiedKey qualifiedKey) 
    {
        VerseKey key = qualifiedKey.getKey();
        if (key instanceof Verse) 
        {
            List<QualifiedKey> kjvKeys = getQualifiedKeys((Key) key);
            if (kjvKeys == null || kjvKeys.size() == 0) {
                kjvKeys = new ArrayList<QualifiedKey>();
                kjvKeys.add(qualifiedKey.reversify(KJV));
                return kjvKeys;
            }
            return kjvKeys;
        }
        return new ArrayList<QualifiedKey>();
    }

    public VerseKey unmap(QualifiedKey kjvVerse) 
    {
        Passage left = fromKJVMappings.get(kjvVerse);
        if ( (left == null) && !kjvVerse.isWhole() ) 
        {
            left = fromKJVMappings.get(new QualifiedKey(kjvVerse.getVerse().getWhole()));
        }

        if (left == null) 
        {
            VerseKey vk = kjvVerse.getKey();
            if ( (vk != null) && absentVerses.contains(vk)) 
            {
                return (VerseKey) createEmptyPassage(KJV);
            }
            return kjvVerse.reversify(nonKjv).getKey();
        }
        return left;
    }

    private Passage createEmptyPassage(Versification ver) { return new RangedPassage(ver); }

}
