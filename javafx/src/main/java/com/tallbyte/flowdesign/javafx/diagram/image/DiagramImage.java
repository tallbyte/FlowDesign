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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-30)<br/>
 */
public abstract class DiagramImage extends Canvas {

    protected ObjectProperty<Color> color = new SimpleObjectProperty<>(this, "color", Color.BLACK);

    /**
     * Creates a new {@link DiagramImage}.
     */
    public DiagramImage() {
        widthProperty().addListener(observable -> {
            repaint();
        });
        heightProperty().addListener(observable -> {
            repaint();
        });

        setWidth(75);
        setHeight(75);

        color.addListener(observable -> {
            repaint();
        });

        repaint();
    }

    public Color getColor() {
        return color.get();
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    /**
     * Repaints this {@link DiagramImage}.
     */
    protected abstract void repaint();

}
