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
import com.tallbyte.flowdesign.data.JointGroup;
import com.tallbyte.flowdesign.data.JointsChangedListener;
import com.tallbyte.flowdesign.javafx.control.AutoSizeTextField;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import com.tallbyte.flowdesign.javafx.property.ColorProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.beans.property.adapter.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-28)<br/>
 */
public class ElementNode extends Group implements SelectableNode {

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
    protected Pane                        wrap        = new Pane();
    protected Pos                         posLabel;
    protected DiagramImage                content;
    protected Element                     element;
    protected List<JointGroupHandler> jointGroupHandlers = new ArrayList<>();

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

    protected DoubleProperty    realX;
    protected DoubleProperty    realY;
    protected DoubleProperty    realWidth;
    protected DoubleProperty    realHeight;
    protected StringProperty    text;
    protected StringProperty    colorString;
    protected ColorProperty     color;

    /*
     * Working variables
     */

    private   boolean           release  = true;


    public ElementNode(Element element, DiagramImage content, Pos posLabel) {
        this.content     = content;
        this.element     = element;
        this.posLabel    = posLabel;

        getStyleClass().add("elementNode");
        getChildren().add(wrap);

        try {
            realX      = JavaBeanDoublePropertyBuilder.create().bean(element).name("x").build();
            realY      = JavaBeanDoublePropertyBuilder.create().bean(element).name("y").build();
            realWidth  = JavaBeanDoublePropertyBuilder.create().bean(element).name("width").build();
            realHeight = JavaBeanDoublePropertyBuilder.create().bean(element).name("height").build();
            text       = JavaBeanStringPropertyBuilder.create().bean(element).name("text").build();
            colorString= JavaBeanStringPropertyBuilder.create().bean(element).name("color").build();
            color      = new ColorProperty(this, "color", null);

            StringConverter<Color> converter = new StringConverter<Color>() {
                @Override
                public String toString(Color object) {
                    if (object == null) {
                        return null;
                    }
                    return String.format("#%02X%02X%02X%02X",
                            (int) (object.getRed()    * 255),
                            (int) (object.getGreen()  * 255),
                            (int) (object.getBlue()   * 255),
                            (int) (object.getOpacity()* 255)
                    );
                }

                @Override
                public Color fromString(String string) {
                    if (string == null || string.isEmpty()) {
                        return null;
                    }

                    try {
                        return Color.web(string);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                }
            };

            String old = colorString.getValue();
            colorString.bindBidirectional(color, converter);
            colorString.setValue(old);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not create properties. This should never happen?!", e);
        }

        content.colorProperty().bind(Bindings.when(color.isNotNull()).then(color).otherwise(defaultColor));

        addDefaultProperties();

        content.setElement(this);
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

    @Override
    public ObservableList<Property<?>> getNodeProperties() {
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
    protected void addDefaultProperties() {
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
                wrap.getChildren().add(element);
                StackPane.setAlignment(element, Pos.TOP_LEFT);
                element.layoutXProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
                element.layoutYProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
                break;

            case TOP_RIGHT:
                element.setCursor(Cursor.NE_RESIZE);
                wrap.getChildren().add(element);
                StackPane.setAlignment(element, Pos.TOP_RIGHT);
                element.layoutXProperty().bind(wrap.widthProperty().subtract(element.widthProperty()));
                element.layoutYProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
                break;

            case BOTTOM_LEFT:
                element.setCursor(Cursor.SW_RESIZE);
                wrap.getChildren().add(element);
                StackPane.setAlignment(element, Pos.BOTTOM_LEFT);
                element.layoutXProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
                element.layoutYProperty().bind(wrap.heightProperty().subtract(element.heightProperty()));
                break;

            case BOTTOM_RIGHT:
                element.setCursor(Cursor.SE_RESIZE);
                wrap.getChildren().add(element);
                StackPane.setAlignment(element, Pos.BOTTOM_RIGHT);
                element.layoutXProperty().bind(wrap.widthProperty().subtract(element.widthProperty()));
                element.layoutYProperty().bind(wrap.heightProperty().subtract(element.heightProperty()));
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
                                Bindings.createBooleanBinding(joint::canOutput, diagramPane.jointProperty())
                        )
                )
        );
        wrap.getChildren().add(element);
        diagramPane.registerJointNode(element);

        return element;
    }

    protected void addJointsAcrossRectangle(JointGroupHandler group, boolean horizontal, double sec) {
        group.setLayoutHandler(g -> {
            double main  = g.getOffset();
            double range = g.getRange();

            if (group.getNodes().size() >= 1) {
                range /= (group.getNodes().size());
            }

            for (JointNode node : g.getNodes()) {
                final double cMain = main;

                setNodeForRectangle(node, horizontal, cMain, sec);

                main += range;
            }
        });
    }

    protected void addJointsAcrossRectangleCentered(JointGroupHandler group, boolean horizontal, double sec) {
        group.setLayoutHandler(g -> {
            double main  = g.getOffset();
            double range = g.getRange();

            if (group.getNodes().size() > 1) {
                range /= (group.getNodes().size() -1);
                main -= g.getRange() / 2;
            }

            for (JointNode node : g.getNodes()) {
                final double cMain = main;

                setNodeForRectangle(node, horizontal, cMain, sec);

                main += range;
            }
        });
    }

    protected void setNodeForRectangle(JointNode node, boolean horizontal, double main, double sec) {
        node.centerXProperty().bind(Bindings.createDoubleBinding(() -> {
                    double x;

                    if (horizontal) {
                        x = (main) * realWidth.get();
                    } else {
                        x = sec * realWidth.get();
                    }

                    return x;
                }, realWidth)
        );
        node.centerYProperty().bind(Bindings.createDoubleBinding(() -> {
                    double y;

                    if (horizontal) {
                        y = sec * realHeight.get();
                    } else {
                        y = (main) * realHeight.get();
                    }

                    return y;
                }, realWidth)
        );
    }

