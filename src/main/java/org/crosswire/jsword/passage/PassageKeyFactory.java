package org.crosswire.jsword.passage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

import java.io.IOException;
import java.io.Reader;

import java.util.Iterator;

/**
 * @ingroup org.crosswire.jsword.passage
 * 
 * @brief Definition of factory for PassageKey
 */
public final class PassageKeyFactory 
{
    public static PassageKeyFactory instance() { return keyf; }

    private static PassageType defaultType = PassageType.SPEED;
    private static PassageKeyFactory keyf = new PassageKeyFactory();

    private static final String THISMODULE = "org.crosswire.jsword.passage.PassageKeyFactory";
    private static final Logger lgr = LoggerFactory.getLogger(PassageKeyFactory.class);

    public Key createEmptyKeyList(Versification v11n)
    {
        return defaultType.createEmptyPassage(v11n);
    }

    public Key getGlobalKeyList(Versification v11n)
    {
        return new ReadOnlyPassage(KeyUtil.getPassage(v11n.getAllVerses()), true);
    }

    public Key getValidKey(Versification v11n, String passageReference, Key basis)
    {
        try
        {
            return getKey(v11n, passageReference, basis);
        }
        catch (NoSuchKeyException e)
        {
            return createEmptyKeyList(v11n);
        }
    }

    public Key getValidKey(Versification v11n, String passageReference)
    {
        return getValidKey(v11n, passageReference, null);
    }

    public Passage getKey(Versification v11n, String passageReference, Key basis) throws NoSuchKeyException
    {
        try
        {
            return defaultType.createPassage(v11n, passageReference, basis);
        }
        catch (NoSuchKeyException e)
        {
            try
            {
                return defaultType.createPassage(v11n, normalize(passageReference), basis);
            }
            catch (NoSuchKeyException ex)
            {
                return defaultType.createPassage(v11n, mungOsisRef(passageReference), basis);
            }
        }
    }

    public Passage getKey(Versification v11n, String passageReference) throws NoSuchKeyException
    {
        return getKey(v11n, passageReference, null);
    }

    public static void setDefaultType(PassageType newDefaultType)
    {
        defaultType = newDefaultType;
    }

    public static PassageType getDefaultType()
    {
        return defaultType;
    }

    public static void setDefaultPassage(int newDefaultType)
    {
        setDefaultType(PassageType.fromInteger(newDefaultType));
    }

    public static int getDefaultPassage()
    {
        return PassageType.toInteger(defaultType);
    }

    public static Passage getSynchronizedPassage(Passage ref)
    {
        return new SynchronizedPassage(ref);
    }

    public static Passage getReadOnlyPassage(Passage ref, boolean ignore)
    {
        return new ReadOnlyPassage(ref, ignore);
    }

    static byte[] toBinaryRepresentation(Passage ref)
    {
        Versification v11n = ref.getVersification();
        int maxOrdinal = v11n.maximumOrdinal();
        int verses = ref.countVerses();
        int ranges = ref.countRanges(RestrictionType.NONE);
        int bitwiseSize = maxOrdinal / 8;
        int rangedSize = ranges * 4 + 1;
        int distinctSize = verses * 2 + 1;
        if (bitwiseSize <= rangedSize && bitwiseSize <= distinctSize)
        {
            int i = binarySize(3) + maxOrdinal / 8 + 1;
            byte[] arrayOfByte = new byte[i];
            int j = 0;
            j += toBinary(arrayOfByte, j, 0, 3);
            for (Key aKey : ref)
            {
                Verse verse = (Verse) aKey;
                int ord = verse.getOrdinal();
                int idx0 = ord / 8 + j;
                int bit = ord % 8 - 1;
                arrayOfByte[idx0] = (byte) (arrayOfByte[idx0] | 1 << bit);
            }
            return arrayOfByte;
        }
        if (distinctSize <= rangedSize)
        {
            int i = binarySize(3) + binarySize(maxOrdinal) + verses * binarySize(maxOrdinal);
            byte[] arrayOfByte = new byte[i];
            int j = 0;
            j += toBinary(arrayOfByte, j, 1, 3);
            j += toBinary(arrayOfByte, j, verses, maxOrdinal);
            for (Key aKey : ref)
            {
                Verse verse = (Verse) aKey;
                int ord = verse.getOrdinal();
                j += toBinary(arrayOfByte, j, ord, maxOrdinal);
            }
            return arrayOfByte;
        }
        int arraySize = binarySize(3) + binarySize(maxOrdinal / 2) + 2 * ranges * binarySize(maxOrdinal);
        byte[] buffer = new byte[arraySize];
        int index = 0;
        index += toBinary(buffer, index, 2, 3);
        index += toBinary(buffer, index, ranges, maxOrdinal / 2);
        Iterator<VerseRange> it = ref.rangeIterator(RestrictionType.NONE);
        while (it.hasNext())
        {
            VerseRange range = it.next();
            index += toBinary(buffer, index, range.getStart().getOrdinal(), maxOrdinal);
            index += toBinary(buffer, index, range.getCardinality(), maxOrdinal);
        }
        return buffer;
    }

