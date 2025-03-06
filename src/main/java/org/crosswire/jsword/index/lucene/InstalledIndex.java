package org.crosswire.jsword.index.lucene;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.jsword.book.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InstalledIndex {

    public static final String INSTALLED_INDEX_DEFAULT_VERSION = "Installed.Index.DefaultVersion";
    public static final String PREFIX_INSTALLED_INDEX_VERSION_BOOK_OVERRIDE = "Installed.Index.Version.Book.";
    public static final float DEFAULT_INSTALLED_INDEX_VERSION = 1.2F;

    private static final String THISMODULE = "InstalledIndex";
    private static final Logger lgr = LoggerFactory.getLogger(InstalledIndex.class);

    private static String metadataFileComment = "Search index properties that stay persistent on clients computer. Used during index upgrades.\nContains Default index version, used for all searchable books, if book specific over-ride is not found.\nJSword adds a Book specific installed index version over-ride property, after an index creation. ";
    private static InstalledIndex myInstance = new InstalledIndex();

    private PropertyMap props;

    private Object writeLock;

    public static InstalledIndex instance() { return myInstance; }

    public float getInstalledIndexDefaultVersion()
    {
        float toReturn = 1.2F;
        String value = this.props.get("Installed.Index.DefaultVersion");
        if (value != null)     { toReturn = Float.parseFloat(value); }
        return toReturn;
    }

    public float getInstalledIndexVersion(Book b)
    {
        if (b == null)         { return getInstalledIndexDefaultVersion(); }
        String value = this.props.get("Installed.Index.Version.Book." + IndexMetadata.getBookIdentifierPropSuffix(b.getBookMetaData()), this.props.get("Installed.Index.DefaultVersion"));
        if (value == null)     { return 1.2F; }
        return Float.parseFloat(value);
    }

    public void storeLatestVersionAsInstalledIndexMetadata(Book b) throws IOException
    {
        synchronized (this.writeLock)
        {
            this.props.put("Installed.Index.Version.Book." + IndexMetadata.getBookIdentifierPropSuffix(b.getBookMetaData()), String.valueOf(IndexMetadata.instance().getLatestIndexVersion(b)));
            NetUtil.storeProperties(this.props, getPropertyFileURI(), metadataFileComment);
        }
    }

    public URI getPropertyFileURI()
    {
        return CWProject.instance().getWritableURI("lucene/" + getClass().getName(), ".properties");
    }

    protected void storeInstalledIndexMetadata() throws IOException
    {
        synchronized (this.writeLock)
        {
            NetUtil.storeProperties(this.props, getPropertyFileURI(), metadataFileComment);
        }
    }

    private InstalledIndex()
    {
        this.writeLock = new Object();
        this.props = new PropertyMap();
        URI propURI = getPropertyFileURI();
        try
        {
            if (NetUtil.canRead(propURI)) { this.props = NetUtil.loadProperties(propURI); }
            if (this.props.size() == 0)
            {
                this.props.put("Installed.Index.DefaultVersion", String.valueOf(1.2F));
                storeInstalledIndexMetadata();
            }
        }
        catch (IOException e)
        {
            String msg = "InstalledIndex(): Property file read error: " 
                        + propURI.toString() 
                        + " - " 
                        + e.getMessage();
            lgr.error(msg, THISMODULE);
        }
    }

    public void storeInstalledIndexMetadata(PropertyMap updateProps) throws IOException
    {
        synchronized (this.writeLock)
        {
            this.props.putAll((Map) updateProps);
            NetUtil.storeProperties(this.props, getPropertyFileURI(), metadataFileComment);
        }
    }

    public void storeInstalledIndexMetadata(Book b, String installedIndexVersionToStore) throws IOException
    {
        synchronized (this.writeLock)
        {
            this.props.put("Installed.Index.Version.Book." + IndexMetadata.getBookIdentifierPropSuffix(b.getBookMetaData()), installedIndexVersionToStore);
            NetUtil.storeProperties(this.props, getPropertyFileURI(), metadataFileComment);
        }
    }

    public void removeFromInstalledIndexMetadata(Book b) throws IOException
    {
        synchronized (this.writeLock)
        {
            this.props.remove("Installed.Index.Version.Book." + IndexMetadata.getBookIdentifierPropSuffix(b.getBookMetaData()));
            NetUtil.storeProperties(this.props, getPropertyFileURI(), metadataFileComment);
        }
    }
}