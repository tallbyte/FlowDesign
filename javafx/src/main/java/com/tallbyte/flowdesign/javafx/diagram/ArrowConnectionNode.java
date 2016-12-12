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

import com.tallbyte.flowdesign.data.Connection;
import com.tallbyte.flowdesign.data.Joint;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-12)<br/>
 */
public class ArrowConnectionNode extends Group {

    private final Line line   = new Line();
    private final Line arrow0 = new Line();
    private final Line arrow1 = new Line();

    private final Connection connection;
    private final DiagramPane diagramPane;

    public ArrowConnectionNode(Connection connection, DiagramPane diagramPane) {
        this.connection  = connection;
        this.diagramPane = diagramPane;

        setup();
    }

    private void setup() {
        Joint source = connection.getSource();
        Joint target = connection.getTarget();

        JointNode sourceNode = diagramPane.getJointNode(source);
        JointNode targetNode = diagramPane.getJointNode(target);

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

        arrow0.startXProperty().bind(line.endXProperty());
        arrow0.startYProperty().bind(line.endYProperty());

        arrow1.startXProperty().bind(line.endXProperty());
        arrow1.startYProperty().bind(line.endYProperty());

        getChildren().addAll(line, arrow0, arrow1);

        line.startXProperty().addListener((observable, oldValue, newValue) -> {
            updateArrow();
        });
        line.startYProperty().addListener((observable, oldValue, newValue) -> {
            updateArrow();
        });
        line.endXProperty().addListener((observable, oldValue, newValue) -> {
            updateArrow();
        });
        line.endYProperty().addListener((observable, oldValue, newValue) -> {
            updateArrow();
        });

        updateArrow();
    }

    private void updateArrow() {
        double al = 10;

        double lenX = line.getEndX()-line.getStartX();
        double lenY = line.getEndY()-line.getStartY();
        double len  = Math.sqrt(lenX*lenX + lenY*lenY);

        double sx   = lenX/len;
        double sy   = lenY/len;

        arrow0.setEndX(line.getEndX()-sx*al);
        arrow0.setEndY(line.getEndY()-sy*al);

        arrow1.setEndX(line.getEndX()-sx*al);
        arrow1.setEndY(line.getEndY()-sy*al);

        arrow0.getTransforms().clear();
        arrow0.getTransforms().add(new Rotate(30, line.getEndX(), line.getEndY()));
        arrow1.getTransforms().clear();
        arrow1.getTransforms().add(new Rotate(-30, line.getEndX(), line.getEndY()));
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
}
