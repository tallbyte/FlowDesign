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

import com.tallbyte.flowdesign.core.*;
import com.tallbyte.flowdesign.core.Connection;
import com.tallbyte.flowdesign.javafx.DiagramManager;
import com.tallbyte.flowdesign.javafx.diagram.factory.*;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-05)<br/>
 */
public class DiagramPane extends StackPane {

    protected final DiagramManager              diagramManager= new DiagramManager();
    protected final Group                       groupContent  = new Group();
    protected final Map<Joint, JointNode>       jointNodes    = new HashMap<>();

    protected final ObjectProperty<Diagram<?>>  diagram = new SimpleObjectProperty<>(this, "diagram", null);

    protected       StringProperty              name;
    protected final ObjectProperty<DiagramNode> node    = new SimpleObjectProperty<>(this, "node", null);
    protected final ObjectProperty<Joint>       joint   = new SimpleObjectProperty<>(this, "joint", null);

    protected double mouseX;
    protected double mouseY;

    protected ElementsChangedListener          listenerElements    = null;
    protected ConnectionsChangedListener       listenerConnections = null;
    protected EventHandler<? super MouseEvent> listenerRelease     = null;


    /**
     * Creates a new {@link DiagramPane} with a default set of factories.
     */
    public DiagramPane() {
        getChildren().add(groupContent);
        setAlignment(groupContent, Pos.TOP_LEFT);

        diagramManager.diagramProperty().bindBidirectional(this.diagramProperty());

        diagram.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeElementsChangedListener(listenerElements);
                oldValue.removeConnectionsChangedListener(listenerConnections);
            }

            // remove all existing entries
            groupContent.getChildren().clear();
            groupContent.getChildren().add(new Rectangle());

            // add new
            if (newValue != null) {
                try {
                    name = JavaBeanStringPropertyBuilder.create().bean(newValue).name("name").build();
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Could not create properties. This should never happen?!");
                }

                listenerElements = (element, added) -> {
                    if (added) {
                        addElement(element);
                    }
                };
                listenerConnections = (connection, added) -> {
                    if (added) {
                        addConnection(connection);
                    }
                };

                for (Element element : newValue.getElements()) {
                    addElement(element);
                }

                for (Connection connection : newValue.getConnections()) {
                    addConnection(connection);
                }

                newValue.addElementsChangedListener(listenerElements);
                newValue.addConnectionsChangedListener(listenerConnections);
            }
        });

        parentProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.setOnMouseMoved(null);
                oldValue.setOnMouseDragged(null);
                oldValue.setOnDragOver(null);
                oldValue.setOnDragExited(null);
                oldValue.setOnMousePressed(null);
                oldValue.removeEventFilter(MouseEvent.MOUSE_RELEASED, listenerRelease);
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
                        Bounds b = newValue.localToScene(newValue.getBoundsInLocal());

                        diagramManager.createElement(event.getDragboard().getString(),
                                mouseX-b.getMinX()-event.getDragboard().getDragViewOffsetX(),
                                mouseY-b.getMinY()-event.getDragboard().getDragViewOffsetY()
                        );

                        event.consume();
                    }
                });

                newValue.setOnMousePressed(event -> {
                    setAllUnselected();
                    event.consume();
                });

                listenerRelease = event -> {
                    Iterator<Node> iterator = groupContent.getChildren().iterator();
                    while(iterator.hasNext()) {
                        Node node = iterator.next();

                        if (node instanceof MarkerNode) {
                            iterator.remove();
                        }
                    }
                };

                newValue.addEventFilter(MouseEvent.MOUSE_RELEASED, listenerRelease);
            }
        });
    }

    public DiagramPane(Diagram diagram) {
        this();

        this.diagram.setValue(diagram);
    }

    private void addElement(Element element) {
        DiagramNode node = diagramManager.createNode(this, element);
        if (node != null) {
            groupContent.getChildren().add(node);
        }
    }

    private void addConnection(Connection connection) {
        groupContent.getChildren().add(new ConnectionNode(connection, this));
    }

    void registerJointNode(JointNode jointNode) {
        jointNodes.put(jointNode.getJoint(), jointNode);
    }

    void unregisterJointNode(JointNode jointNode) {
        jointNodes.remove(jointNode.getJoint(), jointNode);
    }

    JointNode getJointNode(Joint joint) {
        return jointNodes.get(joint);
    }

    /**
     * Marks all {@link DiagramNode}s as unselected.
     */
    void setAllUnselected() {
        groupContent.getChildrenUnmodifiable()
                .stream()
                .filter(node
                        -> node instanceof DiagramNode
                ).forEach(node
                        -> ((DiagramNode) node).selectedProperty().set(false)
        );
    }

    /**
     * Notifies all selected @{@link DiagramNode} to move.
     * @param dx the x delta
     * @param dy the y delta
     */
    void moveAllSelected(double dx, double dy) {
        groupContent.getChildrenUnmodifiable()
                .stream()
                .filter(node
                        -> node instanceof DiagramNode && ((DiagramNode) node).selectedProperty().get()
                ).forEach(node
                        -> ((DiagramNode) node).move(dx, dy)
        );
    }

    /**
     * Checks if any {@link DiagramNode} is selected.
     * @return True if one is selected, else false.
     */
    boolean hasSelectedNodes() {
        for (Node node : groupContent.getChildrenUnmodifiable()) {
            if (node instanceof DiagramNode && ((DiagramNode) node).selectedProperty().get()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds an (temporary) {@link Node} to the drawing board.
     * This will refuse {@link DiagramNode}s.
     * @param node the {@link Node} to add
     */
    void addDisplayNode(Node node) {
        if (node instanceof DiagramNode) {
            return;
        }

        groupContent.getChildren().add(node);
    }

    /**
     * Removes an (temporary) {@link Node} from the drawing board.
     * This will refuse {@link DiagramNode}s.
     * @param node the {@link Node} to remove
     */
    void removeDisplayNode(Node node) {
        if (node instanceof DiagramNode) {
            return;
        }

        groupContent.getChildren().remove(node);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    /**
     * Gets the current {@link Diagram}.
     * @return Returns the {@link Diagram} or null if none is set.
     */
    public Diagram<?> getDiagram() {
        return diagram.get();
    }

    /**
     * Sets the current {@link Diagram}.
     * @param diagram the new {@link Diagram}
     */
    public void setDiagram(Diagram<?> diagram) {
        this.diagram.set(diagram);
    }

    public ObjectProperty<Diagram<?>> diagramProperty() {
        return diagram;
    }

    public DiagramNode getNode() {
        return node.get();
    }

    public void setNode(DiagramNode node) {
        this.node.set(node);
    }

    public ObjectProperty<DiagramNode> nodeProperty() {
        return node;
    }

    public Joint getJoint() {
        return joint.get();
    }

    void setJoint(Joint joint) {
        this.joint.set(joint);
    }

    ObjectProperty<Joint> jointProperty() {
        return joint;
    }
}
