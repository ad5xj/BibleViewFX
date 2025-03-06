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
package com.kds.GUI;

import java.util.EventObject;

/**
 * @ingroup GUI
 * @brief Custom Event handler for an EventObject
 * @extends EventObject
 */
public class URIEvent extends EventObject 
{
    private static final long serialVersionUID = 3978710575457187633L;

    private String scheme;
    private String uri;

    public URIEvent(Object source, String scheme, String uri)
    {
        super(source);
        this.scheme = scheme;
        this.uri = uri;
    }

    public String getScheme() { return this.scheme; }

    public String getURI()    { return this.uri; }
}