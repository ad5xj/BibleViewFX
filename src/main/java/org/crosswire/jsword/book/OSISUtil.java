package org.crosswire.jsword.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.diff.Difference;
import org.crosswire.common.diff.EditType;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseFactory;
import org.crosswire.jsword.versification.Versification;

import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Parent;
import org.jdom2.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief Helper class for Book module
 */
public final class OSISUtil 
{
    public static final String HI_ACROSTIC = "acrostic";
    public static final String HI_BOLD = "bold";
    public static final String HI_EMPHASIS = "emphasis";
    public static final String HI_ILLUMINATED = "illuminated";
    public static final String HI_ITALIC = "italic";
    public static final String HI_LINETHROUGH = "line-through";
    public static final String HI_NORMAL = "normal";
    public static final String HI_SMALL_CAPS = "small-caps";
    public static final String HI_SUB = "sub";
    public static final String HI_SUPER = "super";
    public static final String HI_UNDERLINE = "underline";
    public static final String HI_X_CAPS = "x-caps";
    public static final String HI_X_BIG = "x-big";
    public static final String HI_X_SMALL = "x-small";
    public static final String HI_X_TT = "x-tt";
    public static final String SEG_JUSTIFYRIGHT = "text-align: right;";
    public static final String SEG_JUSTIFYLEFT = "text-align: left;";
    public static final String SEG_CENTER = "text-align: center;";
    public static final String DIV_PRE = "x-pre";
    public static final String SEG_COLORPREFIX = "color: ";
    public static final String SEG_SIZEPREFIX = "font-size: ";
    public static final String TYPE_X_PREFIX = "x-";
    public static final String NOTETYPE_STUDY = "x-StudyNote";
    public static final String NOTETYPE_REFERENCE = "crossReference";
    public static final String VARIANT_TYPE = "x-variant";
    public static final String VARIANT_CLASS = "x-";
    public static final String GENERATED_CONTENT = "x-gen";
    public static final String POS_TYPE = "x-pos";
    public static final String DEF_TYPE = "x-def";
    public static final String LEMMA_STRONGS = "strong:";
    public static final String LEMMA_MISC = "lemma:";
    public static final String MORPH_ROBINSONS = "robinson:";
    public static final String MORPH_STRONGS = "x-StrongsMorph:T";
    public static final String Q_BLOCK = "blockquote";
    public static final String Q_CITATION = "citation";
    public static final String Q_EMBEDDED = "embedded";
    public static final String LIST_ORDERED = "x-ordered";
    public static final String LIST_UNORDERED = "x-unordered";
    public static final String TABLE_ROLE_LABEL = "label";
    public static final String CELL_ALIGN_LEFT = "left";
    public static final String CELL_ALIGN_RIGHT = "right";
    public static final String CELL_ALIGN_CENTER = "center";
    public static final String CELL_ALIGN_JUSTIFY = "justify";
    public static final String CELL_ALIGN_START = "start";
    public static final String CELL_ALIGN_END = "end";
    public static final String OSIS_ELEMENT_ABBR = "abbr";
    public static final String OSIS_ELEMENT_TITLE = "title";
    public static final String OSIS_ELEMENT_TABLE = "table";
    public static final String OSIS_ELEMENT_SPEECH = "speech";
    public static final String OSIS_ELEMENT_SPEAKER = "speaker";
    public static final String OSIS_ELEMENT_ROW = "row";
    public static final String OSIS_ELEMENT_REFERENCE = "reference";
    public static final String OSIS_ELEMENT_NOTE = "note";
    public static final String OSIS_ELEMENT_NAME = "name";
    public static final String OSIS_ELEMENT_Q = "q";
    public static final String OSIS_ELEMENT_LIST = "list";
    public static final String OSIS_ELEMENT_P = "p";
    public static final String OSIS_ELEMENT_ITEM = "item";
    public static final String OSIS_ELEMENT_FIGURE = "figure";
    public static final String OSIS_ELEMENT_FOREIGN = "foreign";
    public static final String OSIS_ELEMENT_W = "w";
    public static final String OSIS_ELEMENT_CHAPTER = "chapter";
    public static final String OSIS_ELEMENT_VERSE = "verse";
    public static final String OSIS_ELEMENT_CELL = "cell";
    public static final String OSIS_ELEMENT_DIV = "div";
    public static final String OSIS_ELEMENT_OSIS = "osis";
    public static final String OSIS_ELEMENT_WORK = "work";
    public static final String OSIS_ELEMENT_HEADER = "header";
    public static final String OSIS_ELEMENT_OSISTEXT = "osisText";
    public static final String OSIS_ELEMENT_SEG = "seg";
    public static final String OSIS_ELEMENT_LG = "lg";
    public static final String OSIS_ELEMENT_L = "l";
    public static final String OSIS_ELEMENT_LB = "lb";
    public static final String OSIS_ELEMENT_HI = "hi";
    public static final String ATTRIBUTE_TEXT_OSISIDWORK = "osisIDWork";
    public static final String ATTRIBUTE_WORK_OSISWORK = "osisWork";
    public static final String OSIS_ATTR_OSISID = "osisID";
    public static final String OSIS_ATTR_SID = "sID";
    public static final String OSIS_ATTR_EID = "eID";
    public static final String ATTRIBUTE_W_LEMMA = "lemma";
    public static final String ATTRIBUTE_FIGURE_SRC = "src";
    public static final String ATTRIBUTE_TABLE_BORDER = "border";
    public static final String ATTRIBUTE_TABLE_ROLE = "role";
    public static final String ATTRIBUTE_CELL_ALIGN = "align";
    public static final String ATTRIBUTE_CELL_ROWS = "rows";
    public static final String ATTRIBUTE_CELL_COLS = "cols";
    public static final String OSIS_ATTR_TYPE = "type";
    public static final String OSIS_ATTR_CANONICAL = "canonical";
    public static final String OSIS_ATTR_SUBTYPE = "subType";
    public static final String OSIS_ATTR_REF = "osisRef";
    public static final String OSIS_ATTR_LEVEL = "level";
    public static final String ATTRIBUTE_SPEAKER_WHO = "who";
    public static final String ATTRIBUTE_Q_WHO = "who";
    public static final String ATTRIBUTE_W_MORPH = "morph";
    public static final String ATTRIBUTE_OSISTEXT_OSISIDWORK = "osisIDWork";
    public static final String OSIS_ATTR_LANG = "lang";
    public static final String ATTRIBUTE_DIV_BOOK = "book";

