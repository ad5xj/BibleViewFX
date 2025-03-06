package org.crosswire.jsword.book;

import org.crosswire.common.util.CWProject;

import java.io.File;

import java.net.URI;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief Enum definition for book module
 */
public enum MetaDataLocator 
{
    TRANSIENT 
        {
        public File getReadLocation()  { return null; }
        public File getWriteLocation() { return null; }
    },
    JSWORD 
    {
        public File getReadLocation() 
        {
            URI[] dirs = CWProject.instance().getProjectResourceDirs();
            if (dirs.length > 1) { return getFile(dirs[1]); }
            return null;
        }

        public File getWriteLocation() 
        {
            URI[] dirs = CWProject.instance().getProjectResourceDirs();
            if (dirs.length > 0) { return getFile(dirs[0]); }
            return null;
        }
    },
    FRONTEND 
    {
        public File getReadLocation() 
        {
            return getFile(CWProject.instance().getReadableFrontendProjectDir());
        }

        public File getWriteLocation() 
        {
            return getFile(CWProject.instance().getWritableFrontendProjectDir());
        }
    };

    private static final String DIR_CONF_OVERRIDE = "jsword-mods.d";

    protected static File getFile(URI u) 
    {
        if ( u == null ) { return null; }

        File parent = new File(u);
        File override = new File(parent, "jsword-mods.d");
        override.mkdirs();
        return override;
    }

    public abstract File getReadLocation();

    public abstract File getWriteLocation();
}
