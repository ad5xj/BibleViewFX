package org.crosswire.jsword.book.filter.thml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;

import org.crosswire.jsword.passage.Key;

import java.util.List;

import org.jdom2.Content;
import org.jdom2.Element;

import org.xml.sax.Attributes;

public class SyncTag extends AbstractTag {
    private static final Logger lgr = LoggerFactory.getLogger(SyncTag.class);
    private static final String THISMODULE = "SyncTag";

    public String getTagName() { return "sync"; }

    public Element processTag(Book book, Key key, Element ele, Attributes attrs)
    {
        String type = attrs.getValue("type");
        String value = attrs.getValue("value");
        if ("Strongs".equals(type))
        {
            List<Content> siblings = ele.getContent();
            int size = siblings.size();
            if (size == 0)
            {
                return null;
            }
            Content lastEle = siblings.get(size - 1);
            if (lastEle instanceof org.jdom2.Text)
            {
                Element w = OSISUtil.factory().createW();
                w.setAttribute("lemma", "strong:" + value);
                siblings.set(size - 1, w);
                w.addContent(lastEle);
            }
            else
            {
                if (lastEle instanceof Element)
                {
                    Element wEle = (Element) lastEle;
                    if (wEle.getName().equals("w"))
                    {
                        StringBuilder buf = new StringBuilder();
                        String strongsAttr = wEle.getAttributeValue("lemma");
                        if (strongsAttr != null)
                        {
                            buf.append(strongsAttr);
                            buf.append(' ');
                        }
                        buf.append("strong:");
                        buf.append(value);
                        wEle.setAttribute("lemma", buf.toString());
                    }
                }
            }
            return null;
        }
        if ("morph".equals(type))
        {
            List<Content> siblings = ele.getContent();
            int size = siblings.size();
            if (size == 0)
            {
                return null;
            }
            Content lastEle = siblings.get(size - 1);
            if (lastEle instanceof org.jdom2.Text)
            {
                Element w = OSISUtil.factory().createW();
                w.setAttribute("morph", "robinson:" + value);
                siblings.set(size - 1, w);
                w.addContent(lastEle);
            }
            else
            {
                if (lastEle instanceof Element)
                {
                    Element wEle = (Element) lastEle;
                    if (wEle.getName().equals("w"))
                    {
                        StringBuilder buf = new StringBuilder();
                        String strongsAttr = wEle.getAttributeValue("morph");
                        if (strongsAttr != null)
                        {
                            buf.append(strongsAttr);
                            buf.append(' ');
                        }
                        buf.append("robinson:");
                        buf.append(value);
                        wEle.setAttribute("morph", buf.toString());
                    }
                }
            }
            return null;
        }
        if ("lemma".equals(type))
        {
            List<Content> siblings = ele.getContent();
            int size = siblings.size();
            if (size == 0)
            {
                return null;
            }
            Content lastEle = siblings.get(size - 1);
            if (lastEle instanceof org.jdom2.Text)
            {
                Element w = OSISUtil.factory().createW();
                w.setAttribute("lemma", "lemma:" + value);
                siblings.set(size - 1, w);
                w.addContent(lastEle);
            }
            else
            {
                if (lastEle instanceof Element)
                {
                    Element wEle = (Element) lastEle;
                    if (wEle.getName().equals("w"))
                    {
                        StringBuilder buf = new StringBuilder();
                        String lemmaAttr = wEle.getAttributeValue("lemma");
                        if (lemmaAttr != null)
                        {
                            buf.append(lemmaAttr);
                            buf.append(' ');
                        }
                        buf.append("lemma:");
                        buf.append(value);
                        wEle.setAttribute("lemma", buf.toString());
                    }
                }
            }
            return null;
        }
        if ("Dict".equals(type))
        {
            Element div = OSISUtil.factory().createDiv();
            div.setAttribute("osisID", "dict://" + value);
            if (ele != null)
            {
                ele.addContent((Content) div);
            }
            return div;
        }
        String msg = "processTag(): sync tag has type=" + type + " when value=" + value;
        lgr.error(msg,THISMODULE);
        //DataPolice.report(book, key, "sync tag has type=" + type + " when value=" + value);
        return null;
    }
}