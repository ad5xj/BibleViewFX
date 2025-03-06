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

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * @ingroup GUI
 * @extends HBox
 */
public class CustomItem extends HBox 
{
    private Label     lblText;
    private ImageView imgobj;

    CustomItem(Label txt) 
    {
        super();

        lblText = txt;

        this.getChildren().add(lblText);
        this.setAlignment(Pos.CENTER_LEFT);
    }

    CustomItem(ImageView img, Label txt) 
    {
        super(20);

        this.lblText = txt;
        this.imgobj = img;

        this.getChildren().addAll(imgobj,lblText);
        this.setAlignment(Pos.CENTER_LEFT);
    }
} // end class
