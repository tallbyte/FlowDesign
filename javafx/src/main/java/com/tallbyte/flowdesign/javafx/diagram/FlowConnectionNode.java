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
import com.tallbyte.flowdesign.javafx.FlowDesignFxApplication;
import com.tallbyte.flowdesign.javafx.popup.DataTypePopup;
import javafx.geometry.Bounds;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-12)<br/>
 */
public class FlowConnectionNode extends ConnectionNode {

    private final DataTypePopup  popup;

    private final Line           arrow0  = new Line();
    private final Line           arrow1  = new Line();


    /**
     * Creates a new {@link FlowConnectionNode}.
     * @param application the main application
     * @param connection the surrounding {@link Connection}
     */
    public FlowConnectionNode(FlowDesignFxApplication application, Connection connection) {
        super(application, connection, "(", ")");

        popup = new DataTypePopup(textField.textProperty());
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        application.getPopupHandler().setupPopup(popup);
    }

    @Override
    protected void setup() {
        super.setup();

        arrow0.startXProperty().bind(line.endXProperty());
        arrow0.startYProperty().bind(line.endYProperty());

        arrow1.startXProperty().bind(line.endXProperty());
        arrow1.startYProperty().bind(line.endYProperty());

        getChildren().addAll(arrow0, arrow1);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            showPopup();
        });

        popup.setKeyHandler(event -> {
            if (event.getCode() == KeyCode.SPACE && event.isControlDown()) {
                attemptAutoResolve();

                event.consume();
            }
        });

        textField.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.SPACE) {
                attemptAutoResolve();

                event.consume();
            }
        });

        update();
    }

    /**
     * Attempts to expand the text. If not possible (i.e. more than one result)
     * the popup will be shown.
     */
    private void attemptAutoResolve() {
        Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
        if (bounds != null) {
            String auto = popup.attemptAutoResolve(this, bounds.getMinX()+10, bounds.getMinY()-100,
                    sourceNode.getJoint().getElement().getDiagram().getProject(),
                    textField.getText()
            );

            if (auto != null) {
                textField.setText(auto);
                popup.hide();
            }
        }
    }

    /**
     * Shows the popup directly
     */
    private void showPopup() {
        Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
        if (bounds != null) {
            popup.show(this, bounds.getMinX()+10, bounds.getMinY()-100,
                    sourceNode
                            .getJoint()
                            .getElement()
                            .getDiagram()
                            .getProject(),
                    textField.getText()
            );
        }
    }

    /**
     * Updates the arrow and labels.
     */
    protected void update() {
        super.update();

        double al = 10;

        double lenX = line.getEndX()-line.getStartX();
        double lenY = line.getEndY()-line.getStartY();
        double len  = Math.sqrt(lenX*lenX + lenY*lenY);

        double sx   = lenX/len;
        double sy   = lenY/len;

        arrow0.setEndX(line.getEndX()-sx*al);
        arrow0.setEndY(line.getEndY()-sy*al);

        arrow1.setEndX(line.getEndX()-sx*al);
        arrow1.setEndY(line.getEndY()-sy*al);

        arrow0.getTransforms().clear();
        arrow0.getTransforms().add(new Rotate(30, line.getEndX(), line.getEndY()));
        arrow1.getTransforms().clear();
        arrow1.getTransforms().add(new Rotate(-30, line.getEndX(), line.getEndY()));
    }
}