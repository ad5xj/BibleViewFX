package org.crosswire.jsword.versification.system;

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

/**
 * @ingroup org.crosswire.jsword.versification.system
 * 
 * @brief Gets system default values for versification
 * @extends Versification
 */
public class SystemDefault extends Versification
{
    public static final String V11N_NAME = "";

    public static final BibleBook[] BOOKS_NONE = new BibleBook[0];

    public static final int[][] LAST_VERSE_NONE = new int[0][];

    /**
     * @brief Define defaults for Old Testament
     */
    public static final BibleBook[] BOOKS_OT = new BibleBook[]
    {
        BibleBook.GEN, BibleBook.EXOD, BibleBook.LEV, BibleBook.NUM, BibleBook.DEUT, BibleBook.JOSH, BibleBook.JUDG, BibleBook.RUTH, BibleBook.SAM1, BibleBook.SAM2,
        BibleBook.KGS1, BibleBook.KGS2, BibleBook.CHR1, BibleBook.CHR2, BibleBook.EZRA, BibleBook.NEH, BibleBook.ESTH, BibleBook.JOB, BibleBook.PS, BibleBook.PROV,
        BibleBook.ECCL, BibleBook.SONG, BibleBook.ISA, BibleBook.JER, BibleBook.LAM, BibleBook.EZEK, BibleBook.DAN, BibleBook.HOS, BibleBook.JOEL, BibleBook.AMOS,
        BibleBook.OBAD, BibleBook.JONAH, BibleBook.MIC, BibleBook.NAH, BibleBook.HAB, BibleBook.ZEPH, BibleBook.HAG, BibleBook.ZECH, BibleBook.MAL
    };

    /**
     * @brief Define defaults for New Testament
     */
    public static final BibleBook[] BOOKS_NT = new BibleBook[]
    {
        BibleBook.MATT, BibleBook.MARK, BibleBook.LUKE, BibleBook.JOHN, BibleBook.ACTS, BibleBook.ROM, BibleBook.COR1, BibleBook.COR2, BibleBook.GAL, BibleBook.EPH,
        BibleBook.PHIL, BibleBook.COL, BibleBook.THESS1, BibleBook.THESS2, BibleBook.TIM1, BibleBook.TIM2, BibleBook.TITUS, BibleBook.PHLM, BibleBook.HEB, BibleBook.JAS,
        BibleBook.PET1, BibleBook.PET2, BibleBook.JOHN1, BibleBook.JOHN2, BibleBook.JOHN3, BibleBook.JUDE, BibleBook.REV
    };


    private static final long serialVersionUID = -921273257871599555L;
    
    public SystemDefault(String name, BibleBook[] booksOT, BibleBook[] booksNT, int[][] lastVerseOT, int[][] lastVerseNT)
    {
        super(name, booksOT, booksNT, lastVerseOT, lastVerseNT);
    }
}