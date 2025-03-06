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
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;

/** 
 * @ingroup GUI
 * @brief Custom item class for List object item
 * @extends HBox
 */
public class ListItem extends HBox 
{
    private CheckBox  itemCheck;
    private Label     lblText;

    ListItem(String txt) 
    {
        super();

        itemCheck = new CheckBox();
        lblText   = new Label();

        lblText.setText(txt);

        this.getChildren().addAll(itemCheck,lblText);
        this.setAlignment(Pos.CENTER_LEFT);
    }

    ListItem(boolean selected, String txt) 
    {
        super(20);

        itemCheck = new CheckBox();
        lblText   = new Label();

        itemCheck.setSelected(selected);
        lblText.setText(txt);

        this.getChildren().addAll(itemCheck,lblText);
        this.setAlignment(Pos.CENTER_LEFT);
    }
} // end class
