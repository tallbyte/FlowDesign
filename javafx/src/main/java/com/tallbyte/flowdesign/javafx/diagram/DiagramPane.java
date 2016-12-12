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

import com.tallbyte.flowdesign.data.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
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
public class DiagramPane extends ScrollPane {

    protected final DiagramManager              diagramManager;
    protected final Group                       groupContent  = new Group();
    protected final Map<Joint, JointNode>       jointNodes    = new HashMap<>();

    protected final ObjectProperty<Diagram<?>>  diagram = new SimpleObjectProperty<>(this, "diagram", null);

    protected       StringProperty              name;
    protected final ObjectProperty<ElementNode> node    = new SimpleObjectProperty<>(this, "node", null);
    protected final ObjectProperty<Joint>       joint   = new SimpleObjectProperty<>(this, "joint", null);

    protected double mouseX;
    protected double mouseY;

    protected ElementsChangedListener listenerElements    = null;
    protected ConnectionsChangedListener listenerConnections = null;
    protected EventHandler<? super MouseEvent> listenerRelease     = null;


    /**
     * Creates a new {@link DiagramPane} with a default set of factories.
     * @param diagramManager the {@link DiagramManager} used for e.g. element creation
     */
    public DiagramPane(DiagramManager diagramManager) {
        this.diagramManager = diagramManager;

        setContent(groupContent);
        setPannable(true);

        setup();
    }

    public DiagramPane(Diagram diagram, DiagramManager diagramManager) {
        this(diagramManager);

        setPannable(true);

        this.diagram.setValue(diagram);
    }

    private void addElement(Element element) {
        setAllUnselected();
        ElementNode node = diagramManager.createNode(getDiagram(), element);
        if (node != null) {
            node.setDiagramPane(this);
            groupContent.getChildren().add(node);
        }
    }

    private void setup() {
        getStyleClass().add("diagramNode");

        diagram.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeElementsChangedListener(listenerElements);
                oldValue.removeConnectionsChangedListener(listenerConnections);
            }

            // remove all existing entries
            groupContent.getChildren().clear();
            groupContent.getChildren().add(new Rectangle());
            // add new
            if (newValue != null && diagramManager.isSupporting(newValue)) {
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

        setOnMouseMoved(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        setOnMouseDragged(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        setOnDragOver(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        setOnDragExited(event -> {
            Diagram diagram = this.diagram.get();

            if (diagram != null) {
                Bounds b = localToScene(getBoundsInLocal());

                diagramManager.createElement(getDiagram(), event.getDragboard().getString(),
                        mouseX-b.getMinX()-event.getDragboard().getDragViewOffsetX(),
                        mouseY-b.getMinY()-event.getDragboard().getDragViewOffsetY()
                );

                event.consume();
            }
        });

        setOnMousePressed(event -> {
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

        addEventFilter(MouseEvent.MOUSE_RELEASED, listenerRelease);
    }

    private void addConnection(Connection connection) {
        // TODO make using factories and map lookup
        if (connection instanceof FlowConnection) {
            groupContent.getChildren().add(new ArrowConnectionNode(connection, this));

        } else if (connection instanceof DependencyConnection) {
            groupContent.getChildren().add(new CircleConnectionNode(connection, this));
        }

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
     * Marks all {@link ElementNode}s as unselected.
     */
    void setAllUnselected() {
        groupContent.getChildrenUnmodifiable()
                .stream()
                .filter(node
                        -> node instanceof ElementNode
                ).forEach(node
                        -> ((ElementNode) node).selectedProperty().set(false)
        );
    }

    /**
     * Notifies all selected @{@link ElementNode} to move.
     * @param dx the x delta
     * @param dy the y delta
     */
    void moveAllSelected(double dx, double dy) {
        groupContent.getChildrenUnmodifiable()
                .stream()
                .filter(node
                        -> node instanceof ElementNode && ((ElementNode) node).selectedProperty().get()
                ).forEach(node
                        -> ((ElementNode) node).move(dx, dy)
        );
    }

    /**
     * Checks if any {@link ElementNode} is selected.
     * @return True if one is selected, else false.
     */
    boolean hasSelectedNodes() {
        for (Node node : groupContent.getChildrenUnmodifiable()) {
            if (node instanceof ElementNode && ((ElementNode) node).selectedProperty().get()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds an (temporary) {@link Node} to the drawing board.
     * This will refuse {@link ElementNode}s.
     * @param node the {@link Node} to add
     */
    void addDisplayNode(Node node) {
        if (node instanceof ElementNode) {
            return;
        }

        groupContent.getChildren().add(node);
    }

    /**
     * Removes an (temporary) {@link Node} from the drawing board.
     * This will refuse {@link ElementNode}s.
     * @param node the {@link Node} to remove
     */
    void removeDisplayNode(Node node) {
        if (node instanceof ElementNode) {
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

    public ElementNode getNode() {
        return node.get();
    }

    public void setNode(ElementNode node) {
        this.node.set(node);
    }

    public ObjectProperty<ElementNode> nodeProperty() {
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
