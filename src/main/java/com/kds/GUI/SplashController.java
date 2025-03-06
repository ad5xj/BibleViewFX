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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.control.ProgressBar;

/**
 * @ingroup GUI
 * @brief Controller for the splash window
 * @implements Initializable
 */
public class SplashController implements Initializable 
{
    @FXML private VBox        Splash;
    @FXML private ImageView   icon;
    @FXML private ProgressBar pnlJobs;
    
    private String style;
    
    private Stage thisstage;
    private Scene thisscene;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        style = "progressBar { background-color: white; foreground-color: black; border: 1px solid black; }";
    }
    
    public void setStage(Stage st)
    {
        thisstage = st;
    }
    
    public void setScene(Scene sc)
    {
        thisscene = sc;
        pnlJobs.setStyle(style);
        thisstage.show();
    }
}
