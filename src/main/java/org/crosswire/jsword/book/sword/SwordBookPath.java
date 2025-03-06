package org.crosswire.jsword.book.sword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.OSType;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.StringUtil;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

import java.util.ArrayList;
import java.util.List;

public final class SwordBookPath 
{
    private static boolean debug_log = true;
    
    private static final String DIR_WINDOWS_DEFAULT = "C:\\Program Files\\CrossWire\\The SWORD Project";
    private static final String DIR_SWORD_LIBRARY = "library";
    private static final String DIR_SWORD_CONF = ".sword";
    private static final String DIR_SWORD_CONF_ALT = "Sword";
    private static final String SWORD_GLOBAL_CONF = "sword.conf";
    private static final String DIR_SWORD_GLOBAL_CONF = "/etc:/usr/local/etc";
    private static final String DATA_PATH = "DataPath";
    private static final String AUGMENT_PATH = "AugmentPath";
    private static final String PROPERTY_SWORD_HOME = "sword.home";
    private static final String PROPERTY_USER_HOME = "user.home";
    private static final String PREFIX_GLOBALS = "globals.";

    private static final Logger log = LoggerFactory.getLogger(SwordBookPath.class);

    private static File[] augmentPath = new File[0];
    private static File defaultDownloadDir = getDefaultDownloadPath();
    private static File overrideDownloadDir;

    public static void setAugmentPath(File[] theNewDirs) throws BookException
    {
        File[] newDirs;

        if (theNewDirs == null) { return; }

        newDirs = theNewDirs;
        augmentPath = newDirs.clone();

        try
        {
            Books.installed().registerDriver(SwordBookDriver.instance());
        }
        catch (Exception e)
        {
            log.error("Cannot create instance for work " + e.getMessage());
        }
    }

    public static File[] getAugmentPath()    { return augmentPath.clone(); }

    public static File[] getSwordPath()
    {
        ArrayList<File> swordPath = new ArrayList<>();
        swordPath.add(getSwordDownloadDir());

        if (augmentPath != null)
        {
            for (int i = 0; i < augmentPath.length; i++)
            {
                File path = augmentPath[i];
                if (!swordPath.contains(path)) { swordPath.add(path); }
            }
        }

        File[] defaultPath = getDefaultPaths();

        if (defaultPath != null)
        {
            for (int i = 0; i < defaultPath.length; i++)
            {
                File path = defaultPath[i];
                if (!swordPath.contains(path))
                {
                    swordPath.add(path);
                }
            }
        }

        return swordPath.<File>toArray(new File[swordPath.size()]);
    }

    public static String[] getBookList(File bookDir)  { return bookDir.list(new CustomFilenameFilter()); }

    public static File getSwordDownloadDir()
    {
        if (overrideDownloadDir != null) { return overrideDownloadDir; }
        return defaultDownloadDir;
    }

    public static File getDownloadDir() { return overrideDownloadDir; }

    public static void setDownloadDir(File dlDir)
    {
        if (!"".equals(dlDir.getPath()))
        {
            overrideDownloadDir = dlDir;
            if ( debug_log )
            {
                log.debug("Setting sword download directory to: {}", dlDir);
            }
        }
    }

    private static File[] getDefaultPaths()
    {
        migrateBookDir();

        List<File> bookDirs = new ArrayList<>();
        String home = System.getProperty("user.home");

        readSwordConf(bookDirs, ".");
        testDefaultPath(bookDirs, ".");
        testDefaultPath(bookDirs, ".." + File.separator + "library");
        String swordhome = System.getProperty("sword.home");
        if (swordhome != null)
        {
            testDefaultPath(bookDirs, swordhome);
            testDefaultPath(bookDirs, swordhome + File.separator + ".." + File.separator + "library");
        }
        if (System.getProperty("os.name").startsWith("Windows"))
        {
            testDefaultPath(bookDirs, "C:\\Program Files\\CrossWire\\The SWORD Project");
            testDefaultPath(bookDirs, "C:\\Program Files\\CrossWire\\The SWORD Project" + File.separator + ".." + File.separator + "library");
        }
        readSwordConf(bookDirs, home + File.separator + ".sword");
        String[] sysconfigPaths = StringUtil.split("/etc:/usr/local/etc", ':');
        for (int i = 0; i < sysconfigPaths.length; i++)
        {
            readSwordConf(bookDirs, sysconfigPaths[i]);
        }
        URI userDataArea = OSType.getOSType().getUserAreaFolder(".sword", "Sword");
        testDefaultPath(bookDirs, new File(userDataArea.getPath()));
        testDefaultPath(bookDirs, new File(CWProject.instance().getWritableProjectDir().getPath()));
        return bookDirs.<File>toArray(new File[bookDirs.size()]);
    }

