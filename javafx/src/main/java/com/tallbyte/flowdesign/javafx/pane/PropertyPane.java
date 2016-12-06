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

import com.tallbyte.flowdesign.javafx.diagram.DiagramNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.converter.NumberStringConverter;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-05)<br/>
 */
public class PropertyPane extends GridPane {

    protected ChangeListener<DiagramNode> listener = null;

    public void setup(DiagramsPane pane) {
        pane.diagramProperty().addListener((o, oldPane, newPane) -> {
            if (listener != null && oldPane != null) {
                oldPane.nodeProperty().removeListener(listener);
            }

            if (newPane != null) {
                listener = (observable, oldValue, newValue) -> {
                    // remove all existing childs
                    getChildren().clear();

                    if (newValue != null) {
                        int row = 0;
                        for (Property<?> property : newValue.getElementProperties()) {
                            row += addProperty(property, row) ? 1 : 0;
                        }
                    }
                };

                listener.changed(newPane.nodeProperty(), null, newPane.nodeProperty().get());

                newPane.nodeProperty().addListener(listener);
            }
        });
    }

    private boolean addProperty(Property<?> property, int row) {
        Label label = new Label(property.getName());
        Node  data  = null;

        if (property instanceof StringProperty) {
            TextField textField = new TextField();
            textField.textProperty().bindBidirectional((StringProperty) property);
            data = textField;

        } else if (property instanceof DoubleProperty) {
            TextField textField = new TextField();
            textField.textProperty().bindBidirectional((DoubleProperty) property, new NumberStringConverter());
            data = textField;

        } else if (property instanceof IntegerProperty) {
            TextField textField = new TextField();
            textField.textProperty().bindBidirectional((IntegerProperty) property, new NumberStringConverter());
            data = textField;
        }

        if (data != null) {
            GridPane.setColumnIndex(label, 0);
            GridPane.setRowIndex(label, row);
            getChildren().add(label);

            GridPane.setColumnIndex(data, 1);
            GridPane.setRowIndex(data, row);
            getChildren().add(data);

            return true;
        }

        return false;
    }

}
