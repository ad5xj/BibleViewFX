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
/** @defgroup GUI User Interface */
import org.slf4j.Logger;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;
import org.slf4j.LoggerFactory;

/** 
 * @ingroup GUI 
 * @brief Controller class for the About Window
 */
public class AboutLibrariesController implements Initializable 
{
    
    @FXML Label  lblHelpText;
    @FXML Button btnCloseHelp;
    
    private Stage stage;
    private Scene scene;

    private Logger lgr;

    private static final String THISMODULE = "AboutLibrariesController";
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // initialization methods here
        lgr = LoggerFactory.getLogger(AboutLibrariesController.class);
    }    

    public void setStage(Stage st) {
        stage = st;
        stage.initModality(Modality.APPLICATION_MODAL);
    }
    
    public void setScene(Scene sc) {
        scene = sc;
        String ht = "This is the help text for the libraries setup window.";
        lblHelpText.setTextAlignment(TextAlignment.LEFT);
        lblHelpText.setWrapText(true);
        lblHelpText.setText(ht);
        scene.getStylesheets().clear();
        try {
            String css = this.getClass().getResource("/CSS/aboutlibraries.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch ( NullPointerException e ) {
            String msg = "setScene(): Exception for AboutLibrariesController scene="+e.getMessage();
            lgr.error(msg,THISMODULE);
        }
    }

    @FXML 
    private void actionCloseHelp(ActionEvent event) {
        stage.close();
    }    
}