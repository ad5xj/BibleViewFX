/*
 * Copyright (C) 2025 org.kds
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */package com.kds.GUI;

import org.crosswire.common.icu.NumberShaper;
import org.crosswire.common.util.MsgBase;
 
/** 
 * @ingroup GUI
 * @brief Module to extend MsgBase
 * 
 * @extends MsgBase
 */
public final class BVMsg extends MsgBase 
{
    private static MsgBase msg = new BVMsg();
    private static NumberShaper shaper = new NumberShaper();
    private static final String VERSION = shaper.shape("0.85");

    public static String getApplicationTitle() 
    {
      return gettext("BibleView", new Object[0]);
    }

    public static String getVersionInfo() 
    {
        String ver = "Version " + getVersion();
        return ver;
    }

    public static String getVersionedApplicationTitle() 
    {
        String t = getApplicationTitle() + " v" + getVersion();
        return t;
    }

    public static String getAboutInfo() 
    {
        String info = "About " + getApplicationTitle();
        return info;
    }

    private static String getVersion() 
    {
        return VERSION;
    }

    public static String gettext(String key, Object... params) 
    {
        return msg.lookup(key, params);
    }
}