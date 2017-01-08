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

import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.shape.Rectangle;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-30)<br/>
 */
public class NodeModificator extends Rectangle {

    public enum Location {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    /**
     * Creates a new {@link NodeModificator}.
     * @param node the {@link ElementNode} that should be resized
     * @param location the target {@link Location} inside the parent
     */
    public NodeModificator(ElementNode node, Location location) {
        setWidth(6);
        setHeight(6);

        setOnMouseDragged(event -> {
            Parent parent = getParent();

            if (parent != null) {
                Bounds trans = parent.localToScene(parent.getBoundsInLocal());
                double dx = event.getSceneX() - trans.getMinX();
                double dy = event.getSceneY() - trans.getMinY();

                double minX = trans.getMinX();
                double maxX = minX + node.getRealWidth();

                double minY = trans.getMinY();
                double maxY = minY + node.getRealHeight();

                DiagramPane diagramPane = node.getDiagramPane();
                
                switch (location) {

                    case TOP_LEFT:
                        dx = Math.min(maxX-20, minX+dx) - minX;
                        dy = Math.min(maxY-20, minY+dy) - minY;

                        dx = diagramPane.toDiagramSpaceWidth (dx);
                        dy = diagramPane.toDiagramSpaceHeight(dy);

                        node.setRealX(node.getRealX()+dx);
                        node.setRealY(node.getRealY()+dy);

                        node.setRealWidth(node.getRealWidth()-dx);
                        node.setRealHeight(node.getRealHeight()-dy);
                        break;

                    case TOP_RIGHT:
                        dx = Math.max(minX+20, maxX+dx-trans.getWidth()) - maxX;
                        dy = Math.min(maxY-20, minY+dy) - minY;

                        dx = diagramPane.toDiagramSpaceWidth (dx);
                        dy = diagramPane.toDiagramSpaceHeight(dy);

                        node.setRealY(node.getRealY()+dy);

                        node.setRealWidth(node.getRealWidth()+dx);
                        node.setRealHeight(node.getRealHeight()-dy);
                        break;

                    case BOTTOM_LEFT:
                        dx = Math.min(maxX-20, minX+dx) - minX;
                        dy = Math.max(minY+20, maxY+dy-trans.getHeight()) - maxY;

                        dx = diagramPane.toDiagramSpaceWidth (dx);
                        dy = diagramPane.toDiagramSpaceHeight(dy);

                        node.setRealX(node.getRealX()+dx);

                        node.setRealWidth(node.getRealWidth()-dx);
                        node.setRealHeight(node.getRealHeight()+dy);
                        break;

                    case BOTTOM_RIGHT:
                        dx = Math.max(minX+20, maxX+dx-trans.getWidth()) - maxX;
                        dy = Math.max(minY+20, maxY+dy-trans.getHeight()) - maxY;

                        dx = diagramPane.toDiagramSpaceWidth (dx);
                        dy = diagramPane.toDiagramSpaceHeight(dy);

                        node.setRealWidth(node.getRealWidth()+dx);
                        node.setRealHeight(node.getRealHeight()+dy);
                        break;
                }
            }

            event.consume();
        });
    }

}