    private static final char SPACE_SEPARATOR = ' ';
    private static final char MORPH_INFO_SEPARATOR = '@';

    private static final String OSISID_PREFIX_BIBLE = "Bible.";

    private static final Set<String> EXTRA_BIBLICAL_ELEMENTS = new HashSet<String>(Arrays.asList(new String[]{"note", "title", "reference"}));

    private static final Logger log = LoggerFactory.getLogger(OSISUtil.class);

    private static String strongsNumber = "strong:([GgHh][0-9]+!?[A-Za-z]*)";

    private static OSISFactory factory = new OSISFactory();

    private static Pattern strongsNumberPattern = Pattern.compile(strongsNumber);


    public static OSISFactory factory() { return factory; }

    public static class OSISFactory 
    {
        public Element createAbbr()      { return new Element("abbr"); }
        public Element createSeg()       { return new Element("seg");  }
        public Element createOsisText()  { return new Element("osisText"); }
        public Element createHeader()    { return new Element("header"); }
        public Element createWork()      { return new Element("work"); }
        public Element createOsis()      { return new Element("osis"); }
        public Element createDiv()       { return new Element("div"); }
        public Element createCell()      { return new Element("cell"); }
        public Element createVerse()     { return new Element("verse"); }
        public Element createW()         { return new Element("w"); }
        public Element createFigure()    { return new Element("figure"); }
        public Element createForeign()   { return new Element("foreign"); }
        public Element createItem()      { return new Element("item"); }
        public Element createP()         { return new Element("p"); }
        public Element createList()      { return new Element("list"); }
        public Element createQ()         { return new Element("q"); }
        public Element createName()      { return new Element("name"); }
        public Element createNote()      { return new Element("note"); }
        public Element createReference() { return new Element("reference"); }
        public Element createRow()       { return new Element("row"); }
        public Element createSpeaker()   { return new Element("speaker"); }
        public Element createSpeech()    { return new Element("speech"); }
        public Element createTable()     { return new Element("table"); }
        public Element createTitle()     { return new Element("title"); }
        public Element createLG()        { return new Element("lg"); }
        public Element createL()         { return new Element("l"); }
        public Element createLB()        { return new Element("lb"); }
        public Element createHI()        { return new Element("hi"); }
        public Text createText(String text) { return new Text(text); }

        public Element createHeaderCell()
        {
            Element ele = new Element("cell");
            ele.setAttribute("role", "label");
            ele.setAttribute("align", "center");
            return ele;
        }

