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

import com.tallbyte.flowdesign.data.Project;
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistory;
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistoryEntry;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceBundle;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-26)<br/>
 */
public class WelcomePane extends BorderPane {

    @FXML private ListView<ProjectStorageHistoryEntry> listProjects;

    /**
     * Creates a new {@link WelcomePane} by loading from a fxml-file
     * @throws LoadException Is thrown if the fxml-file could not be loaded.
     */
    public WelcomePane(ProjectStorageHistory history) throws LoadException {
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
                                System.out.println("test");
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
        for (ProjectStorageHistoryEntry entry : history.getEntries()) {
            listProjects.getItems().add(entry);
        }
    }
}
