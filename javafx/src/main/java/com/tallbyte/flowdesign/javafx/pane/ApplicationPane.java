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

import com.tallbyte.flowdesign.core.storage.ApplicationManager;
import com.tallbyte.flowdesign.core.storage.NoPathSpecifiedException;
import com.tallbyte.flowdesign.core.storage.ProjectNotFoundException;
import com.tallbyte.flowdesign.core.storage.ProjectStorage;
import com.tallbyte.flowdesign.data.Diagram;
import com.tallbyte.flowdesign.data.DiagramsChangedListener;
import com.tallbyte.flowdesign.data.environment.EnvironmentDiagram;
import com.tallbyte.flowdesign.data.Project;
import com.tallbyte.flowdesign.data.flow.FlowDiagram;
import com.tallbyte.flowdesign.javafx.diagram.DiagramPane;
import com.tallbyte.flowdesign.storage.xml.XmlStorage;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.*;

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
    @FXML private MenuItem            menuItemAddFlow;
    @FXML private MenuItem            menuItemLoad;
    @FXML private MenuItem            menuItemSave;
    @FXML private MenuItem            menuItemSaveAs;

    private final ApplicationManager       applicationManager;

    private ObjectProperty<Project>        project           = new SimpleObjectProperty<>(this, "project", null);
    private List<DiagramsChangedListener>  listenersDiagrams = new ArrayList<>();
    private PropertyChangeListener         listenerName     = null;

    public ApplicationPane(ApplicationManager applicationManager) throws LoadException {
        this.applicationManager = applicationManager;

        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/applicationPane.fxml") );
        loader.setController(this);
        loader.setRoot(this);
        loader.setResources(getResourceBundle());

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
        menuItemAddFlow.disableProperty().bind(projectProperty().isNull());
        menuItemSave.disableProperty().bind(projectProperty().isNull());
        menuItemSaveAs.disableProperty().bind(projectProperty().isNull());

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
                listenersDiagrams.forEach(oldValue::removeDiagramsChangedListener);
                oldValue.removePropertyChangeListener(listenerName);
            }

            if (newValue != null) {
                TreeItem<TreeEntry> root = new TreeItem<>(new TreeEntry(newValue.getName()));

                root.setExpanded(true);
                treeProject.setRoot(root);

                for (Class<? extends Diagram> supported : paneDiagrams.getDiagramManager().getSupportedDiagramTypes()) {
                    registerForDiagram(root, supported, newValue, listenersDiagrams);
                }

                listenersDiagrams.forEach(newValue::addDiagramsChangedListener);

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

    private void registerForDiagram(TreeItem<TreeEntry> root,
                                    Class<? extends Diagram> diagramClazz,
                                    Project project,
                                    List<DiagramsChangedListener> listeners) {
        TreeItem<TreeEntry> overview = new TreeItem<>(
                new TreeEntry(getResourceString("tree.overview."+diagramClazz.getSimpleName(), diagramClazz.getSimpleName()))
        );
        overview.setExpanded(true);
        root.getChildren().add(overview);

        for (Diagram d : project.getDiagrams(diagramClazz)) {
            TreeItem<TreeEntry> item = new TreeItem<>();
            item.setValue(new DiagramEntry(d));
            overview.getChildren().add(item);
        }

        DiagramsChangedListener listenerDiagrams = (diagram, added) -> {
            if (diagram.getClass() == diagramClazz) {
                if (added) {
                    TreeItem<TreeEntry> item = new TreeItem<>();
                    item.setValue(new DiagramEntry(diagram));
                    overview.getChildren().add(item);
                } else {
                    TreeItem<TreeEntry> remove = null;
                    for (TreeItem<TreeEntry> item : overview.getChildren()) {
                        TreeEntry value = item.getValue();

                        if (value instanceof DiagramEntry) {
                            ((DiagramEntry) value).remove();
                            remove = item;
                        }
                    }
                    if (remove != null) {
                        overview.getChildren().remove(remove);
                    }
                }
            }

        };
        listeners.add(listenerDiagrams);
    }

    /**
     * Opens a new {@link Dialog} and creates a new {@link Project}.
     */
    @FXML
    public void onCreateProject() {
        Dialog<String> dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/images/realIcon.png"));
        dialog.setTitle(getResourceString("popup.newProject.title"));
        dialog.setContentText(getResourceString("popup.newProject.field.name"));
        dialog.setHeaderText(null);
        dialog.showAndWait().ifPresent(response -> project.set(new Project(response)));
    }

    /**
     * Loads the {@link Project}.
     */
    @FXML
    public void onLoad() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All", "*"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Flow", "flow"));
        File file = chooser.showOpenDialog(getScene().getWindow());

        if (file != null) {
            try {
                project.set(applicationManager.loadProject(file.getPath()));
            } catch (IOException | ProjectNotFoundException e) {
                e.printStackTrace();
                // TODO temporary
            }
        }
    }

    /**
     * Saves the {@link Project}.
     */
    @FXML
    public void onSave() {
        try {
            applicationManager.saveProject(project.get());
        } catch (IOException e) {
            e.printStackTrace();
            // TODO temporary
        } catch (NoPathSpecifiedException e) {
            onSaveAs();
        }
    }

    /**
     * Saves the {@link Project} in a user-defined location.
     */
    @FXML
    public void onSaveAs() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showSaveDialog(getScene().getWindow());

        if (file != null) {
            try {
                Project project = getProject();
                String path = file.getPath();
                if (!path.endsWith(".flow")) {
                    path += ".flow";
                }

                applicationManager.saveProject(project, path);

            } catch (IOException e) {
                e.printStackTrace();
                // TODO temporary
            }
        }
    }

    /**
     * Test for internal use only.
     */
    @FXML
    public void onTest() {

    }

    /**
     * Adds a new {@link EnvironmentDiagram} to the {@link Project}.
     * If no {@link Project} is set, this method will do nothing.
     */
    @FXML
    public void onAddEnvironment() {
        Dialog<String> dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/images/realIcon.png"));
        dialog.setTitle(getResourceString("popup.newEnvironment.title"));
        dialog.setContentText(getResourceString("popup.newEnvironment.field.name"));
        dialog.setHeaderText(null);
        dialog.showAndWait().ifPresent(response -> {
            Project project = getProject();
            if (project != null) {
                project.addDiagram(new EnvironmentDiagram(response));
            }
        });
    }

    /**
     * Adds a new {@link FlowDiagram} to the {@link Project}.
     * If no {@link Project} is set, this method will do nothing.
     */
    @FXML
    public void onAddFlow() {
        Dialog<String> dialog = new TextInputDialog();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/images/realIcon.png"));
        dialog.setTitle(getResourceString("popup.newFlow.title"));
        dialog.setContentText(getResourceString("popup.newFlow.field.name"));
        dialog.setHeaderText(null);
        dialog.showAndWait().ifPresent(response -> {
            Project project = getProject();
            if (project != null) {
                project.addDiagram(new FlowDiagram(response));
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
            stage.getIcons().add(new Image("/images/realIcon.png"));
            stage.setScene(new Scene(new AboutPane()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setHeight(400);
            stage.setTitle(getResourceString("popup.about.title"));
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
