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

package com.tallbyte.flowdesign.javafx.popup;

import com.tallbyte.flowdesign.javafx.Action;
import com.tallbyte.flowdesign.javafx.pane.ActionEntry;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.util.List;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-16)<br/>
 */
public class ActionPopup extends Popup {

    private final ListView<Action>        list;

    /**
     * Creates a new {@link ActionPopup}.
     * @param actions the list of available actions
     */
    public ActionPopup(List<Action> actions) {

        list          = new ListView<>();

        list.setCellFactory(param -> {
            ListCell<Action> cell = new ListCell<Action>() {
                @Override
                protected void updateItem(Action item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(new ActionEntry(item));
                    }
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    close();
                }
            });

            return cell;
        });

        list.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                close();
                event.consume();

            } else if (event.getCode() == KeyCode.ESCAPE) {
                hide();
                event.consume();

            }
        });

        VBox vBox = new VBox(list);
        vBox.getStyleClass().add("dataTypePopup");

        getContent().add(vBox);

        setAutoHide(true);
        setHideOnEscape(true);

        list.setItems(FXCollections.observableArrayList(actions));
    }
    /**
     * Shows this popup.
     * @param ownerNode the owning {@link Node} of the shown popup
     * @param anchorX the x location in screen coordinates
     * @param anchorY the y location in screen coordinates
     */
    public void show(Node ownerNode, double anchorX, double anchorY) {
        super.show(ownerNode, anchorX, anchorY);
    }

    /**
     * Closes this {@link ActionPopup} and executes the selected action.
     */
    private void close() {
        Action selected = list.getSelectionModel().getSelectedItem();

        if (selected != null) {
            selected.getAction().handle(new ActionEvent());
        }

        hide();
    }
}
