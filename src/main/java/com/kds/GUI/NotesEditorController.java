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
/** @ingroup GUI */

import IniUtil.IniUtil;


import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;

/**
 * @brief NoteEditor FXML Controller class
 * 
 * @details
 * This window allows a simple notepad function that can create
 * and save the note to a file of the user choice.
 * 
 * Simple edit functions are supplied as well as limited format functions.
 * 
 * @implements Initializable
 * @sa GPL License
 */
public class NotesEditorController implements Initializable
{
    private boolean debug_log;

    private double uix;
    private double uiy;
    private double uiw;
    private double uih;

    private IniUtil    settings;

    private FileChooser files;
    private DirectoryChooser dirs;
    private File choice;

    private Stage thisstage;
    private Scene thisscene;
    private Parent thiscont;
    private MainWindowController MWC;
    
    // ================================= //
    // FXML DEFINITIONS                  //
    // ================================= //
    @FXML private MenuBar  menuBar;
    @FXML private Menu     menuFile;
    @FXML private MenuItem menuFileClose;
    @FXML private Menu     menuEdit;
    @FXML private MenuItem menuEditInsert;
    @FXML private MenuItem menuEditCut;
    @FXML private MenuItem menuEditCopy;
    @FXML private MenuItem menuEditDelete;
    @FXML private MenuItem menuEditSetTextBold;
    @FXML private MenuItem menuEditSetTextItalic;
    @FXML private MenuItem menuEditTextUnderline;
    @FXML private Menu     menuHelp;
    @FXML private MenuItem menuHelpAbout;
    // ================================= //
    // END FXML DEFINITIONS              //
    // ================================= //

    private static final String THISMODULE = "com.kds.GUI.NoteEditorController";
    private static final String NOTESSECTION = "NOTES";
    private static final Logger lgr = LoggerFactory.getLogger(NotesEditorController.class);
    
    /**
     * @brief Initializes the controller class.
     * @param url URL
     * @param rb  ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        initLocalVars();
        readSettings();
    }    

    // ========================================== //
    // PUBLIC METHODS                             //
    // ========================================== //
    public void setCont(Parent c, MainWindowController mwc) 
    {
        MWC = mwc;

        try
        {
            thiscont = c;
        }
        catch ( NullPointerException e )
        {
            String msg = "setStage(): NullPointerException stage not set="+e.getMessage();
            lgr.error(msg,THISMODULE);
            System.exit(1);
        }
    }
    
    public void setStage(Stage mstage) 
    {
        try
        {
            thisstage = mstage;
        }
        catch ( NullPointerException e )
        {
            String msg = "setStage(): NullPointerException stage not set="+e.getMessage();
            lgr.error(msg,THISMODULE);
            System.exit(1);
        }
    }
    
    public void setScene(Scene mscene) 
    {

        try
        {
            thisscene = mscene;
            thisstage.setScene(mscene);
        }
        catch ( NullPointerException e )
        {
            String msg = "NullPointerException for MainWindow scene="+e.getMessage();
            lgr.error(msg,THISMODULE);
            System.exit(1);
        }

        thisstage.show();
        displaySettings();
    }

    // =============================  //
    // ACTION HANDLERS                //
    // =============================  //
    @FXML
    private void actionClose(ActionEvent event)
    {
        scrapeScreen();
        saveSettings();
        MWC.deselectNotes();
        thisstage.close();
    }

    @FXML
    private void actionEditInsert(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionEditCut(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionEditCopy(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionEditDelete(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionEditSetTextBold(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionEditSetTextItalic(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionEditSetTextUnderline(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionHelpAbout(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actopmTextSetBold(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actopmTextSetItalic(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actopmTextSetUnderline(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionTextCut(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionTextCopy(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionTextPaste(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionFileNew(WindowEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionEditUndo(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionEditRedo(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionNewFile(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionSaveFile(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionSaveAsFile(ActionEvent event)
    {
        // TODO:
    }

    @FXML
    private void actionDeleteFile(ActionEvent event)
    {
        // TODO:
    }

    private void displaySettings()
    {
        thisstage.setX(uix);
        thisstage.setY(uiy);
        thisstage.setWidth(uiw);
        thisstage.setHeight(uih);
    }

    private void scrapeScreen()
    {
        uix = thisstage.getX();
        uiy = thisstage.getY();
        uiw = thisstage.getWidth();
        uih = thisstage.getHeight();
    }

    private void initLocalVars()
    {
        debug_log = true;
        
        uix = 0.0;
        uiy = 0.0;
        uiw = 0.0;
        uih = 0.0;

    }
    
    // ======================================
    // INI FILE HANDER                     //
    // ======================================
    private void readSettings() 
    {
        String yesno = "false";

        settings = new IniUtil();

        try
        {
            yesno = settings.getSetting(NOTESSECTION, "debug_log");
            debug_log = yesno.contains("true");
        }
        catch ( NullPointerException e )
        {
            debug_log = false;
        }
        try
        {
            double x = Double.parseDouble(settings.getSetting(NOTESSECTION, "UIX"));
            uix = x;
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uix = 300.0;
        }

        try
        {
            double y = Double.parseDouble(settings.getSetting(NOTESSECTION, "UIY"));
            uiy = y;
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uiy = 300.0;
        }

        try
        {
            double w = Double.parseDouble(settings.getSetting(NOTESSECTION, "UIW"));
            uiw = w;
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uiw = 1700.0;
        }

        try
        {
            double h = Double.parseDouble(settings.getSetting(NOTESSECTION, "UIH"));
            uih = h;
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uih = 1000.0;
        }
    }
    
    private void saveSettings() 
    {
        String yesno = "false";
        String suix = "";
        String suiy = "";
        String suiw = "";
        String suih = "";

        settings = new IniUtil();

        if (debug_log) {yesno = "true"; }
        settings.setValue(NOTESSECTION, "debug_log",yesno);

        try
        {
            suix = Double.toString(uix);
        }
        catch ( NullPointerException | NumberFormatException e)
        {
            suix = "400.0";
        }
        
        try
        {
            suiy = Double.toString(uiy);
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            suiy = "200.0";
        }
        
        try
        {
            suiw = Double.toString(uiw);
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            suix = "800.0";
        }

        try
        {
            suih = Double.toString(uih);
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            suih = "600.0";
        }

        try
        {
            settings.setValue(NOTESSECTION, "UIX", suix);
            settings.setValue(NOTESSECTION, "UIY", suiy);
            settings.setValue(NOTESSECTION, "UIW", suiw);
            settings.setValue(NOTESSECTION, "UIH", suih);
        }
        catch ( Exception e )
        {
            String msg = "saveSettings: error on save -" + e.getMessage() +
                   "\nx:" + suix + "\ty:" + suiy +
                   "\tw:" + suiw + "\th:" + suih;
            if (debug_log) { lgr.error(msg,THISMODULE); }
        }

        settings.saveSettings();

        settings = null;
    }
    // =================================== //
    // END INI FILE HANDER                 //
    // =================================== //
}