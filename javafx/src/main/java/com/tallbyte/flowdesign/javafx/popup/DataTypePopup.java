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

import com.tallbyte.flowdesign.data.DataType;
import com.tallbyte.flowdesign.data.Project;
import com.tallbyte.flowdesign.javafx.pane.DataTypeEntry;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-16)<br/>
 */
public class DataTypePopup extends Popup {

    private       EventHandler<KeyEvent>    eventHandler;

    private final ListView<DataType>        list;
    private final StringProperty            property;

    /**
     * Creates a new {@link DataTypePopup}.
     * @param property the property that should be set
     */
    public DataTypePopup(StringProperty property) {

        list          = new ListView<>();
        this.property = property;

        list.setCellFactory(param -> {
            ListCell<DataType> cell = new ListCell<DataType>() {
                @Override
                protected void updateItem(DataType item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(new DataTypeEntry(item));
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

            } else if (eventHandler != null) {
                eventHandler.handle(event);
            }
        });

        VBox vBox = new VBox(list);
        vBox.getStyleClass().add("dataTypePopup");

        getContent().add(vBox);
    }

    /**
     * Attempts to find a suitable value for the given string.
     * This will onOpen this popup if expansion as not successful.
     * @param ownerNode the owning {@link Node} of the shown popup
     * @param anchorX the x location in screen coordinates
     * @param anchorY the y location in screen coordinates
     * @param project the containing {@link Project}
     * @param start the string that should be expanded
     * @return Returns the expanded value or null if multiple choices were present.
     */
    public String attemptAutoResolve(Node ownerNode, double anchorX, double anchorY, Project project, String start) {
        String text = null;

        for (DataType type : project.getDataTypeHolder()
                .getDataTypes(start)) {

            if (text == null) {
                text = type.getClassName();
            } else {
                show(ownerNode, anchorX, anchorY, project, start);
                return null;
            }
        }

        return text;
    }

    /**
     * Shows this popup with all matching values.
     * @param ownerNode the owning {@link Node} of the shown popup
     * @param anchorX the x location in screen coordinates
     * @param anchorY the y location in screen coordinates
     * @param project the containing {@link Project}
     * @param start the string that serves as base for the content.
     */
    public void show(Node ownerNode, double anchorX, double anchorY, Project project, String start) {
        list.getItems().clear();

        for (DataType type : project.getDataTypeHolder().getDataTypes(start)) {
            list.getItems().add(type);
        }

        if (list.getItems().size() > 0) {
            DataType first = list.getItems().get(0);

            if (!first.getClassName().equals(start)) {
                show(ownerNode, anchorX, anchorY);
            }
        }

    }

    /**
     * Closes this {@link DataTypePopup} and sets the registered property to the selected value.
     */
    private void close() {
        DataType selected = list.getSelectionModel().getSelectedItem();

        if (selected != null) {
            property.setValue(selected.getClassName());
        }

        hide();
    }

    /**
     * Registeres an additional handler for key events.
     * @param eventHandler the handler
     */
    public void setKeyHandler(EventHandler<KeyEvent> eventHandler) {
        this.eventHandler = eventHandler;
    }
}
