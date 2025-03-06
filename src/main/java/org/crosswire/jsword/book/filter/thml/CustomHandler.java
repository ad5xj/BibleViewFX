package org.crosswire.jsword.book.filter.thml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.passage.Key;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Text;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CustomHandler extends DefaultHandler {
    private static final Logger lgr =  LoggerFactory.getLogger(CustomHandler.class);
    private static final String THISMODULE = "CustomHandler";

    private Element rootElement;
    private Book m_book;
    private Key m_key;
    private LinkedList<Content> stack;

    public CustomHandler(Book book, Key key)
    {
        m_book = book;
        m_key = key;
        stack = new LinkedList<Content>();
    }

    public void startElement(String uri, String localname, String qname, Attributes attrs) throws SAXException
    {
        Element ele = null;
        if (!stack.isEmpty())
        {
            Object top = stack.getFirst();
            if (top == null)
            {
                return;
            }
            if (top instanceof Element)
            {
                ele = (Element) top;
            }
        }
        Tag t = getTag(localname, qname);
        if (t != null)
        {
            stack.addFirst(t.processTag(m_book, m_key, ele, attrs));
        }
    }

    public void characters(char[] data, int offset, int length)
    {
        String text = new String(data, offset, length);
        if ( stack.isEmpty() )
        {
            stack.addFirst(new Text(text));
            return;
        }
        Content top = this.stack.getFirst();
        if (top == null) { return; }
        if (top instanceof Text)
        {
            ((Text) top).append(text);
            return;
        }
        if (top instanceof Element)
        {
            Element current = (Element) top;
            int size = current.getContentSize();
            if (size > 0)
            {
                Content last = current.getContent(size - 1);
                if (last instanceof Text)
                {
                    ((Text) last).append(text);
                    return;
                }
            }
            current.addContent((Content) new Text(text));
        }
    }

    public void endElement(String uri, String localname, String qname)
    {
        if ( stack.isEmpty())
        {
            return;
        }
        Content top = stack.removeFirst();
        if (top instanceof Element)
        {
            Element finished = (Element) top;
            Tag t = getTag(localname, qname);
            if (t != null)
            {
                t.processContent( m_book, m_key, finished);
            }
            if ( stack.isEmpty())
            {
                this.rootElement = finished;
            }
        }
    }

    public Element getRootElement() { return rootElement; }

    private Tag getTag(String localname, String qname)
    {
        String name;
        if (qname != null && qname.length() > 0)
        {
            name = qname;
        }
        else
        {
            name = localname;
        }
        Tag t = TAG_MAP.get(name);
        if (t == null)
        {
            t = TAG_MAP.get(name.toLowerCase(Locale.ENGLISH));
            if (t == null)
            {
                String msg = "getTag(): Unknown thml element: " + localname + " qname=" + name;
                lgr.error(msg, THISMODULE);
                //DataPolice.report(this.book, this.key, "Unknown thml element: " + localname + " qname=" + name);
                t = new AnonymousTag(name);
                TAG_MAP.put(name, t);
                return t;
            }
        }
        return t;
    }

    private static final Map<String, Tag> TAG_MAP = new HashMap<String, Tag>();

    static
    {
        Tag[] tags =
        {
            new ATag(), new AbbrTag(), new AliasTag("acronym", new AbbrTag()), new AnonymousTag("address"), new SkipTag("applet"), new SkipTag("area"), new BTag(), new SkipTag("base"), new SkipTag("basefont"), new IgnoreTag("bdo"),
            new BigTag(), new BlockquoteTag(), new IgnoreTag("body"), new BrTag(), new SkipTag("button"), new AnonymousTag("caption"), new CenterTag(), new AnonymousTag("cite"), new AnonymousTag("code"), new SkipTag("col"),
            new SkipTag("colgroup"), new AliasTag("dd", new LiTag()), new AnonymousTag("del"), new AnonymousTag("dfn"), new DivTag(), new AliasTag("dl", new UlTag()), new AliasTag("dt", new LiTag()), new AliasTag("em", new ITag()), new IgnoreTag("fieldset"), new FontTag(),
            new SkipTag("form"), new SkipTag("frame"), new SkipTag("frameset"), new AliasTag("h1", new HTag(1)), new AliasTag("h2", new HTag(2)), new AliasTag("h3", new HTag(3)), new AliasTag("h4", new HTag(4)), new AliasTag("h5", new HTag(5)), new AliasTag("h6", new HTag(6)), new SkipTag("head"),
            new HrTag(), new IgnoreTag("html"), new IgnoreTag("frameset"), new ITag(), new SkipTag("iframe"), new ImgTag(), new SkipTag("input"), new AnonymousTag("ins"), new AnonymousTag("kbd"), new AnonymousTag("label"),
            new AnonymousTag("legend"), new LiTag(), new SkipTag("link"), new SkipTag("map"), new SkipTag("meta"), new SkipTag("noscript"), new SkipTag("object"), new OlTag(), new SkipTag("optgroup"), new SkipTag("option"),
            new PTag(), new SkipTag("param"), new IgnoreTag("pre"), new QTag(), new RootTag(), new STag(), new AnonymousTag("samp"), new SkipTag("script"), new SkipTag("select"), new SmallTag(),
            new IgnoreTag("span"), new AliasTag("strong", new BTag()), new SkipTag("style"), new SubTag(), new SupTag(), new SyncTag(), new TableTag(), new IgnoreTag("tbody"), new TdTag(), new IgnoreTag("tfoot"),
            new SkipTag("textarea"), new SkipTag("title"), new IgnoreTag("thead"), new ThTag(), new TrTag(), new TtTag(), new UTag(), new UlTag(), new AnonymousTag("var"), new AnonymousTag("added"),
            new AnonymousTag("attr"), new AnonymousTag("argument"), new CitationTag(), new AnonymousTag("date"), new AnonymousTag("deleted"), new AnonymousTag("def"), new AliasTag("div1", new DivTag(1)), new AliasTag("div2", new DivTag(2)), new AliasTag("div3", new DivTag(3)), new AliasTag("div4", new DivTag(4)),
            new AliasTag("div5", new DivTag(5)), new AliasTag("div6", new DivTag(6)), new ForeignTag(), new AnonymousTag("index"), new AnonymousTag("insertIndex"), new AnonymousTag("glossary"), new NoteTag(), new NameTag(), new PbTag(), new AnonymousTag("scripCom"),
            new AnonymousTag("scripContext"), new ScripRefTag(), new ScriptureTag(), new TermTag(), new AnonymousTag("unclear"), new VerseTag()
        };
        for (int i = 0; i < tags.length; i++)
        {
            Tag t = tags[i];
            String tagName = t.getTagName();
            TAG_MAP.put(tagName, t);
        }
    }
}