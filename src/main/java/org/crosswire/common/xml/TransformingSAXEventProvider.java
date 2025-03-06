package org.crosswire.common.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.NetUtil;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class TransformingSAXEventProvider extends Transformer implements SAXEventProvider {
  private static boolean developmentMode;
  
  private ErrorListener errors;
  
  private URIResolver resolver;
  
  private Properties outputs;
  
  private Map<String, Object> params;
  
  private URI xsluri;
  
  private SAXEventProvider xmlsep;
  
  private TransformerFactory transfact;
  
  public TransformingSAXEventProvider(URI xsluri, SAXEventProvider xmlsep) {
    this.xsluri = xsluri;
    this.xmlsep = xmlsep;
    this.outputs = new Properties();
    this.params = new HashMap<String, Object>();
  }
  
  private TemplateInfo getTemplateInfo() throws TransformerConfigurationException, IOException {
    TemplateInfo tinfo = txers.get(this.xsluri);
    long modtime = -1L;
    if (developmentMode && 
      tinfo != null) {
      modtime = NetUtil.getLastModified(this.xsluri);
      if (modtime > tinfo.getModtime()) {
        txers.remove(this.xsluri);
        tinfo = null;
        log.debug("updated style, re-caching. xsl={}", this.xsluri);
      } 
    } 
    if (tinfo == null) {
      log.debug("generating templates for {}", this.xsluri);
      InputStream xslStream = null;
      try {
        xslStream = NetUtil.getInputStream(this.xsluri);
        if (this.transfact == null)
          this.transfact = TransformerFactory.newInstance(); 
        Templates templates = this.transfact.newTemplates(new StreamSource(xslStream));
        if (modtime == -1L)
          modtime = NetUtil.getLastModified(this.xsluri); 
        tinfo = new TemplateInfo(templates, modtime);
        txers.put(this.xsluri, tinfo);
      } finally {
        IOUtil.close(xslStream);
      } 
    } 
    return tinfo;
  }
  
  public void transform(Source xmlSource, Result outputTarget) throws TransformerException {
    TemplateInfo tinfo;
    try {
      tinfo = getTemplateInfo();
    } catch (IOException e) {
      throw new TransformerException(e);
    } 
    Transformer transformer = tinfo.getTemplates().newTransformer();
    for (Object obj : this.outputs.keySet()) {
      String key = (String)obj;
      String val = getOutputProperty(key);
      transformer.setOutputProperty(key, val);
    } 
    for (String key : this.params.keySet()) {
      Object val = this.params.get(key);
      transformer.setParameter(key, val);
    } 
    if (this.errors != null)
      transformer.setErrorListener(this.errors); 
    if (this.resolver != null)
      transformer.setURIResolver(this.resolver); 
    transformer.transform(xmlSource, outputTarget);
  }
  
  public void provideSAXEvents(ContentHandler handler) throws SAXException {
    try {
      Source xmlSource = new SAXSource(new SAXEventProviderXMLReader(this.xmlsep), new SAXEventProviderInputSource());
      SAXResult outputTarget = new SAXResult(handler);
      transform(xmlSource, outputTarget);
    } catch (TransformerException ex) {
      throw new SAXException(ex);
    } 
  }
  
  public ErrorListener getErrorListener() {
    return this.errors;
  }
  
  public void setErrorListener(ErrorListener errors) throws IllegalArgumentException {
    this.errors = errors;
  }
  
  public URIResolver getURIResolver() {
    return this.resolver;
  }
  
  public void setURIResolver(URIResolver resolver) {
    this.resolver = resolver;
  }
  
  public Properties getOutputProperties() {
    return this.outputs;
  }
  
  public void setOutputProperties(Properties outputs) throws IllegalArgumentException {
    this.outputs = outputs;
  }
  
  public String getOutputProperty(String name) throws IllegalArgumentException {
    return this.outputs.getProperty(name);
  }
  
  public void setOutputProperty(String name, String value) throws IllegalArgumentException {
    this.outputs.setProperty(name, value);
  }
  
  public void clearParameters() {
    this.params.clear();
  }
  
  public Object getParameter(String name) {
    return this.params.get(name);
  }
  
  public void setParameter(String name, Object value) {
    this.params.put(name, value);
  }
  
  public static void setDevelopmentMode(boolean developmentMode) {
    TransformingSAXEventProvider.developmentMode = developmentMode;
  }
  
  public static boolean isDevelopmentMode() {
    return developmentMode;
  }
  
  private static class TemplateInfo {
    private Templates templates;
    
    private long modtime;
    
    TemplateInfo(Templates templates, long modtime) {
      this.templates = templates;
      this.modtime = modtime;
    }
    
    Templates getTemplates() {
      return this.templates;
    }
    
    long getModtime() {
      return this.modtime;
    }
  }
  
  private static Map<URI, TemplateInfo> txers = new HashMap<URI, TemplateInfo>();
  
  private static final Logger log = LoggerFactory.getLogger(TransformingSAXEventProvider.class);
}