    static Passage fromBinaryRepresentation(byte[] buffer) throws NoSuchKeyException
    {
        int i;
        int j;
        int k;
        int ord;
        int bit;
        int idx0;
        int len;
        int verses;
        int ranges;
        int maxOrdinal;
        int type;
        Passage ref;
        AbstractPassage aref;
        Versification ver;
        
        i = -1;
        j = -1;
        k = -1;
        ord = -1;
        bit = -1;
        len = -1;
        verses = -1;
        ranges = -1;
        type = -1;
        idx0 = -1;
        int[] index = { 0 };

        ref = null;
        aref = null;
        try
        {
            ver = Versifications.instance().getVersification("KJV");
            maxOrdinal = ver.maximumOrdinal();
            ref = (Passage) keyf.createEmptyKeyList(ver);
        }
        catch ( Exception e )
        {
            String msg = "";
            lgr.error(msg,THISMODULE);
            return null;
        }

        if ( ref instanceof AbstractPassage )
        {
            aref = (AbstractPassage) ref;
            aref.raiseEventSuppresion();
            aref.raiseNormalizeProtection();
        }
        type = fromBinary(buffer, index, 3);
        switch (type)
        {
        case 0 ->
            {
                for (ord = 1; ord <= maxOrdinal; ord++)
                {
                    idx0 = ord / 8 + index[0];
                    bit = ord % 8 - 1;
                    if ((buffer[idx0] & 1 << bit) != 0)
                    {
                        ref.add(ver.decodeOrdinal(ord));
                    }
                }
            }
        case 1 ->
            {
                verses = fromBinary(buffer, index, maxOrdinal);
                for ( i = 0; i < verses; i++)
                {
                    k = fromBinary(buffer, index, maxOrdinal);
                    try
                    {
                        ref.add(ver.decodeOrdinal(k));
                    }
                    catch ( Exception e )
                    {
                        String msg = "Error adding Passage to array - " + e.getLocalizedMessage();
                        lgr.error(msg,THISMODULE);
                    }                }
            }
        case 2 ->
            {
                ranges = fromBinary(buffer, index, maxOrdinal / 2);
                for (j = 0; j < ranges; j++)
                {
                    k = fromBinary(buffer, index, maxOrdinal);
                    len = fromBinary(buffer, index, maxOrdinal);
                    try
                    {
                        ref.add(RestrictionType.NONE.toRange(ver, ver.decodeOrdinal(k), len));
                    }
                    catch ( Exception e )
                    {
                        String msg = "Error adding Passage to array - " + e.getLocalizedMessage();
                        lgr.error(msg,THISMODULE);
                    }
                }
            }
        default -> throw new NoSuchKeyException(JSOtherMsg.lookupText("Unknown passage type.", new Object[0]));
        }
        if (aref != null)
        {
            aref.lowerEventSuppressionAndTest();
            aref.lowerNormalizeProtection();
        }
        return ref;
    }

