/*
 * (C)Copyright 2023, 2024 ken
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
 */

/**
 * @file IniUtil.java
 * @defgroup SETTINGS Windows Style .INI file handler */
package IniUtil;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileReader;

import java.util.Iterator;
import java.util.Set;


/**
 * @ingroup SETTINGS
 * @brief Implementation of a parser for Windows style .ini file
 * @details
 * This is an implementation of the simple Java API for handling configuration 
 * files in Windows style (.ini) file settings from 
 * https://ini4j.sourceforge.net
 * 
 * @copyright
 * &copy;Copyright 2025 ken AD5XJ@qso.com see the copyright notice in 
 * the help files.
 * \license 
 * This product is released under the GNU Public license found in the 
 * help documentation.
 */
public class IniUtil
{
    private String filepath;
    
    private Ini ini;
    private Ini.Section section;

    Set<String> sectionNames;
    Iterator<String> key;

    public IniUtil()
    {
        initLocalVars();
    }

   
    public String getSetting(String section, String key)
    {
        ini = new Ini();
        try
        {
            ini.load(new FileReader(filepath));
        }
        catch ( IOException e )
        {
            String msg;
            msg  = "IniUtil::getSetting 76: IO Error";
            msg += e.getMessage();
            msg += "\nCause:";
            msg += e.getCause();
            msg += "\n\n";
            System.out.println(msg);
        }

        String val = "";

        try
        {
            val = ini.get(section,key);
        }
        catch ( NullPointerException e )
        {
            System.out.println("INIFileParser::getSetting 61: Sec="+section+"\tkey="+key+"\n");
            return "";
        }
        ini = null;
        return val;
    }
    
    public void setValue(String sec, String key, String val)
    {
        FileInputStream inputStream = null;

        try 
        {
            inputStream = new FileInputStream(filepath);
            ini = new Ini();
            try
            {
                ini.load(new FileReader(filepath));
            }
            catch ( IOException e )
            {
                String msg;
                msg = "IniUtil:setValue - I/O exception finding file " + filepath
                          + "\nsec=" + sec + "\tkey=" + key + "\tval=" + val;
                System.out.println(msg);
            }
            ini.getConfig().setStrictOperator(true);
            section = (Section)ini.get(sec);
            try
            {
                if ( section.containsKey(key) ) 
                {
                    section.put(key, val);
                }
                else {
                    section.add(key, val);
                }
            }
            catch ( NullPointerException e ) 
            {
                section = ini.add(sec);
                section.add(key, val);
            }

            File iniFile = new File(filepath);
            ini.store(iniFile);
        }
        catch (IOException e) 
        {
            System.out.println("IniUtil::setValue 101: Error - "+e.getMessage()+"\n");
        }
        finally 
        {
            try
            {
                inputStream.close();
                ini = null;
            }
            catch ( IOException e )
            {
                String msg;
                msg  = "IniUtil::setValue 151: Error - ";
                msg += e.getMessage();
                msg += "\n\n";
                System.out.println(msg+"\n");
            }
        }
    }

    public void saveSettings()
    {
        ini = new Ini();
        /*
        try
        {
            file.exists();
        }
        catch ( NullPointerException e )
        {
            System.out.println("IniUtil::saveSettings 94: Err -"+e.getMessage());
        }

        try
        {
            ini.store(file);
        }
        catch ( IOException | NullPointerException e )
        {
            System.out.println("IniUtil::saveSettings 114: Err -"+e.getMessage());
        }
       */
        ini = null;
    }

    private void initLocalVars()
    {
        filepath = System.getProperty("user.home")+"/bin/BibleView/BibleViewFX.ini";
        System.out.println(filepath);
    }
} // end class