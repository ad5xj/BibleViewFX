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
 */
package com.kds.display;

import java.io.IOException;

import org.crosswire.common.util.PluginUtil;

public final class BookDataDisplayFactory {
    public static BookDataDisplay createBookDataDisplay() {
        Exception ex = null;
        try {
            return (BookDataDisplay) PluginUtil.getImplementation(BookDataDisplay.class);
        } catch ( ClassCastException e ) {
            ex = e;
        } catch ( IOException e ) {
            ex = e;
        } catch ( ClassNotFoundException e ) {
            ex = e;
        } catch ( InstantiationException e ) {
            ex = e;
        } catch ( IllegalAccessException e ) {
            ex = e;
        }
        assert false : ex;
        return null;
    }
}