        public Element createGeneratedTitle()
        {
            Element title = new Element("title");
            title.setAttribute("type", "x-gen");
            return title;
        }
    }

    public static List<Content> getFragment(Element root) 
    {
        if ( root != null ) 
        {
            Element content = root;
            if ("osis".equals(root.getName())) { content = root.getChild("osisText"); }
            if ("osisText".equals(root.getName())) { content = root.getChild("div"); }
            if (content != null && content.getContentSize() == 1) 
            {
                Content firstChild = content.getContent(0);
                if ( (firstChild instanceof Element) && ("div".equals(((Element) firstChild).getName())) ) 
                {
                    content = (Element) firstChild;
                }
            }
            if ( content != null ) { return content.getContent(); }
        }
        return new ArrayList<Content>();
    }

    public static String getCanonicalText(Element root) 
    {
        if ( !isCanonical((Content) root) ) { return ""; }

        String buffer;
        String sID;

        Content data;
        Element ele;

        List<Content> frag;
        Iterator<Content> dit;

        buffer = null;
        sID = null;
        frag = getFragment(root);
        dit = frag.iterator();
        data = null;
        ele = null;
        
        while ( dit.hasNext() ) 
        {
            data = dit.next();
            if ( data instanceof Element ) 
            {
                ele = (Element) data;
                if ( !isCanonical((Content) ele) ) { continue; }
                
                if ( ele.getName().equals("verse") ) { sID = ele.getAttributeValue("sID"); }
                if ( sID != null ) 
                {
                    getCanonicalContent(ele, sID, dit, buffer);
                    continue;
                }
                getCanonicalContent(ele, null, ele.getContent().iterator(), buffer);
                continue;
            }

            if ( data instanceof Text ) 
            {
                int lastIndex = buffer.length() - 1;
                String text = ((Text) data).getText();
                if ( text.length() != 0 ) 
                {
                    if (lastIndex >= 0 && !Character.isWhitespace(buffer.charAt(lastIndex)) && !Character.isWhitespace(text.charAt(0))) 
                    {
                        buffer += " ";
                    }
                    buffer += text;
                }
            }
        }
        return buffer.trim();
    }

    public static String getPlainText(Element root) { return getTextContent(getFragment(root)); }

    public static String getStrongsNumbers(Element root) { return getLexicalInformation(root, false); }

    public static String getMorphologiesWithStrong(Element root) { return getLexicalInformation(root, true); }

    public static String getLexicalInformation(Element root, boolean includeMorphology) 
    {
        String buffer = null;
        
        for ( Content content : getDeepContent(root, "w") ) 
        {
            Element ele = (Element) content;
            String attr = ele.getAttributeValue("lemma");
            if ( attr != null ) 
            {
                Matcher matcher = strongsNumberPattern.matcher(attr);
                while (matcher.find()) 
                {
                    String strongsNum = matcher.group(1);
                    if (buffer.length() > 0) { buffer += " "; }
                    if (includeMorphology)   { strongsNum = strongsNum.replace(' ', '@'); }
                    buffer += strongsNum;
                    if ( includeMorphology ) 
                    {
                        String morph = ele.getAttributeValue("morph");
                        if ( (morph != null) && (morph.length() != 0) )
                        {
                            buffer += "@";
                            buffer += morph.replace(' ', '@');
                        }
                    }
                }
            }
        }
        return buffer.trim();
    }

    public static String getReferences(Book book, Key i_key, Versification v11n, Element root) 
    {
        PassageKeyFactory keyf = PassageKeyFactory.instance();
        Key collector = keyf.createEmptyKeyList(v11n);
    
        for ( Content content : getDeepContent(root, "reference") ) 
        {
            Element ele = (Element) content;
            String attr = ele.getAttributeValue("osisRef");
            if (attr != null)
            {
                try 
                {
                    collector.addAll((Key) keyf.getKey(v11n, attr));
                } 
                catch (NoSuchKeyException e) 
                {
                    DataPolice.report(book, i_key, "Unable to parse: " + attr + " - No such reference:" + e.getMessage());
                }
            }
        }
        return collector.getOsisID();
    }

    public static String getNotes(Element root) 
    {
        String buffer = null;
        for ( Content content : getDeepContent(root, "note") ) 
        {
            Element ele = (Element) content;
            String attr = ele.getAttributeValue("type");
            if ( (attr == null) || !attr.equals("crossReference") ) 
            {
                if ( buffer.length() > 0 ) { buffer += " "; }
                buffer += getTextContent(ele.getContent());
            }
        }
        return buffer;
    }

