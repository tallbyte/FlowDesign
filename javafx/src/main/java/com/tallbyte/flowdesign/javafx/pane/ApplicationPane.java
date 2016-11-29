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
import com.tallbyte.flowdesign.javafx.diagram.FactoryNode;
import com.tallbyte.flowdesign.javafx.diagram.factory.SystemDiagramImageFactory;
import com.tallbyte.flowdesign.javafx.diagram.factory.ActorDiagramImageFactory;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-26)<br/>
 */
public class ApplicationPane extends BorderPane {

    @FXML private TreeView<TreeEntry> treeProject;
    @FXML private VBox                paneFactory;
    @FXML private DiagramPane         paneDiagram;

    private ObjectProperty<Project> project  = new SimpleObjectProperty<>(this, "project", null);
    private DiagramsChangedListener listener = null;

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

        treeProject.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TreeItem<TreeEntry> item = treeProject.getSelectionModel().getSelectedItem();
                if (item != null) {
                    TreeEntry value = item.getValue();
                    if (value instanceof DiagramEntry) {
                        ((DiagramEntry) value).open();
                    }
                }
            }
        });

        project.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeDiagramsChangedListener(listener);
            }

            if (newValue != null) {
                TreeItem<TreeEntry> root           = new TreeItem<>(new TreeEntry(newValue.getName()));
                TreeItem<TreeEntry> environment    = new TreeItem<>(new TreeEntry("Environment"));
                TreeItem<TreeEntry> mask           = new TreeItem<>(new TreeEntry("Mask"));
                TreeItem<TreeEntry> flow           = new TreeItem<>(new TreeEntry("Flow"));

                root.getChildren().add(environment);
                root.getChildren().add(mask);
                root.getChildren().add(flow);

                root.setExpanded(true);
                environment.setExpanded(true);
                mask.setExpanded(true);
                flow.setExpanded(true);
                treeProject.setRoot(root);

                for (Diagram d : newValue.getDiagrams(EnvironmentDiagram.class)) {
                    TreeItem<TreeEntry> item = new TreeItem<>();
                    item.setValue(new DiagramEntry(d));
                    environment.getChildren().add(item);
                }

                listener = (diagram, added) -> {
                    if (added) {
                        TreeItem<TreeEntry> item = new TreeItem<>();
                        item.setValue(new DiagramEntry(diagram));
                        environment.getChildren().add(item);
                    } else {
                        TreeItem<TreeEntry> remove = null;
                        for (TreeItem<TreeEntry> item : environment.getChildren()) {
                            TreeEntry value = item.getValue();

                            if (value instanceof DiagramEntry) {
                                ((DiagramEntry) value).remove();
                                remove = item;
                            }
                        }
                        if (remove != null) {
                            environment.getChildren().remove(remove);
                        }
                    }
                };
                newValue.addDiagramsChangedListener(listener);
            }
        });

        paneFactory.getChildren().add(new FactoryNode(new SystemDiagramImageFactory(), "System"));
        paneFactory.getChildren().add(new FactoryNode(new ActorDiagramImageFactory(), "Actor"));

        /*
         * Other
         */

        project.set(new Project("Hoho"));

        // initialize focus
        Platform.runLater(() -> paneDiagram.requestFocus());

    }

    @FXML
    public void addEnvironment() {
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("Create new Environment-Diagram");
        dialog.setContentText("Name");
        dialog.setHeaderText(null);
        dialog.showAndWait().ifPresent(response -> {
            Project project = getProject();
            if (project != null) {
                project.addDiagram(new EnvironmentDiagram(response));
            }
        });


    }

    @FXML
    public void addFlow() {

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

    private class TreeEntry {

        protected String name;

        public TreeEntry(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private class DiagramEntry extends TreeEntry {

        private final Diagram                diagram;
        private final PropertyChangeListener listener;

        public DiagramEntry(Diagram diagram) {
            super(diagram.getName());
            this.diagram  = diagram;
            this.listener = evt -> {
                if (evt.getPropertyName().equals("name")) {
                    name = evt.getNewValue().toString();
                    treeProject.refresh();
                }
            };

            diagram.addPropertyChangeListener(listener);
        }

        public void open() {
            paneDiagram.setDiagram(diagram);
        }

        public void remove() {
            diagram.removePropertyChangeListener(listener);
        }
    }

}
