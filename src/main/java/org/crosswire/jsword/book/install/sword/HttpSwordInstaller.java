package org.crosswire.jsword.book.install.sword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.progress.Progress;

import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.WebResource;

import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.install.InstallException;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpSwordInstaller extends AbstractSwordInstaller 
{
    private static final String THISMODULE = "HttpSwordInstaller";
    private static final Logger lgr = LoggerFactory.getLogger(HttpSwordInstaller.class);


    private boolean debug_log = false;

    @Override
    public String getType() { return "sword-http"; }

    @Override
    public boolean equals(Object object) 
    {
        if ( !(object instanceof HttpSwordInstaller) ) { return false; }
        HttpSwordInstaller that = (HttpSwordInstaller) object;
        if ( !super.equals(that) ) { return false; }
        return true;
    }

    @Override
    public int hashCode() { return super.hashCode(); }

    @Override
    public int getSize(Book book) 
    {
        return NetUtil.getSize(toRemoteURI(book), this.proxyHost, this.proxyPort);
    }

    /**
     * @brief Method to convert to Remote URI
     * 
     * @param book
     * @return URI
     */
    @Override
    public URI toRemoteURI(Book book) 
    {
        try 
        {
            return new URI("http", this.host, this.packageDirectory + '/' + book.getInitials() + ".zip", null);
        } 
        catch ( URISyntaxException e )
        {
            String msg = "toRemoteURI(): Error creating URI - " + e.getMessage();
            lgr.error(msg,THISMODULE);
            return null;
        }
    }

    @Override
    protected void download(Progress job, String dir, String file, URI dest) throws InstallException 
    {
        URI uri;

        try 
        {
            uri = new URI("http", this.host, dir + '/' + file, null);
        } 
        catch ( URISyntaxException ex ) 
        {
            String msg = "toRemoteURI(): Unable to find " + file
                       + "\n  Error=" + ex.getMessage();
            lgr.error(msg,THISMODULE);
            throw new InstallException(JSMsg.gettext("Unable to find: {0}", new Object[]{dir + '/' + file}), ex);
        }

        try 
        {
            copy(job, uri, dest);
        } 
        catch ( LucidException ex ) 
        {
            String msg = "toRemoteURI(): Unable to find " + uri.toString()
                       + "\n  Error=" + ex.getMessage();
            lgr.error(msg,THISMODULE);
            throw new InstallException(JSMsg.gettext("Unable to find: {0}", new Object[]{uri.toString()}), ex);
        }
    }

    private void copy(Progress job, URI uri, URI dest) throws LucidException 
    {
        WebResource wr;
        
        if ( job != null ) 
        {
            String msg = "Downloading files from " + uri.toString() + " to " + dest.toString();
            if ( debug_log ) { lgr.info(msg,THISMODULE); }
            job.setSectionName(JSMsg.gettext("Downloading files", new Object[0]));
        }
        
        wr = new WebResource(uri, proxyHost, proxyPort);
        if ( !(proxyHost == null) && !(proxyPort <= 0) )
        {
            try
            {
                wr.copy(dest, job);
            }
            catch ( LucidException e )
            {
                String msg = "copy: Unable to copy from installer " + proxyHost + ":" + proxyPort
                           + "\n  Error=" + e.getMessage();
                lgr.error(msg,THISMODULE);
            }
        }
        wr.shutdown();
    }
}