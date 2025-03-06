package org.crosswire.jsword.versification.system;
/** @defgroup org.crosswire.jsword.versification */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.versification.Versification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @ingroup org.crosswire.jsword
 * @brief Object Definition of Versifications
 */
public class Versifications 
{
    public static final String DEFAULT_V11N = "KJV";
    private static final String THISMODULE = "org.crosswire.jsword.versification.system.Versifications";

    private static final Versifications instance = new Versifications();

    private static final Logger lgr = LoggerFactory.getLogger(Versifications.class);

    public static Versifications instance()   { return instance; }

    private Set<String> known;
    private Map<String, Versification> fluffmap;

    /**
     * @brief Create a set of known bibles
     */
    private Versifications()
    {
        known = new HashSet<>();
        known.add("Calvin");
        known.add("Catholic");
        known.add("Catholic2");
        known.add("DarbyFR");
        known.add("German");
        known.add("KJV");
        known.add("KJVA");
        known.add("Leningrad");
        known.add("Luther");
        known.add("LXX");
        known.add("MT");
        known.add("NRSV");
        known.add("NRSVA");
        known.add("Orthodox");
        known.add("Segond");
        known.add("Synodal");
        known.add("SynodalProt");
        known.add("Vulg");
        fluffmap = new HashMap<>();
    }

    @SuppressWarnings("UnusedAssignment")
    public synchronized Versification getVersification(String name) throws Exception 
    {
        String msg;
        String actual;
        Versification ver;
        
        msg = "";
        ver = null;
        actual = "KJV";

        if ( name == null ) 
        {
            actual = "KJV"; 
        }
        else
        {
          actual = name;
        }

        try
        {
            ver = fluffmap.get(actual);
        }
        catch ( Exception e )
        {
            msg = "Error geting versification " + actual + "from map"
                       + "\n    error=" + e.getMessage();
            lgr.error(msg,THISMODULE);
            throw e;
        }

        if ( ver == null )
        {
            try
            {
                ver = fluff(actual);
                if ( ver != null )  { fluffmap.put(actual, ver); }
            }
            catch ( Exception e )
            {
                msg = "Error putting versification " + ver
                           + "\n    error=" + e.getMessage();
                lgr.error(msg,THISMODULE);
                throw e;
            }
        }
        return ver;
    }

    public synchronized boolean isDefined(String name)
    {
        return (name == null || known.contains(name));
    }

    public synchronized void register(Versification ver)
    {
        try
        {
            fluffmap.put(ver.getName(), ver);
            known.add(ver.getName());
        }
        catch ( Exception e )
        {
            String msg = "Error putting versification - " + e.getMessage()
                       + "\n    cause=" + e.getCause();
            lgr.error(msg,THISMODULE);
        }
    }

    public Iterator<String> iterator()   { return known.iterator(); }

    public int size()                    { return known.size(); }

    private Versification fluff(String name)
    {
        if ( name == null ) { return new SystemKJV(); }

        return switch(name)
        {
        case "KJV" -> new SystemKJV();
        case "Calvin" -> new SystemCalvin();
        case "Catholic" -> new SystemCatholic();
        case "Catholic2" -> new SystemCatholic2();
        case "DarbyFR" -> new SystemDarbyFR();
        case "German" -> new SystemGerman();
        case "KJVA" -> new SystemKJVA();
        case "Leningrad" -> new SystemLeningrad();
        case "Luther" -> new SystemLuther();
        case "LXX" -> new SystemLXX();
        case "MT" -> new SystemMT();
        case "NRSV" -> new SystemNRSV();
        case "NRSVA" -> new SystemNRSVA();
        case "Orthodox" -> new SystemOrthodox();
        case "Segond" -> new SystemSegond();
        case "Synodal" -> new SystemSynodal();
        case "SynodalProt" -> new SystemSynodalProt();
        case "Vulg" -> new SystemVulg();
        default -> new SystemKJV();
        };
    }
}