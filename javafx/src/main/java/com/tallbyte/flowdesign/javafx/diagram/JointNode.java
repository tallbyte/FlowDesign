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

import com.tallbyte.flowdesign.data.Joint;
import com.tallbyte.flowdesign.data.JointJoinException;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-05)<br/>
 */
public class JointNode extends Circle {

    private final Joint       joint;
    private final ElementNode node;

    public JointNode(Joint joint, ElementNode node) {
        this.joint = joint;
        this.node  = node;

        setup();
    }

    public Joint getJoint() {
        return joint;
    }

    public ElementNode getNode() {
        return node;
    }

    private void setup() {
        setCursor(Cursor.CROSSHAIR);
        setOnMouseDragged(Event::consume);

        setOnMousePressed(event -> node.getDiagramPane().setJoint(joint));
        setOnMouseReleased(event -> node.getDiagramPane().setJoint(null));

        DiagramPane diagramPane = node.getDiagramPane();
        setOnDragDetected(event -> {
            ConnectionNode line = new ConnectionNode(null, null, null);
            line.startXProperty().bind(centerXProperty().add(node.realXProperty()));
            line.startYProperty().bind(centerYProperty().add(node.realYProperty()));
            line.setEndX(line.getStartX());
            line.setEndY(line.getStartY());
            line.setMouseTransparent(true);

            startFullDrag();

            diagramPane.addDisplayNode(line);
            event.consume();
        });

        setOnMouseDragReleased(event -> {
            Joint source = diagramPane.getJoint();
            if (source != null && source != joint) {
                try {
                    if (source.join(joint) != null) {
                    }
                } catch (JointJoinException e) {
                    // this should never happen
                    e.printStackTrace();
                }
            }
            diagramPane.setJoint(null);
        });
    }

}
