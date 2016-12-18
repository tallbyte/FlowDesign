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

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import com.tallbyte.flowdesign.data.Connection;
import com.tallbyte.flowdesign.data.Joint;
import com.tallbyte.flowdesign.javafx.popup.DataTypePopup;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Popup;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import static javafx.scene.layout.Region.USE_PREF_SIZE;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-12)<br/>
 */
public class ArrowConnectionNode extends Group {

    private final Line      line    = new Line();
    private final Line      arrow0  = new Line();
    private final Line      arrow1  = new Line();
    private final HBox      boxText = new HBox();
    private final TextField text    = new TextField("");

    private final Connection connection;
    private final DiagramPane diagramPane;

    public ArrowConnectionNode(Connection connection, DiagramPane diagramPane) {
        this.connection  = connection;
        this.diagramPane = diagramPane;
        System.out.println("new conn");
        setup();
    }

    private void setup() {
        Joint source = connection.getSource();
        Joint target = connection.getTarget();

        JointNode sourceNode = diagramPane.getJointNode(source);
        JointNode targetNode = diagramPane.getJointNode(target);

        startXProperty().bind(sourceNode.layoutXProperty()
                .add(sourceNode.centerXProperty())
                .add(sourceNode.getNode().realXProperty()));
        startYProperty().bind(sourceNode.layoutYProperty()
                .add(sourceNode.centerYProperty())
                .add(sourceNode.getNode().realYProperty()));

        endXProperty().bind(targetNode.layoutXProperty()
                .add(targetNode.centerXProperty())
                .add(targetNode.getNode().realXProperty()));
        endYProperty().bind(targetNode.layoutYProperty()
                .add(targetNode.centerYProperty())
                .add(targetNode.getNode().realYProperty()));

        arrow0.startXProperty().bind(line.endXProperty());
        arrow0.startYProperty().bind(line.endYProperty());

        arrow1.startXProperty().bind(line.endXProperty());
        arrow1.startYProperty().bind(line.endYProperty());

        System.out.println("add");
        getChildren().addAll(line, arrow0, arrow1, boxText);
        boxText.getChildren().addAll(new Label("("), text, new Label(")"));


        line.startXProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });
        line.startYProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });
        line.endXProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });
        line.endYProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });
        text.heightProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });
        text.widthProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });

        final Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        boxText.setPadding(new Insets(0, 0, 5, 0));
        text.textProperty().addListener((observable, oldValue, newValue) -> {
            final String newText = newValue != null ? newValue : "";
            /**
             * Warning this is using an internal API which might change in the future.
             *
             * As of now (December 2016) JavaFX has no public text measurement API...
             */
            FontMetrics metrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(text.getFont());
            text.setPrefWidth(metrics.computeStringWidth(newText)+5);

            /**
             * Otherwise the textfield will "scroll" when updated, despite getting bigger
             */
            Platform.runLater(() -> {
                text.positionCaret(0);
                Platform.runLater(() -> text.positionCaret(newText.length()));
            });

            /**
             * Popup handling
             */
            final DataTypePopup dtp = new DataTypePopup(
                    sourceNode.getJoint().getElement().getDiagram().getProject(),
                    text.getText()
            );
            dtp.getStylesheets().add("/css/main.css");
            dtp.setPrefHeight(100);
            popup.getContent().clear();
            popup.getContent().add(dtp);

            Bounds bounds = text.localToScreen(text.getBoundsInLocal());
            if (bounds != null) {
                popup.show(this, bounds.getMinX()+10, bounds.getMinY()-100);
                System.out.println("showing");
            }
        });
        text.setText(null);
        text.setText("");
        text.setPrefColumnCount(60);
        text.setMinWidth(10);
        text.setMaxWidth(60);

        update();
    }

    private void update() {
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

        boxText.setLayoutX(line.getEndX()-sx*len*0.5- boxText.getWidth()/2);
        boxText.setLayoutY(line.getEndY()-sy*len*0.5- boxText.getHeight());

        Point2D normal = new Point2D(1, 0);
        Point2D line   = new Point2D(lenX, lenY).normalize();
        double  angle  = normal.angle(line);

        if (getStartY() > getEndY()) {
            angle = 360-angle;
        }

        boxText.getTransforms().clear();
        boxText.getTransforms().add(new Rotate(angle, boxText.getWidth()/2, boxText.getHeight()));
    }

    public DoubleProperty startYProperty() {
        return line.startYProperty();
    }

    public void setStartX(double value) {
        line.setStartX(value);
    }

    public double getStartX() {
        return line.getStartX();
    }

    public DoubleProperty startXProperty() {
        return line.startXProperty();
    }

    public void setStartY(double value) {
        line.setStartY(value);
    }

    public double getStartY() {
        return line.getStartY();
    }

    public void setEndX(double value) {
        line.setEndX(value);
    }

    public double getEndX() {
        return line.getEndX();
    }

    public DoubleProperty endXProperty() {
        return line.endXProperty();
    }

    public void setEndY(double value) {
        line.setEndY(value);
    }

    public double getEndY() {
        return line.getEndY();
    }

    public DoubleProperty endYProperty() {
        return line.endYProperty();
    }
}
