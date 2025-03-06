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
package org.crosswire.jsword.book.install;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PluginUtil;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.Reporter;

import java.io.IOException;

import java.net.URI;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class InstallManager 
{
    private static final String THISMODULE = "InstallManager";  
    private static final Logger log = LoggerFactory.getLogger(InstallManager.class);
    private static final String PREFIX = "Installer.";
  
    private Map<String, Class<InstallerFactory>> factories;
    private Map<String, Installer> installers = new LinkedHashMap<>();

    private List<InstallerListener> listeners = new CopyOnWriteArrayList<>();

    private static boolean debug_log = true;
  
    public InstallManager() 
    {
        int i = 0;

        try
        {
            PropertyMap sitemap = PluginUtil.getPlugin(getClass());
            factories = PluginUtil.getImplementorsMap(InstallerFactory.class);
            String def = null;
            for ( def = sitemap.get("Installer." + ++i); def != null; def = sitemap.get("Installer." + ++i) )
            {
                try
                {
                    String[] parts = def.split(",", 3);
                    String type = parts[0];
                    String name = parts[1];
                    String rest = parts[2];
                    Class<InstallerFactory> clazz = this.factories.get(type);
                    if (clazz == null)
                    {
                        String msg = "Unable to get class for " + type;
                        log.error(msg,THISMODULE);
                    }
                    else
                    {
                        InstallerFactory ifactory = clazz.newInstance();
                        Installer installer = ifactory.createInstaller(rest);
                        internalAdd(name, installer);
                    }
                }
                catch (InstantiationException | IllegalAccessException e)
                {
                    String msg = "Could not enstantiate Install Manager - " + e.getMessage();
                    log.error(msg,THISMODULE);
//                    Reporter.informUser(this, e);
                }
            }
        }
        catch (IOException ex)
        {
            String msg = "Could not enstantiate Install Manager - " + ex.getMessage();
            log.error(msg,THISMODULE);
//            Reporter.informUser(this, ex);
        }
    }

    public void save()
    {
        int i = 1;
        PropertyMap props = new PropertyMap();
        String buf;
  
        for (String name : this.installers.keySet())
        {
            Installer installer = this.installers.get(name);
            buf = installer.getType() + "," + name + "," + installer.getInstallerDefinition();
            props.put("Installer." + i++, buf);
        }

        URI outputURI = CWProject.instance().getWritableURI(getClass().getName(), ".plugin");

        try
        {
            NetUtil.storeProperties(props, outputURI, "Saved Installer Sites");
        }
        catch (IOException e)
        {
            String msg = "I/O Error - Failed to save installers..." + e.getMessage();
            log.error(msg,THISMODULE);
        }
    }

    public Set<String> getInstallerFactoryNames()
    {
        return Collections.unmodifiableSet(this.factories.keySet());
    }

    public String getFactoryNameForInstaller(Installer installer)
    {
        InstallerFactory ifactory = null;
        Class<? extends Installer> match = null;
        Class<? extends Installer> clazz;
        Class<InstallerFactory> factclazz;

        try
        {
            match = installer.getClass();
        }
        catch ( Exception e )
        {
            String msg = "Conversion Error..." + e.getMessage();
            log.error(msg,THISMODULE);
        }

        for (String name : factories.keySet())
        {
            factclazz = this.factories.get(name);

            try
            {
                ifactory = factclazz.newInstance();
                clazz    = ifactory.createInstaller().getClass();
                if (clazz == match)    {  return name; }
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                String msg = "Failed to instantiate installer factory: error=" + e.getMessage()
                           + "\n    " + name + "=" + factclazz.getName();
                log.error(msg,THISMODULE);
            }
        }
        String msg = "Failed to find factory name for " + installer
                   + " among the " + Integer.toString(this.factories.size()) + "factories.";
        log.error(msg,THISMODULE);
        return null;
    }

    public String getInstallerNameForInstaller(Installer instlr)
    {
        for (String name : installers.keySet())
        {
            Installer test = installers.get(name);
            if (instlr.equals(test))  { return name; }
        }
        String msg = "Failed to find installer name for " + instlr 
                   + " among the " + Integer.toString(installers.size()) + " installers.";
        log.error(msg,THISMODULE);
        for (String name : installers.keySet())
        {
            Installer test = this.installers.get(name);
            if ( debug_log ) { log.warn("  it isn't equal to {}", test.getInstallerDefinition()); }
        }
        return null;
    }

    public InstallerFactory getInstallerFactory(String name)
    {
        Class<InstallerFactory> clazz = this.factories.get(name);
        try
        {
            return clazz.newInstance();
        }
        catch (InstantiationException e)
        {
            assert false : e;
        }
        catch (IllegalAccessException e)
        {
            assert false : e;
        }
        return null;
    }

    public Map<String, Installer> getInstallers()
    {
        return Collections.unmodifiableMap(this.installers);
    }

    public Installer getInstaller(String name)
    {
        return this.installers.get(name);
    }

    public void addInstaller(String name, Installer installer)
    {
        assert installer != null;
        assert name != null;
        removeInstaller(name);
        internalAdd(name, installer);
        fireInstallersChanged(this, installer, true);
    }

    private void internalAdd(String name, Installer installer)
    {
        Iterator<String> it = this.installers.keySet().iterator();
        while (it.hasNext())
        {
            String tname = it.next();
            Installer tinstaller = this.installers.get(tname);
            if (tinstaller.equals(installer))
            {
                log.warn("duplicate installers: {}={}. removing {}", new Object[]
                {
                    name, tname, tname
                });
                it.remove();
                fireInstallersChanged(this, tinstaller, false);
            }
        }
        this.installers.put(name, installer);
    }

    public void removeInstaller(String name)
    {
        if (this.installers.containsKey(name))
        {
            Installer old = this.installers.remove(name);
            fireInstallersChanged(this, old, false);
        }
    }

    public void addInstallerListener(InstallerListener li)
    {
        this.listeners.add(li);
    }

    public void removeBooksListener(InstallerListener li)
    {
        this.listeners.remove(li);
    }

    protected void fireInstallersChanged(Object source, Installer installer, boolean added)
    {
        InstallerEvent ev = new InstallerEvent(source, installer, added);
        for (InstallerListener listener : this.listeners)
        {
            if (added)
            {
                listener.installerAdded(ev);
                continue;
            }
            listener.installerRemoved(ev);
        }
    }
}
