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
import com.tallbyte.flowdesign.core.EnvironmentDiagram;
import com.tallbyte.flowdesign.javafx.diagram.DiagramNode;
import com.tallbyte.flowdesign.javafx.diagram.factory.CircleDiagramImageFactory;
import com.tallbyte.flowdesign.javafx.diagram.factory.DiagramImageFactory;
import com.tallbyte.flowdesign.javafx.diagram.factory.StickmanDiagramImageFactory;
import com.tallbyte.flowdesign.javafx.diagram.image.CircleDiagramImage;
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

    protected final Group                                           groupContent  = new Group();
    protected final Map<Class<?>, Map<String, DiagramImageFactory>> fullFactories = new HashMap<>();
    protected       Map<String, DiagramImageFactory>                factories     = new HashMap<>();

    protected final ObjectProperty<Diagram> diagram = new SimpleObjectProperty<>(this, "diagram", null);

    protected double mouseX;
    protected double mouseY;

    public DiagramPane() {
        getChildren().add(groupContent);
        setAlignment(groupContent, Pos.TOP_LEFT);

        addFactory(EnvironmentDiagram.class, new CircleDiagramImageFactory());
        addFactory(EnvironmentDiagram.class, new StickmanDiagramImageFactory());

        factories = fullFactories.get(EnvironmentDiagram.class);

        diagram.addListener((observable, oldValue, newValue) -> {
            // remove all existing entries
            //groupContent.getChildren().clear();

            // add new
            if (newValue != null) {
                factories = fullFactories.get(newValue.getClass());

                factories = factories == null ? new HashMap<>() : factories;
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
                    DiagramImageFactory factory = factories.get(event.getDragboard().getString());

                    if (factory != null) {
                        Bounds b = newValue.localToScene(newValue.getBoundsInLocal());

                        DiagramNode node = new DiagramNode(factory.createDiagramImage());
                        node.setLayoutX(mouseX-b.getMinX()-event.getDragboard().getDragViewOffsetX());
                        node.setLayoutY(mouseY-b.getMinY()-event.getDragboard().getDragViewOffsetY());

                        System.out.println("x="+(mouseX-b.getMinX())+" y="+(mouseY-b.getMinY())+" mx="+event.getDragboard().getDragViewOffsetX()+" my="+event.getDragboard().getDragViewOffsetY()+" b="+ b.getMinX());

                        groupContent.getChildren().add(node);
                    }

                    event.consume();
                });
            }
        });


        groupContent.getChildren().add(new Rectangle()); // upper left border
        groupContent.getChildren().add(new DiagramNode(new CircleDiagramImage()));
    }

    public void addFactory(Class<? extends Diagram> clazz, DiagramImageFactory factory) {
        Map<String, DiagramImageFactory> map = fullFactories.get(clazz);

        if (map == null) {
            map = new HashMap<>();
            fullFactories.put(clazz, map);
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