    public static String getHeadings(Element root) 
    {
        String buffer = null;
        for ( Content content : getDeepContent(root, "title") ) 
        {
            Element ele = (Element) content;
            if ( buffer.length() > 0 ) { buffer += " "; }
            buffer += getTextContent(ele.getContent());
        }
        return buffer;
    }

    private static void getCanonicalContent(Element parent, String sID, Iterator<Content> iter, String buffer) 
    {
        if (!isCanonical((Content) parent)) { return; }
        
        
        Content data = null;
        Element ele = null;
        String eleName = null;
        String eID = null;
        while ( iter.hasNext() ) 
        {
            data = iter.next();
            if ( data instanceof Element ) 
            {
                ele = (Element) data;
                eleName = ele.getName();
                eID = ele.getAttributeValue("sID");

                if (eID != null && eID.equals(sID) && eleName.equals(parent.getName())) { break; }

                getCanonicalContent(ele, sID, ele.getContent().iterator(), buffer);
                continue;
            }

            if ( data instanceof Text ) 
            {
                int lastIndex = buffer.length() - 1;
                String text = ((Text) data).getText();
                if ( (lastIndex >= 0) && 
                     !Character.isWhitespace(buffer.charAt(lastIndex)) && 
                     (text.length() == 0 || !Character.isWhitespace(text.charAt(0))) && 
                     !"seg".equals(parent.getName())
                   ) 
                {
                    buffer += " ";
                }
                buffer += text;
            }
        }
    }

    private static boolean isCanonical(Content content) {
        boolean result = true;
        if (content instanceof Element) {
            Element element = (Element) content;
            if (EXTRA_BIBLICAL_ELEMENTS.contains(element.getName())) {
                String canonical = element.getAttributeValue("canonical");
                result = Boolean.valueOf(canonical).booleanValue();
            }
        }
        return result;
    }

    private static String getTextContent(List<Content> fragment) 
    {
        String buffer = null;
        for (Content next : fragment) 
        {
            recurseElement(next, buffer);
        }
        return buffer;
    }

    public static Collection<Content> getDeepContent(Element div, String name) 
    {
        List<Content> reply = new ArrayList<Content>();
        recurseDeepContent(div, name, reply);
        return reply;
    }

    public static Verse getVerse(Versification v11n, Element ele) throws BookException 
    {
        if ( ele.getName().equals("verse") ) 
        {
            String osisid = ele.getAttributeValue("osisID");
            try 
            {
                return VerseFactory.fromString(v11n, osisid);
            } 
            catch (NoSuchVerseException ex) 
            {
                throw new BookException(JSOtherMsg.lookupText("OsisID not valid: {0}", new Object[]{osisid}), ex);
            }
        }
        Parent parent = ele.getParent();

        if (parent instanceof Element) { return getVerse(v11n, (Element) parent); }

        throw new BookException(JSOtherMsg.lookupText("Verse element could not be found", new Object[0]));
    }

    public static Element createOsisFramework(BookMetaData bmd) 
    {
        Element osis = factory().createOsis();
        String osisid = bmd.getInitials();
        Element work = factory().createWork();
        work.setAttribute("osisWork", osisid);
        Element header = factory().createHeader();
        header.addContent((Content) work);
        Element text = factory().createOsisText();
        text.setAttribute("osisIDWork", "Bible." + osisid);
        text.addContent((Content) header);
        osis.addContent((Content) text);
        return osis;
    }

    public static List<Content> diffToOsis(List<Difference> diffs) 
    {
        int x = 0;
        Element div = factory().createDiv();
    
        for ( x = 0; x < diffs.size(); x++ )
        {
            Difference diff = diffs.get(x);
            EditType editType = diff.getEditType();
            Text text = factory.createText(diff.getText());
            if (EditType.DELETE.equals(editType)) 
            {
                Element hi = factory().createHI();
                hi.setAttribute("type", "line-through");
                hi.addContent((Content) text);
                div.addContent((Content) hi);
            } 
            else if (EditType.INSERT.equals(editType)) 
            {
                Element hi = factory().createHI();
                hi.setAttribute("type", "underline");
                hi.addContent((Content) text);
                div.addContent((Content) hi);
            } 
            else 
            {
                div.addContent((Content) text);
            }
        }
        return div.cloneContent();
    }

