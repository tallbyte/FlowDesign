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

package com.tallbyte.flowdesign.javafx.diagram.element;

import com.tallbyte.flowdesign.data.Diagram;
import com.tallbyte.flowdesign.data.flow.FlowDiagram;
import com.tallbyte.flowdesign.data.mask.MaskComment;
import com.tallbyte.flowdesign.javafx.diagram.ElementNode;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-08)<br/>
 */
public class MaskCommentElementNode extends ElementNode<DiagramImage> {

    private final MaskComment comment;

    protected ObjectProperty<?> reference;

    public MaskCommentElementNode(MaskComment element, DiagramImage content) {
        super(element, content, Pos.CENTER);


        try {
            reference = JavaBeanObjectPropertyBuilder.create().bean(element).name("reference").build();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not create properties. This should never happen?!", e);
        }

        reference.addListener((observable, oldValue, newValue) -> {
            pseudoClassStateChanged(PseudoClass.getPseudoClass("referenced"), newValue != null);
        });
        pseudoClassStateChanged(PseudoClass.getPseudoClass("referenced"), reference.getValue() != null);

        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            Object ref = reference.get();

            if (event.isControlDown() && event.getCode() == KeyCode.B && ref instanceof FlowDiagram) {
                diagramPane.getDiagramsPane().addDiagram((Diagram) ref);
            }
        });

        setOnMousePressed(event -> requestFocus());

        this.comment = element;
    }

    @Override
    protected void setup() {
        super.setup();
    }
}
