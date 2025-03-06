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

import java.io.File;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.TreeView;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.cell.CheckBoxTreeCell;

/**
 * @ingroup GUI
 * @brief Controller for the File Dialog window
 * @implements Initalizable
 */
public class FileDialogController implements Initializable
{
    @FXML private TreeView<String> tvFolders;

    private String inputDirectoryLocation = "/home/ken/.sword";
    
    private Parent thisroot;
    private Stage thisstage;
    private Scene thisscene;

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        initLocalVars();
        initLocalObjects();
    }    

    public void setRoot(Parent p)
    {
        thisroot = p;
    }

    public void setStage(Stage s)
    {
        thisstage = s;
    }
    
    public void setScene(Scene sc)
    {
        thisscene = sc;
    }

    public static void createTree(File file, CheckBoxTreeItem<String> parent) 
    {
        if ( file.isDirectory() ) 
        {
            CheckBoxTreeItem<String> treeItem = new CheckBoxTreeItem<>(file.getName());
            parent.getChildren().add(treeItem);
            for (File f : file.listFiles()) 
            {
                createTree(f, treeItem);
            }
        }
    }

    private void displayTreeView()
    {
        // Creates the root item.
        CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>(inputDirectoryLocation);

        // Hides the root item of the tree view.
        tvFolders.setShowRoot(false);

        // Creates the cell factory.
        tvFolders.setCellFactory(CheckBoxTreeCell.forTreeView());

        // Get a list of files.
        File fileInputDirectoryLocation = new File(inputDirectoryLocation);
        File fileList[] = fileInputDirectoryLocation.listFiles();

        // Loop through each file and directory in the fileList.
        // create tree
        for (File file : fileList) 
        {
                createTree(file, rootItem);
        }
        tvFolders.setRoot(rootItem);
    }

    private void initLocalVars()
    {
        // TODO
    }
    
    private void initLocalObjects()
    {
        // TODO: maybe not needed
    }
}
