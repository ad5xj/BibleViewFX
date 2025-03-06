package org.crosswire.jsword.book.filter.gbf;

import org.crosswire.common.util.ClassUtil;

import org.crosswire.common.xml.XMLUtil;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;

import java.util.LinkedList;

import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Text;

public final class GBFTags
{

    private static final OSISUtil.OSISFactory OSIS_FACTORY = OSISUtil.factory();

    public static final class DefaultEndTag extends AbstractTag
    {

        public DefaultEndTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            if (stack.isEmpty())
            {
                DataPolice.report(book, key, "Ignoring end tag without corresponding start tag: " + getName());
                return;
            }
            stack.removeFirst();
        }

    }

    public static final class BoldStartTag extends AbstractTag
    {

        public BoldStartTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            Element ele = GBFTags.OSIS_FACTORY.createHI();
            ele.setAttribute("type", "bold");
            GBFTags.updateOsisStack(stack, (Content) ele);
        }

    }

    public static final class BookTitleStartTag extends AbstractTag
    {

        public BookTitleStartTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            Element ele = GBFTags.OSIS_FACTORY.createTitle();
            ele.setAttribute("type", "main");
            GBFTags.updateOsisStack(stack, (Content) ele);
        }

    }

    public static final class CrossRefStartTag extends AbstractTag
    {

        public CrossRefStartTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            Element ele = GBFTags.OSIS_FACTORY.createReference();
            String refstr = getName().substring(2);
            try
            {
                Passage ref = (Passage) book.getKey(refstr);
                ele.setAttribute("osisRef", ref.getOsisRef());
            }
            catch (NoSuchKeyException ex)
            {
                DataPolice.report(book, key, "unable to parse reference: " + refstr);
            }
            GBFTags.updateOsisStack(stack, (Content) ele);
        }

    }

    public static final class EOLTag extends AbstractTag
    {

        public EOLTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            Element p = GBFTags.OSIS_FACTORY.createLB();
            if (stack.isEmpty())
            {
                stack.addFirst(p);
            }
            else
            {
                Content top = stack.get(0);
                if (top instanceof Element)
                {
                    Element current = (Element) top;
                    current.addContent((Content) p);
                }
            }
        }

    }

    public static final class FootnoteStartTag extends AbstractTag
    {

        public FootnoteStartTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            Element ele = GBFTags.OSIS_FACTORY.createNote();
            ele.setAttribute("type", "x-StudyNote");
            GBFTags.updateOsisStack(stack, (Content) ele);
        }

    }

    public static final class FootnoteEndTag extends AbstractTag
    {

        public FootnoteEndTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            if (stack.isEmpty())
            {
                DataPolice.report(book, key, "Ignoring end tag without corresponding start tag: " + getName());
                return;
            }
            Object pop = stack.removeFirst();
            if (!(pop instanceof Element))
            {
                DataPolice.report(book, key, "expected to pop a Note, but found " + ClassUtil.getShortClassName(pop.getClass()));
                return;
            }
            Element note = (Element) pop;
            if (note.getContentSize() < 1)
            {
                Content top = stack.get(0);
                if (top instanceof Element)
                {
                    Element ele = (Element) top;
                    ele.removeContent((Content) note);
                }
            }
        }

    }

    public static final class HeaderStartTag extends AbstractTag
    {

        public HeaderStartTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            GBFTags.updateOsisStack(stack, (Content) GBFTags.OSIS_FACTORY.createTitle());
        }

    }

    public static final class IgnoredTag extends AbstractTag
    {

        public IgnoredTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
        }

    }

    public static final class ItalicStartTag extends AbstractTag
    {

        public ItalicStartTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            Element ele = GBFTags.OSIS_FACTORY.createHI();
            ele.setAttribute("type", "italic");
            GBFTags.updateOsisStack(stack, (Content) ele);
        }

    }

    public static final class JustifyRightTag extends AbstractTag
    {

        public JustifyRightTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            Element ele = GBFTags.OSIS_FACTORY.createSeg();
            ele.setAttribute("type", "text-align: right;");
            GBFTags.updateOsisStack(stack, (Content) ele);
        }

    }

    public static final class JustifyLeftTag extends AbstractTag
    {

        public JustifyLeftTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            Element ele = GBFTags.OSIS_FACTORY.createSeg();
            ele.setAttribute("type", "text-align: left;");
            GBFTags.updateOsisStack(stack, (Content) ele);
        }

    }

    public static final class OTQuoteStartTag extends AbstractTag
    {

        public OTQuoteStartTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            GBFTags.updateOsisStack(stack, (Content) GBFTags.OSIS_FACTORY.createQ());
        }

    }

    public static final class ParagraphTag extends AbstractTag
    {

        public ParagraphTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            if (stack.isEmpty())
            {
                Element p = GBFTags.OSIS_FACTORY.createLB();
                stack.addFirst(p);
            }
            else
            {
                Element p = GBFTags.OSIS_FACTORY.createP();
                Content top = stack.get(0);
                if (top instanceof Element)
                {
                    Element current = (Element) top;
                    current.addContent((Content) p);
                }
            }
        }

    }

    public static final class PoetryStartTag extends AbstractTag
    {

        public PoetryStartTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            GBFTags.updateOsisStack(stack, (Content) GBFTags.OSIS_FACTORY.createLG());
        }

    }

    public static final class PsalmStartTag extends AbstractTag
    {

        public PsalmStartTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            Element title = GBFTags.OSIS_FACTORY.createTitle();
            title.setAttribute("type", "psalm");
            title.setAttribute("subType", "x-preverse");
            title.setAttribute("canonical", "true");
            GBFTags.updateOsisStack(stack, (Content) title);
        }

    }

    public static final class RedLetterStartTag extends AbstractTag
    {

        public RedLetterStartTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            Element ele = GBFTags.OSIS_FACTORY.createQ();
            ele.setAttribute("who", "Jesus");
            GBFTags.updateOsisStack(stack, (Content) ele);
        }

    }

    public static final class StrongsMorphTag extends AbstractTag
    {

        public StrongsMorphTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            String name = getName().trim();
            Content top = stack.get(0);
            if (top instanceof Element)
            {
                Element ele = (Element) top;
                int size = ele.getContentSize();
                if (size == 0)
                {
                    DataPolice.report(book, key, "No content to attach Strong's Morph tag to: <" + name + ">.");
                    return;
                }
                int lastIndex = size - 1;
                Content prevObj = ele.getContent(lastIndex);
                Element word = null;
                if (prevObj instanceof Text)
                {
                    word = GBFTags.OSIS_FACTORY.createW();
                    ele.removeContent(prevObj);
                    word.addContent(prevObj);
                    ele.addContent((Content) word);
                }
                else
                {
                    if (prevObj instanceof Element)
                    {
                        word = (Element) prevObj;
                    }
                    else
                    {
                        DataPolice.report(book, key, "No words to attach Strong's Morph tag to: <" + name + ">.");
                        return;
                    }
                }
                String existingMorph = word.getAttributeValue("morph");
                StringBuilder newMorph = new StringBuilder();
                if (existingMorph != null && existingMorph.length() > 0)
                {
                    newMorph.append(existingMorph).append('|');
                }
                newMorph.append("x-StrongsMorph:T").append(name.substring(2));
                word.setAttribute("morph", newMorph.toString());
            }
        }

    }

    public static final class StrongsWordTag extends AbstractTag
    {

        public StrongsWordTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            boolean empty = false;
            String name = getName().trim();
            Element word = null;
            Content top = stack.get(0);
            if (top instanceof Element)
            {
                Element ele = (Element) top;
                int size = ele.getContentSize();
                if (size > 0)
                {
                    int lastIndex = size - 1;
                    Content prevObj = ele.getContent(lastIndex);
                    if (prevObj instanceof Text)
                    {
                        Text textItem = (Text) prevObj;
                        word = GBFTags.OSIS_FACTORY.createW();
                        ele.removeContent((Content) textItem);
                        word.addContent((Content) textItem);
                        ele.addContent((Content) word);
                    }
                    else
                    {
                        if (prevObj instanceof Element)
                        {
                            word = (Element) prevObj;
                        }
                    }
                }
            }
            if (word == null)
            {
                word = GBFTags.OSIS_FACTORY.createW();
                empty = true;
            }
            String existingLemma = word.getAttributeValue("lemma");
            StringBuilder newLemma = new StringBuilder();
            if (existingLemma != null && existingLemma.length() > 0)
            {
                newLemma.append(existingLemma).append(' ');
            }
            newLemma.append("strong:").append(name.substring(1));
            word.setAttribute("lemma", newLemma.toString());
            if (empty)
            {
                top = stack.getLast();
                if (top instanceof Element)
                {
                    Element ele = (Element) top;
                    ele.addContent((Content) word);
                }
            }
        }

    }

    public static final class TextFootnoteTag extends AbstractTag
    {

        public TextFootnoteTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            Element ele = GBFTags.OSIS_FACTORY.createNote();
            ele.setAttribute("type", "x-StudyNote");
            GBFTags.updateOsisStack(stack, (Content) ele);
        }

    }

    public static final class TextTag extends AbstractTag
    {

        public TextTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            String text = XMLUtil.escape(getName());
            if (stack.isEmpty())
            {
                stack.addFirst(new Text(text));
            }
            else
            {
                Content top = stack.get(0);
                if (top instanceof Element)
                {
                    Element ele = (Element) top;
                    if ("w".equals(ele.getName()))
                    {
                        top = stack.getLast();
                        if (top instanceof Element)
                        {
                            ele = (Element) top;
                            ele.addContent((Content) new Text(text));
                        }
                    }
                    else
                    {
                        ele.addContent(text);
                    }
                }
            }
        }

    }

    public static final class TitleStartTag extends AbstractTag
    {

        public TitleStartTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            GBFTags.updateOsisStack(stack, (Content) GBFTags.OSIS_FACTORY.createTitle());
        }

    }

    public static final class UnderlineStartTag extends AbstractTag
    {

        public UnderlineStartTag(String name)
        {
            super(name);
        }

        public void updateOsisStack(Book book, Key key, LinkedList<Content> stack)
        {
            Element ele = GBFTags.OSIS_FACTORY.createHI();
            ele.setAttribute("type", "underline");
            GBFTags.updateOsisStack(stack, (Content) ele);
        }

    }

    static void updateOsisStack(LinkedList<Content> stack, Content content)
    {
        Content top = stack.get(0);
        if (top instanceof Element)
        {
            Element current = (Element) top;
            current.addContent(content);
            stack.addFirst(content);
        }
    }
}