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

import org.crosswire.jsword.book.Book;

import java.net.URL;

import java.io.IOException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;

/**
 * @ingroup GUI
 * @brief FXML Dialog Window Controller class
 *
 * @implements Initializable
 */
public class DlgWorksMaintController implements Initializable
{
    @FXML private VBox mainVLayout;
    @FXML private Label lblStatusMsg1;
    @FXML private Label lblStatusMsg2;

    @FXML private ListView<Object> listWorks;

    private List<Object> worksList;    

    private static final String THISMODULE = "com.kds.GUI.DlgWorksMainController";    
    private static final Logger lgr = LoggerFactory.getLogger(DlgWorksMaintController.class);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        initLocalVars();
        try
        {
            loadWorksList();
        }
        catch ( IOException | NullPointerException e )
        {
            String msg = "initialize(): " + e.getMessage()
                       + "    cause=" + e.getCause();
            lgr.error(msg,THISMODULE);
        }
    }    

    private void initLocalVars()
    {
        worksList = null;
    }

    private void loadWorksList() throws IOException
    {
        int start = -1;
        int end   = -1;
        int len   = -1;

        String dirpath = System.getProperty("user.home") + "/.jsword/";
        String strPathShort = "";
        
        List<ListItem> subDirectory = null;

        // to build list item with checkbox and text together //
        ListItem item = null;
        CheckBox itemCheck = null;
        Label lblText = null;
        
        item = null;
        //                                       //
        
        // init list of bibles
        ObservableList<String> listBibles = FXCollections.observableArrayList();
        List<Book> bibles                 = FXCollections.observableArrayList();
        List<ListItem> dirs               = new ArrayList<>();
        List<ListItem> files              = new ArrayList<>();

        DirectoryStream<Path> directoryStream;

        // start a directory stream to get the files listing //
        try 
        {
            directoryStream = Files.newDirectoryStream(Paths.get(dirpath));
            for ( Path path: directoryStream ) 
            {
                start = 0;
                end   = 0;
                item = null;
                lblText = new Label();
                itemCheck = new CheckBox();
                
                if ( Files.isDirectory(path) ) 
                {
                    len = path.toString().length();
                    start = len-2;
                    end = len;
                    strPathShort = path.toString().substring(start,end);
                    lblText.setText(strPathShort);
                    item = new ListItem(false,strPathShort);
                    dirs.add(item);
                    getSubLeafs(path, subDirectory);

                    if ( !files.isEmpty() ) { listWorks.getItems().addAll(files); }
                }
                else 
                {
                    files.add(item);
                }
            }
        }
        catch ( IOException e ) 
        {
            String msg = "getNodesForDirectory(): Error building leafs..." + e.getMessage()
                       + "\n    Dirs=" + dirs
                       + "\n    files=" + files;
            lgr.error(msg,THISMODULE);
            throw e;
        }
        
        for ( Book bible : bibles)
        {
            // create elements for the new list item
            String strdesc;
            Label  desc = new Label();
            item = null;

            item.getChildren().add(new CheckBox());
            strdesc = bible.getName();
            desc.setText(strdesc);
            item.getChildren().add(desc);
            worksList.add(item);
        }

        listWorks.getItems().addAll(worksList); // put the created list into the list view
    }
    
    private void getSubLeafs(Path subPath, List<ListItem> parent) throws IOException
    {
        String strPath;
        String subTree;
        String msg;

        msg = "";


        List<ListItem> p = parent;

        if ( p == null ) { return; }

        // to build leaf image and text together //
        ListItem item;
        //                                       //

        item = null;        
        try ( DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(subPath.toString())) ) 
        {
            List<ListItem> bibles = null;

            for( Path subDir: directoryStream ) 
            {
                CheckBox itemCheck = new CheckBox();
                Label    lblText   = new Label();
                
                if ( (subDir == null ) || !Files.isReadable(subPath) ) { break; }

                // explicit search for files because we dont want to get sub-sub-directories
                if ( !Files.isDirectory(subDir) ) 
                {
                    strPath = subPath.toString();
                    subTree = subDir.toString();
                    lblText.setText(subTree);
                    item.getChildren().add(itemCheck);
                    item.getChildren().add(lblText);
                    bibles.add(item);
                    p.add(item);
                }
            }
        } 
        catch (IOException e) 
        {
            msg = "error creating leaf..."+e.getMessage()
                       + "\n    cause=" + e.getCause();
            lgr.error(msg,THISMODULE);
            throw e;
        }
    }
}