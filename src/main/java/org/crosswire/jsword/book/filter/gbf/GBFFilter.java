package org.crosswire.jsword.book.filter.gbf;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.passage.Key;
import org.jdom2.Content;
import org.jdom2.Element;

public class GBFFilter implements SourceFilter {

    private static final String SEPARATORS = " ,:;.?!";

    public List<Content> toOSIS(Book book, Key key, String plain)
    {
        Element ele = OSISUtil.factory().createDiv();
        LinkedList<Content> stack = new LinkedList<Content>();
        stack.addFirst(ele);
        List<Tag> taglist = parseTags(book, key, plain.trim());
        while (!taglist.isEmpty())
        {
            Tag tag = taglist.remove(0);
            tag.updateOsisStack(book, key, stack);
        }
        stack.removeFirst();
        return ele.removeContent();
    }

    public GBFFilter clone()
    {
        GBFFilter clone = null;
        try
        {
            clone = (GBFFilter) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            assert false : e;
        }
        return clone;
    }

    private List<Tag> parseTags(Book book, Key key, String aRemains)
    {
        String remains = aRemains;
        List<Tag> taglist = new ArrayList<Tag>();
        while (true)
        {
            int ltpos = remains.indexOf('<');
            int gtpos = remains.indexOf('>', ltpos + 1);
            if (ltpos == -1 || gtpos == -1)
            {
                if (ltpos >= 0 && ltpos < remains.length() + 1 && Character.isUpperCase(remains.charAt(ltpos + 1)))
                {
                    DataPolice.report(book, key, "Possible bad GBF tag" + remains);
                }
                if (gtpos != -1 && ltpos >= 0)
                {
                    DataPolice.report(book, key, "Possible bad GBF tag" + remains);
                }
                int pos = Math.max(ltpos, gtpos) + 1;
                if (pos == 0 || pos == remains.length())
                {
                    taglist.add(GBFTagBuilders.getTextTag(remains));
                    break;
                }
                taglist.add(GBFTagBuilders.getTextTag(remains.substring(0, pos)));
                remains = remains.substring(pos);
                continue;
            }
            char firstChar = remains.charAt(ltpos + 1);
            if (!Character.isUpperCase(firstChar))
            {
                taglist.add(GBFTagBuilders.getTextTag(remains.substring(0, gtpos + 1)));
                remains = remains.substring(gtpos + 1);
                continue;
            }
            String start = remains.substring(0, ltpos);
            int strLen = start.length();
            if (strLen > 0)
            {
                int beginIndex = 0;
                boolean inSepStr = (" ,:;.?!".indexOf(start.charAt(0)) >= 0);
                for (int i = 1; inSepStr && i < strLen; i++)
                {
                    char currentChar = start.charAt(i);
                    if (" ,:;.?!".indexOf(currentChar) < 0)
                    {
                        taglist.add(GBFTagBuilders.getTextTag(start.substring(beginIndex, i)));
                        beginIndex = i;
                        inSepStr = false;
                    }
                }
                if (beginIndex < strLen)
                {
                    taglist.add(GBFTagBuilders.getTextTag(start.substring(beginIndex)));
                }
            }
            String tag = remains.substring(ltpos + 1, gtpos);
            int length = tag.length();
            if (length > 0)
            {
                Tag reply = GBFTagBuilders.getTag(book, key, tag);
                if (reply != null)
                {
                    taglist.add(reply);
                }
            }
            remains = remains.substring(gtpos + 1);
        }
        return taglist;
    }
}