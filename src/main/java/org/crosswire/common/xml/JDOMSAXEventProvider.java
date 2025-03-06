package org.crosswire.common.xml;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.output.SAXOutputter;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class JDOMSAXEventProvider implements SAXEventProvider {

    private Document doc;

    public JDOMSAXEventProvider(Document doc) {
        this.doc = doc;
    }

    public void provideSAXEvents(ContentHandler handler) throws SAXException {
        try {
            SAXOutputter output = new SAXOutputter(handler);
            output.output(this.doc);
        } catch (JDOMException ex) {
            throw new SAXException(ex);
        }
    }
}
