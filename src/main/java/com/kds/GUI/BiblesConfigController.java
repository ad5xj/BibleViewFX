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

import ch.qos.logback.classic.LoggerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

import java.io.File;

import java.util.ResourceBundle;
import javafx.collections.FXCollections;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.layout.Pane;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * @ingroup GUI
 * @brief Bibles Configuration Window controller
 * 
 * @implements Initializable
 * 
 */
public class BiblesConfigController implements Initializable 
{

    //   FXML COMPONENTS   //
    @FXML private MenuItem          menuFileClose;
    @FXML private MenuItem          menuHelpAboutConfig;

    @FXML private Pane              paneBibles;
    @FXML private Pane              paneBooks;
    @FXML private Pane              paneDisplay;
    @FXML private Pane              paneApp;

    @FXML private ChoiceBox<String> cboDefaultBible;
    @FXML private ChoiceBox<String> cboDefaultDev;
    @FXML private ChoiceBox<String> cboGreekDef;
    @FXML private ChoiceBox<String> cboDefaultGreekDef;
    @FXML private ChoiceBox<String> cboDefaultGreekMorph;
    @FXML private ChoiceBox<String> cboAppLanguage;
    @FXML private ChoiceBox<String> cboAppFontFamily;
    @FXML private ChoiceBox<String> cboAppFontSize;
    @FXML private ChoiceBox<String> cboBibleDisplayFont;
    @FXML private ChoiceBox<String> cboBibleDisplayFontSize;

    @FXML private Spinner<Double>   spinVersesPerTab;
    @FXML private Spinner<Double>   spinDefaultMatchedVerses;
    @FXML private Spinner<Double>   spinParallelMax;

    @FXML private RadioButton       radioCurBibleYes;
    @FXML private RadioButton       radioCurBibleNo;
    @FXML private RadioButton       radioWebWarnYes;
    @FXML private RadioButton       radioWebWarnNo;
    @FXML private RadioButton       radioFullNameYes;
    @FXML private RadioButton       radioFullNameNo;
    @FXML private RadioButton       radioSaveViewYes;
    @FXML private RadioButton       radioSaveViewNo;
    @FXML private RadioButton       radioCommWBiblesYes;
    @FXML private RadioButton       radioCommWBiblesNo;
    @FXML private RadioButton       radioShowSideBarYes;
    @FXML private RadioButton       radioShowSideBarNo;

    @FXML private CheckBox          chkAppBold;
    @FXML private CheckBox          chkAppItalic;
    @FXML private CheckBox          chkDisplayItalic;
    @FXML private CheckBox          chkDisplayBold;

    @FXML private TextField         fieldDownloadPath;

    @FXML private Button            btnAddBooksLoc;
    @FXML private Button            btnRemoveBooksLoc;
    @FXML private Button            btnBrowse;
    @FXML private Button            btnOK;
    @FXML private Button            btnCancel;
    @FXML private Button            btnApply;

    @FXML private TitledPane        toolButtonBibles;
    @FXML private TitledPane        toolButtonBibleDisplay;
    @FXML private TitledPane        toolButtonBooks;
    @FXML private TitledPane        toolButtonApp;

    @FXML private ListView<String>  listAddlLocations;

    private boolean debug_log;

    private double uix;
    private double uiy;
    private double uiw;
    private double uih;

    private String suix;
    private String suiy;
    private String suiw;
    private String suih;

    private String biblespath;
    private String colwidths;

    private String default_download_path;

    private ObservableList<String> languages; // for use with combobox

    private DirectoryChooser dirs;    
    private FileChooser      files;
    private File             file;
    
    private LoggerContext lc;
    private Logger        lgr;

    private IniUtil settings;
    
    private static final String THISMODULE = "BiblesConfgController";
    private static final String MAINSECTION = "MAIN";
    private static final String CONFIGSECTION = "CONFIG";

