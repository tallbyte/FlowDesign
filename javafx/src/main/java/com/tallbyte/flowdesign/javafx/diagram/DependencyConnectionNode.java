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

import com.tallbyte.flowdesign.data.Connection;
import com.tallbyte.flowdesign.data.DependencyConnection;
import com.tallbyte.flowdesign.data.Diagram;
import com.tallbyte.flowdesign.data.Joint;
import com.tallbyte.flowdesign.data.flow.FlowDiagram;
import com.tallbyte.flowdesign.javafx.FlowDesignFxApplication;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.css.PseudoClass;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-12)<br/>
 */
public class DependencyConnectionNode extends ConnectionNode {

    protected final Circle circle = new Circle();
    protected       ObjectProperty<?> reference;

    public DependencyConnectionNode(FlowDesignFxApplication application, DependencyConnection connection) {
        super(application, connection, null, null);

        try {
            reference = JavaBeanObjectPropertyBuilder.create().bean(connection).name("reference").build();
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

        circle.getStyleClass().add("connectionBlob");
    }

    @Override
    protected void setup() {
        super.setup();

        circle.centerXProperty().bind(endXProperty());
        circle.centerYProperty().bind(endYProperty());
        circle.setRadius(3);

        getChildren().addAll(circle);
    }
}
