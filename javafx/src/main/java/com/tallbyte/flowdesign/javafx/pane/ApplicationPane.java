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
import com.tallbyte.flowdesign.core.EnvironmentDiagram;
import com.tallbyte.flowdesign.core.Project;
import com.tallbyte.flowdesign.javafx.diagram.DiagramNode;
import com.tallbyte.flowdesign.javafx.diagram.FactoryNode;
import com.tallbyte.flowdesign.javafx.diagram.factory.CircleDiagramImageFactory;
import com.tallbyte.flowdesign.javafx.diagram.factory.StickmanDiagramImageFactory;
import com.tallbyte.flowdesign.javafx.diagram.image.CircleDiagramImage;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-26)<br/>
 */
public class ApplicationPane extends BorderPane {

    @FXML private TreeView<String>  treeProject;
    @FXML private VBox              paneFactory;
    @FXML private DiagramPane       paneDiagram;

    private ObjectProperty<Project> project = new SimpleObjectProperty<>(this, "project", null);

    public ApplicationPane() throws LoadException {
        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/applicationPane.fxml") );
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new LoadException("Could not load "+getClass().getSimpleName());
        }

        /*
         * Add listeners
         */

        project.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                TreeItem<String> root           = new TreeItem<>(newValue.getName());
                TreeItem<String> environment    = new TreeItem<>("Environment");
                TreeItem<String> mask           = new TreeItem<>("Mask");
                TreeItem<String> flow           = new TreeItem<>("Flow");

                root.getChildren().add(environment);
                root.getChildren().add(mask);
                root.getChildren().add(flow);

                root.setExpanded(true);
                treeProject.setRoot(root);

                for (Diagram d : newValue.getDiagrams(EnvironmentDiagram.class)) {
                    TreeItem<Diagram> item = new TreeItem<Diagram>();
                }
            }
        });

        paneFactory.getChildren().add(new FactoryNode(new CircleDiagramImageFactory(), "System"));
        paneFactory.getChildren().add(new FactoryNode(new StickmanDiagramImageFactory(), "Actor"));

        /*
         * Other
         */

        project.set(new Project("Hoho"));

        // initialize focus
        Platform.runLater(() -> paneDiagram.requestFocus());

    }

    public Project getProject() {
        return project.get();
    }

    public void setProject(Project project) {
        this.project.set(project);
    }

    public ObjectProperty<Project> projectProperty() {
        return project;
    }

}
