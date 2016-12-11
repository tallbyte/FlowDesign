/*
 * FlowDesign
 * Copyright (C) 2016 Tallbyte <copyright at tallbyte.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tallbyte.flowdesign.javafx.pane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceBundle;
import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceString;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-11)<br/>
 */
public class NewProjectPane extends SwitchContentPane {

    @FXML protected TextField textFieldName;
    @FXML protected TextField textFieldDirectory;

    public NewProjectPane() throws LoadException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/newProjectPane.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        loader.setResources(getResourceBundle());

        try {
            loader.load();
        } catch (IOException e) {
            throw new LoadException("Could not load " + getClass().getSimpleName(), e);
        }

        okProperty().bind(textFieldName.textProperty().isNotEmpty().and(textFieldDirectory.textProperty().isNotEmpty()));
    }

    @FXML
    private void onChoose() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(getResourceString("pane.newProject.chooseDirectory"));
        File file = chooser.showDialog(getScene().getWindow());

        if (file != null) {
            textFieldDirectory.setText(file.getPath());
        }
    }

    @Override
    public boolean hasOk() {
        return true;
    }

    @Override
    public boolean hasCancel() {
        return true;
    }

    @Override
    public String getTitle() {
        return getResourceString("pane.newProject.title");
    }
}
