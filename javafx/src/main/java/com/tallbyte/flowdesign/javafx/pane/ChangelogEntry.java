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

import com.tallbyte.flowdesign.data.ui.storage.ApplicationChangelogEntry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceBundle;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-26)<br/>
 */
public class ChangelogEntry extends BorderPane {

    @FXML private Label labelSummary;
    @FXML private Label labelCommit;
    @FXML private VBox  vBoxDetails;

    /**
     * Creates a new {@link ChangelogEntry} by loading from a fxml-file
     * @params the {@link ApplicationChangelogEntry} that should be reflected
     * @throws LoadException Is thrown if the fxml-file could not be loaded.
     */
    public ChangelogEntry(ApplicationChangelogEntry entry) throws LoadException {
        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/changelogEntry.fxml") );
        loader.setController(this);
        loader.setRoot(this);
        loader.setResources(getResourceBundle());

        try {
            loader.load();
        } catch (IOException e) {
            throw new LoadException("Could not load "+getClass().getSimpleName(), e);
        }

        labelSummary.setText(entry.getSummary());
        labelCommit.setText(entry.getCommit());

        for (String s : entry.getDetailed()) {
            vBoxDetails.getChildren().add(new Label(s));
        }

        labelCommit.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                System.out.println("test");
                try {
                    new ProcessBuilder("x-www-browser", "https://git.tallbyte.com/commit/hs!flowDesign.git/"+entry.getCommit()).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onClose(ActionEvent event) {
        event.consume();
    }
}