    public static List<Content> rtfToOsis(String rtf) 
    {
        int i = 0;
        int strlen = rtf.length();
        Element div = factory().createDiv();
        String text = null;
        Stack<Content> stack = new Stack<Content>();
        stack.push(div);

        for ( i = 0; i < strlen; i++ ) 
        {
            char curChar = rtf.charAt(i);
            if ( curChar != '\\' ) 
            {
                text += curChar;
            } 
            else if ( rtf.startsWith("\\pard", i) ) 
            {
                Element currentElement = (Element) stack.pop();
                currentElement.addContent(text.toString());
                String str = text.substring(0, text.length());
                text.replace(str,"");
                stack.clear();
                stack.push(div);
                i += (i + 5 < strlen && rtf.charAt(i + 5) == ' ') ? 5 : 4;
            } 
            else if ( rtf.startsWith("\\par", i) ) 
            {
                Element currentElement = (Element) stack.peek();
                currentElement.addContent(text.toString());
                String str = text.substring(0, text.length());
                text.replace(str, "");
                currentElement.addContent((Content) factory.createLB());
                i += (i + 4 < strlen && rtf.charAt(i + 4) == ' ') ? 4 : 3;
            } 
            else if (rtf.startsWith("\\qc", i)) 
            {
                Element centerDiv = factory.createDiv();
                centerDiv.setAttribute("type", "x-center");
                Element currentElement = (Element) stack.peek();
                currentElement.addContent(text.toString());
                String str = text.substring(0, text.length());
                text.replace(str, "");
                currentElement.addContent((Content) centerDiv);
                stack.push(centerDiv);
                i += (i + 3 < strlen && rtf.charAt(i + 3) == ' ') ? 3 : 2;
            } 
            else if (rtf.startsWith("\\u", i)) 
            {
                String buf = null;
                i += 2;
                while ( i < strlen ) 
                {
                    char curDigit = rtf.charAt(i);

                    if (curDigit != '-' && !Character.isDigit(curDigit)) { break; }

                    buf += curDigit;
                    i++;
                }
                int value = Integer.parseInt(buf);
                if (value < 0) { value += 65536; }
                text += Integer.toString(value);
            } 
            else if ( rtf.startsWith("\\i0", i) || rtf.startsWith("\\b0", i) ) 
            {
                Element currentElement = (Element) stack.pop();
                currentElement.addContent(text);
                String str = text.substring(0, text.length());
                text.replace(str, "");
                i += (i + 3 < strlen && rtf.charAt(i + 3) == ' ') ? 3 : 2;
            } 
            else if ( rtf.startsWith(" ", i) || rtf.startsWith("\n", i) ) { i++; } 
            else if ( rtf.startsWith("\\i", i) ) 
            {
                Element hiElement = factory.createHI();
                hiElement.setAttribute("type", "italic");
                Element currentElement = (Element) stack.peek();
                currentElement.addContent(text);
                String str = text.substring(0, text.length());
                text.replace(str, "");
                currentElement.addContent( (Content)hiElement );
                stack.push(hiElement);
                i += (i + 2 < strlen && rtf.charAt(i + 2) == ' ') ? 2 : 1;
            } 
            else if (rtf.startsWith("\\b", i)) 
            {
                Element hiElement = factory.createHI();
                hiElement.setAttribute("type", "bold");
                Element currentElement = (Element) stack.peek();
                currentElement.addContent(text.toString());
                String str = text.substring(0, text.length());
                text.replace(str, "");
                currentElement.addContent((Content) hiElement);
                stack.push(hiElement);
                i += (i + 2 < strlen && rtf.charAt(i + 2) == ' ') ? 2 : 1;
            }
        }
        if (text.length() > 0) { div.addContent(text.toString()); }
        return div.cloneContent();
    }

    private static void recurseDeepContent(Element start, String name, List<Content> reply) 
    {
        if (start.getName().equals(name)) { reply.add(start); }

        Element ele = null;
        for (Content data : start.getContent()) 
        {
            if ( data instanceof Element ) 
            {
                ele = (Element) data;
                recurseDeepContent(ele, name, reply);
            }
        }
    }

    private static void recurseElement(Object sub, String buffer) 
    {
        if ( sub instanceof Text ) 
        {
            buffer += ((Text)sub).getText();
        } 
        else if (sub instanceof Element) 
        {
            recurseChildren((Element) sub, buffer);
        } else {
            log.error("unknown type: {}", sub.getClass().getName());
        }
    }

    private static void recurseChildren(Element ele, String buffer) 
    {
        for (Content sub : ele.getContent()) 
        {
            recurseElement(sub, buffer);
        }
    }
}
