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
import com.tallbyte.flowdesign.core.Joint;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
     * Gets the containing {@link DiagramPane}.
     * @return Returns the pane
     */
    public DiagramPane getDiagramPane() {
        return diagramPane;
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
    public DoubleProperty realXProperty() {
        return realXProperty;
    }

    /**
     * Gets the real y property
     * @return Returns the property.
     */
    public DoubleProperty realYProperty() {
        return realYProperty;
    }

    /**
     * Gets the real width property
     * @return Returns the property.
     */
    public DoubleProperty realWidthProperty() {
        return realWidthProperty;
    }

    /**
     * Gets the real height property
     * @return Returns the property.
     */
    public DoubleProperty realHeightProperty() {
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
     * Gets the modifiable properties
     * @return Returns a list of such properties.
     */
    public Iterable<Property<?>> getElementProperties() {
        return properties;
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

    private NodeModificator addModificator(NodeModificator.Location modLoc,
                                           Cursor cursor,
                                           Pos locStack,
                                           DoubleBinding bindingX,
                                           boolean subtractX,
                                           DoubleBinding bindingY,
                                           boolean subtractY) {
        NodeModificator element = new NodeModificator(content, modLoc);

        if (subtractX) {
            bindingX = bindingX.subtract(element.widthProperty());
        }

        if (subtractY) {
            bindingY = bindingY.subtract(element.heightProperty());
        }

        element.setCursor(cursor);
        getChildren().add(element);
        StackPane.setAlignment(element, locStack);
        element.visibleProperty().bind(selected.or(hoverProperty()));
        element.layoutXProperty().bind(bindingX);
        element.layoutYProperty().bind(bindingY);

        return element;
    }

    private JointNode addJoint(Joint joint) {
        JointNode element = new JointNode(joint, this);
        switch (joint.getLocation()) {
            case NORTH:
                element.centerXProperty().bind(widthProperty().multiply(0.5));
                element.centerYProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
                break;

            case EAST:
                element.centerXProperty().bind(widthProperty());
                element.centerYProperty().bind(heightProperty().multiply(0.5));
                break;

            case SOUTH:
                element.centerXProperty().bind(widthProperty().multiply(0.5));
                element.centerYProperty().bind(heightProperty());
                break;

            case WEST:
                element.centerXProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
                element.centerYProperty().bind(heightProperty().multiply(0.5));
                break;
        }
        element.visibleProperty().bind(
                selected.or(hoverProperty())
                        .and(Bindings.createBooleanBinding(joint::isOutput))
                        .or(Bindings.createBooleanBinding(() -> {
                            Joint j = diagramPane.getJoint();

                            return j != null && j.canJoin(joint);
                        }, diagramPane.jointProperty()))
        );
        element.setRadius(3);
        getChildren().add(element);
        diagramPane.registerJointNode(element);

        return element;
    }

    /**
     * Setup of bindings and listeners.
     */
    private void setup() {
        /*
         * Basic layout
         */

        Rectangle border = new Rectangle();
        border.getStyleClass().add("nodeBorderRectangle");
        getChildren().add(border);
        border.widthProperty().bind(widthProperty());
        border.heightProperty().bind(heightProperty());
        border.setStrokeWidth(2);
        border.setMouseTransparent(true);
        border.setFill(Color.TRANSPARENT);
        border.visibleProperty().bind(selected);

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

        NodeModificator topRight = addModificator(
                NodeModificator.Location.TOP_RIGHT,
                Cursor.NE_RESIZE,
                Pos.TOP_RIGHT,
                widthProperty().subtract(0),
                true,
                Bindings.createDoubleBinding(() -> 0.0),
                false
        );

        NodeModificator topLeft = addModificator(
                NodeModificator.Location.TOP_LEFT,
                Cursor.NW_RESIZE,
                Pos.TOP_LEFT,
                Bindings.createDoubleBinding(() -> 0.0),
                false,
                Bindings.createDoubleBinding(() -> 0.0),
                false
        );

        NodeModificator bottomRight = addModificator(
                NodeModificator.Location.BOTTOM_RIGHT,
                Cursor.SE_RESIZE,
                Pos.BOTTOM_RIGHT,
                widthProperty().subtract(0),
                true,
                heightProperty().subtract(0),
                true
        );

        NodeModificator bottomLeft = addModificator(
                NodeModificator.Location.BOTTOM_LEFT,
                Cursor.SW_RESIZE,
                Pos.BOTTOM_LEFT,
                Bindings.createDoubleBinding(() -> 0.0),
                false,
                heightProperty().subtract(0),
                true
        );

        element.getJoints().forEach(this::addJoint);

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

        selected.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                diagramPane.setNode(this);
            }
        });

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

        setOnMousePressed(handlerClickedPre);
        textFieldText.setOnMousePressed(handlerClickedPre);

        setOnMouseClicked(handlerClickedAfter);
        textFieldText.setOnMouseClicked(handlerClickedAfter);
    }


}
