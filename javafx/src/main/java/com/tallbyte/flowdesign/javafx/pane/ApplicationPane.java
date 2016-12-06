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
import com.tallbyte.flowdesign.javafx.diagram.DiagramPane;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

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
    @FXML private FactoryPane         paneFactory;
    @FXML private DiagramsPane        paneDiagrams;
    @FXML private PropertyPane        paneProperty;
    @FXML private MenuItem            menuItemAddEnvironment;

    private ObjectProperty<Project> project  = new SimpleObjectProperty<>(this, "project", null);
    private DiagramsChangedListener listenerDiagrams = null;
    private PropertyChangeListener  listenerName     = null;

    public ApplicationPane() throws LoadException {
        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/applicationPane.fxml") );
        loader.setController(this);
        loader.setRoot(this);

        updateTitle();

        try {
            loader.load();
        } catch (IOException e) {
            throw new LoadException("Could not load "+getClass().getSimpleName(), e);
        }

        /*
         * Prepare
         */

        paneFactory.setup(paneDiagrams);
        paneProperty.setup(paneDiagrams);
        menuItemAddEnvironment.disableProperty().bind(projectProperty().isNull());

        /*
         * Add listeners
         */

        paneDiagrams.diagramProperty().addListener((observable, oldValue, newValue) -> {
            updateTitle();
        });

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
                oldValue.removeDiagramsChangedListener(listenerDiagrams);
                oldValue.removePropertyChangeListener(listenerName);
            }

            if (newValue != null) {
                TreeItem<TreeEntry> root           = new TreeItem<>(new TreeEntry(newValue.getName()));
                TreeItem<TreeEntry> environment    = new TreeItem<>(new TreeEntry("Environment"));
                //TreeItem<TreeEntry> mask           = new TreeItem<>(new TreeEntry("Mask"));
                //TreeItem<TreeEntry> flow           = new TreeItem<>(new TreeEntry("Flow"));

                root.getChildren().add(environment);
                //root.getChildren().add(mask);
                //root.getChildren().add(flow);

                root.setExpanded(true);
                environment.setExpanded(true);
                //mask.setExpanded(true);
                //flow.setExpanded(true);
                treeProject.setRoot(root);

                for (Diagram d : newValue.getDiagrams(EnvironmentDiagram.class)) {
                    TreeItem<TreeEntry> item = new TreeItem<>();
                    item.setValue(new DiagramEntry(d));
                    environment.getChildren().add(item);
                }

                listenerDiagrams = (diagram, added) -> {
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
                newValue.addDiagramsChangedListener(listenerDiagrams);

                listenerName = evt -> {
                    if (evt.getPropertyName().equals("name")) {
                        updateTitle();
                    }
                };
                newValue.addPropertyChangeListener(listenerName);
            }
        });

        /*
         * Other
         */

        // initialize focus
        Platform.runLater(() -> paneDiagrams.requestFocus());

    }

    /**
     * Opens a new {@link Dialog} and creates a new {@link Project}.
     */
    @FXML
    public void onCreateProject() {
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("Create Project");
        dialog.setContentText("Name");
        dialog.setHeaderText(null);
        dialog.showAndWait().ifPresent(response -> project.set(new Project(response)));
    }

    /**
     * Adds a new {@link EnvironmentDiagram} to the {@link Project}.
     * If no {@link Project} is set, this method will do nothing.
     */
    @FXML
    public void onAddEnvironment() {
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

    /**
     * Quits the application.
     */
    @FXML
    public void onQuit() {
        Platform.exit();
    }

    /**
     * Shows the about popup.
     */
    @FXML
    public void onAbout() {
        try  {
            Stage stage = new Stage();
            stage.setScene(new Scene(new AboutPane()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setHeight(400);
            stage.setTitle("About FlowDesign");
            stage.show();
        } catch (LoadException e) {
            // TODO
            e.printStackTrace();
        }
    }

    /**
     * Gets the current {@link Project}.
     * @return Returns the current {@link Project} or null if none is set.
     */
    public Project getProject() {
        return project.get();
    }

    /**
     * Sets the current {@link Project}.
     * @param project the new {@link Project}
     */
    public void setProject(Project project) {
        this.project.set(project);
    }

    public ObjectProperty<Project> projectProperty() {
        return project;
    }

    /**
     * Updates the current title using internal data.
     */
    private void updateTitle() {
        Scene scene = getScene();
        Window window = scene != null ? scene.getWindow() : null;

        if (window instanceof Stage) {
            String title = "Flow Design";

            Project project = getProject();
            if (project != null) {
                title = project.getName();

                DiagramPane diagram = paneDiagrams.getDiagram();
                if (diagram != null) {
                    title += " - ["+diagram.getClass().getSimpleName()+"] - "+diagram.getName();
                }
            }

            ((Stage) window).setTitle(title);
        }
    }

    private class TreeEntry {

        protected String name;

        /**
         * Creates a new {@link TreeEntry} using a static string.
         * @param name
         */
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

        /**
         * Creates a new {@link DiagramEntry}.
         * Make sure to call <code>#remove()</code> before
         * releasing this object in order to avoid zombie-listeners.
         * @param diagram the {@link Diagram} to contain
         */
        public DiagramEntry(Diagram diagram) {
            super(diagram.getName());
            this.diagram  = diagram;
            this.listener = evt -> {
                if (evt.getPropertyName().equals("name")) {
                    name = evt.getNewValue().toString();
                    treeProject.refresh();
                    updateTitle();
                }
            };

            diagram.addPropertyChangeListener(listener);
        }

        /**
         * Opens the containing {@link Diagram} in the {@link DiagramPane}
         * and updates the title.
         */
        public void open() {
            paneDiagrams.addDiagram(diagram);
        }

        /**
         * Removes internal listeners.
         */
        public void remove() {
            diagram.removePropertyChangeListener(listener);
        }
    }

}
