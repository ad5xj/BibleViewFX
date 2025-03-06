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
package com.kds;
/** @defgroup Application BibleViewFX Application */

import com.kds.GUI.MainWindowController;
import com.kds.GUI.SplashController;
import com.kds.GUI.ExceptionPaneController;

import IniUtil.IniUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.StatusManager;

import java.io.IOException;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;

/**
 * @ingroup  Application
 * @brief Application launcher entry point hook for BibleView application
 *
 * @details
 * This module creates the application main window using the scene loader for
 * JavaFX from a XML description file in resources that defines the window and
 * all the objects it contains. This FXML file was created with SceneBuilder
 * and is in the resources folder.
 *
 * @copyright
 * &copy;Copyright 2024 ken AD5XJ@qso.com see the copyright notice in
 * the help files.
 * \license
 * This product is released under the GNU Public license found in the
 * help documentation.
 *
 */
public class BibleViewApp extends Application 
{
    protected double uix = -1.0;
    protected double uiy = -1.0;
    protected double uiw = -1.0;
    protected double uih = -1.0;

    protected String context = "MainWindow";
    protected String strappPath = "";
    protected String strresPath = "";

    protected MainWindowController maincont;
    
    protected SplashController splash;
    protected OnConsoleStatusListener onConsoleListener;

    StatusManager statusManager;
    
    private static final String SPLASH_PROPS = "splash";
    private static final String EMPTY_STRING = "";
    private static final String CONFIG_KEY   = "config";
    private static final String DESKTOP_KEY  = "desktop";
    private static final String CONV_KEY     = "converters";
    private static final String CSWING_KEY   = "cswing-styles";

    private static final String THISMODULE   = "BibleViewApp";
    private static final String MAINSECTION  = "MAIN";
    private static final String FALSE        = "false";
    private static final String TRUE         = "true";
    private static final String TITLE        =  "BibleView Main Window ";

    public static final String BIBLE_PROTOCOL = "bible";
    public static final String DICTIONARY_PROTOCOL = "dict";
    public static final String GREEK_DEF_PROTOCOL = "gdef";
    public static final String HEBREW_DEF_PROTOCOL = "hdef";
    public static final String GREEK_MORPH_PROTOCOL = "gmorph";
    public static final String HEBREW_MORPH_PROTOCOL = "hmorph";
    public static final String COMMENTARY_PROTOCOL = "comment";


    private static boolean sidebarShowing;
    private static boolean viewSourceShowing;

    private static FXMLLoader loader;
    private static ExceptionPaneController ExceptionPane;
    private static Scene scene;

    private boolean debug_log = true;
    private boolean hasRefBooks;
    private boolean compareShowing;

    private String version;
    private String appPath;
    private String selectedTheme;

    private IniUtil settings;
    
    private Stage stage;
    private Stage splashstage;
    private Scene splashscene;
    private SplashController splashcont;

    private static Logger lgr;
    
    private static LoggerContext lc;

    public static void main(String[] args) { launch(); }
  
    @Override
    public void start(Stage initstage) 
    {
        lgr = LoggerFactory.getLogger(MainWindowController.class);
        // must be set in .ini file for this module to activate
        // must be set before the first call to  LoggerFactory.getLogger()
        // ContextInitializer.CONFIG_FILE_PROPERTY is set to "logback.configurationFile"
        // assume SLF4J is bound to logback in the current environment

        initLocalVars();
        readSettings();

        if ( debug_log ) { lgr.info("Starting The BibleView...",THISMODULE); }
        
        try
        {
            showMainStage(initstage);
        }
        catch ( Exception ie )
        {
            String msg = "Error on startup - interruped - " + ie.getMessage();
            lgr.error(msg,THISMODULE);
            Thread.currentThread().interrupt();
            System.exit(1);
        }
    }

    // ================================== //
    // PUBLIC METHODS AND FUNCTIONS       //
    // ================================== //
    public void setRoot(String fxml) throws IOException 
    {
        
    }

    public String getAppPath()
    {
        return strappPath;
    }
    // ================================== //
    // END PUBLIC METHODS AND FUNCTIONS   //
    // ================================== //
    
    // =================================== //
    // PRIVATE LOCAL METHODS AND FUNCTIONS //
    // =================================== //
    private void showMainStage(Stage mainstage) throws InterruptedException
    {
//        showSplash();

        Parent bvroot = null;
    	loader = new FXMLLoader();
        stage = mainstage;
    
        try 
        {
            loader.setLocation(getClass().getResource("/FXML/MainWindow.fxml"));
            bvroot = loader.load();
            scene = new Scene(bvroot);
            maincont = loader.getController();
        }
        catch ( IOException e ) 
        {
            String msg;
            msg  = "showMainStage: Err = " + e.getCause() + " - " + 
                   e.getMessage() + "\nres=" + loader.getLocation().toString();
            lgr.error(msg,THISMODULE);
        }

        stage.setScene(scene);

        try 
        {
            maincont.setStage(stage);
            maincont.setScene(scene);
        }
        catch ( Exception e) {
            String msg;
            msg  = "showMainStage: Err = " + " - " + e.getMessage() 
                 + "\n    cause=" + e.getCause() ;
            lgr.error(msg,THISMODULE);
        }
        stage.setX(uix);
        stage.setY(uiy);
        stage.setWidth(uiw);
        stage.setHeight(uih);
        stage.setTitle("BibleView Main Window "+version);
        stage.show();
    }

