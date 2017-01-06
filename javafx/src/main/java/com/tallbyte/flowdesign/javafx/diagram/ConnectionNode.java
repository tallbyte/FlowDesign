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

package com.tallbyte.flowdesign.javafx.diagram;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import com.tallbyte.flowdesign.data.Joint;
import com.tallbyte.flowdesign.data.Connection;
import com.tallbyte.flowdesign.data.notation.FlowNotationParserException;
import com.tallbyte.flowdesign.data.notation.actions.Chain;
import com.tallbyte.flowdesign.data.notation.actions.FlowAction;
import com.tallbyte.flowdesign.javafx.Action;
import com.tallbyte.flowdesign.javafx.FlowDesignFxApplication;
import com.tallbyte.flowdesign.javafx.ShortcutGroup;
import com.tallbyte.flowdesign.javafx.Shortcuts;
import com.tallbyte.flowdesign.javafx.control.AutoSizeTextField;
import com.tallbyte.flowdesign.javafx.popup.DataTypePopup;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Popup;

import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceString;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-05)<br/>
 */
public class ConnectionNode extends SelectableNode {

    protected       DiagramPane             diagramPane;

    protected final Connection              connection;

    protected       JointNode               sourceNode;
    protected       JointNode               targetNode;

    protected final Line                    line      = new Line();
    protected final Pane                    paneMarker= new Pane();
    protected final HBox                    boxText   = new HBox();
    protected final TextField               textField = new AutoSizeTextField();

    protected final StringProperty          realText;
    protected       ReadOnlyBooleanProperty selected;

    public ConnectionNode(Connection connection, String textLeft, String textRight) {
        this.connection  = connection;

        try {
            if (connection != null) {
                realText = JavaBeanStringPropertyBuilder.create().bean(connection).name(Connection.PROPERTY_TEXT).build();
            } else {
                realText = new SimpleStringProperty(this, "text", "");
            }

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not create properties. This should never happen?!", e);
        }

        textField.textProperty().bindBidirectional(realText);

        boxText.getStyleClass().add("boxText");

        getChildren().addAll(paneMarker, line, boxText);

        if (textLeft != null) {
            Label labelLeft = new Label(textLeft);
            labelLeft.getStyleClass().add("labelLeft");
            boxText.getChildren().add(labelLeft);
        }
        boxText.getChildren().add(textField);
        if (textRight != null) {
            Label labelRight = new Label(textRight);
            labelRight.getStyleClass().add("labelRight");
            boxText.getChildren().add(labelRight);
        }

        paneMarker.setOnMousePressed(event -> textField.requestFocus());

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                textField.requestFocus();
            }
        });

        getStyleClass().add("connectionNode");
    }

    /**
     * Sest the containing {@link DiagramPane}
     * @param diagramPane the new pane
     */
    void setDiagramPane(DiagramPane diagramPane, ReadOnlyBooleanProperty selected) {
        this.diagramPane = diagramPane;
        this.selected    = selected;

        if (diagramPane != null) {
            setup();
        }
    }

    /**
     * Does basic setup.
     */
    protected void setup() {
        sourceNode = diagramPane.getJointNode(connection.getSource());
        targetNode = diagramPane.getJointNode(connection.getTarget());

        startXProperty().bind(sourceNode.layoutXProperty()
                .add(sourceNode.centerXProperty())
                .add(sourceNode.getNode().realXProperty()));
        startYProperty().bind(sourceNode.layoutYProperty()
                .add(sourceNode.centerYProperty())
                .add(sourceNode.getNode().realYProperty()));

        endXProperty().bind(targetNode.layoutXProperty()
                .add(targetNode.centerXProperty())
                .add(targetNode.getNode().realXProperty()));
        endYProperty().bind(targetNode.layoutYProperty()
                .add(targetNode.centerYProperty())
                .add(targetNode.getNode().realYProperty()));

        line.startXProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });
        line.startYProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });
        line.endXProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });
        line.endYProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });
        textField.heightProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });
        textField.widthProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });

        // TODO remove
        boxText.setPadding(new Insets(0, 0, 5, 0));
    }

    /**
     * Updates the text box.
     */
    protected void update() {
        double lenX = line.getEndX()-line.getStartX();
        double lenY = line.getEndY()-line.getStartY();
        double len  = Math.sqrt(lenX*lenX + lenY*lenY);

        double sx   = lenX/len;
        double sy   = lenY/len;

        Point2D normal = new Point2D(1, 0);
        Point2D se     = new Point2D(lenX, lenY).normalize();
        double  angle  = normal.angle(se);

        if (getStartY() > getEndY()) {
            angle = 360-angle;
        }

        boxText.setLayoutX(line.getEndX()-sx*len*0.5- boxText.getWidth()/2);
        boxText.setLayoutY(line.getEndY()-sy*len*0.5- boxText.getHeight());

        boxText.getTransforms().clear();
        boxText.getTransforms().add(new Rotate(angle, boxText.getWidth()/2, boxText.getHeight()));

        paneMarker.setPrefWidth(len);
        paneMarker.setPrefHeight(20);
        paneMarker.setLayoutX(line.getStartX());
        paneMarker.setLayoutY(line.getStartY()-paneMarker.getHeight()/2);

        paneMarker.getTransforms().clear();
        paneMarker.getTransforms().add(new Rotate(angle, 0, paneMarker.getHeight()/2));
    }

    protected void remove() {
    }

    public Connection getConnection() {
        return connection;
    }

    public DoubleProperty startYProperty() {
        return line.startYProperty();
    }

    public void setStartX(double value) {
        line.setStartX(value);
    }

    public double getStartX() {
        return line.getStartX();
    }

    public DoubleProperty startXProperty() {
        return line.startXProperty();
    }

    public void setStartY(double value) {
        line.setStartY(value);
    }

    public double getStartY() {
        return line.getStartY();
    }

    public void setEndX(double value) {
        line.setEndX(value);
    }

    public double getEndX() {
        return line.getEndX();
    }

    public DoubleProperty endXProperty() {
        return line.endXProperty();
    }

    public void setEndY(double value) {
        line.setEndY(value);
    }

    public double getEndY() {
        return line.getEndY();
    }

    public DoubleProperty endYProperty() {
        return line.endYProperty();
    }

    @Override
    public boolean isSelected() {
        return selected.get();
    }

    @Override
    public ReadOnlyBooleanProperty selectedProperty() {
        return selected;
    }

    @Override
    protected FlowDesignFxApplication getApplication() {
        return diagramPane.getApplication();
    }

    @Override
    public ObservableList<Property<?>> getNodeProperties() {
        return FXCollections.observableArrayList();
    }

    @Override
    public void registerShortcuts(ShortcutGroup group) {
        super.registerShortcuts(group);

        group.getShortcut(Shortcuts.SHORTCUT_MOVE_LEFT).setAction(event -> {
            diagramPane.requestSelection(connection.getSource().getElement());
            event.consume();
        });

        group.getShortcut(Shortcuts.SHORTCUT_MOVE_RIGHT).setAction(event -> {
            diagramPane.requestSelection(connection.getTarget().getElement());
            event.consume();
        });
    }
}
