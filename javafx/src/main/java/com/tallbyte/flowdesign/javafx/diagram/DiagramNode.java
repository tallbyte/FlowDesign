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

import com.tallbyte.flowdesign.core.Element;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import javafx.beans.property.*;
import javafx.beans.property.adapter.JavaBeanDoubleProperty;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-28)<br/>
 */
public class DiagramNode extends Pane {

    protected StringProperty    text       = new SimpleStringProperty(this, "text", "hey");

    protected List<Property<?>> properties = new ArrayList<>();
    protected DiagramImage      content;

    protected double            mouseX;
    protected double            mouseY;

    protected Element           element;

    protected DoubleProperty    realXProperty;
    protected DoubleProperty    realYProperty;
    protected DoubleProperty    realWidthProperty;
    protected DoubleProperty    realHeightProperty;

    public DiagramNode(Element element, DiagramImage content) {
        this.content = content;

        JavaBeanDoublePropertyBuilder.create().bean(element).name("x");

        this.element = element;

        try {
            realXProperty = JavaBeanDoublePropertyBuilder.create().bean(element).name("x").build();
            realYProperty = JavaBeanDoublePropertyBuilder.create().bean(element).name("y").build();
            realWidthProperty  = JavaBeanDoublePropertyBuilder.create().bean(element).name("width").build();
            realHeightProperty = JavaBeanDoublePropertyBuilder.create().bean(element).name("height").build();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not create properties. This should never happen?!");
        }

        layoutXProperty().bindBidirectional(realXProperty);
        layoutYProperty().bindBidirectional(realYProperty);

        content.widthProperty().bindBidirectional(realWidthProperty);
        content.heightProperty().bindBidirectional(realHeightProperty);

        addDefaultProperties();
        setup();
    }

    public Element getElement() {
        return element;
    }

    private void addDefaultProperties() {
        properties.add(content.layoutXProperty());
        properties.add(content.layoutYProperty());
        properties.add(content.heightProperty());
        properties.add(content.widthProperty());
        properties.add(text);
    }

    private void setup() {

        setOnMouseMoved(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();

            event.consume();
        });

        setOnMouseDragged(event -> {
            double dx = event.getSceneX() - mouseX;
            double dy = event.getSceneY() - mouseY;

            setLayoutX(getLayoutX() + dx);
            setLayoutY(getLayoutY() + dy);

            mouseX = event.getSceneX();
            mouseY = event.getSceneY();

            event.consume();
        });

        getChildren().add(content);
        StackPane.setMargin(content, new Insets(3, 3, 3, 3));

        TextField textFieldText = new TextField();
        textFieldText.textProperty().bindBidirectional(text);
        getChildren().add(textFieldText);
        textFieldText.getStyleClass().add("nodeTextHolder");
        StackPane.setAlignment(textFieldText, Pos.CENTER);
        textFieldText.prefWidthProperty().bind(widthProperty());
        textFieldText.setLayoutX(0);
        textFieldText.layoutYProperty().bind(heightProperty().divide(2).subtract(textFieldText.heightProperty().divide(2)));

        NodeModificator topRight = new NodeModificator(content, NodeModificator.Location.TOP_RIGHT);
        topRight.setCursor(Cursor.NE_RESIZE);
        getChildren().add(topRight);
        StackPane.setAlignment(topRight, Pos.TOP_RIGHT);
        topRight.layoutXProperty().bind(widthProperty().subtract(topRight.widthProperty()));
        topRight.setLayoutY(0);

        NodeModificator topLeft = new NodeModificator(content, NodeModificator.Location.TOP_LEFT);
        topLeft.setCursor(Cursor.NW_RESIZE);
        getChildren().add(topLeft);
        StackPane.setAlignment(topLeft, Pos.TOP_LEFT);
        topLeft.setLayoutX(0);
        topLeft.setLayoutY(0);

        NodeModificator bottomRight = new NodeModificator(content, NodeModificator.Location.BOTTOM_RIGHT);
        bottomRight.setCursor(Cursor.SE_RESIZE);
        getChildren().add(bottomRight);
        StackPane.setAlignment(bottomRight, Pos.BOTTOM_RIGHT);
        bottomRight.layoutXProperty().bind(widthProperty().subtract(bottomRight.widthProperty()));
        bottomRight.layoutYProperty().bind(heightProperty().subtract(bottomRight.heightProperty()));

        NodeModificator bottomLeft = new NodeModificator(content, NodeModificator.Location.BOTTOM_LEFT);
        bottomLeft.setCursor(Cursor.SW_RESIZE);
        getChildren().add(bottomLeft);
        StackPane.setAlignment(bottomLeft, Pos.BOTTOM_LEFT);
        bottomLeft.setLayoutX(0);
        bottomLeft.layoutYProperty().bind(heightProperty().subtract(bottomLeft.heightProperty()));

        prefWidthProperty().bind(content.widthProperty());
        prefHeightProperty().bind(content.heightProperty());

        setCursor(Cursor.MOVE);
    }


}
