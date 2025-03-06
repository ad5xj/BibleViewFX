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

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


/**
 * @ingroup GUI
 * @brief FXML Controller class for display of some exceptions
 *
 * @implements Initializable
 */
public class ExceptionPaneController implements Initializable 
{

    @FXML private DialogPane ExceptionPane;
    @FXML private Pane       upperPane;
    @FXML private Pane       lowerPane;
    @FXML private Label      lblMessage;
    @FXML private ImageView  imgMsgIcon;

    Throwable ex;

    String exmsg;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        exmsg = "<html><font size=\"-1\">{0}</font> {1}\nAn error has occurred:";
        imgMsgIcon.setVisible(false);
    }
    
    public void setExMsg(String exmsg)
    {
        lblMessage.setText(exmsg);
    }

    public void setHelpDeskListener(boolean setit)
    {
        
    }

    public void showExceptionDialog(String str, Exception ex)
    {
        
    }
}