    public static Passage readPassage(Reader in) throws IOException, NoSuchKeyException
    {
        Passage ref;
        Versification ver = null;
        
        try
        {
            ver = Versifications.instance().getVersification("KJV");
        }
        catch ( Exception e )
        {
            String msg = "Error getting versification for KJV - " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        ref = (Passage) keyf.createEmptyKeyList(ver);
        ref.readDescription(in);
        return ref;
    }

    protected static int binarySize(int max)
    {
        if (max < 256)        { return 1; }
        if (max < 65536)      { return 2; }
        if (max < 16777216)   { return 3; }
        return 4;
    }

    protected static int toBinary(byte[] buffer, int index, int number, int max)
    {
        assert number >= 0 : "No -ve output " + number;
        assert number <= max : "number " + number + " > max " + max;
        if (max < 256)
        {
            buffer[index] = (byte) number;
            return 1;
        }
        if (max < 65536)
        {
            buffer[index + 0] = (byte) (number >>> 8);
            buffer[index + 1] = (byte) (number >>> 0);
            return 2;
        }
        if (max < 16777216)
        {
            buffer[index + 0] = (byte) (number >>> 16);
            buffer[index + 1] = (byte) (number >>> 8);
            buffer[index + 2] = (byte) (number >>> 0);
            return 3;
        }
        buffer[index + 0] = (byte) (number >>> 24);
        buffer[index + 1] = (byte) (number >>> 16);
        buffer[index + 2] = (byte) (number >>> 8);
        buffer[index + 3] = (byte) (number >>> 0);
        return 4;
    }

    @SuppressWarnings("UnusedAssignment")
    protected static int fromBinary(byte[] buffer, int[] index, int max)
    {
        int b0;
        int b1;
        int b2;
        int b3;

        b0 = -1;
        b1 = -1;
        b2 = -1;
        b3 = -1;

        index[0] = index[0] + 1;
        b0 = buffer[index[0]] & 0xFF;
        if (max < 256)        {  return b0; }
        index[0] = index[0] + 1;
        b1 = buffer[index[0]] & 0xFF;
        if (max < 65536)      {  return ((b0 << 8) + (b1 << 0)); }
        index[0] = index[0] + 1;
        b2 = buffer[index[0]] & 0xFF;
        if (max < 16777216)   {  return ((b0 << 16) + (b1 << 8) + (b2 << 0)); }
        index[0] = index[0] + 1;
        b3 = buffer[index[0]] & 0xFF;
        return ((b0 << 24) + (b1 << 16) + (b2 << 8) + (b3 << 0));
    }

    private String mungOsisRef(String passageReference)
    {
        return passageReference.replace(' ', ';');
    }

    private String normalize(String passageReference)
    {
        boolean isNumber;
        boolean wasNumberOrMarker;
        boolean isEndMarker;
        boolean isNumberOrMarker;

        int i;
        int size;
        
        char curChar;
        String buf;

        isNumber = false;
        wasNumberOrMarker = false;
        isEndMarker = false;
        isNumberOrMarker = false;

        i = 0;
        size = -1;

        curChar = ' ';
        
        buf = "";

        if ( passageReference == null ) {  return null; }
        
        size = passageReference.length();
        buf = "  ";
        curChar = ' ';

        while (i < size)
        {
            curChar = passageReference.charAt(i);
            isNumber = Character.isDigit(curChar);
            isEndMarker = (curChar == '$' || (curChar == 'f' && ((i + 1 < size) ? passageReference.charAt(i + 1) : 32) == 102));
            isNumberOrMarker = (isNumber || isEndMarker);
            if (wasNumberOrMarker)
            {
                if (isNumber)
                {
                    buf += ", ";
                }
                else if (isEndMarker)
                {
                    buf += '-';
                }
                else if (Character.isLetter(curChar))
                {
                    buf += ' ';
                }
                wasNumberOrMarker = false;
            }
            if ( isNumberOrMarker )
            {
                wasNumberOrMarker = true;
                buf += curChar;
                i++;
                if ( curChar == 'f' )
                {
                    buf += 'f';
                    i++;
                }
                else if ( curChar != '$' )
                {
                    while ( i < size )
                    {
                        curChar = passageReference.charAt(i);
                        if (!Character.isDigit(curChar))    {  break;  }
                        buf += curChar;
                        i++;
                    }
                }

                while (i < size && Character.isWhitespace(passageReference.charAt(i)))
                {
                    i++;
                }
                continue;
            }
            buf += curChar;
            i++;
        }
        return buf;
    }
}