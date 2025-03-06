/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSMsg;

import org.crosswire.common.progress.Progress;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.DateUtils;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URI;

import java.util.Date;

import java.lang.NoClassDefFoundError;

/**
 * @brief Construct a WebResource for the given URL
 * 
 * @details
 * Construct a WebResource for the given URL, while timing out if too much
 * time has passed. A WebResource is backed by an URL and potentially the 
 * proxy through which it need go. It can get basic information about the 
 * resource and it can get the resource. The requests are subject to a 
 * timeout, which can be set via the constructor or previously by a call 
 * to set the default timeout. The initial default timeout is 750 milliseconds.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * 
 * @author DM Smith
 */
public class WebResource 
{
    private static int timeout = 750; // Define a 750 ms timeout to get a connection

    private static final String THISMODULE = "WebResource";
    private static final Logger lgr = LoggerFactory.getLogger(WebResource.class);

    /**
     * @brief Get the timeout value
     *
     * @return the timeout in milliseconds
     */
    public static int getTimeout() {  return timeout; }
    /**
     * @brief Set the timeout value as an Integer
     *
     * @param timeout the timeout to set in milliseconds
     */
    public static void setTimeout(int timeout) { WebResource.timeout = timeout; }
    
    private URI uri;

    private CloseableHttpClient client;

    /**
     * @param theURI the Resource to get via HTTP
     */
    public WebResource(URI theURI) { this(theURI, null, null, timeout); }

    /**
     * @brief Overloaded constructor with timeout param
     *
     * @details Construct a WebResource for the given URL, while timing out if
     * too much time has passed.
     *
     * @param theURI the Resource to get via HTTP
     * @param timo   the length of time in milliseconds to allow a
     * connection to respond before timing out
     */
    public WebResource(URI theURI, int timo) { this(theURI, null, null, timo); }

    /**
     * @brief Overloaded constructor with Proxy HOST param
     *
     * @details Construct a WebResource for the given URL, going through the
     * optional proxy and default port, while timing out if too much time has
     * passed.
     *
     * @param theURI the Resource to get via HTTP
     * @param theProxyHost the proxy host or null
     */
    public WebResource(URI theURI, String theProxyHost) { this(theURI, theProxyHost, null, timeout); }

    /**
     * @brief Overloaded constructor with Proxy HOST param and timeout param
     *
     * @details Construct a WebResource for the given URL, going through the
     * optional proxy and default port, while timing out if too much time has
     * passed.
     *
     * @param theURI the Resource to get via HTTP
     * @param theProxyHost the proxy host or null
     * @param timo   the length of time in milliseconds to allow a
     * connection to respond before timing out
     */
    public WebResource(URI theURI, String theProxyHost, int timo) { this(theURI, theProxyHost, null, timo); }

    /**
     * @brief Overloaded constructor with Proxy HOST param, and port number on
     * proxy
     *
     * @details Construct a WebResource for the given URL, going through the
     * optional proxy and port, while timing out if too much time has passed.
     *
     * @param theURI the Resource to get via HTTP
     * @param theProxyHost the proxy host or null
     * @param theProxyPort the proxy port or null, where null means use the
     * standard port
     */
    public WebResource(URI theURI, String theProxyHost, Integer theProxyPort) 
    {
        this(theURI, theProxyHost, theProxyPort, timeout); 
    }

    /**
     * @brief Overloaded constructor with Proxy HOST param, and port number on
     * proxy and a timeout param
     *
     * @details Construct a WebResource for the given URL, going through the
     * optional proxy and port, while timing out if too much time has passed.
     *
     * @param theURI the Resource to get via HTTP
     * @param theProxyHost the proxy host or null
     * @param theProxyPort the proxy port or null, where null means use the
     * standard port
     * @param timo the length of time in milliseconds to allow a
     * connection to respond before timing out
     */
    public WebResource(URI theURI, String theProxyHost, Integer theProxyPort, int timo)
    {
        uri = theURI;
        HttpHost proxy = null;
        RequestConfig.Builder builder = RequestConfig.custom();
        
        if ( (theProxyHost != null) && (theProxyHost.length() > 0) )
        {
            proxy = new HttpHost(theProxyHost, (theProxyPort == null) ? -1 : theProxyPort);
        }

        builder.setConnectTimeout(timo).setConnectionRequestTimeout(timo).setSocketTimeout(timo).setProxy(proxy);
        try
        {
            client = HttpClientBuilder.create().setDefaultRequestConfig(builder.build()).build();
        }
        catch ( NoClassDefFoundError e )
        {
            String msg = "Exception on HttpClientBuilder = " + e.getMessage() + e.getCause();
            lgr.error(msg,THISMODULE);
        }
    }

    
    /**
     * @brief Called for shutdown
     *
     * @details When this WebResource is no longer needed it should be shutdown
     * to return underlying resources back to the OS.
     */
    public void shutdown() { IOUtil.close(client); }


    /**
     * @brief Determine the size of this WebResource.
     *
     * @details Note that the HTTP client may read the entire file to determine
     * this.
     *
     * @return the size of the file
     */
    public int getSize() 
    {
        StatusLine statusLine;
        HttpRequestBase method;
        HttpResponse response;
        
        response = null;
        statusLine = null;
        method = new HttpHead(uri);

        try 
        {
            // Execute the method.
            response = client.execute(method);
            statusLine = response.getStatusLine();
            if ( statusLine.getStatusCode() == HttpStatus.SC_OK ) 
            {
                return getHeaderAsInt(response, "Content-Length");
            }
            String reason = response.getStatusLine().getReasonPhrase();
            String msg = "Unable to find: " + uri.getPath()
                       + "\n    reason=" + reason
                       + "\n    error=I/O Error";
            lgr.error(msg,THISMODULE);
            // TRANSLATOR: Common error condition: {0} is a placeholder for the
            // URL of what could not be found.
//            Reporter.informUser(this, JSMsg.gettext("Unable to find: {0}", reason + ':' + uri.getPath()));
        } 
        catch ( IOException e ) 
        {
            String msg = "I/O exception "
                       + "\n    reason=" + response.getStatusLine().getReasonPhrase()
                       + "\n    file=" + uri.getPath() 
                       + "\n    error=" + e.getMessage();
            lgr.error(msg,THISMODULE);
            return 0;
        }
        return 0;
    }

