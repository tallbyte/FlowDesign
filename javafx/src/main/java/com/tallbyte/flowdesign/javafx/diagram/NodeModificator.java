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

import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
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

    public NodeModificator(DiagramImage image, Location location) {
        setWidth(6);
        setHeight(6);

        setOnMouseDragged(event -> {
            Parent parent = getParent();

            if (parent != null) {
                Bounds trans = parent.localToScene(parent.getBoundsInLocal());
                double dx = event.getSceneX() - trans.getMinX();
                double dy = event.getSceneY() - trans.getMinY();

                System.out.println("dx="+dx+" dy="+dy);

                switch (location) {

                    case TOP_LEFT:
                        dx = Math.min(trans.getMaxX()-20, trans.getMinX()+dx) - trans.getMinX();
                        dy = Math.min(trans.getMaxY()-20, trans.getMinY()+dy) - trans.getMinY();

                        parent.setLayoutX(parent.getLayoutX()+dx);
                        parent.setLayoutY(parent.getLayoutY()+dy);

                        image.setWidth(image.getWidth()-dx);
                        image.setHeight(image.getHeight()-dy);
                        break;

                    case TOP_RIGHT:
                        dx = Math.max(trans.getMinX()+20, trans.getMaxX()+dx-trans.getWidth()) - trans.getMaxX();
                        dy = Math.min(trans.getMaxY()-20, trans.getMinY()+dy) - trans.getMinY();

                        parent.setLayoutY(parent.getLayoutY()+dy);

                        image.setWidth(image.getWidth()+dx);
                        image.setHeight(image.getHeight()-dy);
                        break;

                    case BOTTOM_LEFT:
                        dx = Math.min(trans.getMaxX()-20, trans.getMinX()+dx) - trans.getMinX();
                        dy = Math.max(trans.getMinY()+20, trans.getMaxY()+dy-trans.getHeight()) - trans.getMaxY();

                        parent.setLayoutX(parent.getLayoutX()+dx);

                        image.setWidth(image.getWidth()-dx);
                        image.setHeight(image.getHeight()+dy);
                        break;

                    case BOTTOM_RIGHT:
                        dx = Math.max(trans.getMinX()+20, trans.getMaxX()+dx-trans.getWidth()) - trans.getMaxX();
                        dy = Math.max(trans.getMinY()+20, trans.getMaxY()+dy-trans.getHeight()) - trans.getMaxY();

                        image.setWidth(image.getWidth()+dx);
                        image.setHeight(image.getHeight()+dy);
                        break;
                }
            }

            event.consume();
        });
    }

}
