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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import IniUtil.IniUtil;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.MapChangeListener;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.stage.PopupWindow;
import javafx.stage.DirectoryChooser;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * @ingroup GUI
 * @brief Window controller to implement model for FXML View LibrariesSetup
 *
 * @implements Initializable
 */
public class LibrariesSetupController implements Initializable 
{

    @FXML private MenuItem menuFileClose;
    @FXML private MenuItem menuFileSelect;
    @FXML private MenuItem menuEditClear;
    @FXML private MenuItem menuHelpAbout;

    @FXML private Button btnClose;
    @FXML private Button btnClear;
    @FXML private Button btnSave;
    @FXML private Button btnSelect;
    
    @FXML private ChoiceBox cboDefaultBible;
    @FXML private ChoiceBox cboDefaultDict;
    @FXML private ChoiceBox cboDefaultDevo;
    @FXML private ChoiceBox cboDefaultGreek;
    @FXML private ChoiceBox cboDefaultHebrew;
    @FXML private ChoiceBox cboGreekMorph;

    private boolean debug_log;

    private double uix;
    private double uiy;
    private double uiw;
    private double uih;
    
    private Stage thisstage;
    private Scene thisscene;

    private String selectedTheme;

    private String suix;
    private String suiy;
    private String suiw;
    private String suih;

    private String osisID;
    private String osisDesc;
    
    // cannonical path prefix to sub-folders    
    private String strdefaultLibPath; // LIBRARY
    private String strBiblesPath;     // BIBLES
    private String strcommentPath;    // COMMENTARIES
    private String strnotesPath;      // NOTES
    private String strdictsPath;      // DICTIONARIES
    private String strmapsPath;       // MAPS AND IMAGES
    private String strlexiPath;       // LEXICONS
    private String strdevoPath;       // DEVOTIONS

    private boolean DefaultDictionary_Hidden;
    private boolean DefaultCommentary_Hidden;
    private boolean DefaultHebrewParse_Hidden;

        // initialize the vars used //
    private Map<String,String> map;
    private ObservableMap<String, String> bibles;

    private Logger lgr;

    private IniUtil    settings;
    
    private PopupWindow about;

    private static final String THISMODULE  = "LibrariesSetupController";
    private static final String MAINSECTION = "MAIN";
    private static final String LIBSECTION  = "LIBRARIES";
    private static final String BIBLESSECTION = "BIBLES";
    private static final String DEVOPATH    = "Devotions";
    private static final String NOTESPATH   = "Notes";
    private static final String LIBPATH     = "LIBRARY";
    private static final String DICPATH     = "Dictionaries";
    private static final String COMPATH     = "Commentaries";
    private static final String LEXPATH     = "Lexicons";
    private static final String MAPPATH     = "Maps";
    
