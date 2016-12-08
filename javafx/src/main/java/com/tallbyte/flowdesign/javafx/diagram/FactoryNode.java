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

import com.tallbyte.flowdesign.javafx.diagram.factory.DiagramImageFactory;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;


/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-04)<br/>
 */
public class FactoryNode extends Label {

    /**
     * Creates a new {@link FactoryNode}
     * @param factory the {@link DiagramImageFactory} used to create images
     * @param name the display-name
     * @param text the display text
     */
    public FactoryNode(DiagramImageFactory factory, String name, String text) {
        setGraphic(factory.createDiagramImage());
        setText(text);

        setOnDragDetected(event -> {
            startFullDrag();

            Dragboard        db      = startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString(name);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);

            db.setDragView(getGraphic().snapshot(params, null));
            db.setDragViewOffsetX(event.getX());
            db.setDragViewOffsetY(event.getY());
            db.setContent(content);

            event.consume();
        });
    }

}
