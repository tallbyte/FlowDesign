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

package com.tallbyte.flowdesign.javafx.diagram.image;

import javafx.scene.canvas.GraphicsContext;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-30)<br/>
 */
public class ResourceDiagramImage extends DiagramImage {

    /**
     * Creates a new {@link ResourceDiagramImage} with default dimension.
     */
    public ResourceDiagramImage() {

    }

    @Override
    public void repaint() {
        GraphicsContext context = getGraphicsContext2D();
        double width  = getWidth();
        double height = getHeight();

        context.clearRect(0, 0, width, height);
        context.setStroke(getColor());
        context.setLineWidth(1.5);
        strokeTriangle(context, 0, 0, width, height);
    }

    private void strokeTriangle(GraphicsContext context, double x, double y, double w, double h) {
        context.strokeLine(
                x+w*0.5, y+context.getLineWidth(),
                x+w-context.getLineWidth(), y+h-context.getLineWidth()
        );

        context.strokeLine(
                x+context.getLineWidth(), y+h-context.getLineWidth(),
                x+w*0.5, y+context.getLineWidth()
        );

        context.strokeLine(
                x+context.getLineWidth(), y+h-context.getLineWidth(),
                x+w-2*context.getLineWidth(), y+h-context.getLineWidth()
        );
    }

}