    private static final String RetainCurrent_Name            = "Use Current Bible";
    private static final String RetainCurrent_Help            = "New Bible Views use the last chosen Bible. Otherwise, use the default Bible.";
    private static final String DefaultBible_Name             = "Default Bible";
    private static final String DefaultBible_Help             = "Which of the available Bibles is the default.";
    private static final String DefaultDictionary_Name        = "Default Dictionary";
    private static final String DefaultDictionary_Help        = "Which of the available Dictionaries is the default.";
    private static final String DefaultCommentary_Name        = "Default Commentary";
    private static final String DefaultCommentary_Help        = "Which of the available Commentaries is the default.";
    private static final String DefaultDailyDevotional_Name   = "Default Daily Devotional";
    private static final String DefaultDailyDevotional_Help   = "Which of the available Daily Devotionals is the default.";
    private static final String DefaultGreekDefinitions_Name  = "Default Greek Definitions (Strong\'s)";
    private static final String DefaultGreekDefinitions_Help  = "Which of the available Greek Definitions (Strong\'s) is the default.";
    private static final String DefaultHebrewDefinitions_Name = "Default Hebrew Definitions (Strong\'s)";
    private static final String DefaultHebrewDefinitions_Help = "Which of the available Hebrew Definitions (Strong\'s) is the default.";
    private static final String DefaultGreekParse_Name        = "Default Greek Morphology/Parsing Guides";
    private static final String DefaultGreekParse_Help        = "Which of the available Greek Morphology/Parsing Guides is the default.";
    private static final String DefaultHebrewParse_Name       = "Default Hebrew Morphology/Parsing Guides";
    private static final String DefaultHebrewParse_Help       = "Which of the available Hebrew Morphology/Parsing Guides is the default.";
    // ================================  //
    //   PUBLIC METHODS AND FUNCTIONS    //
    // ================================  //
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initLocalVars();
        readSettings();
    }

    public void setStage(Stage st) {
        thisstage = st;
    }

    public void setScene(Scene sc) {
        thisscene = sc;

        cboDefaultBible.getItems().add("Select One");
        cboDefaultBible.setTooltip(new Tooltip(DefaultBible_Help));
        cboDefaultDict.getItems().add("Select One");
        cboDefaultDict.setTooltip(new Tooltip(DefaultDictionary_Help));
        cboDefaultGreek.getItems().add("Select One");
        cboDefaultGreek.setTooltip(new Tooltip(DefaultGreekParse_Help));
        cboDefaultHebrew.getItems().add("Select One");
        cboDefaultHebrew.setTooltip(new Tooltip(DefaultHebrewParse_Help));
        cboDefaultDevo.getItems().add("Select One");
        cboDefaultDevo.setTooltip(new Tooltip(DefaultDailyDevotional_Help));
        cboGreekMorph.getItems().add("Select One");
        cboGreekMorph.setTooltip(new Tooltip(DefaultGreekParse_Help));

        displaySettings();

        doStyling(selectedTheme);

        thisstage.show();
    }

    public void setCont(Parent libsu) {
        // TODO:
    }
    // ================================  //
    // END PUBLIC METHODS AND FUNCTIONS  //
    // ================================  //

    // ================================  //
    //          ACTION HANDLERS          //
    // ================================  //
    @FXML
    private void actionClose(ActionEvent event) {
        scrapeScreen();
        saveSettings();
        thisstage.close();
    }

    @FXML
    private void actionEditClear(ActionEvent event) {
        // TODO:
    }

    /**
     * @brief Help About popup as an alert message
     * 
     * @details
     * We are using the Alert object as a popup here to simplify 
     * the coding and avoid creating another window with FXML, loading
     * it and styling it. We could do that if we wanted a more elaborate
     * window but for this purpose this will suffice.
     * 
     * @param event 
     */
    @FXML
    private void actionHelpAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setResizable(true);
        alert.setTitle("Libraries Setup");
        alert.setHeaderText("About this window");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        String ct = """
                    Thank you for inquiring about the Libraries
                    setup window. See the help on the Main Window
                    for more details and license notifications.
                    
                    """;
        alert.setContentText(ct);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().add(ButtonType.OK);
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
        alert.show();
    }

    @FXML
    private void actionClearAll(ActionEvent event) {
        // TODO:
    }

    /**
     * @brief On Action handler for Save function
     * 
     * @param event 
     */
    @FXML
    private void actionSave(ActionEvent event) {
        scrapeScreen();
        verifyEntry();
        saveSettings();
        // now create folders for all
    }
    // ================================  //
    //        END ACTION HANDLERS        //
    // ================================  //

    // ================================  //
    //   PRIVATE METHODS AND FUNCTIONS   //
    // ================================  //
    private void doStyling(String theme) {
        if ( theme.isBlank() ) { theme = "Normal"; }
        
        switch (theme) {
        case "Normal" -> {
            selectedTheme = "Normal";
            thisscene.getStylesheets().clear();
            try {
                String css = this.getClass().getResource("/CSS/normalstyles.css").toExternalForm();
                thisscene.getStylesheets().add(css);
            }
            catch ( NullPointerException e )
            {
                String msg = "setNormalStyle(): Exception for MainWindow scene="+e.getMessage();
                if ( debug_log ) { lgr.error(msg,THISMODULE); }
            }
         }
        case "Mint" -> {
            selectedTheme = "Mint";
            thisscene.getStylesheets().clear();
            try {
                String css = this.getClass().getResource("/CSS/mintstyles.css").toExternalForm();
                thisscene.getStylesheets().add(css);
            }
            catch ( NullPointerException e )
            {
                String msg = "setMintStyle(): Exception for MainWindow scene="+e.getMessage();
                if ( debug_log ) { lgr.error(msg,THISMODULE); }
            }
         }
        case "Dark" -> {
            selectedTheme = "Dark";
            thisscene.getStylesheets().clear();
            try {
                String css = this.getClass().getResource("/CSS/darkstyles.css").toExternalForm();
                thisscene.getStylesheets().add(css);
            }
            catch ( NullPointerException e )
            {
                String msg = "setDarkStyle(): Exception for MainWindow scene="+e.getMessage();
                if ( debug_log ) { lgr.error(msg,THISMODULE); }
            }
          }
        case "Colorful" -> {
            selectedTheme = "Colorful";
            thisscene.getStylesheets().clear();
            try {
                String css = this.getClass().getResource("/CSS/colorfulstyles.css").toExternalForm();
                thisscene.getStylesheets().add(css);
            }
            catch ( NullPointerException e )
            {
                String msg = "setColorfulStyle(): Exception for MainWindow scene="+e.getMessage();
                if ( debug_log ) { lgr.error(msg,THISMODULE); }
            }
          }
        default -> {
            selectedTheme = "Normal";
            thisscene.getStylesheets().clear();
            try {
                String css = this.getClass().getResource("/CSS/normalstyles.css").toExternalForm();
                thisscene.getStylesheets().add(css);
            }
            catch ( NullPointerException e )
            {
                String msg = "setNormalStyle(): Exception for MainWindow scene="+e.getMessage();
                if ( debug_log ) { lgr.error(msg,THISMODULE); }
            }
         }
        } // switch()
    } // doStyling()

    /**
     * @brief Verify entry fields
     * 
     * @details
     * All sub-folders are expected and created from the root
     * sub-folder LIBRARY. Therefore the directory chosen from the
     * DirectoryChooser for the default folder, will have the
     * sub-folder LIBRARY by default.
     * 
     * The same is true for each of the other sub-folders. Each
     * is created under the default sub-folder LIBRARY to form
     * a collection (hence, the folder name LIBRARY).
     */
    private void verifyEntry() {
        // make sure the root dir is specified and 
        // has been created
        boolean found;
        /* There will be alerts but not for directories 
        found = directory.exists();
        if ( !found ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setResizable(true);
            alert.setTitle("Libraries Setup");
            alert.setHeaderText("Required Data Missing");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            String ct = """
                        It is necessary to enter and create a valid
                        path for library downloads. The Default Folder
                        name is required. All other folders used will
                        be created under the 'default'/LIBRARY folder.
                        All bible texts are stored in the LIBRARY folder.

                        """;
            alert.setContentText(ct);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().add(ButtonType.OK);
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
            alert.show();
        }
        */
    }

    /**
     * @brief Create the bibles list for the library
     * 
     * @details
     * Returns a collection of the bibles in the specified directory
     * 
     * @param dirpath Pathname for library of bibles
     */
    private void getBiblesInLibrary(String dirpath) {
        int filecnt = 0;
        int x = 0;
        String key = "kjv";
        String desc = "King James Version";
        String dirname = "";

        // use the default library path to read
        // each available bible and get the description
        // to use in an array for display of choices
        //                                       //
        // initialize the vars used //
        map = new HashMap<>();
        bibles = FXCollections.observableMap(map);

        // Start a directory stream to get the files listing
        // of all bibles in the library. For each one, get the 
        // name and description from the OSIS header. Put it
        // in the map for use later.
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(dirpath))) {
            for(Path path: directoryStream) {
                if ( !Files.isDirectory(path))  
                {
                    getBibleInfo(path);
                }
            }

            try {
                cboDefaultBible.getItems().addAll(bibles);
            } catch ( Exception e ) {
                String msg = "getNodesForDirectory(): Error building leafs..."+e.getMessage();
                lgr.error(msg,THISMODULE);
            }
        } catch (IOException e) {
            lgr.error("getNodesForDirectory(): error building node..."+e.getMessage());
        }     
    } // getNodesForDirectory()

    private void getBibleInfo(Path path) {
        Document doc = null;
        DocumentBuilder builder = null;

        try {
            doc = builder.parse(new File(path.toString()));
            doc.getDocumentElement().normalize();
        } catch ( SAXException | IOException se ) {
            String msg = "getBibleInfo(): SAX error in " + 
                         path.toString() + "\nerror: "+ se.getMessage();
            lgr.error(msg,THISMODULE);
        }

        // search for the osisid element and save the value
        try {
            NodeList nodeList = doc.getElementsByTagName("osisText");
            for (int itr = 0; itr < nodeList.getLength(); itr++)   
            {  
                Node node = nodeList.item(itr);  
                if (node.getNodeType() == Node.ELEMENT_NODE)   
                {  
                    Element eElement = (Element)node;  
                    osisID =  eElement.getAttribute("osisIDWork");
                    osisDesc = eElement.getAttribute("title");
                    map.put(osisID, osisDesc);
                }
            }
        } catch ( Exception e ) {
            String msg = "getOSISID(): SAX error in " + 
                         path.toString() + "\nerror: "+ e.getMessage();
            lgr.error(msg,THISMODULE);
            osisID = "unknown";
        }
    }

    private void displaySettings() {
        thisstage.setX(uix);
        thisstage.setY(uiy);
        thisstage.setWidth(uiw);
        thisstage.setHeight(uih);

    }

    /**
     * @brief Utility function to get info from screen 
     */
    private void scrapeScreen() {
        // get window position
        uix = thisstage.getX();
        uiy = thisstage.getY();
        uih = thisstage.getHeight();
        uiw = thisstage.getWidth();

        suix = Double.toString(uix);
        suiy = Double.toString(uiy);
        suiw = Double.toString(uiw);
        suih = Double.toString(uih);
    }

    /**
     * @brief Initialize local vars for first use
     */
    private void initLocalVars() {
        uix = 0.0;
        uiy = 0.0;
        uiw = 0.0;
        uih = 0.0;

        DefaultDictionary_Hidden = true;
        DefaultCommentary_Hidden = true;
        DefaultHebrewParse_Hidden = true;

        selectedTheme = "Normal";

        strdefaultLibPath = System.getProperty("user.home") + File.separator + "bin/BibleView/" + LIBPATH;
        strBiblesPath     = strdefaultLibPath + File.separator + "bibles/en";

        lgr = LoggerFactory.getLogger(LibrariesSetupController.class);
    }
    // ================================  //
    // END PRIVATE METHODS AND FUNCTIONS //
    // ================================  //
    
    
    // ====================================//
    // INI FILE HANDER                     //
    // ====================================//
    private void readSettings() {
        String yesno = "false";

        settings = new IniUtil();

        try
        {
            yesno = settings.getSetting(LIBSECTION, "debug_log");
            debug_log = yesno.contains("true");
        }
        catch ( NullPointerException e )
        {
            debug_log = false;
        }


        try
        {
            strdefaultLibPath = settings.getSetting(LIBSECTION, "defaultLibPath");
        }
        catch ( NullPointerException e )
        {
            strdefaultLibPath = System.getProperty("user.home");
        }

        try
        {
            uix = Double.parseDouble(settings.getSetting(LIBSECTION, "UIX"));
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uix = 300.0;
        }

        try
        {
            uiy = Double.parseDouble(settings.getSetting(LIBSECTION, "UIY"));
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uiy = 300.0;
        }

        try
        {
            uiw = Double.parseDouble(settings.getSetting(LIBSECTION, "UIW"));
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uiw = 1700.0;
        }

        try
        {
            uih = Double.parseDouble(settings.getSetting(LIBSECTION, "UIH"));
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uih = 1000.0;
        }

        try {
            selectedTheme = settings.getSetting(MAINSECTION, "skin");
        }
        catch ( NullPointerException e ) {
            selectedTheme = "Normal";
        }

        try {
            strdefaultLibPath = settings.getSetting(LIBSECTION, "defaultLibPath");
        }
        catch (NullPointerException e )
        {
            strdefaultLibPath = System.getProperty("user.dir");
        }
        
        try {
            strdictsPath = settings.getSetting(LIBSECTION, "dictionaries");
        }
        catch (NullPointerException e )
        {
            strdictsPath = System.getProperty("user.dir"+File.separator+"LIBRARY/Dictionaries");
        }
        
        try {
            strcommentPath = settings.getSetting(LIBSECTION, "commentaries");
        }
        catch (NullPointerException | NumberFormatException e )
        {
            strcommentPath = System.getProperty("user.dir"+File.separator+"LIBRARY/Commentaries");
        }
        
        try {
            strnotesPath = settings.getSetting(LIBSECTION, "notes");
        }
        catch (NullPointerException | NumberFormatException e )
        {
            strnotesPath = System.getProperty("user.dir"+File.separator+"LIBRARY/Notes");
        }
        
        try {
            strlexiPath = settings.getSetting(LIBSECTION, "lexicons");
        }
        catch (NullPointerException | NumberFormatException e )
        {
            strlexiPath = System.getProperty("user.dir"+File.separator+"LIBRARY/Lexicons");
        }

        try {
            strmapsPath = settings.getSetting(LIBSECTION, "maps");
        }
        catch (NullPointerException | NumberFormatException e )
        {
            strmapsPath = System.getProperty("user.dir"+File.separator+"LIBRARY/Maps");
        }
        
        try {
            strdevoPath = settings.getSetting(LIBSECTION, "devotions");
        }
        catch (NullPointerException | NumberFormatException e )
        {
            strdevoPath = System.getProperty("user.dir"+File.separator+"LIBRARY/Devotions");
        }

        // get current config settings //
        yesno = "false";
        try {
            yesno = settings.getSetting(BIBLESSECTION, "DefaultDictionary_Hidden");
            DefaultDictionary_Hidden = yesno.contains("true");
        } catch ( NullPointerException e ) {
            DefaultDictionary_Hidden = false;
        }

        yesno = "false";
        try {
            yesno = settings.getSetting(BIBLESSECTION, "DefaultCommentary_Hidden");
            DefaultCommentary_Hidden = yesno.contains("true");
        } catch ( NullPointerException e ) {
            DefaultCommentary_Hidden = false;
        }

        yesno = "false";
        try {
            yesno = settings.getSetting(BIBLESSECTION, "DefaultHebrewParse_Hidden");
            DefaultHebrewParse_Hidden = yesno.contains("true");
        } catch ( NullPointerException e ) {
            DefaultHebrewParse_Hidden = false;
        }
        // --------------------------- //
        
        settings = null;
    }
    
    private void saveSettings() {
        String yesno = "false";

        settings = new IniUtil();

        try {
            selectedTheme = settings.getSetting(MAINSECTION, "skin");
        }
        catch ( NullPointerException e ) {
            selectedTheme = "Normal";
        }

        try
        {
            settings.setValue(LIBSECTION, "UIX",suix);
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(LIBSECTION, "UIX","300.0");
        }

        try
        {
            settings.setValue(LIBSECTION, "UIY",suiy);
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(LIBSECTION, "UIY","300.0");
        }
       
        try
        {
            settings.setValue(LIBSECTION, "UIW",suiw);
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(LIBSECTION, "UIW","1204.0");
        }

        try
        {
            settings.setValue(LIBSECTION, "UIH",suih);
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(LIBSECTION, "UIH","800.0");
        }

        try {
            settings.setValue(LIBSECTION, "defaultLibPath", strdefaultLibPath);
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(LIBSECTION, "dictionaries",System.getProperty("user.dir"+File.separator+"LIBRARY"));
        }
        
        try {
            settings.setValue(LIBSECTION, "dictionaries",   strdictsPath);
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(LIBSECTION, "dictionaries",System.getProperty("user.dir"+File.separator+"LIBRARY/Dictionaries"));
        }
        
        try {
            settings.setValue(LIBSECTION, "commentaries",   strcommentPath);
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(LIBSECTION, "commentaries",System.getProperty("user.dir"+File.separator+"LIBRARY/Commentaries"));
        }
        
        try {
            settings.setValue(LIBSECTION, "notes",          strnotesPath);
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(LIBSECTION, "notes",System.getProperty("user.dir"+File.separator+"LIBRARY/Notes"));
        }
        
        try {
            settings.setValue(LIBSECTION, "lexicons",       strlexiPath);
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(LIBSECTION, "lecicons",System.getProperty("user.dir"+File.separator+"LIBRARY/Lexicons"));
        }

        try {
            settings.setValue(LIBSECTION, "maps",           strmapsPath);
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(LIBSECTION, "maps",System.getProperty("user.dir"+File.separator+"LIBRARY/Maps"));
        }
        
        try {
            settings.setValue(LIBSECTION, "devotions",      strdevoPath);
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(LIBSECTION, "devotions",System.getProperty("user.dir"+File.separator+"LIBRARY/Devotions"));
        }

        settings = null;
    }
    // =================================== //
    // END INI FILE HANDER                 //
    // =================================== //
} // end class