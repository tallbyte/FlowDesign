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

import javafx.animation.TranslateTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-30)<br/>
 */
public class StickmanDiagramImage extends DiagramImage {

    /**
     * Creates a new {@link StickmanDiagramImage} with default dimension.
     */
    public StickmanDiagramImage() {
        setWidth(50);
    }

    @Override
    public void repaint() {
        GraphicsContext context = getGraphicsContext2D();

        double width  = getWidth();
        double height = getHeight();

        context.clearRect(0, 0, width, height);
        context.save();

        //context.getTransform().append(transform);

        double mid = width*0.5;
        double armTarget = height*0.4;
        double armBase   = height*0.3;
        double footBase  = height*0.6;
        double headBase  = height*0.2;


        context.setStroke(getColor());
        context.setLineWidth(1.5);

        // feet
        context.strokeLine(width*0.2, height, mid, footBase);
        context.strokeLine(width*0.8, height, mid, footBase);

        // body
        context.strokeLine(mid, footBase, mid, armBase);

        // arms
        context.strokeLine(0    , armTarget, mid, armBase);
        context.strokeLine(width, armTarget, mid, armBase);

        // throat
        context.strokeLine(mid, armBase, mid, headBase);

        // head
        context.strokeOval(width*0.4, context.getLineWidth(), width*0.2, height*0.2-2*context.getLineWidth());

        context.restore();
    }

}
