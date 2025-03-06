package org.crosswire.jsword.book.basic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.crosswire.common.util.Language;
import org.crosswire.common.util.StringUtil;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.VerseKey;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;

public class DefaultBookMetaData extends AbstractBookMetaData {

    private static final String DEFAULT_CHARSET = "UTF-8";

    private String name;
    private String initials;

    private BookCategory type;

    private Map<String, String> props;

    public DefaultBookMetaData(BookDriver driver, String name, BookCategory type)
    {
        this.props = new HashMap<String, String>();
        setDriver(driver);
        setName(name);
        setBookCategory(type);
        setLanguage(Language.DEFAULT_LANG);
    }

    public void setInitials(String initials)
    {
        if (initials == null)
        {
            if (this.name == null)
            {
                this.initials = "";
            }
            else
            {
                this.initials = StringUtil.getInitials(this.name);
            }
        }
        else
        {
            this.initials = initials;
        }
    }

    public void setName(String name)
    {
        this.name = name;
        putProperty("Description", this.name);
        setInitials(StringUtil.getInitials(name));
    }

    public void setBookCategory(BookCategory aType)
    {
        BookCategory t = aType;
        if (t == null) { t = BookCategory.BIBLE; }
        this.type = t;
        putProperty("Category", this.type.toString());
    }

    public void setType(String typestr)
    {
        BookCategory newType = null;
        if (typestr != null) { newType = BookCategory.fromString(typestr); }
        setBookCategory(newType);
    }

    public void setProperty(String key, String value) { this.props.put(key, value); }

    public void putProperty(String key, String value) { setProperty(key, value); }

    public void putProperty(String key, String value, boolean forFrontend) { setProperty(key, value); }

    public boolean isLeftToRight() { return getLanguage().isLeftToRight(); }

    public String getBookCharset() { return "UTF-8"; }

    public Set<String> getPropertyKeys() { return Collections.unmodifiableSet(this.props.keySet()); }

    public String getProperty(String key)
    {
        if ("Language".equals(key)) { return getLanguage().getName(); }
        return this.props.get(key);
    }

    public BookCategory getBookCategory() { return this.type; }
    public String getName()               { return this.name; }
    public String getAbbreviation()       { return this.initials; }
    public String getInitials()           { return this.initials; }

    public Document toOSIS()
    {
        OSISUtil.OSISFactory factory = OSISUtil.factory();
        Element ele = factory.createTable();
        addRow(factory, ele, "Initials", getInitials());
        addRow(factory, ele, "Description", getName());
        addRow(factory, ele, "Key", getBookCategory().toString());
        addRow(factory, ele, "Language", getLanguage().getName());
        return new Document(ele);
    }

    public VerseKey getScope()
    {
        throw new UnsupportedOperationException();
    }

    private void addRow(OSISUtil.OSISFactory factory, Element table, String key, String value)
    {
        if (value == null) { return;  }

        Element rowEle = factory.createRow();
        Element nameEle = factory.createCell();
        Element hiEle = factory.createHI();
        hiEle.setAttribute("type", "bold");
        nameEle.addContent((Content) hiEle);
        Element valueElement = factory.createCell();
        rowEle.addContent((Content) nameEle);
        rowEle.addContent((Content) valueElement);
        hiEle.addContent(key);
        String expandedValue = XMLUtil.escape(value);
        valueElement.addContent(expandedValue);
        table.addContent((Content) rowEle);
    }
}