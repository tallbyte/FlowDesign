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

import com.tallbyte.flowdesign.javafx.diagram.ElementNode;
import com.tallbyte.flowdesign.javafx.diagram.SelectableNode;
import com.tallbyte.flowdesign.javafx.property.ColorProperty;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.glyphfont.FontAwesome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.*;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-05)<br/>
 */
public class PropertyPane extends GridPane {

    protected ChangeListener<SelectableNode> listener = null;

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
                        for (Property<?> property : newValue.getNodeProperties()) {
                            row += addProperty(property, row) ? 1 : 0;
                        }
                    }
                };

                listener.changed(newPane.nodeProperty(), null, newPane.nodeProperty().get());

                newPane.nodeProperty().addListener(listener);
            }
        });
    }

    private void createForBoolean(Property<?> property, List<Node> data) {
        if (property instanceof BooleanProperty) {
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().bindBidirectional((BooleanProperty) property);
            data.add(checkBox);
        }
    }

    private void createForString(Property<?> property, List<Node> data) {
        if (property instanceof StringProperty) {
            TextField textField = new TextField();
            textField.textProperty().bindBidirectional((StringProperty) property);
            data.add(textField);
        }
    }

    private void createForDouble(Property<?> property, List<Node> data) {
        if (property instanceof DoubleProperty) {
            TextField textField = new TextField();
            textField.textProperty().bindBidirectional((DoubleProperty) property, new NumberStringConverter());
            data.add(textField);
        }
    }

    private void createForInteger(Property<?> property, List<Node> data) {
        if (property instanceof IntegerProperty) {
            TextField textField = new TextField();
            textField.textProperty().bindBidirectional((IntegerProperty) property, new NumberStringConverter());
            data.add(textField);
        }
    }

    private void createForColor(Property<?> property, List<Node> data) {
        if (property instanceof ColorProperty) {
            HBox hBox = new HBox();
            hBox.getStyleClass().add("propertyColorBox");
            ColorPicker picker = new ColorPicker();
            picker.getStyleClass().add("propertyColorPicker");
            picker.valueProperty().bindBidirectional((ColorProperty) property);
            HBox.setHgrow(picker, Priority.ALWAYS);

            Button reset = new Button();
            reset.getStyleClass().add("propertyColorResetButton");
            reset.getStyleClass().add("iconButton");
            reset.setText("\uf00d"); // TODO can this be applied differently?
            reset.setOnAction(event -> picker.setValue(null));

            hBox.getChildren().addAll(picker, reset);

            data.add(hBox);
        }

    }

    private boolean addProperty(Property<?> property, int row) {
        Label      label = new Label(getResourceString("pane.properties.property."+property.getName(), property.getName()));
        List<Node> data  = new ArrayList<>();

        // TODO change to interface
        createForBoolean(property, data);
        createForString(property, data);
        createForDouble(property, data);
        createForInteger(property, data);
        createForColor(property, data);

        if (data.size() > 0) {
            GridPane.setColumnIndex(label, 0);
            GridPane.setRowIndex(label, row);
            getChildren().add(label);

            for (int i = 0 ; i < data.size() ; ++i) {
                Node node = data.get(i);
                GridPane.setColumnIndex(node, i+1);
                GridPane.setRowIndex(node, row);
                getChildren().add(node);
            }

            return true;
        }

        return false;
    }

}