    private void showSplash() throws InterruptedException
    {
        splashstage = new Stage();
        splashscene = null;
        Parent root = null;
        splashcont = null;
    	loader = new FXMLLoader();
    
        try
        {
            loader.setLocation(getClass().getResource("/FXML/Splash.fxml"));
            root = loader.load();
            splashscene = new Scene(root);
            splashcont = loader.getController();
        }
        catch ( IOException e )
        {
            String msg;
            msg  = "showSplash: Err = " + e.getMessage() +
                   "\nres=" + loader.getLocation().toString();
            if ( debug_log )  {  lgr.error(msg,THISMODULE); }
        }

        splashstage.setScene(splashscene);
        splashstage.setX(uix);
        splashstage.setY(uiy);
        splashstage.setWidth(uiw);
        splashstage.setHeight(uih);
        splashstage.setScene(splashscene);
        splashcont.setStage(splashstage);
        splashcont.setScene(splashscene);
        splashstage.show();
    }


    private FXMLLoader loadFXML() throws IOException {
        URL respath = this.getClass().getResource("/FXML/MainWindow.fxml");
        loader = new FXMLLoader();
        try
        {
            loader.setLocation(respath);
        }
        catch ( NullPointerException e )
        {
            String msg = "start(): err =" +
                         e.getLocalizedMessage() +
                         "\nres=" +
                         respath.toString();
            lgr.error(msg,THISMODULE);
            System.exit(1);
        }
        loader.load();
        return loader;
    }

    private void initLocalVars() 
    {
        debug_log = false;

        uix  = 0.0;
        uiy  = 0.0;
        uiw  = 0.0;
        uih  = 0.0;


        context = "MainWindow";
        strappPath = "";
        strresPath = "";
        version = "v1.0";
        context = "Desktop Config";

        lc       = null;
    }
    
    private void readSettings() 
    {
        String yesno = "false";

        settings = new IniUtil();

        try
        {
            yesno = settings.getSetting(MAINSECTION, "debug_log");
            debug_log = yesno.contains("true");
        }
        catch ( NullPointerException e )
        {
            debug_log = false;
        }


        try
        {
            appPath =  settings.getSetting(MAINSECTION,"AppPath");
        }
        catch ( NullPointerException e )
        {
            appPath = System.getProperty("user.dir");
        }

        try
        {
            version =settings.getSetting(MAINSECTION, "AppVersion");
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            version = "v0.85";
        }

        try 
        {
            selectedTheme = settings.getSetting(MAINSECTION, "skin");
        }
        catch ( NullPointerException e ) 
        {
            selectedTheme = "Normal";
        }


        try
        {
            version =settings.getSetting(MAINSECTION, "AppVersion");
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            version = "v0.89";
        }
        

        try
        {
            double x = Double.parseDouble(settings.getSetting(MAINSECTION, "UIX"));
            uix = x;
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uix = 300.0;
        }

        try
        {
            double y = Double.parseDouble(settings.getSetting(MAINSECTION, "UIY"));
            uiy = y;
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uiy = 300.0;
        }

        try
        {
            double w = Double.parseDouble(settings.getSetting(MAINSECTION, "UIW"));
            uiw = w;
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uiw = 1700.0;
        }

        try
        {
            double h = Double.parseDouble(settings.getSetting(MAINSECTION, "UIH"));
            uih = h;
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uih = 1000.0;
        }
       
        settings = null;
    }
    
    private void saveSettings() {
        String yesno = "false";

        settings = new IniUtil();

        if (debug_log) {yesno = "true"; }
        settings.setValue(MAINSECTION, "debug_log",yesno);

        try
        {
            settings.setValue(MAINSECTION, "UIX",Double.toString(uix));
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(MAINSECTION, "UIX","300.0");
        }

        try
        {
            settings.setValue(MAINSECTION, "UIY",Double.toString(uiy));
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(MAINSECTION, "UIY","300.0");
        }
       
        try
        {
            settings.setValue(MAINSECTION, "UIW",Double.toString(uiw));
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(MAINSECTION, "UIW","1204.0");
        }

        try
        {
            settings.setValue(MAINSECTION, "UIH",Double.toString(uih));
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(MAINSECTION, "UIH","800.0");
        }

        settings = null;
    }
}