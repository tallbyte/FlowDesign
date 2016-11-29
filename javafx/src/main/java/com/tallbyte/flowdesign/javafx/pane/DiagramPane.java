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

package com.tallbyte.flowdesign.javafx.pane;

import com.tallbyte.flowdesign.core.Diagram;
import com.tallbyte.flowdesign.core.Element;
import com.tallbyte.flowdesign.core.ElementsChangedListener;
import com.tallbyte.flowdesign.core.EnvironmentDiagram;
import com.tallbyte.flowdesign.javafx.diagram.DiagramNode;
import com.tallbyte.flowdesign.javafx.diagram.factory.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-05)<br/>
 */
public class DiagramPane extends StackPane {

    protected final Group                                      groupContent  = new Group();
    protected final Map<Class<?>, Map<Class<?>, DiagramImageFactory>> fullImageFactories = new HashMap<>();
    protected       Map<Class<?>, DiagramImageFactory>                imageFactories     = new HashMap<>();

    protected final Map<Class<?>, Map<String, ElementFactory>>        fullElementFactories = new HashMap<>();
    protected       Map<String, ElementFactory>                       elementFactories     = new HashMap<>();

    protected final ObjectProperty<Diagram> diagram = new SimpleObjectProperty<>(this, "diagram", null);

    protected double mouseX;
    protected double mouseY;

    protected ElementsChangedListener listener = null;

    public DiagramPane() {
        getChildren().add(groupContent);
        setAlignment(groupContent, Pos.TOP_LEFT);

        addImageFactory(EnvironmentDiagram.class, new SystemDiagramImageFactory());
        addImageFactory(EnvironmentDiagram.class, new ActorDiagramImageFactory());

        addElementFactory(EnvironmentDiagram.class, new ActorElementFactory());
        addElementFactory(EnvironmentDiagram.class, new SystemElementFactory());

        diagram.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeElementsChangedListener(listener);
            }

            // remove all existing entries
            groupContent.getChildren().clear();
            groupContent.getChildren().add(new Rectangle());

            // add new
            if (newValue != null) {
                imageFactories = fullImageFactories.get(newValue.getClass());
                imageFactories = imageFactories == null ? new HashMap<>() : imageFactories;

                elementFactories = fullElementFactories.get(newValue.getClass());
                elementFactories = elementFactories == null ? new HashMap<>() : elementFactories;

                listener = (element, added) -> {
                    if (added) {
                        DiagramImageFactory factory = imageFactories.get(element.getClass());

                        if (factory != null) {
                            groupContent.getChildren().add(new DiagramNode(element, factory.createDiagramImage()));
                        }
                    }
                };
                newValue.addElementsChangedListener(listener);
            }
        });

        parentProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                newValue.setOnMouseMoved(null);
                newValue.setOnMouseDragged(null);
                newValue.setOnDragOver(null);
                oldValue.setOnDragExited(null);
            }

            if (newValue != null) {
                newValue.setOnMouseMoved(event -> {
                    mouseX = event.getSceneX();
                    mouseY = event.getSceneY();
                });

                newValue.setOnMouseDragged(event -> {
                    mouseX = event.getSceneX();
                    mouseY = event.getSceneY();
                });

                newValue.setOnDragOver(event -> {
                    mouseX = event.getSceneX();
                    mouseY = event.getSceneY();
                });

                newValue.setOnDragExited(event -> {
                    Diagram diagram = this.diagram.get();

                    if (diagram != null) {
                        ElementFactory factory = elementFactories.get(event.getDragboard().getString());

                        if (factory != null) {
                            Bounds b = newValue.localToScene(newValue.getBoundsInLocal());

                            Element element = factory.createElement();
                            element.setX(mouseX-b.getMinX()-event.getDragboard().getDragViewOffsetX());
                            element.setY(mouseY-b.getMinY()-event.getDragboard().getDragViewOffsetY());
                            element.setWidth(75);
                            element.setHeight(75);

                            diagram.addElement(element);
                        }

                        event.consume();
                    }
                });
            }
        });
    }

    public void addImageFactory(Class<? extends Diagram> clazz, DiagramImageFactory factory) {
        Map<Class<?>, DiagramImageFactory> map = fullImageFactories.get(clazz);

        if (map == null) {
            map = new HashMap<>();
            fullImageFactories.put(clazz, map);
        }

        map.put(factory.getTargetClass(), factory);
    }

    public void addElementFactory(Class<? extends Diagram> clazz, ElementFactory factory) {
        Map<String, ElementFactory> map = fullElementFactories.get(clazz);

        if (map == null) {
            map = new HashMap<>();
            fullElementFactories.put(clazz, map);
        }

        map.put(factory.getName(), factory);
    }

    public Diagram getDiagram() {
        return diagram.get();
    }

    public void setDiagram(Diagram diagram) {
        this.diagram.set(diagram);
    }

    public ObjectProperty<Diagram> diagramProperty() {
        return diagram;
    }

}
