package org.crosswire.common.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SAXEventProviderXMLReader implements XMLReader {

    private SAXEventProvider docIn;
    private ErrorHandler errors;
    private ContentHandler content;
    private DTDHandler dtds;
    private EntityResolver entities;

    public SAXEventProviderXMLReader(SAXEventProvider docIn) {
        this.docIn = docIn;
    }

    public boolean getFeature(String arg0) {
        return false;
    }

    public void setFeature(String arg0, boolean arg1) {
    }

    public Object getProperty(String arg0) {
        return null;
    }

    public void setProperty(String arg0, Object arg1) {
    }

    public void setEntityResolver(EntityResolver entities) {
        this.entities = entities;
    }

    public EntityResolver getEntityResolver() {
        return this.entities;
    }

    public void setDTDHandler(DTDHandler dtds) {
        this.dtds = dtds;
    }

    public DTDHandler getDTDHandler() {
        return this.dtds;
    }

    public void setContentHandler(ContentHandler content) {
        this.content = content;
    }

    public ContentHandler getContentHandler() {
        return this.content;
    }

    public void setErrorHandler(ErrorHandler errors) {
        this.errors = errors;
    }

    public ErrorHandler getErrorHandler() {
        return this.errors;
    }

    public void parse(InputSource is) throws SAXException {
        if (!(is instanceof SAXEventProviderInputSource)) {
            throw new SAXException("SAXEventProviderInputSource required");
        }
        this.docIn.provideSAXEvents(getContentHandler());
    }

    public void parse(String arg0) throws SAXException {
        throw new SAXException("SAXEventProviderInputSource required");
    }
}
