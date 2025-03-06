/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief An Enumeration of the possible Features a Book may have.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public enum FeatureType 
{
    GREEK_DEFINITIONS("GreekDef"),      /// The book is one of Greek Definitions. AKA, Strong's.
    GREEK_PARSE("GreekParse"),          /// The book is one of Greek word parsings. AKA, Robinson.
    HEBREW_DEFINITIONS("HebrewDef"),    /// The book is one of Hebrew Definitions. AKA, Strong's.
    HEBREW_PARSE("HebrewParse"),        /// The book is one of Hebrew word parsings. AKA, ???.
    DAILY_DEVOTIONS("DailyDevotions"),  /// The book is one of Daily Devotions.
    GLOSSARY("Glossary"),               /// The book is one of Glossary
    STRONGS_NUMBERS("StrongsNumbers", "Strongs"), /// The book contains Strong's Numbers. The alias is used to match
    FOOTNOTES("Footnotes"),             /// The book contains footnotes
    SCRIPTURE_REFERENCES("Scripref"),   /// The book contains Scripture cross references
    WORDS_OF_CHRIST("RedLetterWords"),  /// The book marks the Word&rsquo;s of Christ 
    MORPHOLOGY("Morph"),                /// The book contains Morphology info
    HEADINGS("Headings"),               /// The book contains Headings 
    GLOSSES("Glosses", "Ruby");         /// The book contains Headings

    private String name;                /// The name of the FeatureType
    private String alias;               /// The alias of the FeatureType

    /**
     * @param name The name of the FeatureType
     */
    FeatureType(String name) { this(name, ""); }

    /**
     * @param name The name of the FeatureType
     * @param alias The alias of the FeatureType
     */
    FeatureType(String i_name, String i_alias) 
    {
        name = i_name;
        alias = i_alias;
    }

    /**
     * @brief Lookup method to convert from a String
     *
     * @param name the name of a FeatureType
     * @return v the matching FeatureType
     */
    public static FeatureType fromString(String name) 
    {
        for ( FeatureType v : values() ) 
        {
            if (v.name.equalsIgnoreCase(name)) { return v; }
        }

        // should not reach here
        assert false;
        return null;
    }

    /* 
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() { return name; }

    /**
     * @brief Get the alias for this feature.
     *
     * @return the alias
     */
    public String getAlias() { return alias; }
}