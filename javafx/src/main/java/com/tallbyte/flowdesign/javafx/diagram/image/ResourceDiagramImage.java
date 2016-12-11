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
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

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
        setWidth(75);
        setHeight(75);
    }

    @Override
    public void repaint() {
        GraphicsContext context = getGraphicsContext2D();
        double width  = getWidth();
        double height = getHeight();

        context.clearRect(0, 0, width, height);
        context.setStroke(Color.BLACK);
        context.setLineWidth(1.5);
        context.strokeOval(
                context.getLineWidth(), context.getLineWidth(),
                width - 2*context.getLineWidth(), height - 2*context.getLineWidth()
        );

        double rx = (1+Math.cos(2*Math.PI*0.125))*0.5*width;
        double ry = (1+Math.sin(2*Math.PI*0.125))*0.5*height;
        double rw = width*0.25;
        double rh = height*0.25;

        rx -= rw*0.75;
        ry -= rh*0.75;

        strokeRect(context, rx, ry, rw, rh);
    }

    private void strokeRect(GraphicsContext context, double x, double y, double w, double h) {
        context.strokeLine(
                x+((w-2*context.getLineWidth())*0.5), y+context.getLineWidth(),
                x+w-context.getLineWidth(), y+h-context.getLineWidth()
        );

        context.strokeLine(
                x+context.getLineWidth(), y+h-context.getLineWidth(),
                x+((w-2*context.getLineWidth())*0.5), y+context.getLineWidth()
        );

        context.strokeLine(
                x+context.getLineWidth(), y+h-context.getLineWidth(),
                x+w-2*context.getLineWidth(), y+h-context.getLineWidth()
        );
    }

}