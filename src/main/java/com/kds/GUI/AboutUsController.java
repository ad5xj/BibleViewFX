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

import IniUtil.IniUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import org.slf4j.LoggerFactory;

/**
 * @ingroup GUI
 * @brief The controller for the AboutUs window
 * @implements Initalizable
 */
public class AboutUsController implements Initializable 
{

    @FXML private TabPane tabPane;
    @FXML private Tab tabInfo;
    @FXML private Tab tabWarranty;
    @FXML private Tab tabLicense;
    @FXML private WebView viewWarranty;
    @FXML private WebView viewLic;
    @FXML private Button  btnClose;


    private String warranty;
    private String license;

    private Parent MWC;
    private Stage  thisstage;
    private Scene  thisscene;
    
    private IniUtil settings;
    
    private static final String THISMODULE = "AboutUsController";
    private static final Logger lgr = LoggerFactory.getLogger(AboutUsController.class);


    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        initLocalVars();
    }    

    public void setCont(Parent p)    { MWC = p; }
    
    public void setStage(Stage st) { thisstage = st; }
    
    public void setScene(Scene sc) 
    {
        thisscene = sc;
        thisstage.show();
    }

    @FXML
    private void actionClose(ActionEvent event) { thisstage.close(); }

    private void initLocalVars() 
    {
        String url1;
        String url2;
        
        WebEngine engine1 = viewWarranty.getEngine();
        WebEngine engine2 = viewLic.getEngine();

        try 
        {
            url1 = AboutUsController.class.getResource("/HTML/warranty.html").toExternalForm();
            engine1.load(url1);
            viewWarranty.setZoom(1.0);
        }
        catch ( Exception e)
        {
            String msg = "initLocalVars(): error loading html - " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }

        try 
        {
            url2 = AboutUsController.class.getResource("/HTML/license.html").toExternalForm();
            engine2.load(url2);
            viewLic.setZoom(1.0);
        }
        catch ( Exception e)
        {
            String msg = "initLocalVars(): error loading html - " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
    }
} // end class