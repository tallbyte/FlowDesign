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
import com.tallbyte.flowdesign.javafx.popup.DataTypePopup;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Popup;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-05)<br/>
 */
public class ConnectionNode extends Group implements SelectableNode {

    protected final Line                    line = new Line();

    protected final Connection              connection;
    protected       DiagramPane             diagramPane;

    protected       ReadOnlyBooleanProperty selected;

    public ConnectionNode(Connection connection) {
        this.connection  = connection;

        getChildren().add(line);
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

    protected void setup() {

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
}
