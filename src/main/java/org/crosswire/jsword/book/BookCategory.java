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

import org.crosswire.jsword.JSMsg;

/**
 * @ingroup org.crosswire.jsword.book
 * @brief An Enumeration of the possible types of Book.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public enum BookCategory 
{
    BIBLE("Biblical Texts", JSMsg.gettext("Biblical Texts")),
    DICTIONARY("Lexicons / Dictionaries", JSMsg.gettext("Dictionaries")),
    COMMENTARY("Commentaries", JSMsg.gettext("Commentaries")),
    DAILY_DEVOTIONS("Daily Devotional", JSMsg.gettext("Daily Devotionals")),
    GLOSSARY("Glossaries", JSMsg.gettext("Glossaries")),
    QUESTIONABLE("Cults / Unorthodox / Questionable Material", JSMsg.gettext("Cults / Unorthodox / Questionable Materials")),
    ESSAYS("Essays", JSMsg.gettext("Essays")),
    IMAGES("Images", JSMsg.gettext("Images")),
    MAPS("Maps", JSMsg.gettext("Maps")),
    GENERAL_BOOK("Generic Books", JSMsg.gettext("General Books")),
    OTHER("Other", JSMsg.gettext("Other"));

    private transient String name;         //!< The names of the BookCategory
    private transient String externalName;

    /**
     * @brief Constructor for BookCategory with params
     * 
     * @param name The name of the BookCategory
     * @param externalName the name of the BookCategory worthy of an end user
     */
    BookCategory(String name, String externalName) 
    {
        this.name = name;
        this.externalName = externalName;
    }

    /**
     * @brief Lookup method to convert from a String
     *
     * @param name the internal name of a BookCategory
     * @return the matching BookCategory
     */
    public static BookCategory fromString(String name) 
    {
        for (BookCategory o : BookCategory.values()) 
        {
            if (o.name.equalsIgnoreCase(name)) { return o; }
        }
        return OTHER;
    }

    /**
     * @brief Lookup method to convert from a String
     *
     * @param name the external name of a BookCategory
     * @return the matching BookCategory
     */
    public static BookCategory fromExternalString(String name) 
    {
        for (BookCategory o : BookCategory.values()) 
        {
            if (o.externalName.equalsIgnoreCase(name)) { return o; }
        }
        return OTHER;
    }

    /**
     * @brief Lookup method to convert from an integer
     *
     * @param i the ordinal value of the BookCategory in this enumeration.
     * @return OTHER   the i-th BookCategory
     */
    public static BookCategory fromInteger(int i) 
    {
        for (BookCategory o : BookCategory.values()) 
        {
            if (i == o.ordinal()) { return o; }
        }
        return OTHER;
    }

    /**
     * @brief get the BookCategory name
     * 
     * @return the internal name.
     */
    public String getName() { return name; }

    /**
     * @brief get the BookCategory internationalized name
     * 
     * @return the internationalized name.
     */
    @Override
    public String toString() { return externalName; }
}
