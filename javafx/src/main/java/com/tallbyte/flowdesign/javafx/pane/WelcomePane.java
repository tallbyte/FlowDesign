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
import com.tallbyte.flowdesign.core.storage.ProjectNotFoundException;
import com.tallbyte.flowdesign.data.Project;
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistory;
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistoryEntry;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceBundle;
import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceString;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-26)<br/>
 */
public class WelcomePane extends SwitchContentPane {

    @FXML private ListView<ProjectStorageHistoryEntry> listProjects;

    private final ApplicationManager manager;
    private       SwitchPane         switchPane;

    /**
     * Creates a new {@link WelcomePane} by loading from a fxml-file
     * @throws LoadException Is thrown if the fxml-file could not be loaded.
     */
    public WelcomePane(ApplicationManager manager) throws LoadException {
        this.manager = manager;

        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/welcomePane.fxml") );
        loader.setController(this);
        loader.setRoot(this);
        loader.setResources(getResourceBundle());

        try {
            loader.load();
        } catch (IOException e) {
            throw new LoadException("Could not load "+getClass().getSimpleName(), e);
        }

        listProjects.setCellFactory(param -> new ListCell<ProjectStorageHistoryEntry>() {

            @Override
            protected void updateItem(ProjectStorageHistoryEntry item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    try {
                        ProjectEntry entry = new ProjectEntry(item.getProjectName(), item.getPath());

                        entry.setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2) {
                                try {
                                    openApplication().setProject(manager.loadProject(item.getPath()));
                                } catch (ProjectNotFoundException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        setGraphic(entry);
                    } catch (LoadException e) {
                        e.printStackTrace();
                    }
                } else {
                    setGraphic(null);
                }
            }
        });

        // add all entries to the list
        for (ProjectStorageHistoryEntry entry : manager.getHistory().getEntries()) {
            listProjects.getItems().add(entry);
        }
    }

    private ApplicationPane openApplication() throws LoadException {
        ApplicationPane pane = new ApplicationPane(manager);

        Stage stage = new Stage();
        stage.getIcons().add(new Image("/images/realIcon.png"));
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.show();

        ((Stage) switchPane.getScene().getWindow()).close();

        return pane;
    }

    @FXML
    private void onNew() {
        try {
            NewProjectPane pane = new NewProjectPane() {
                @Override
                public void onOk() {
                    Project project = new Project(textFieldName.getText());
                    File    file    = new File(textFieldDirectory.getText(),
                            textFieldName.getText()+".flow"
                    );
                    try {
                        manager.saveProject(project, file.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        openApplication().setProject(project);
                    } catch (LoadException e) {
                        e.printStackTrace();
                    }
                }
            };
            switchPane.setContent(pane);
        } catch (LoadException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onOpen() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Flow", "*.flow"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All", "*"));
        File file = chooser.showOpenDialog(getScene().getWindow());

        if (file != null) {
            try {
                openApplication().setProject(manager.loadProject(file.getPath()));
            } catch (IOException | ProjectNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onShowHistory() {

    }

    @Override
    public void onOpen(SwitchPane pane) {
        this.switchPane = pane;
    }

    @Override
    public boolean hasOk() {
        return false;
    }

    @Override
    public boolean hasCancel() {
        return false;
    }

    @Override
    public String getTitle() {
        return getResourceString("pane.welcome.title");
    }
}
