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
import com.tallbyte.flowdesign.data.notation.FlowNotationParser;
import com.tallbyte.flowdesign.data.notation.FlowNotationParserException;
import com.tallbyte.flowdesign.data.notation.SimpleFlowNotationParser;
import com.tallbyte.flowdesign.data.notation.actions.FlowAction;
import com.tallbyte.flowdesign.data.notation.actions.Tupel;
import com.tallbyte.flowdesign.data.notation.actions.Type;
import com.tallbyte.flowdesign.javafx.pane.DataTypeEntry;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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

    private final FlowNotationParser        parser;
    private final ListView<DataType>        list;
    private final TextField                 textField;

    /**
     * Creates a new {@link DataTypePopup}.
     *
     * @param textField the backed field
     */
    public DataTypePopup(TextField textField) {

        parser         = new SimpleFlowNotationParser();
        list           = new ListView<>();
        this.textField = textField;

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

        setAutoHide(true);
        setHideOnEscape(true);

        VBox vBox = new VBox(list);
        vBox.getStyleClass().add("dataTypePopup");

        getContent().add(vBox);
    }

    /**
     * Attempts to find a suitable value for the given string.
     * This will onOpen this popup if expansion as not successful.
     *
     * @param ownerNode the owning {@link Node} of the shown popup
     * @param anchorX the x location in screen coordinates
     * @param anchorY the y location in screen coordinates
     * @param project the containing {@link Project}
     */
    public void attemptAutoResolve(Node ownerNode, double anchorX, double anchorY, Project project) {
        if (!attemptAutoResolve(project, textField.getText(), textField.getCaretPosition())) {
            show(ownerNode, anchorX, anchorY, project);
        }
    }

    /**
     * Tries to automatically set the text. This is only
     * possible if only one choice is given.
     *
     * @param project the {@link Project}
     * @param start the text
     * @param caret the current caret location
     * @return Returns true if a value was set, else false.
     */
    private boolean attemptAutoResolve(Project project, String start, int caret) {
        Type type = getType(start, caret);

        if (type != null) {
            String base = start.substring(type.getStart(), caret);
            String text = null;

            for (DataType dataType : project.getDataTypeHolder()
                    .getDataTypes(base)) {

                if (text == null) {
                    text = dataType.getClassName();
                } else {
                    text = null;
                    break;
                }
            }

            if (text != null) {
                set(start, caret, text);
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the string that is to be expanded.
     *
     * @param start the text
     * @param caret the current caret location
     * @return Returns the text or null if none could be found.
     */
    private String getToExpand(String start, int caret) {
        Type type = getType(start, caret);

        if (type != null) {
            return start.substring(type.getStart(), caret);
        }

        return null;
    }

    /**
     * Gets the type at the given caret location.
     *
     * @param start the text
     * @param caret the current caret location
     * @return Returns the type or null if none can be found.
     */
    private Type getType(String start, int caret) {
        try {
            FlowAction action = parser.parse(start);

            if (action != null) {
                action = action.getChildAt(caret);

                if (action instanceof Type) {
                    return (Type) action;

                } else if (action instanceof Tupel) {
                    return new Type(caret, caret, new DataType(""), "", false);
                }
            }
        } catch (FlowNotationParserException e) {
            // ignore
        }

        return null;
    }

    /**
     * Sets the fields value to the specified one and updates the caret accordingly.
     *
     * @param start the current text
     * @param caret the current caret location
     * @param to the value to insert
     */
    private void set(String start, int caret, String to) {
        Type type = getType(start, caret);

        if (type != null) {
            String before = "";
            String after  = "";

            if (caret > 0) {
                before = start.substring(0, type.getStart());
            }

            if (caret < start.length()) {
                after = start.substring(caret, start.length());
            }

            textField.setText(before+to+after);
            textField.positionCaret(type.getStart()+to.length());
        }
    }

    /**
     * Shows this popup with all matching values.
     *
     * @param ownerNode the owning {@link Node} of the shown popup
     * @param anchorX the x location in screen coordinates
     * @param anchorY the y location in screen coordinates
     * @param project the containing {@link Project}
     */
    public void show(Node ownerNode, double anchorX, double anchorY, Project project) {
        list.getItems().clear();

        String expand = getToExpand(textField.getText(), textField.getCaretPosition());

        for (DataType type : project.getDataTypeHolder().getDataTypes(expand)) {
            list.getItems().add(type);
        }

        if (list.getItems().size() > 0) {
            DataType first = list.getItems().get(0);

            if (!first.getClassName().equals(expand)) {
                show(ownerNode, anchorX, anchorY);
            } else {
                close();
            }
        }

    }

    /**
     * Closes this {@link DataTypePopup} and sets the registered property to the selected value.
     */
    private void close() {
        DataType selected = list.getSelectionModel().getSelectedItem();

        if (selected != null) {
            set(textField.getText(), textField.getCaretPosition(), selected.getClassName());
        }

        hide();
    }

    /**
     * Registeres an additional handler for key events.
     *
     * @param eventHandler the handler
     */
    public void setKeyHandler(EventHandler<KeyEvent> eventHandler) {
        this.eventHandler = eventHandler;
    }
}
