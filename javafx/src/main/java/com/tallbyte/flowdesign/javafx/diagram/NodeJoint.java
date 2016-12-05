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

import com.tallbyte.flowdesign.core.Joint;
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
public class NodeJoint extends Circle {

    private final Joint       joint;
    private final DiagramNode node;

    public NodeJoint(Joint joint, DiagramNode node) {
        this.joint = joint;
        this.node  = node;

        setup();
    }

    public Joint getJoint() {
        return joint;
    }

    public DiagramNode getNode() {
        return node;
    }

    private void setup() {
        setCursor(Cursor.CROSSHAIR);
        setOnMouseDragged(Event::consume);
        setOnDragDetected(event -> {
            ConnectionLine line = new ConnectionLine();
            line.startXProperty().bind(centerXProperty());
            line.startYProperty().bind(centerYProperty());
            line.setEndX(line.getStartX());
            line.setEndY(line.getStartY());
            line.setMouseTransparent(true);

            startFullDrag();

            node.getDiagramPane().addEventFilter(MouseEvent.MOUSE_DRAGGED, eventDrag -> {
                line.setEndX(eventDrag.getX());
                line.setEndY(eventDrag.getY());
            });

            node.getDiagramPane().setConnectionRequest(new ConnectionRequest(node.getElement()), line);
            event.consume();
        });
    }

}
