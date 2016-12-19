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

import com.tallbyte.flowdesign.data.Element;
import com.tallbyte.flowdesign.data.Joint;
import com.tallbyte.flowdesign.javafx.ColorHandler;
import com.tallbyte.flowdesign.javafx.control.AutoSizeTextField;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import com.tallbyte.flowdesign.javafx.property.ColorProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanStringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-28)<br/>
 */
public class ElementNode extends Pane implements SelectableNode {

    private static final List<CssMetaData<? extends Styleable, ?>> STYLABLES;
    private static final CssMetaData<ElementNode, Color>           DEFAULT_COLOR = new CssMetaData<ElementNode, Color>(
            "-fx-default-color",
            StyleConverter.getColorConverter(),
            Color.BLACK ) {

        @Override
        public boolean isSettable(ElementNode styleable) {
            return !styleable.defaultColorProperty().isBound();
        }

        @Override
        public StyleableProperty<Color> getStyleableProperty(ElementNode styleable) {
            return styleable.defaultColorProperty();
        }
    };

    static {
        List<CssMetaData<? extends Styleable, ?>> list = new ArrayList<>(getClassCssMetaData());
        list.add(DEFAULT_COLOR);
        STYLABLES = Collections.unmodifiableList(list);
    }

    /*
     * General stuff
     */

    protected ObservableList<Property<?>> properties  = FXCollections.observableArrayList();
    protected DiagramPane                 diagramPane = null;
    protected Pos                         posLabel;
    protected DiagramImage                content;
    protected Element                     element;

    /*
     * General properties
     */

    protected DoubleBinding                  widthExtend  = Bindings.createDoubleBinding(() -> 0.0);
    protected DoubleBinding                  heightExtend = Bindings.createDoubleBinding(() -> 0.0);
    protected ReadOnlyBooleanProperty        selected;


    protected StyleableObjectProperty<Color> defaultColor = new SimpleStyleableObjectProperty<>(DEFAULT_COLOR);


    /*
     * Outside properties
     */

    protected DoubleProperty realX;
    protected DoubleProperty realY;
    protected DoubleProperty realWidth;
    protected DoubleProperty realHeight;
    protected StringProperty text;
    protected ColorProperty  color;

    /*
     * Working variables
     */

    private   boolean           release  = true;


    public ElementNode(Element element, DiagramImage content, Pos posLabel) {
        this.content     = content;
        this.element     = element;
        this.posLabel    = posLabel;

        getStyleClass().add("elementNode");

        try {
            realX      = JavaBeanDoublePropertyBuilder.create().bean(element).name("x").build();
            realY      = JavaBeanDoublePropertyBuilder.create().bean(element).name("y").build();
            realWidth  = JavaBeanDoublePropertyBuilder.create().bean(element).name("width").build();
            realHeight = JavaBeanDoublePropertyBuilder.create().bean(element).name("height").build();
            text       = JavaBeanStringPropertyBuilder.create().bean(element).name("text").build();
            color      = new ColorProperty(this, "color", null);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not create properties. This should never happen?!");
        }

        content.colorProperty().bind(Bindings.when(color.isNotNull()).then(color).otherwise(defaultColor));

        addDefaultProperties();
    }

    /**
     * Gets the containing {@link DiagramPane}.
     * @return Returns the pane
     */
    public DiagramPane getDiagramPane() {
        return diagramPane;
    }

    /**
     * Sest the containing {@link DiagramPane}
     * @param diagramPane the new pane
     */
    void setDiagramPane(DiagramPane diagramPane, ReadOnlyBooleanProperty selected) {
        this.diagramPane = diagramPane;
        this.selected    = selected;

        if (diagramPane != null) {
            setup();
        }
    }

    /**
     * Gets the backed {@link Element}.
     * @return Returns the backed {@link Element}.
     */
    public Element getElement() {
        return element;
    }

    public void setRealX(double realX) {
        this.realX.set(realX);
    }

    public double getRealX() {
        return realX.get();
    }

    /**
     * Gets the real x property
     * @return Returns the property.
     */
    public DoubleProperty realXProperty() {
        return realX;
    }

    public void setRealY(double realY) {
        this.realY.set(realY);
    }

    public double getRealY() {
        return realY.get();
    }

    /**
     * Gets the real y property
     * @return Returns the property.
     */
    public DoubleProperty realYProperty() {
        return realY;
    }

    public void setRealWidth(double realWidth) {
        this.realWidth.set(realWidth);
    }

    public double getRealWidth() {
        return realWidth.get();
    }

    /**
     * Gets the real width property
     * @return Returns the property.
     */
    public DoubleProperty realWidthProperty() {
        return realWidth;
    }

    public void setRealHeight(double realHeight) {
        this.realHeight.set(realHeight);
    }

    public double getRealHeight() {
        return realHeight.get();
    }

    /**
     * Gets the real height property
     * @return Returns the property.
     */
    public DoubleProperty realHeightProperty() {
        return realHeight;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public String getText() {
        return text.get();
    }

    /**
     * Gets the text property
     * @return Returns the property.
     */
    public StringProperty textProperty() {
        return text;
    }

    /**
     * Gets the current {@link Color}.
     * @return Returns the {@link Color}.
     */
    public Color getColor() {
        return color.get();
    }

    /**
     * Sets the current {@link Color}.
     * @param color the new {@link Color}
     */
    public void setColor(Color color) {
        this.color.set(color);
    }

    /**
     * Gets the color property
     * @return Returns the property.
     */
    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public Color getDefaultColor() {
        return defaultColor.get();
    }

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor.set(defaultColor);
    }

