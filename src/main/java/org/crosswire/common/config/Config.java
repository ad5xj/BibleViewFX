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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.jsword.JSOtherMsg;

import org.crosswire.common.util.LucidException;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.Reporter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.IOException;

import java.net.URI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jdom2.Document;
import org.jdom2.Element;

public class Config implements Iterable<Choice> {
    private static final Logger LGR = LoggerFactory.getLogger(Config.class);

    protected String title;

    protected List<String> keys;
    protected List<Choice> models;
    protected List<ConfigListener> listeners;

    protected PropertyMap local;
    protected PropertyChangeSupport changeListeners;

    public Config(String title) {
        this.keys      = new ArrayList<String>();
        this.models    = new ArrayList<Choice>();
        this.title     = title;
        this.keys      = new ArrayList<String>();
        this.models    = new ArrayList<Choice>();
        this.local     = new PropertyMap();
        this.listeners = new CopyOnWriteArrayList<ConfigListener>();
    }

    public String getTitle() { return this.title; }

    public void add(Choice model) {
        String key = model.getKey();
        this.keys.add(key);
        this.models.add(model);
        String value = model.getString();
        if ( value == null ) 
        {
            value = "";
            LGR.info("key={} had a null value", key);
        }
        this.local.put(key, value);
        fireChoiceAdded(key, model);
    }

    public void add(Document xmlconfig, ResourceBundle configResources) {
        Element root = xmlconfig.getRootElement();
        Iterator<?> iter = root.getChildren().iterator();
        while (iter.hasNext()) 
        {
            Element element = (Element) iter.next();
            String key = element.getAttributeValue("key");
            Exception ex = null;
            try {
                Choice choice = ChoiceFactory.getChoice(element, configResources);
                if (!choice.isIgnored()) {
                    add(choice);
                }
            } catch (StartupException e) {
                StartupException startupException1 = e;
            } catch (ClassNotFoundException e) {
                ex = e;
            } catch (IllegalAccessException e) {
                ex = e;
            } catch (InstantiationException e) {
                ex = e;
            } catch (ReflectiveOperationException e ) {
                ex = e;
            }

            if (ex != null) {
                LGR.warn("Config(): Error creating config element, key={}", key, ex);
            }
        }
    }

    public void remove(String key) {
        Choice model = getChoice(key);
        this.keys.remove(key);
        this.models.remove(model);
        fireChoiceRemoved(key, model);
    }

    public Iterator<Choice> iterator() { return this.models.iterator(); }

    public Choice getChoice(String key) {
        int index = this.keys.indexOf(key);
        if (index == -1) { return null; }
        return this.models.get(index);
    }

    public int size() { return this.keys.size(); }

    public void setLocal(String name, String value) {
        assert name != null;
        assert value != null;
        this.local.put(name, value);
    }

    public String getLocal(String name) { return this.local.get(name); }

    public void applicationToLocal() {
        for (String key : this.keys) 
        {
            Choice model = getChoice(key);
            String value = model.getString();
            this.local.put(key, value);
        }
    }

    public void localToApplication() {
        for (String key : this.keys) 
        {
            Choice choice = getChoice(key);
            String oldValue = choice.getString();
            String newValue = this.local.get(key);
            if ( (newValue == null) || (newValue.length() == 0) ) 
            {
                if ( (oldValue == null) || (oldValue.length() == 0) ) { continue; }
                this.local.put(key, oldValue);
                newValue = oldValue;
            }
            if (!newValue.equals(oldValue)) 
            {
                LGR.info("Setting {}={} (was {})", new Object[]{key, newValue, oldValue});
                try {
                    choice.setString(newValue);
                    if (this.changeListeners != null) 
                    {
                        this.changeListeners.firePropertyChange(new PropertyChangeEvent(choice, choice.getKey(), oldValue, newValue));
                    }
                } catch (LucidException ex) {
                    LGR.warn("Failure setting {}={}", new Object[]{key, newValue, ex});
                    Reporter.informUser(this, new ConfigException(JSOtherMsg.lookupText("Failed to set option: {0}", new Object[]{choice.getFullPath()}), (Throwable) ex));
                }
            }
        }
    }

    public void setProperties(PropertyMap prop) {
        for (String key : prop.keySet()) {
            String value = prop.get(key);
            Choice model = getChoice(key);
            if (value != null && model != null && model.isSaveable()) {
                this.local.put(key, value);
            }
        }
    }

    public PropertyMap getProperties() {
        PropertyMap prop = new PropertyMap();
        for (String key : this.keys) {
            String value = this.local.get(key);
            Choice model = getChoice(key);
            if (model.isSaveable()) {
                prop.put(key, value);
                continue;
            }
            prop.remove(key);
        }
        return prop;
    }

    public void permanentToLocal(URI uri) throws IOException {
        setProperties(NetUtil.loadProperties(uri));
    }

    public void localToPermanent(URI uri) throws IOException {
        NetUtil.storeProperties(getProperties(), uri, this.title);
    }

    public static String getPath(String key) {
        int lastDot = key.lastIndexOf('.');
        if (lastDot == -1) {
            throw new IllegalArgumentException("key=" + key + " does not contain a dot.");
        }
        return key.substring(0, lastDot);
    }

    public static String getLeaf(String key) {
        int lastDot = key.lastIndexOf('.');
        if (lastDot == -1) {
            throw new IllegalArgumentException("key=" + key + " does not contain a dot.");
        }
        return key.substring(lastDot + 1);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (this.changeListeners == null) {
            this.changeListeners = new PropertyChangeSupport(this);
        }
        this.changeListeners.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (this.changeListeners == null) {
            this.changeListeners = new PropertyChangeSupport(this);
        }
        this.changeListeners.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (this.changeListeners != null) {
            this.changeListeners.removePropertyChangeListener(listener);
        }
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (this.changeListeners != null) {
            this.changeListeners.removePropertyChangeListener(propertyName, listener);
        }
    }

    public void addConfigListener(ConfigListener li) { this.listeners.add(li); }

    public void removeConfigListener(ConfigListener li) { this.listeners.remove(li); }

    protected void fireChoiceAdded(String key, Choice model) {
        ConfigEvent ev = new ConfigEvent(this, key, model);
        for (ConfigListener listener : this.listeners) 
        {
            listener.choiceAdded(ev);
        }
    }

    protected void fireChoiceRemoved(String key, Choice model) {
        ConfigEvent ev = new ConfigEvent(this, key, model);
        for (ConfigListener listener : this.listeners) 
        {
            listener.choiceRemoved(ev);
        }
    }
}