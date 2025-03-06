package org.crosswire.jsword.versification;
/** @ingroup org.crosswire.jsword */

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum BibleBook
{
    INTRO_BIBLE("Intro.Bible"),
    INTRO_OT("Intro.OT"),
    GEN("Gen"),
    EXOD("Exod"),
    LEV("Lev"),
    NUM("Num"),
    DEUT("Deut"),
    JOSH("Josh"),
    JUDG("Judg"),
    RUTH("Ruth"),
    SAM1("1Sam"),
    SAM2("2Sam"),
    KGS1("1Kgs"),
    KGS2("2Kgs"),
    CHR1("1Chr"),
    CHR2("2Chr"),
    EZRA("Ezra"),
    NEH("Neh"),
    ESTH("Esth"),
    JOB("Job"),
    PS("Ps"),
    PROV("Prov"),
    ECCL("Eccl"),
    SONG("Song"),
    ISA("Isa"),
    JER("Jer"),
    LAM("Lam"),
    EZEK("Ezek"),
    DAN("Dan"),
    HOS("Hos"),
    JOEL("Joel"),
    AMOS("Amos"),
    OBAD("Obad", true),
    JONAH("Jonah"),
    MIC("Mic"),
    NAH("Nah"),
    HAB("Hab"),
    ZEPH("Zeph"),
    HAG("Hag"),
    ZECH("Zech"),
    MAL("Mal"),
    INTRO_NT("Intro.NT"),
    MATT("Matt"),
    MARK("Mark"),
    LUKE("Luke"),
    JOHN("John"),
    ACTS("Acts"),
    ROM("Rom"),
    COR1("1Cor"),
    COR2("2Cor"),
    GAL("Gal"),
    EPH("Eph"),
    PHIL("Phil"),
    COL("Col"),
    THESS1("1Thess"),
    THESS2("2Thess"),
    TIM1("1Tim"),
    TIM2("2Tim"),
    TITUS("Titus"),
    PHLM("Phlm", true),
    HEB("Heb"),
    JAS("Jas"),
    PET1("1Pet"),
    PET2("2Pet"),
    JOHN1("1John"),
    JOHN2("2John", true),
    JOHN3("3John", true),
    JUDE("Jude", true),
    REV("Rev"),
    TOB("Tob"),
    JDT("Jdt"),
    ADD_ESTH("AddEsth"),
    WIS("Wis"),
    SIR("Sir"),
    BAR("Bar"),
    EP_JER("EpJer", true),
    PR_AZAR("PrAzar"),
    SUS("Sus"),
    BEL("Bel"),
    MACC1("1Macc"),
    MACC2("2Macc"),
    MACC3("3Macc"),
    MACC4("4Macc"),
    PR_MAN("PrMan", true),
    ESD1("1Esd"),
    ESD2("2Esd"),
    PSS151("Ps151"),
    ODES("Odes"),
    PSALM_SOL("PssSol"),
    EP_LAO("EpLao"),
    ESD3("3Esd"),
    ESD4("4Esd"),
    ESD5("5Esd"),
    EN1("1En"),
    JUBS("Jub"),
    BAR4("4Bar"),
    ASCEN_ISA("AscenIsa"),
    PS_JOS("PsJos"),
    APOSTOLIC("AposCon"),
    CLEM1("1Clem"),
    CLEM2("2Clem"),
    COR3("3Cor"),
    EP_COR_PAUL("EpCorPaul"),
    JOS_ASEN("JosAsen"),
    T12PATR("T12Patr"),
    T12PATR_TASH("T12Patr.TAsh"),
    T12PATR_TBENJ("T12Patr.TBenj"),
    T12PATR_TDAN("T12Patr.TDan"),
    T12PATR_GAD("T12Patr.TGad"),
    T12PATR_TISS("T12Patr.TIss"),
    T12PATR_TJOS("T12Patr.TJos"),
    T12PATR_TJUD("T12Patr.TJud"),
    T12PATR_TLEVI("T12Patr.TLevi"),
    T12PATR_TNAPH("T12Patr.TNaph"),
    T12PATR_TREU("T12Patr.TReu"),
    T12PATR_TSIM("T12Patr.TSim"),
    T12PATR_TZeb("T12Patr.TZeb"),
    BAR2("2Bar"),
    EP_BAR("EpBar"),
    BARN("Barn"),
    HERM("Herm"),
    HERM_MAND("Herm.Mand"),
    HERM_SIM("Herm.Sim"),
    HERM_VIS("Herm.Vis"),
    ADD_DAN("AddDan"),
    ADD_PS("AddPs"),
    ESTH_GR("EsthGr");

    private String osis;

    private boolean isShortBook;

    private static Map<String, BibleBook> osisMap;

    private static Map<String, BibleBook> exactMatches;

    BibleBook(String osis, boolean shortBook)    { isShortBook = shortBook; }

    BibleBook(String i_osis)                     { osis = i_osis; }

    public boolean isShortBook()                 { return isShortBook; }

    public String getOSIS()                      { return osis; }
    @Override
    public String toString()                     { return osis; }

    public static BibleBook fromExactOSIS(String osis) { return exactMatches.get(osis); }
    public static BibleBook fromOSIS(String osis)
    {
        String match = BookName.normalize(osis, Locale.ENGLISH);
        return osisMap.get(match);
    }

    static
    {
        osisMap = new HashMap<>(128);
        exactMatches = new HashMap<>(128);
        for (BibleBook book : values())
        {
            osisMap.put(BookName.normalize(book.getOSIS(), Locale.ENGLISH), book);
            exactMatches.put(book.getOSIS(), book);
        }
    }
}
