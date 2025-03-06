package org.crosswire.common.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

public class PrettySerializingContentHandler implements ContentHandler {

    public PrettySerializingContentHandler() {
        this(FormatType.AS_IS);
    }

    public PrettySerializingContentHandler(FormatType theFormat) {
        this(theFormat, null);
    }

    public PrettySerializingContentHandler(FormatType theFormat, Writer theWriter) {
        this.formatting = theFormat;
        this.writer = (theWriter == null) ? new StringWriter() : theWriter;
    }

    public String toString() {
        return this.writer.toString();
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() {
    }

    public void endDocument() {
    }

    public void startPrefixMapping(String prefix, String uri) {
    }

    public void endPrefixMapping(String prefix) {
    }

    public void startElement(String uri, String localname, String qname, Attributes attrs) {
        if (this.depth > 0) {
            handlePending();
        }
        write(getTagStart());
        write(decorateTagName(localname));
        for (int i = 0; i < attrs.getLength(); i++) {
            write(' ');
            write(decorateAttributeName(XMLUtil.getAttributeName(attrs, i)));
            write("='");
            write(decorateAttributeValue(XMLUtil.escape(attrs.getValue(i))));
            write('\'');
        }
        this.pendingEndTag = true;
        this.depth++;
    }

    public void endElement(String uri, String localname, String qname) {
        this.depth--;
        if (this.pendingEndTag) {
            if (this.formatting.isAnalytic() && this.depth > 0) {
                emitWhitespace(this.depth - 1);
            }
            write(getTagEnd());
        }
        if (this.formatting.isClassic()) {
            emitWhitespace(this.depth);
        }
        write(getEndTagStart());
        write(decorateTagName(localname));
        if (this.formatting.isAnalytic()) {
            emitWhitespace(this.depth);
        }
        write(getTagEnd());
        this.pendingEndTag = false;
        this.lookingForChars = false;
    }

    public void characters(char[] chars, int start, int length) {
        if (!this.lookingForChars) {
            handlePending();
        }
        String s = new String(chars, start, length);
        write(decorateCharacters(s));
        this.lookingForChars = true;
    }

    public void ignorableWhitespace(char[] chars, int start, int length) {
        characters(chars, start, length);
    }

    public void processingInstruction(String target, String data) {
        handlePending();
        write(getPIStart());
        write(target);
        write(' ');
        write(decorateCharacters(data));
        write(getPIEnd());
        if (this.formatting.isMultiline()) {
            write(getNewline());
        }
    }

    public void skippedEntity(String name) {
    }

    protected String getTagStart() {
        return "<";
    }

    protected String getTagEnd() {
        return ">";
    }

    protected String getEmptyTagEnd() {
        return "/>";
    }

    protected String getEndTagStart() {
        return "</";
    }

    protected String getPIStart() {
        return "<!";
    }

    protected String getPIEnd() {
        return "!>";
    }

    protected String getNewline() {
        return "\n";
    }

    protected String decorateTagName(String tagName) {
        return tagName;
    }

    protected String decorateAttributeName(String attrName) {
        return attrName;
    }

    protected String decorateAttributeValue(String attrValue) {
        return attrValue;
    }

    protected String decorateCharacters(String characters) {
        return characters;
    }

    protected String decorateIndent(int indentLevel) {
        return (new String(indentation, 0, indentLevel)).intern();
    }

    protected void write(String obj) {
        try {
            this.writer.write(obj);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    protected void write(char obj) {
        try {
            this.writer.write(obj);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private void handlePending() {
        if (this.pendingEndTag) {
            this.pendingEndTag = false;
            if (this.formatting.isAnalytic()) {
                emitWhitespace(this.depth);
            }
            write(getTagEnd());
        }
        if (this.formatting.isClassic()) {
            emitWhitespace(this.depth);
        }
        this.lookingForChars = false;
    }

    private void emitWhitespace(int indentLevel) {
        write(getNewline());
        if (this.formatting.isIndented()) {
            write(decorateIndent(indentLevel));
        }
    }

    private static char[] indentation = new char[]{
        '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t',
        '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t',
        '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t', '\t'};

    private int depth;

    private boolean lookingForChars;

    private boolean pendingEndTag;

    private FormatType formatting;

    private Writer writer;
}