    /**
     * @brief Determine the last modified date of this WebResource.
     *
     * @details Note that the HTTP client may read the entire file.
     *
     * @return the last mod date of the file
     */
    public long getLastModified() 
    {
        String reason = "";

        HttpRequestBase method;
        StatusLine statusLine;
        
        method = new HttpHead(uri);
        HttpResponse response = null;

        try 
        {
            // Execute the method.
            response = client.execute(method);
            statusLine = response.getStatusLine();
            if ( statusLine.getStatusCode() == HttpStatus.SC_OK ) 
            {
                return getHeaderAsDate(response, "Last-Modified");
            }
            reason = response.getStatusLine().getReasonPhrase();
            // TRANSLATOR: Common error condition: {0} is a placeholder for the
            // URL of what could not be found.
            String msg = "Unable to find: " + reason + ':' + uri.getPath();
            lgr.error(msg,THISMODULE);
//            Reporter.informUser(this, JSMsg.gettext("Unable to find: {0}", reason + ':' + uri.getPath()));
        } 
        catch (IOException e) 
        {
            String msg = "I/O exception "
                       + "\n    error=" + e.getMessage()
                       + "\n    reason=" + response.getStatusLine().getReasonPhrase()
                       + "\n    file=" + uri.getPath();
            lgr.error(msg,THISMODULE);
            return new Date().getTime();
        }
        return new Date().getTime();
    }

    /**
     * @brief Copy this WebResource to the destination and report progress.
     *
     * @param dest the URI of the destination, typically a file:///.
     * @param meter the job on which to report progress
     * @throws LucidException when an error is encountered
     */
    public void copy(URI dest, Progress meter) throws LucidException 
    {
        int size = 0;

        String reason;

        InputStream in = null;
        OutputStream out = null;
        HttpRequestBase method = new HttpGet(uri);
        HttpResponse response = null;
        HttpEntity entity = null;

        try 
        {
            // Execute the method.
            response = client.execute(method);
            // Initialize the meter, if present
            if ( meter != null ) 
            {
                // Find out how big it is
                size = getHeaderAsInt(response, "Content-Length");
                // Sometimes the Content-Length is not given and we have to grab it via HEAD method
                if (size == 0) { size = getSize(); }
                meter.setTotalWork(size);
            }

            entity = response.getEntity();

            if ( entity != null ) 
            {
                in = entity.getContent();
                // Download the index file
                out = NetUtil.getOutputStream(dest);

                byte[] buf = new byte[4096];
                int count = in.read(buf);
                while (-1 != count) 
                {
                    if (meter != null) { meter.incrementWorkDone(count); }
                    out.write(buf, 0, count);
                    count = in.read(buf);
                }
            } 
            else 
            {
                reason = response.getStatusLine().getReasonPhrase();
                // TRANSLATOR: Common error condition: {0} is a placeholder for
                // the URL of what could not be found.
                String msg = "Unable to find: " + reason + ':' + uri.getPath();
                lgr.error(msg,THISMODULE);
                Reporter.informUser(this, JSMsg.gettext("Unable to find: {0}", reason + ':' + uri.getPath()));
            }
        } 
        catch (IOException e) 
        {
            // TRANSLATOR: Common error condition: {0} is a placeholder for the
            // URL of what could not be found.
            String msg = "I/O exception - reason=" + response.getStatusLine().getReasonPhrase()
                       + "\n    file=" + uri.getPath() 
                       + "\n    error=" + e.getMessage();
            lgr.error(msg,WebResource.class);
            throw new LucidException(JSMsg.gettext("Unable to find: {0}", uri.toString()), e);
        } 
        finally 
        {
            // Close the streams
            IOUtil.close(in);
            IOUtil.close(out);
        }
    }

    /**
     * @brief Copy this WebResource to the destination.
     *
     * @param dest the destination URI
     * @throws LucidException when an error is encountered
     */
    public void copy(URI dest) throws LucidException { copy(dest, null); }

    /**
     * @brief Get the header field as a long.
     *
     * @param response The response from the request
     * @param field the header field to check
     * @return the int value for the field
     */
    private int getHeaderAsInt(HttpResponse response, String field) 
    {
        Header header = response.getFirstHeader(field);
        // If there is no matching header in the message null is returned.
        if ( header == null ) { return 0; }

        String value = header.getValue();

        try 
        {
            return Integer.parseInt(value);
        } 
        catch (NumberFormatException ex) 
        {
            String msg = "Number format exception for " + value + " - " + ex.getMessage();
            lgr.error(msg,THISMODULE);
            return 0;
        }
    }

    /**
     * @brief Get the number of seconds
     *
     * @details Get the number of seconds since start of epoch for the field in
     * the response headers as a Date.
     *
     * @param response The response from the request
     * @param field the header field to check
     * @return number of seconds since start of epoch
     */
    private long getHeaderAsDate(HttpResponse response, String field) 
    {
        Header header = response.getFirstHeader(field);
        String value = header.getValue();
        // This date cannot be readily parsed with DateFormatter
        return DateUtils.parseDate(value).getTime();
    }
}