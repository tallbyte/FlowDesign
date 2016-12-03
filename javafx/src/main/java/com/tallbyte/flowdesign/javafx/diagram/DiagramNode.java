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
import com.tallbyte.flowdesign.core.environment.Connection;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import javafx.beans.property.*;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-28)<br/>
 */
public class DiagramNode extends Pane {

    protected List<Property<?>> properties = new ArrayList<>();
    protected DiagramPane       diagramPane;
    protected DiagramImage      content;

    protected double            mouseX;
    protected double            mouseY;

    protected Element           element;

    protected DoubleProperty    realXProperty;
    protected DoubleProperty    realYProperty;
    protected DoubleProperty    realWidthProperty;
    protected DoubleProperty    realHeightProperty;

    protected StringProperty    text     = new SimpleStringProperty(this, "text", "");
    protected BooleanProperty   selected = new SimpleBooleanProperty(this, "selected", false);

    private   boolean           release  = true;

    public DiagramNode(DiagramPane diagramPane, Element element, DiagramImage content) {
        this.diagramPane = diagramPane;
        this.content     = content;
        this.element     = element;

        try {
            realXProperty = JavaBeanDoublePropertyBuilder.create().bean(element).name("x").build();
            realYProperty = JavaBeanDoublePropertyBuilder.create().bean(element).name("y").build();
            realWidthProperty  = JavaBeanDoublePropertyBuilder.create().bean(element).name("width").build();
            realHeightProperty = JavaBeanDoublePropertyBuilder.create().bean(element).name("height").build();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not create properties. This should never happen?!");
        }

        addDefaultProperties();
        setup();
    }

    /**
     * Gets the backed {@link Element}.
     * @return Returns the backed {@link Element}.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Gets the real x property
     * @return Returns the property.
     */
    public DoubleProperty realXPropertyProperty() {
        return realXProperty;
    }

    /**
     * Gets the real y property
     * @return Returns the property.
     */
    public DoubleProperty realYPropertyProperty() {
        return realYProperty;
    }

    /**
     * Gets the real width property
     * @return Returns the property.
     */
    public DoubleProperty realWidthPropertyProperty() {
        return realWidthProperty;
    }

    /**
     * Gets the real height property
     * @return Returns the property.
     */
    public DoubleProperty realHeightPropertyProperty() {
        return realHeightProperty;
    }

    /**
     * Gets the text property
     * @return Returns the property.
     */
    public StringProperty textProperty() {
        return text;
    }

    /**
     * Gets the selected property
     * @return Returns the property.
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * Moves this {@link DiagramNode}.
     * @param dx the delta x
     * @param dy the delta y
     */
    void move(double dx, double dy) {
        setLayoutX(getLayoutX() + dx);
        setLayoutY(getLayoutY() + dy);

        release = false;
    }

    /**
     * Adds the according properties to the externally-accessible list.
     */
    private void addDefaultProperties() {
        properties.add(realXProperty);
        properties.add(realYProperty);
        properties.add(realWidthProperty);
        properties.add(realHeightProperty);
        properties.add(text);
    }

