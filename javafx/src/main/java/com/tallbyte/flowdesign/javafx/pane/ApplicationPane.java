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
import com.tallbyte.flowdesign.data.ui.storage.ApplicationChangelog;
import com.tallbyte.flowdesign.data.ui.storage.ApplicationChangelogEntry;
import com.tallbyte.flowdesign.javafx.ColorHandler;
import com.tallbyte.flowdesign.javafx.FlowDesignFxApplication;
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
import java.util.Collections;
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
    @FXML private DataTypePane        paneTypes;
    @FXML private Menu                menuEdit;
    @FXML private MenuItem            menuItemLoad;
    @FXML private MenuItem            menuItemSave;
    @FXML private MenuItem            menuItemSaveAs;

    private final FlowDesignFxApplication  application;

    private ObjectProperty<Project>        project           = new SimpleObjectProperty<>(this, "project", null);
    private List<DiagramsChangedListener>  listenersDiagrams = new ArrayList<>();
    private PropertyChangeListener         listenerName     = null;

    public ApplicationPane(FlowDesignFxApplication application) throws LoadException {
        this.application = application;

        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/applicationPane.fxml") );
        loader.setController(this);
        loader.setRoot(this);
        loader.setResources(getResourceBundle());

        try {
            loader.load();
        } catch (IOException e) {
            throw new LoadException("Could not load "+getClass().getSimpleName(), e);
        }

        sceneProperty().addListener((observable, oldValue, newValue) -> {
            updateTitle();
        });

        /*
         * Prepare
         */

        paneDiagrams.setup(this);
        paneFactory.setup(paneDiagrams);
        paneProperty.setup(paneDiagrams);
        paneTypes.setup(this);
        menuItemSave.disableProperty().bind(projectProperty().isNull());
        menuItemSaveAs.disableProperty().bind(projectProperty().isNull());

        /*
         * Add listeners
         */

        paneDiagrams.diagramProperty().addListener((observable, oldValue, newValue) -> {
            updateTitle();
        });

        treeProject.setCellFactory(param -> new TreeCell<TreeEntry>() {

            @Override
            protected void updateItem(TreeEntry item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setContextMenu(null);
                    setText(null);
                } else {
                    if (item instanceof MenuTreeEntry) {
                        setContextMenu(((MenuTreeEntry) item).getContextMenu());
                    } else {
                        setContextMenu(null);
                    }

                    setText(item.toString());
                }

            }
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

                menuEdit.getItems().clear();

                List<Class<? extends Diagram>> list = new ArrayList<>();
                for (Class<? extends Diagram> supported : paneDiagrams.getDiagramManager().getSupportedDiagramTypes()) {
                    list.add(supported);
                }
                Collections.sort(list, (o1, o2) -> o1.getSimpleName().compareTo(o2.getSimpleName()));

                for (Class<? extends Diagram> supported : list) {
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

        ContextMenu menu     = new ContextMenu();
        MenuItem    menuItem = new MenuItem();
        menuItem.setText(getResourceString("context.new."+diagramClazz.getSimpleName()));
        menuItem.setOnAction(event -> createDiagram(diagramClazz));
        menu.getItems().add(menuItem);

        TreeItem<TreeEntry> overview = new TreeItem<>(
                new MenuTreeEntry(
                        getResourceString("tree.overview."+diagramClazz.getSimpleName(), diagramClazz.getSimpleName()),
                        menu
                )
        );

        overview.setExpanded(true);
        root.getChildren().add(overview);

        MenuItem itemEdit = new MenuItem();
        itemEdit.setText(getResourceString("menu.edit.new."+diagramClazz.getSimpleName()));
        itemEdit.disableProperty().bind(projectProperty().isNull());
        itemEdit.setOnAction(event -> createDiagram(diagramClazz));
        menuEdit.getItems().add(itemEdit);

        for (Diagram d : project.getDiagrams(diagramClazz)) {
            TreeItem<TreeEntry> item = new TreeItem<>();
            item.setValue(new DiagramEntry(d, createDiagramContextMenu(d)));
            overview.getChildren().add(item);
        }

        DiagramsChangedListener listenerDiagrams = (diagram, added) -> {
            if (diagram.getClass() == diagramClazz) {
                if (added) {
                    TreeItem<TreeEntry> item = new TreeItem<>();
                    item.setValue(new DiagramEntry(diagram, createDiagramContextMenu(diagram)));
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

    public ContextMenu createDiagramContextMenu(Diagram diagram) {
        ContextMenu menu = new ContextMenu();
        MenuItem itemRemove = new MenuItem(getResourceString("context.diagram.remove"));
        MenuItem itemRename = new MenuItem(getResourceString("context.diagram.rename"));
        menu.getItems().addAll(itemRemove, itemRename);

        return menu;
    }

    public FlowDesignFxApplication getApplication() {
        return application;
    }

    /**
     * Creates and adds a new {@link Diagram} based on its {@link Class}.
     * @param clazz the target {@link Class}
     */
    private void createDiagram(Class<? extends Diagram> clazz) {
        application.setupSimpleDialog(
                new TextInputDialog(),
                "popup.new."+clazz.getSimpleName()+".title",
                "popup.new."+clazz.getSimpleName()+".field.name"
        ).showAndWait().ifPresent(response -> {
            Project project = getProject();
            if (project != null) {
                Diagram diagram = paneDiagrams.getDiagramManager().createDiagram(response, clazz);
                project.addDiagram(diagram);
                paneDiagrams.addDiagram(diagram);
            }
        });
    }

    /**
     * Opens a new {@link Dialog} and creates a new {@link Project}.
     */
    @FXML
    public void onCreateProject() {
        application.setupSimpleDialog(
                new TextInputDialog(),
                "popup.newProject.title",
                "popup.newProject.field.name"
        ).showAndWait().ifPresent(response -> project.set(new Project(response)));
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
                project.set(application.getApplicationManager().loadProject(file.getPath()));
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
            application.getApplicationManager().saveProject(project.get());
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

                application.getApplicationManager().saveProject(project, path);

            } catch (IOException e) {
                e.printStackTrace();
                // TODO temporary
            }
        }
    }

    @FXML
    private void onDarkStyle() {
        application.getColorHandler().setStyle(ColorHandler.Style.DARK);
    }

    @FXML
    private void onDarkContrastStyle() {
        application.getColorHandler().setStyle(ColorHandler.Style.DARK_CONTRAST);
    }

    @FXML
    private void onLightStyle() {
        application.getColorHandler().setStyle(ColorHandler.Style.LIGHT);
    }

    /**
     * Test for internal use only.
     */
    @FXML
    public void onTest() throws IOException {
        ApplicationChangelog changelog = new ApplicationChangelog();
        changelog.add(new ApplicationChangelogEntry(0, "1394528a5d6f0d492764f3c9b0753099ee632c6f", "Version 0.1 - First functional version", new ArrayList<String>() {{
            add("Implemented Flow-Diagram");
            add("Implemented System-Environment-Diagram");
            add("Added storage functionality");
        }}));

        changelog.add(new ApplicationChangelogEntry(0, "1394528a5d6f0d492764f3c9b0753099ee632c6f", "Version 0.2 - Added better visuals and more content", new ArrayList<String>() {{
            add("Added multiple style choices");
            add("Added more diagram components");
            add("Internal cleanup for further expansion");
        }}));

        application.getApplicationManager().serialize(changelog, "/tmp/changelog.xml");
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
            application.setupManualDialog(new Stage(), new AboutPane(), "popup.about.title", 400).show();
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
            String title = "FlowDesign";

            Project project = getProject();
            if (project != null) {
                title += "- [" + project.getName()+"]";

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

    private class MenuTreeEntry extends TreeEntry {

        private final ContextMenu contextMenu;

        /**
         * Creates a new {@link TreeEntry} using a static string.
         *
         * @param name
         */
        public MenuTreeEntry(String name, ContextMenu menu) {
            super(name);

            this.contextMenu = menu;
        }

        /**
         * Gets the desired {@link ContextMenu}.
         * @return Returns the menu.
         */
        public ContextMenu getContextMenu() {
            return contextMenu;
        }
    }

    private class DiagramEntry extends MenuTreeEntry {

        private final Diagram                diagram;
        private final PropertyChangeListener listener;

        /**
         * Creates a new {@link DiagramEntry}.
         * Make sure to call <code>#remove()</code> before
         * releasing this object in order to avoid zombie-listeners.
         * @param diagram the {@link Diagram} to contain
         */
        public DiagramEntry(Diagram diagram, ContextMenu menu) {
            super(diagram.getName(), menu);
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
