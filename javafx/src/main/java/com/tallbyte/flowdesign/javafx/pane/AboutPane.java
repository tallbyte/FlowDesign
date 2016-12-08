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

import com.tallbyte.flowdesign.core.Diagram;
import com.tallbyte.flowdesign.core.DiagramsChangedListener;
import com.tallbyte.flowdesign.core.EnvironmentDiagram;
import com.tallbyte.flowdesign.core.Project;
import com.tallbyte.flowdesign.javafx.ResourceUtils;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.beans.PropertyChangeListener;
import java.io.IOException;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.*;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-26)<br/>
 */
public class AboutPane extends BorderPane {

    /**
     * Creates a new {@link AboutPane} by loading from a fxml-file
     * @throws LoadException Is thrown if the fxml-file could not be loaded.
     */
    public AboutPane() throws LoadException {
        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/aboutPane.fxml") );
        loader.setController(this);
        loader.setRoot(this);
        loader.setResources(getResourceBundle());

        try {
            loader.load();
        } catch (IOException e) {
            throw new LoadException("Could not load "+getClass().getSimpleName(), e);
        }

    }
}