    protected void addJointsAcrossCircle(JointGroupHandler group) {
        group.setLayoutHandler(g -> {
            double angle = g.getOffset()*360;
            double range = g.getRange()*360;

            if (group.getNodes().size() >= 1) {
                range /= (group.getNodes().size());
            }

            for (JointNode node : g.getNodes()) {
                final double cAngle = angle;

                node.centerXProperty().bind(Bindings.createDoubleBinding(()
                                -> realWidth.get()/2+Math.cos(Math.toRadians(cAngle))*(realWidth.get()/2),
                        realWidth)
                );
                node.centerYProperty().bind(Bindings.createDoubleBinding(()
                                -> realHeight.get()/2+Math.sin(Math.toRadians(cAngle))*(realHeight.get()/2),
                        realHeight)
                );

                angle += range;
            }
        });
    }

    protected void addJointsAcrossCircleCentered(JointGroupHandler group) {
        group.setLayoutHandler(g -> {
            double main  = g.getOffset();
            double range = g.getRange();

            if (group.getNodes().size() > 1) {
                range /= (group.getNodes().size() -1);
                main -= g.getRange() / 2;
            }

            for (JointNode node : g.getNodes()) {
                final double cAngle = main*360;

                node.centerXProperty().bind(Bindings.createDoubleBinding(()
                        -> realWidth.get()/2+Math.cos(Math.toRadians(cAngle))*(realWidth.get()/2),
                        realWidth)
                );
                node.centerYProperty().bind(Bindings.createDoubleBinding(()
                                -> realHeight.get()/2+Math.sin(Math.toRadians(cAngle))*(realHeight.get()/2),
                        realHeight)
                );

                main += range;
            }
        });
    }

    protected TextField setupText(TextField element, StringProperty bind, String cssClass, Pos position) {
        element.setPrefHeight(15);
        element.applyCss();
        element.layout();
        element.textProperty().bindBidirectional(bind);
        element.getStyleClass().add(cssClass);
        StackPane.setAlignment(element, position);
        element.minWidthProperty().bind(Bindings.createDoubleBinding(() -> 10.0));

        return element;
    }

    protected TextField addText(StringProperty bind, String cssClass, Pos position, boolean extend) {
        return addText(new AutoSizeTextField(), bind, cssClass, position, extend);
    }

    protected TextField addText(TextField element, StringProperty bind, String cssClass, Pos position, boolean extend) {
        setupText(element, bind, cssClass, position);

        wrap.getChildren().add(element);

        switch (position) {
            case BOTTOM_CENTER:
                element.layoutXProperty().bind(wrap.widthProperty().divide(2).subtract(element.widthProperty().divide(2)));
                element.layoutYProperty().bind(wrap.heightProperty().subtract(element.heightProperty()));

                if (extend) {
                    heightExtend = heightExtend.add(element.prefHeightProperty().multiply(1.1));
                }
                break;

            case CENTER:
                element.layoutXProperty().bind(wrap.widthProperty().divide(2).subtract(element.widthProperty().divide(2)));
                element.layoutYProperty().bind(wrap.heightProperty().divide(2).subtract(element.heightProperty().divide(2)));
                break;

            default:

        }

        return element;
    }

    private void addBorder() {
        Rectangle border = new Rectangle();
        border.getStyleClass().add("nodeBorderRectangle");
        wrap.getChildren().add(border);
        border.widthProperty().bind(wrap.widthProperty());
        border.heightProperty().bind(wrap.heightProperty());
        border.setStrokeWidth(2);
        border.setMouseTransparent(true);
        border.setFill(Color.TRANSPARENT);
        border.visibleProperty().bind(selected);
    }

    private void addContent() {
        wrap.getChildren().add(content);
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

        wrap.prefWidthProperty().bind(realWidth.add(widthExtend));
        wrap.prefHeightProperty().bind(realHeight.add(heightExtend));

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

    protected void remove() {
        jointGroupHandlers.forEach(JointGroupHandler::remove);
    }

    protected interface JointGroupLayoutHandler {

        void layout(JointGroupHandler group);

    }

    protected class JointGroupHandler {

        protected final JointGroup<?>           group;

        protected final double                  offset;
        protected final double                  range;

        protected       JointsChangedListener   listener;
        protected       JointGroupLayoutHandler layoutHandler;
        protected       List<JointNode>         nodes = new ArrayList<>();

        public JointGroupHandler(JointGroup<?> group, double offset, double range) {
            this.group  = group;
            this.offset = offset;
            this.range  = range;

            nodes.addAll(group.getJoints().stream().map(ElementNode.this::addJoint).collect(Collectors.toList()));

            group.addJointsChangedListener(listener = (joint, added) -> {
                if (added) {
                    nodes.add(addJoint(joint));
                } else {
                    // TODO
                }

                if (layoutHandler != null) {
                    layoutHandler.layout(this);
                }
            });
        }

        public JointGroup<?> getGroup() {
            return group;
        }

        public double getOffset() {
            return offset;
        }

        public double getRange() {
            return range;
        }

        private JointsChangedListener getListener() {
            return listener;
        }

        private void setLayoutHandler(JointGroupLayoutHandler layoutHandler) {
            this.layoutHandler = layoutHandler;

            layoutHandler.layout(this);
        }

        private JointGroupLayoutHandler getLayoutHandler() {
            return layoutHandler;
        }

        private List<JointNode> getNodes() {
            return nodes;
        }

        private void remove() {
            group.removeJointsChangedListener(listener);
        }
    }
}