    /**
     * Setup of bindings and listeners.
     */
    private void setup() {
        /*
         * Basic layout
         */

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
        topRight.visibleProperty().bind(selected);
        topRight.setLayoutY(0);

        NodeModificator topLeft = new NodeModificator(content, NodeModificator.Location.TOP_LEFT);
        topLeft.setCursor(Cursor.NW_RESIZE);
        getChildren().add(topLeft);
        StackPane.setAlignment(topLeft, Pos.TOP_LEFT);
        topLeft.visibleProperty().bind(selected);
        topLeft.setLayoutX(0);
        topLeft.setLayoutY(0);

        NodeModificator bottomRight = new NodeModificator(content, NodeModificator.Location.BOTTOM_RIGHT);
        bottomRight.setCursor(Cursor.SE_RESIZE);
        getChildren().add(bottomRight);
        StackPane.setAlignment(bottomRight, Pos.BOTTOM_RIGHT);
        bottomRight.visibleProperty().bind(selected);
        bottomRight.layoutXProperty().bind(widthProperty().subtract(bottomRight.widthProperty()));
        bottomRight.layoutYProperty().bind(heightProperty().subtract(bottomRight.heightProperty()));

        NodeModificator bottomLeft = new NodeModificator(content, NodeModificator.Location.BOTTOM_LEFT);
        bottomLeft.setCursor(Cursor.SW_RESIZE);
        getChildren().add(bottomLeft);
        StackPane.setAlignment(bottomLeft, Pos.BOTTOM_LEFT);
        bottomLeft.visibleProperty().bind(selected);
        bottomLeft.setLayoutX(0);
        bottomLeft.layoutYProperty().bind(heightProperty().subtract(bottomLeft.heightProperty()));

        Circle circle = new Circle();
        circle.getStyleClass().add("nodeConnectionBlob");
        circle.setRadius(3);
        circle.visibleProperty().bind(selected);
        circle.centerXProperty().bind(widthProperty().multiply(0.5));
        circle.centerYProperty().bind(heightProperty().multiply(1.0));
        getChildren().add(circle);


        /*
         * Basic settings
         */

        selected.set(true);

        layoutXProperty().bindBidirectional(realXProperty);
        layoutYProperty().bindBidirectional(realYProperty);

        content.widthProperty().bindBidirectional(realWidthProperty);
        content.heightProperty().bindBidirectional(realHeightProperty);

        prefWidthProperty().bind(content.widthProperty());
        prefHeightProperty().bind(content.heightProperty());

        setCursor(Cursor.MOVE);

        /*
         * Handlers
         */

        setOnMouseMoved(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();

            event.consume();
        });

        setOnMouseDragged(event -> {
            double dx = event.getSceneX() - mouseX;
            double dy = event.getSceneY() - mouseY;

            diagramPane.moveAllSelected(dx, dy);

            mouseX = event.getSceneX();
            mouseY = event.getSceneY();

            event.consume();
        });

        EventHandler<? super MouseEvent> handlerClickedPre = event -> {
            if (!diagramPane.hasSelectedNodes()) {
                if (!event.isShiftDown()) {
                    diagramPane.setAllUnselected();
                }

                selected.set(true);
            }

            release = true;
            event.consume();
        };

        EventHandler<? super MouseEvent> handlerClickedAfter = event -> {
            if (release) {
                if (!event.isShiftDown()) {
                    diagramPane.setAllUnselected();
                }

                selected.set(true);

                event.consume();
            }
        };

        content.setMouseTransparent(true);

        EventHandler<? super MouseEvent> handlerReleased = event -> {
            ConnectionRequest request = diagramPane.getConnectionRequest();
            if (request != null && request.getSource() != element) {
                diagramPane.getDiagram().addConnection(
                        new Connection(
                                request.getSource(),
                                element
                        )
                );

                /*System.out.println(request.getSource());
                System.out.println(element);*/
            }
            diagramPane.setConnectionRequest(null, null);
        };

        setOnMousePressed(handlerClickedPre);
        textFieldText.setOnMousePressed(handlerClickedPre);

        setOnMouseDragReleased(handlerReleased);
        textFieldText.setOnMouseDragReleased(handlerReleased);

        setOnMouseClicked(handlerClickedAfter);
        textFieldText.setOnMouseClicked(handlerClickedAfter);

        circle.setCursor(Cursor.CROSSHAIR);
        circle.setOnMouseDragged(Event::consume);
        circle.setOnDragDetected(event -> {
            ConnectionLine line = new ConnectionLine();
            line.startXProperty().bind(realXProperty.add(widthProperty().multiply(0.5)));
            line.startYProperty().bind(realYProperty.add(heightProperty().multiply(1).subtract(circle.radiusProperty().multiply(0.5).multiply(0))));
            line.setEndX(line.getStartX());
            line.setEndY(line.getStartY());
            line.setMouseTransparent(true);

            circle.startFullDrag();

            diagramPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, eventDrag -> {
                line.setEndX(eventDrag.getX());
                line.setEndY(eventDrag.getY());
            });

            diagramPane.setConnectionRequest(new ConnectionRequest(element), line);
            event.consume();
        });
    }


}