    public StyleableObjectProperty<Color> defaultColorProperty() {
        return defaultColor;
    }

    /**
     * Gets the modifiable properties
     * @return Returns a list of such properties.
     */
    public ObservableList<Property<?>> getElementProperties() {
        return properties;
    }

    /**
     * Moves this {@link ElementNode}.
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
        properties.add(realX);
        properties.add(realY);
        properties.add(realWidth);
        properties.add(realHeight);
        properties.add(text);
        properties.add(color);
    }

    private NodeModificator addModificator(NodeModificator.Location modLoc) {
        NodeModificator element = new NodeModificator(this, modLoc);

        switch (modLoc) {
            case TOP_LEFT:
                element.setCursor(Cursor.NW_RESIZE);
                getChildren().add(element);
                StackPane.setAlignment(element, Pos.TOP_LEFT);
                element.layoutXProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
                element.layoutYProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
                break;

            case TOP_RIGHT:
                element.setCursor(Cursor.NE_RESIZE);
                getChildren().add(element);
                StackPane.setAlignment(element, Pos.TOP_RIGHT);
                element.layoutXProperty().bind(widthProperty().subtract(element.widthProperty()));
                element.layoutYProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
                break;

            case BOTTOM_LEFT:
                element.setCursor(Cursor.SW_RESIZE);
                getChildren().add(element);
                StackPane.setAlignment(element, Pos.BOTTOM_LEFT);
                element.layoutXProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
                element.layoutYProperty().bind(heightProperty().subtract(element.heightProperty()));
                break;

            case BOTTOM_RIGHT:
                element.setCursor(Cursor.SE_RESIZE);
                getChildren().add(element);
                StackPane.setAlignment(element, Pos.BOTTOM_RIGHT);
                element.layoutXProperty().bind(widthProperty().subtract(element.widthProperty()));
                element.layoutYProperty().bind(heightProperty().subtract(element.heightProperty()));
                break;
        }

        element.visibleProperty().bind(selected.or(hoverProperty()));

        return element;
    }

    protected JointNode addJoint(Joint joint) {
        JointNode element = new JointNode(joint, this);
        element.setRadius(3);
        element.visibleProperty().bind(
                Bindings.when(
                        diagramPane.jointProperty().isNotNull()
                ).then(
                        Bindings.createBooleanBinding(() -> {
                            Joint j = diagramPane.getJoint();

                            return j != null && j.canJoin(joint);
                        }, diagramPane.jointProperty())
                ).otherwise(
                        selected.or(hoverProperty()).and(
                                Bindings.createBooleanBinding(joint::isOutput)
                        )
                )
        );
        getChildren().add(element);
        diagramPane.registerJointNode(element);

        return element;
    }

    private TextField addText(StringProperty bind, String cssClass, Pos position, boolean extend) {

        TextField element = new AutoSizeTextField();
        getChildren().add(element);
        layout();
        element.setPrefHeight(15);
        element.applyCss();
        element.layout();

        switch (position) {
            case BOTTOM_CENTER:
                element.layoutXProperty().bind(widthProperty().divide(2).subtract(element.widthProperty().divide(2)));
                element.layoutYProperty().bind(heightProperty().subtract(element.heightProperty()));

                if (extend) {
                    heightExtend = heightExtend.add(element.prefHeightProperty().multiply(1.1));
                }
                break;

            case CENTER:
            default:
                element.layoutXProperty().bind(widthProperty().divide(2).subtract(element.widthProperty().divide(2)));
                element.layoutYProperty().bind(heightProperty().divide(2).subtract(element.heightProperty().divide(2)));
        }

        element.textProperty().bindBidirectional(bind);
        element.getStyleClass().add(cssClass);
        StackPane.setAlignment(element, position);
        element.minWidthProperty().bind(Bindings.createDoubleBinding(() -> 10.0));
        element.maxWidthProperty().bind(widthProperty().multiply(0.8));

        return element;
    }

    private void addBorder() {
        Rectangle border = new Rectangle();
        border.getStyleClass().add("nodeBorderRectangle");
        getChildren().add(border);
        border.widthProperty().bind(widthProperty());
        border.heightProperty().bind(heightProperty());
        border.setStrokeWidth(2);
        border.setMouseTransparent(true);
        border.setFill(Color.TRANSPARENT);
        border.visibleProperty().bind(selected);
    }

    private void addContent() {
        getChildren().add(content);
    }

    /**
     * Setup of bindings and listeners.
     */
    protected void setup() {
        /*
         * Basic layout
         */

        addContent();
        addBorder();

        addText(text, "nodeTextHolder", posLabel, true);

        addModificator(NodeModificator.Location.TOP_RIGHT);
        addModificator(NodeModificator.Location.TOP_LEFT);

        addModificator(NodeModificator.Location.BOTTOM_RIGHT);
        addModificator(NodeModificator.Location.BOTTOM_LEFT);

        /*
         * Basic settings
         */

        content.setMouseTransparent(true);

        layoutXProperty().bindBidirectional(realX);
        layoutYProperty().bindBidirectional(realY);


        content.widthProperty().bindBidirectional(realWidth);
        content.heightProperty().bindBidirectional(realHeight);

        prefWidthProperty().bind(realWidth.add(widthExtend));
        prefHeightProperty().bind(realHeight.add(heightExtend));

        setCursor(Cursor.MOVE);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return STYLABLES;
    }

    @Override
    public boolean isSelected() {
        return selected.get();
    }

    @Override
    public ReadOnlyBooleanProperty selectedProperty() {
        return selected;
    }
}