    private static void readSwordConf(List<File> bookDirs, File swordConfDir)
    {
        File sysconfig = new File(swordConfDir, "sword.conf");
        if (sysconfig.canRead())
        {
            InputStream is = null;
            try
            {
                PropertyMap prop = new PropertyMap();
                is = new FileInputStream(sysconfig);
                prop.load(is);
                String datapath = prop.get("DataPath");
                testDefaultPath(bookDirs, datapath);
                datapath = prop.get("AugmentPath");
                testDefaultPath(bookDirs, datapath);
            }
            catch (IOException ex)
            {
                log.warn("Failed to read system config file", ex);
            }
            finally
            {
                if (is != null)
          try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    log.warn("Failed to close system config file", e);
                }
            }
        }
    }

    private static void readSwordConf(List<File> bookDirs, String swordConfDir)
    {
        readSwordConf(bookDirs, new File(swordConfDir));
    }

    private static void testDefaultPath(List<File> bookDirs, File path)
    {
        if (path == null)
        {
            return;
        }
        File mods = new File(path, "mods.d");
        if (mods.isDirectory() && mods.canRead())
        {
            bookDirs.add(path);
        }
    }

    private static void testDefaultPath(List<File> bookDirs, String path)
    {
        if (path == null)
        {
            return;
        }
        testDefaultPath(bookDirs, new File(path));
    }

    private static File getDefaultDownloadPath()
    {
        File path = null;
        File[] possiblePaths = getDefaultPaths();
        if (possiblePaths != null)
        {
            for (int i = 0; i < possiblePaths.length; i++)
            {
                File mods = new File(possiblePaths[i], "mods.d");
                if (mods.canWrite())
                {
                    path = possiblePaths[i];
                    break;
                }
            }
        }
        if (path == null)
        {
            URI userDataArea = OSType.getOSType().getUserAreaFolder(".sword", "Sword");
            path = new File(userDataArea.getPath());
        }
        return path;
    }

    private static void migrateBookDir()
    {
        URI userDataArea = OSType.getOSType().getUserAreaFolder(".sword", "Sword");
        File swordBookPath = new File(userDataArea.getPath());
        File oldPath = new File(CWProject.instance().getDeprecatedWritableProjectDir().getPath());
        if (oldPath.isDirectory())
        {
            migrateBookDir(oldPath, swordBookPath);
            return;
        }
        oldPath = new File(CWProject.instance().getWritableProjectDir().getPath());
        if (oldPath.isDirectory())
        {
            migrateBookDir(oldPath, swordBookPath);
            return;
        }
        oldPath = new File(OSType.DEFAULT.getUserAreaFolder(".sword", "Sword").getPath());
        if (oldPath.isDirectory())
        {
            migrateBookDir(oldPath, swordBookPath);
        }
    }

    private static void migrateBookDir(File oldPath, File newPath)
    {
        File oldDataDir = new File(oldPath, "modules");
        File newDataDir = new File(newPath, "modules");
        File oldConfDir = new File(oldPath, "mods.d");
        File newConfDir = new File(newPath, "mods.d");
        if (!migrate(oldDataDir, newDataDir))
        {
            return;
        }
        if (!migrate(oldConfDir, newConfDir))
        {
            migrate(newDataDir, oldDataDir);
        }
    }

    private static boolean migrate(File oldPath, File newPath)
    {
        if (oldPath.equals(newPath) || !oldPath.exists())
        {
            return true;
        }
        File parent = newPath.getParentFile();
        if (!parent.exists() && !parent.mkdirs())
        {
            return false;
        }
        return oldPath.renameTo(newPath);
    }

    static class CustomFilenameFilter implements FilenameFilter
    {

        public boolean accept(File parent, String name)
        {
            return (!name.startsWith("globals.") && name.endsWith(".conf"));
        }
    }
}