    protected Parent cont;
    protected Stage thisstage;
    protected Scene thisscene;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        readSettings();
    }

    public void setStage(Stage st) {
        thisstage = st;
    }
    
    public void setScene(Scene sc) {
        thisscene = sc;
        sc.setCursor(Cursor.WAIT);
        initLocalVars();
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                try
                {
                    paneBibles.setVisible(true);
                    displaySettings();
                }
                catch ( Exception e )
                {
                    String msg = "setScene(): err=" + e.getMessage() + " - " + e.getCause();
                    if ( debug_log ) {  lgr.error(msg,THISMODULE); }
                    System.exit(1);
                }
                initControls();
                fillLanguageDropdown();
                fillFontsDropdown();
                return null ;
            }
        };
        task.setOnSucceeded(e -> sc.setCursor(Cursor.DEFAULT));
        new Thread(task).start();

        thisstage.show();
    }
    
    public void setCont(Parent p) {
        cont = p;
    }

    // ============================== //
    // ======= ACTION HANDLERS ====== //
    // ============================== //
    /** @brief Action on click event of Bibles tool button */
    @FXML
    private void showBiblePane(MouseEvent event) {
        paneBibles.setVisible(true);
        paneDisplay.setVisible(false);
        paneBooks.setVisible(false);
        paneApp.setVisible(false);
    }

    /** @brief Action on click event of Display tool button */
    @FXML
    private void showDisplayPane(MouseEvent event) {
        paneBibles.setVisible(false);
        paneDisplay.setVisible(true);
        paneBooks.setVisible(false);
        paneApp.setVisible(false);
    }

    /** @brief Action on click event of Books tool button */
    @FXML
    private void showBooksPane(MouseEvent event) {
        paneBibles.setVisible(false);
        paneDisplay.setVisible(false);
        paneBooks.setVisible(true);
        paneApp.setVisible(false);
    }

    /** @brief Action on click event of Application tool button */
    @FXML
    private void showAppPane(MouseEvent event) {
        paneBibles.setVisible(false);
        paneDisplay.setVisible(false);
        paneBooks.setVisible(false);
        paneApp.setVisible(true);
    }
    
    /** @brief Action on click event of Browse button */
    @FXML
    private void showFileChooser(ActionEvent event) {
        dirs = null;
        file = null;
        fieldDownloadPath.setText("");

        dirs = new DirectoryChooser();
        file = dirs.showDialog(thisstage);
        fieldDownloadPath.setText(file.getAbsolutePath());
    }
    
    /** @brief Action on click event of Menu->Help->About */
    @FXML
    private void actionHelpAboutConfig(ActionEvent event) {
        // TODO: show help about config popup window.
    }

    /**
     * @brief Close event
     * 
     * @details 
     * Called by menuFileClose() and btnCancel click event.
     * Gets current screen settings and saves to .ini file
     * in the CONFIG section
     * 
     * @param event 
     */
    @FXML
    private void actionClose(ActionEvent event) {
        scrapeScreen();
        saveSettings();
        thisstage.close();
    }
    // ============================== //
    // ==== END ACTION HANDLERS ===== //
    // ============================== //

    // ============================================= //
    // ==== PRIVATE LOCAL METHODS AND FUNCTIONS ==== //
    // ============================================= //
    /**
     * @brief Fill Combobox with available languages
     * 
     * @details
     * This is a listing of languages that BibleDesktop has been translated into.<br>
     * Each language should be on a line to itself.<br>
     * @note This list is ordered by the English representation of the file.
     */
    private void fillLanguageDropdown() {
        languages = FXCollections.observableArrayList();
        languages.clear();
        languages.add("en");
        languages.add("zh_CN");
        languages.add("zh_TW");
        languages.add("de");
        // Note: 'id' will become 'in' within a Locale
        // It needs to be 'id' here to pick up the right 
        // Language from iso639.properties
        languages.add("id");
        languages.add("fa");
        //languages.add("tr");
        languages.add("vi");
        
        cboAppLanguage.getItems().clear();
        cboAppLanguage.setItems(languages);
        cboAppLanguage.getSelectionModel().select(0);
    }

    /**
     * @brief Fill the Fonts dropdown box and Font Sizes.
     * 
     * @details
     * Fill the Fonts dropdown box with all <br>
     * current system fonts and a set of sizes.
     */
    private void fillFontsDropdown() {
        thisscene.setCursor(Cursor.WAIT);

        ObservableList<String> fontfams = FXCollections.observableList(Font.getFamilies());

        try
        {
            cboAppFontFamily.getItems().addAll(fontfams);
        }
        catch ( Exception e )
        {
            String msg = "fillFontsDropdown(): err=" + e.getCause() + " - " + e.getMessage() + " - ";
            if ( debug_log ) {  lgr.error(msg,THISMODULE); }
            thisscene.setCursor(Cursor.DEFAULT);
        }
        try
        {
            cboBibleDisplayFont.getItems().addAll(fontfams);
        }
        catch ( Exception e )
        {
            String msg = "fillFontsDropdown(): err=" + e.getCause() + " - " + e.getMessage() + " - ";
            if ( debug_log ) {  lgr.error(msg,THISMODULE); }
            thisscene.setCursor(Cursor.DEFAULT);
        }
        cboAppFontFamily.getSelectionModel().select("Liberation Serif");
        cboBibleDisplayFont.getSelectionModel().select("Liberation Serif");
        
        ObservableList<String> fontsiz = FXCollections.observableArrayList();
        fontsiz.clear();
        cboAppFontSize.getItems().clear();
        cboBibleDisplayFontSize.getItems().clear();
        try {
            fontsiz.addAll(
            " 4"," 6"," 8"," 9","10","11","12","14","16","18","20","22","24",
            "30","32","34","36","38","40","42","44","46","48","52","54","60",
            "64","68","70","72","74");
            cboAppFontSize.getItems().addAll(fontsiz);
            cboBibleDisplayFontSize.getItems().addAll(fontsiz);
        }
        catch ( Exception e )
        {
            String msg = "fillFontsizeDropdown(): err=" + e.getCause() + " - " + e.getMessage() + " - ";
            if ( debug_log ) {  lgr.error(msg,THISMODULE); }
            thisscene.setCursor(Cursor.DEFAULT);
        }
        cboAppFontSize.getSelectionModel().select("14");
        cboBibleDisplayFontSize.getSelectionModel().select("14");
        
        thisscene.setCursor(Cursor.DEFAULT);
    }

    /**
     * @brief Get all the current screen info 
     * 
     * @details
     * Get the screen dimensions and any info needed
     * to be restored on next startup.
     */
    private void scrapeScreen()
    {
        try {
        uix = thisstage.getX();
        uiy = thisstage.getY();
        uiw = thisstage.getWidth();
        uih = thisstage.getHeight();
        }
        catch ( Exception e )
        {
            String msg = "scrapeScreen(): err=" + e.getMessage() + " - " + e.getCause();
            if ( debug_log ) {  lgr.error(msg,THISMODULE); }
            System.exit(1);
        }
    }

    /**
     * @brief Display stored settings from the .ini file from last session.
     */
    private void displaySettings() {
        try {
        thisstage.setX(uix);
        thisstage.setY(uiy);
        thisstage.setWidth(uiw);
        thisstage.setHeight(uih);
        }
        catch ( Exception e )
        {
            String msg = "displaySettings(): err=" + e.getMessage() + " - " + e.getCause();
            if ( debug_log ) {  lgr.error(msg,THISMODULE); }
            System.exit(1);
        }
    }

    /**
     * @brief Init all display controls to default values
     */
    private void initControls() {
        // configure default settings for menus //
        menuFileClose.setDisable(false);
        menuHelpAboutConfig.setDisable(false);
        //                                      //

        radioCurBibleYes.setSelected(true);
        radioCurBibleNo.setSelected(false);
        radioWebWarnYes.setSelected(false);
        radioWebWarnNo.setSelected(true);
        radioFullNameYes.setSelected(true);
        radioFullNameNo.setSelected(false);
        radioSaveViewYes.setSelected(true);
        radioSaveViewNo.setSelected(true);
        radioCommWBiblesYes.setSelected(true);
        radioCommWBiblesNo.setSelected(false);
        radioShowSideBarYes.setSelected(false);
        radioShowSideBarNo.setSelected(true);

        chkDisplayItalic.setSelected(false);
        chkDisplayBold.setSelected(false);
        chkAppBold.setSelected(false);
        chkAppItalic.setSelected(false);
        
        spinParallelMax.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 10.0) );
        spinParallelMax.setEditable(false);
        spinParallelMax.getValueFactory().setValue(2.0);
        
        spinVersesPerTab.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 50.0) );
        spinVersesPerTab.setEditable(false);
        spinVersesPerTab.getValueFactory().setValue(12.0);
        
        spinDefaultMatchedVerses.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 50.0) );
        spinDefaultMatchedVerses.setEditable(false);
        spinDefaultMatchedVerses.getValueFactory().setValue(12.0);

        toolButtonBibles.setExpanded(true);
        toolButtonBibleDisplay.setExpanded(false);
        toolButtonBooks.setExpanded(false);
        toolButtonApp.setExpanded(false);

        showBiblePane(null); // default display pane
        
        btnCancel.setDefaultButton(true);

        files = new FileChooser();
        files.setInitialFileName(System.getProperty("user.home"));

        dirs = new DirectoryChooser();
    }

    /**
     * @brief Initialize local vars to some value before use
     */
    private void initLocalVars()
    {
        debug_log = false;

        uix = 300.0;
        uiy = 100.0;
        uiw = 1000.0;
        uih = 606.0;
        
        suix = "";
        suiy = "";
        suiw = "";
        suih = "";

        default_download_path = "/home/ken/.jsword";
        
        lgr = LoggerFactory.getLogger(BiblesConfigController.class);
    }
    // ============================================= //
    // = END PRIVATE LOCAL METHODS AND FUNCTIONS === //
    // ============================================= //

    // ====================================== //
    // =========== INI FILE HANDER ========== //
    // ====================================== //
    /**
     * @brief Settings for this form only.
     * 
     * @details
     * The settings here are for display of the form
     * and styling of the values shown. The settings
     * do not pertain to any other object. 
     */
    private void readSettings() {
        settings = new IniUtil();

        String yesno;
        
        try
        {
            yesno = settings.getSetting(CONFIGSECTION, "debug_log");
            debug_log = yesno.contains("true");
        }
        catch ( NullPointerException e )
        {
            yesno = "false";
            debug_log = false;
        }
        if ( debug_log )
        {
            lgr = LoggerFactory.getLogger(BiblesConfigController.class);
        }
        
        debug_log = yesno.contains("true");
        if ( debug_log ) {  lgr = LoggerFactory.getLogger(MainWindowController.class); }


        try
        {
            uix = Double.parseDouble(settings.getSetting(CONFIGSECTION, "UIX"));
        }
        catch ( NullPointerException | NumberFormatException e)
        {
            uix = 400.0;
        }
        try
        {
            uiy = Double.parseDouble(settings.getSetting(CONFIGSECTION, "UIY"));
        }
        catch ( NullPointerException | NumberFormatException e)
        {
            uiy = 200.0;
        }
                
        try
        {
            uiw = Double.parseDouble(settings.getSetting(CONFIGSECTION, "UIW"));
        }
        catch ( NullPointerException | NumberFormatException e)
        {
            uiw = 1000.0;
        }

        try
        {
            uih = Double.parseDouble(settings.getSetting(CONFIGSECTION, "UIH"));
        }
        catch ( NullPointerException | NumberFormatException e)
        {
            uih = 606.0;
        }

        try {
            default_download_path = settings.getSetting(CONFIGSECTION, "default_download_path");
        }
        catch ( NullPointerException e)
        {
            default_download_path = "/home/ken/.jsword";
        }

        settings = null;
    } // readSettings()

    /**
     * @brief Save settings for this form only.
     * 
     * @details
     * The current display and var values here are of the form
     * and styling of the values shown. The settings
     * do not pertain to any other object.<br>
     * All is stored in the application .ini file for this form.
     */
    private void saveSettings()
    {
        settings = new IniUtil();

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
            settings.setValue(CONFIGSECTION, "UIX", suix);
            settings.setValue(CONFIGSECTION, "UIY", suiy);
            settings.setValue(CONFIGSECTION, "UIW", suiw);
            settings.setValue(CONFIGSECTION, "UIH", suih);
        }
        catch ( Exception e )
        {
            String msg = "saveSettings: error on save -" + e.getMessage() +
                   "\nx:" + suix + "\ty:" + suiy +
                   "\tw:" + suiw + "\th:" + suih;
            if (debug_log) { lgr.error(msg,THISMODULE); }
        }

        try
        {
            settings.setValue(MAINSECTION, "default_download_path", default_download_path);
        }
        catch ( NullPointerException e )
        {
            settings.setValue(MAINSECTION, "default_download_path", "/home/ken/.jsword");
        }

        settings.saveSettings();
        
        settings = null;
    } // saveSettings()
    // ====================================== //
    // ======= END INI FILE HANDER ========== //
    // ====================================== //
} // end class