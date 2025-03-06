package org.crosswire.common.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.ResourceUtil;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaders;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public final class XMLUtil {
    private static final String THISMODULE = "XMLUitl";

    public static Document getDocument(String subject) throws JDOMException, IOException {
        Document doc = null;
        String resource = subject + ".xml";
        InputStream in = ResourceUtil.getResourceAsStream(resource);
        log.debug("Loading {}.xml from classpath: [OK]", subject);
        DocumentBuilder builder = null;
        try {
            doc = builder.parse(in);
        } catch ( SAXException e ) {
            log.error("getDocument(): Error parsing input - "+  e.getMessage(),THISMODULE);
        }        
        return doc;
    }

    public static String writeToString(SAXEventProvider provider) throws SAXException {
        ContentHandler ser = new PrettySerializingContentHandler();
        provider.provideSAXEvents(ser);
        return ser.toString();
    }

    public static String getAttributeName(Attributes attrs, int index) {
        String qName = attrs.getQName(index);
        if (qName != null) {
            return qName;
        }
        return attrs.getLocalName(index);
    }

    public static void debugSAXAttributes(Attributes attrs) {
        for (int i = 0; i < attrs.getLength(); i++) {
            log.debug("attr[{}]: {}={}", new Object[]{Integer.toString(i), attrs.getQName(i), attrs.getValue(i)});
        }
    }

    public static String escape(String s) {
        if (s == null) {
            return s;
        }
        int len = s.length();
        StringBuilder str = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '<':
                    str.append("&lt;");
                    break;
                case '>':
                    str.append("&gt;");
                    break;
                case '&':
                    str.append("&amp;");
                    break;
                case '"':
                    str.append("&quot;");
                    break;
                default:
                    str.append(ch);
                    break;
            }
        }
        return str.toString();
    }

    public static String cleanAllEntities(String broken) {
        if (broken == null) {
            return null;
        }
        String working = broken;
        int cleanfrom = 0;
        while (true) {
            int amp = working.indexOf('&', cleanfrom);
            if (amp == -1) {
                break;
            }
            if (validCharacterEntityPattern.matcher(working.substring(amp)).find()) {
                cleanfrom = working.indexOf(';', amp) + 1;
                continue;
            }
            int i = amp + 1;
            while (true) {
                if (i >= working.length()) {
                    return working.substring(0, amp) + "&amp;" + working.substring(amp + 1);
                }
                char c = working.charAt(i);
                if (c == ';') {
                    String entity = working.substring(amp, i + 1);
                    String replace = handleEntity(entity);
                    working = working.substring(0, amp) + replace + working.substring(i + 1);
                    break;
                }
                if (!Character.isLetterOrDigit(c)) {
                    working = working.substring(0, amp) + "&amp;" + working.substring(amp + 1);
                    amp = i + 4;
                    break;
                }
                i++;
            }
            cleanfrom = amp + 1;
        }
        return working;
    }

    public static String cleanAllCharacters(String broken) {
        return invalidCharacterPattern.matcher(broken).replaceAll(" ");
    }

    public static String recloseTags(String broken) {
        String result = broken;
        while (result.matches(".*</[a-zA-Z]+>[ \t\r\n]*")) {
            result = result.substring(0, result.lastIndexOf('<'));
        }
        List<String> openTags = new ArrayList<String>();
        Matcher m = Pattern.compile("</?[a-zA-Z]+").matcher(result);
        boolean lTagFound = false;
        boolean lgTagFound = false;
        while (m.find()) {
            String match = m.group();
            if (match.startsWith("</")) {
                if (openTags.size() == 0 && "</l".equals(match) && !lTagFound) {
                    return recloseTags("<l>" + broken);
                }
                if (openTags.size() == 0 && "</lg".equals(match) && !lgTagFound) {
                    return recloseTags("<lg>" + broken);
                }
                if (openTags.size() == 0) {
                    return null;
                }
                String lastTag = openTags.remove(openTags.size() - 1);
                if (!("</" + lastTag).equals(match)) {
                    return null;
                }
                continue;
            }
            int closePos = result.indexOf('>', m.end());
            if (closePos == -1) {
                return null;
            }
            while (Character.isWhitespace(result.charAt(closePos - 1))) {
                closePos--;
            }
            if (result.charAt(closePos - 1) != '/') {
                if ("<l".equals(match)) {
                    lTagFound = true;
                }
                if ("<lg".equals(match)) {
                    lgTagFound = true;
                }
                openTags.add(match.substring(1));
            }
        }
        Collections.reverse(openTags);
        for (String openTag : openTags) {
            result = result + "</" + openTag + ">";
        }
        return result;
    }

    public static String closeEmptyTags(String broken) {
        if (broken == null) {
            return null;
        }
        return openHTMLTagPattern.matcher(broken).replaceAll("<$1$2/>");
    }

    public static String cleanAllTags(String broken) {
        if (broken == null) {
            return null;
        }
        String working = broken;
        while (true) {
            int lt = working.indexOf('<');
            if (lt == -1) {
                break;
            }
            int i = lt;
            int startattr = -1;
            while (true) {
                i++;
                if (i >= working.length()) {
                    i--;
                    break;
                }
                char c = working.charAt(i);
                if (c == '>') {
                    break;
                }
                if (c == ' ') {
                    if (startattr == -1) {
                        startattr = i;
                        continue;
                    }
                    String value = working.substring(startattr, i);
                    if (value.indexOf('=') == -1) {
                        break;
                    }
                }
            }
            working = working.substring(0, lt) + " " + working.substring(i + 1);
        }
        return working;
    }

    private static String handleEntity(String entity) {
        if (goodEntities.contains(entity)) {
            return entity;
        }
        String replace = badEntities.get(entity);
        if (replace != null) {
            return replace;
        }
        return " ";
    }

    private static Set<String> goodEntities = new HashSet<String>();

    private static PropertyMap badEntities = new PropertyMap();

    static {
        goodEntities.add("&quot;");
        goodEntities.add("&amp;");
        goodEntities.add("&lt;");
        goodEntities.add("&gt;");
        badEntities.put("&euro;", "\u20AC");
        badEntities.put("&lsquo;", "\u2018");
        badEntities.put("&rsquo;", "\u2019");
        badEntities.put("&nbsp;", "\u00A0");
        badEntities.put("&iexcl;", "\u00A1");
        badEntities.put("&cent;", "\u00A2");
        badEntities.put("&pound;", "\u00A3");
        badEntities.put("&curren;", "\u00A4");
        badEntities.put("&yen;", "\u00A5");
        badEntities.put("&brvbar;", "\u00A6");
        badEntities.put("&sect;", "\u00A7");
        badEntities.put("&uml;", "\u00A8");
        badEntities.put("&copy;", "\u00A9");
        badEntities.put("&ordf;", "\u00AA");
        badEntities.put("&laquo;", "\u00AB");
        badEntities.put("&not;", "\u00AC");
        badEntities.put("&shy;", "\u00AD");
        badEntities.put("&reg;", "\u00AE");
        badEntities.put("&macr;", "\u00AF");
        badEntities.put("&deg;", "\u00B0");
        badEntities.put("&plusmn;", "\u00B1");
        badEntities.put("&sup2;", "\u00B2");
        badEntities.put("&sup3;", "\u00B3");
        badEntities.put("&acute;", "\u00B4");
        badEntities.put("&micro;", "\u00B5");
        badEntities.put("&para;", "\u00B6");
        badEntities.put("&middot;", "\u00B7");
        badEntities.put("&cedil;", "\u00B8");
        badEntities.put("&sup1;", "\u00B9");
        badEntities.put("&ordm;", "\u00BA");
        badEntities.put("&raquo;", "\u00BB");
        badEntities.put("&frac14;", "\u00BC");
        badEntities.put("&frac12;", "\u00BD");
        badEntities.put("&frac34;", "\u00BE");
        badEntities.put("&iquest;", "\u00BF");
        badEntities.put("&Agrave;", "\u00C0");
        badEntities.put("&Aacute;", "\u00C1");
        badEntities.put("&Acirc;", "\u00C2");
        badEntities.put("&Atilde;", "\u00C3");
        badEntities.put("&Auml;", "\u00C4");
        badEntities.put("&Aring;", "\u00C5");
        badEntities.put("&AElig;", "\u00C6");
        badEntities.put("&Ccedil;", "\u00C7");
        badEntities.put("&Egrave;", "\u00C8");
        badEntities.put("&Eacute;", "\u00C9");
        badEntities.put("&Ecirc;", "\u00CA");
        badEntities.put("&Euml;", "\u00CB");
        badEntities.put("&Igrave;", "\u00CC");
        badEntities.put("&Iacute;", "\u00CD");
        badEntities.put("&Icirc;", "\u00CE");
        badEntities.put("&Iuml;", "\u00CF");
        badEntities.put("&ETH;", "\u00D0");
        badEntities.put("&Ntilde;", "\u00D1");
        badEntities.put("&Ograve;", "\u00D2");
        badEntities.put("&Oacute;", "\u00D3");
        badEntities.put("&Ocirc;", "\u00D4");
        badEntities.put("&Otilde;", "\u00D5");
        badEntities.put("&Ouml;", "\u00D6");
        badEntities.put("&times;", "\u00D7");
        badEntities.put("&Oslash;", "\u00D8");
        badEntities.put("&Ugrave;", "\u00D9");
        badEntities.put("&Uacute;", "\u00DA");
        badEntities.put("&Ucirc;", "\u00DB");
        badEntities.put("&Uuml;", "\u00DC");
        badEntities.put("&Yacute;", "\u00DD");
        badEntities.put("&THORN;", "\u00DE");
        badEntities.put("&szlig;", "\u00DF");
        badEntities.put("&agrave;", "\u00E0");
        badEntities.put("&aacute;", "\u00E1");
        badEntities.put("&acirc;", "\u00E2");
        badEntities.put("&atilde;", "\u00E3");
        badEntities.put("&auml;", "\u00E4");
        badEntities.put("&aring;", "\u00E5");
        badEntities.put("&aelig;", "\u00E6");
        badEntities.put("&ccedil;", "\u00E7");
        badEntities.put("&egrave;", "\u00E8");
        badEntities.put("&eacute;", "\u00E9");
        badEntities.put("&ecirc;", "\u00EA");
        badEntities.put("&euml;", "\u00EB");
        badEntities.put("&igrave;", "\u00EC");
        badEntities.put("&iacute;", "\u00ED");
        badEntities.put("&icirc;", "\u00EE");
        badEntities.put("&iuml;", "\u00EF");
        badEntities.put("&eth;", "\u00F0");
        badEntities.put("&ntilde;", "\u00F1");
        badEntities.put("&ograve;", "\u00F2");
        badEntities.put("&oacute;", "\u00F3");
        badEntities.put("&ocirc;", "\u00F4");
        badEntities.put("&otilde;", "\u00F5");
        badEntities.put("&ouml;", "\u00F6");
        badEntities.put("&divide;", "\u00F7");
        badEntities.put("&oslash;", "\u00F8");
        badEntities.put("&ugrave;", "\u00F9");
        badEntities.put("&uacute;", "\u00FA");
        badEntities.put("&ucirc;", "\u00FB");
        badEntities.put("&uuml;", "\u00FC");
        badEntities.put("&yacute;", "\u00FD");
        badEntities.put("&thorn;", "\u00FE");
        badEntities.put("&yuml;", "\u00FF");
    }

    private static Pattern validCharacterEntityPattern = Pattern.compile("^&#x?\\d{2,4};");

    private static Pattern invalidCharacterPattern = Pattern.compile("[^\t\r\n -\uD7FF\uE000-\uFFFD]");

    private static Pattern openHTMLTagPattern = Pattern.compile("<(img|hr|br)([^>]*)(?<!/)>");

    private static final Logger log = LoggerFactory.getLogger(XMLUtil.class);
}
