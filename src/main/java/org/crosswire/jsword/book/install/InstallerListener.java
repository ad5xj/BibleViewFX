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

import java.util.EventListener;

/**
 * InstallerListener are able to be notified about changes to the numbers of
 * Installers known to the system.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public interface InstallerListener extends EventListener {
    /**
     * Called whenever a new Installer is added to the system.
     * 
     * @param ev
     *            A description of the change
     */
    void installerAdded(InstallerEvent ev);

    /**
     * Called whenever an Installer is removed from the system.
     * 
     * @param ev
     *            A description of the change
     */
    void installerRemoved(InstallerEvent ev);
}