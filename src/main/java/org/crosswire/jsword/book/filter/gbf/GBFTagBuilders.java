package org.crosswire.jsword.book.filter.gbf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

import java.util.HashMap;
import java.util.Map;

public final class GBFTagBuilders {

    private static final Logger lgr = LoggerFactory.getLogger(GBFTagBuilders.class);
    private static final String THISMODULE = "GBFTagBuilders";
    private static final Map<String, TagBuilder> BUILDERS = new HashMap<String, TagBuilder>();

    public static Tag getTag(Book book, Key key, String name)
    {
        Tag tag = null;
        if (name.startsWith("W") && (name.contains("-") || name.contains(":")) && name.matches("WT?[GH] ?[0-9]+[-:][0-9abc-]+"))
        {
            return null;
        }
        int length = name.length();
        if (length > 0)
        {
            TagBuilder builder = null;
            if (length == 2)
            {
                builder = BUILDERS.get(name);
            }
            else
            {
                if (length > 2) { builder = BUILDERS.get(name.substring(0, 2)); }
            }
            if (builder != null) { tag = builder.createTag(name); }
            if (tag == null)
            {
                String msg = "getTag(): ignoring tag ,"+key.getName()+">";
                lgr.warn(msg, THISMODULE);
            }
        }
        return tag;
    }

    public static Tag getTextTag(String text)  { return new GBFTags.TextTag(text); }

    static final class BoldStartTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.BoldStartTag(name); }
    }

    static final class BookTitleStartTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.BookTitleStartTag(name); }
    }

    static final class CrossRefStartTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.CrossRefStartTag(name); }
    }

    static final class DefaultEndTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.DefaultEndTag(name); }
    }

    static final class EndOfLineTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.EOLTag(name); }
    }

    static final class EscapeTagBuilder implements TagBuilder
    {
        public Tag createTag(String name)
        {
            if ("CG".equals(name)) {return new GBFTags.TextTag("&gt;"); }
            return new GBFTags.TextTag("&lt;");
        }
    }

    static final class FootnoteStartTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.FootnoteStartTag(name); }
    }

    static final class FootnoteEndTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.FootnoteEndTag(name); }
    }

    static final class HeaderStartTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.HeaderStartTag(name); }
    }

    static final class IgnoredTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.IgnoredTag(name); }
    }

    static final class ItalicStartTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.ItalicStartTag(name); }
    }

    static final class JustifyRightTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.JustifyRightTag(name); }
    }

    static final class OTQuoteStartTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.OTQuoteStartTag(name); }
    }

    static final class ParagraphTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.ParagraphTag(name); }
    }

    static final class PoetryStartTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.PoetryStartTag(name); }
    }

    static final class PsalmTitleStartTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.PsalmStartTag(name); }
    }

    static final class RedLetterStartTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.RedLetterStartTag(name); }
    }

    static final class StrongsMorphTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.StrongsMorphTag(name); }
    }

    static final class StrongsWordTagBuilder implements TagBuilder
    {
        public Tag createTag(String name) { return new GBFTags.StrongsWordTag(name); }
    }

    static final class TextFootnoteTagBuilder implements TagBuilder
    {
        public Tag createTag(String name)  { return new GBFTags.TextFootnoteTag(name); }
    }

    static final class TitleStartTagBuilder implements TagBuilder
    {
        public Tag createTag(String name)  { return new GBFTags.TitleStartTag(name); }
    }

    static final class UnderlineStartTagBuilder implements TagBuilder
    {
        public Tag createTag(String name)  { return new GBFTags.UnderlineStartTag(name); }
    }

    static
    {
        TagBuilder defaultEndTagBuilder = new DefaultEndTagBuilder();
        TagBuilder ignoreTagBuilder = new IgnoredTagBuilder();

        BUILDERS.put("FB", new BoldStartTagBuilder());
        BUILDERS.put("Fb", defaultEndTagBuilder);
        BUILDERS.put("FI", new ItalicStartTagBuilder());
        BUILDERS.put("Fi", defaultEndTagBuilder);
        BUILDERS.put("FR", new RedLetterStartTagBuilder());
        BUILDERS.put("Fr", defaultEndTagBuilder);
        BUILDERS.put("FU", new UnderlineStartTagBuilder());
        BUILDERS.put("Fu", defaultEndTagBuilder);
        BUILDERS.put("RX", new CrossRefStartTagBuilder());
        BUILDERS.put("Rx", defaultEndTagBuilder);
        BUILDERS.put("CL", new EndOfLineTagBuilder());
        BUILDERS.put("CM", new ParagraphTagBuilder());
        BUILDERS.put("RF", new FootnoteStartTagBuilder());
        BUILDERS.put("Rf", new FootnoteEndTagBuilder());
        BUILDERS.put("RB", new TextFootnoteTagBuilder());
        BUILDERS.put("TS", new HeaderStartTagBuilder());
        BUILDERS.put("Ts", defaultEndTagBuilder);
        BUILDERS.put("TB", new PsalmTitleStartTagBuilder());
        BUILDERS.put("Tb", defaultEndTagBuilder);
        BUILDERS.put("TH", new TitleStartTagBuilder());
        BUILDERS.put("Th", defaultEndTagBuilder);
        BUILDERS.put("TT", new BookTitleStartTagBuilder());
        BUILDERS.put("Tt", defaultEndTagBuilder);
        BUILDERS.put("BA", ignoreTagBuilder);
        BUILDERS.put("BC", ignoreTagBuilder);
        BUILDERS.put("BI", ignoreTagBuilder);
        BUILDERS.put("BN", ignoreTagBuilder);
        BUILDERS.put("BO", ignoreTagBuilder);
        BUILDERS.put("BP", ignoreTagBuilder);
        BUILDERS.put("JR", new JustifyRightTagBuilder());
        BUILDERS.put("JC", ignoreTagBuilder);
        BUILDERS.put("JL", ignoreTagBuilder);
        BUILDERS.put("FO", new OTQuoteStartTagBuilder());
        BUILDERS.put("Fo", defaultEndTagBuilder);
        BUILDERS.put("PP", new PoetryStartTagBuilder());
        BUILDERS.put("Pp", defaultEndTagBuilder);

        TagBuilder builder = new StrongsWordTagBuilder();

        BUILDERS.put("WH", builder);
        BUILDERS.put("WG", builder);
        BUILDERS.put("WT", new StrongsMorphTagBuilder());
        BUILDERS.put("CG", new EscapeTagBuilder());
        BUILDERS.put("CT", new EscapeTagBuilder());
    }
}