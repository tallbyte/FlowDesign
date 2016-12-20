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

import com.tallbyte.flowdesign.core.ApplicationManager;
import com.tallbyte.flowdesign.data.ui.storage.ApplicationChangelog;
import com.tallbyte.flowdesign.data.ui.storage.ApplicationChangelogEntry;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.io.IOException;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceBundle;
import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceString;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-26)<br/>
 */
public class ChangelogPane extends SwitchContentPane {

    @FXML private ListView<ApplicationChangelogEntry> listChangelog;

    private final ApplicationManager manager;
    private       SwitchPane         switchPane;

    /**
     * Creates a new {@link ChangelogPane} by loading from a fxml-file
     * @throws LoadException Is thrown if the fxml-file could not be loaded.
     */
    public ChangelogPane(ApplicationManager manager) throws LoadException {
        this.manager = manager;

        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/changelogPane.fxml") );
        loader.setController(this);
        loader.setRoot(this);
        loader.setResources(getResourceBundle());

        try {
            loader.load();
        } catch (IOException e) {
            throw new LoadException("Could not load "+getClass().getSimpleName(), e);
        }

        listChangelog.setCellFactory(param -> new ListCell<ApplicationChangelogEntry>() {

            @Override
            protected void updateItem(ApplicationChangelogEntry item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    try {
                        ChangelogEntry entry = new ChangelogEntry(item);

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
        try {
            for (ApplicationChangelogEntry entry : manager.deserialize(ApplicationChangelog.class, "!/changelog.xml").getEntries()) {
                listChangelog.getItems().add(0, entry);
            }
        } catch (IOException e) {
            throw new LoadException("could not load changelog", e);
        }
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
        return true;
    }

    @Override
    public String getTitle() {
        return getResourceString("pane.welcome.title");
    }
}
