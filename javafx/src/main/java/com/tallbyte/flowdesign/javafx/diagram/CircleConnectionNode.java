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
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-12)<br/>
 */
public class CircleConnectionNode extends ConnectionNode {

    private final Circle circle = new Circle();


    public CircleConnectionNode(Connection connection) {
        super(connection);
    }

    @Override
    protected void setup() {
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

        circle.centerXProperty().bind(endXProperty());
        circle.centerYProperty().bind(endYProperty());
        circle.setRadius(3);

        getChildren().addAll(circle);
    }
}
