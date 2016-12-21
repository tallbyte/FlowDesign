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
import com.tallbyte.flowdesign.javafx.FlowDesignFxApplication;
import com.tallbyte.flowdesign.javafx.pane.DiagramsPane;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.*;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-05)<br/>
 */
public class DiagramPane extends ScrollPane {

    /*
     * ===============================================
     * Layout related
     * ===============================================
     */

    protected final Group                                                groupContent     = new Group();
    protected final Group                                                groupConnections = new Group();
    protected final Group                                                groupMarker      = new Group();

    /*
     * ===============================================
     * Dependencies and lookup maps
     * ===============================================
     */

    protected final FlowDesignFxApplication                              application;

    protected final DiagramManager                                       diagramManager;
    protected final DiagramsPane                                         diagramsPane;

    protected final Map<Joint, JointNode>                                jointNodes       = new HashMap<>();
    protected final Map<SelectableNode, List<EventHandler<MouseEvent>>>  mouseHandlers    = new HashMap<>();

    /*
     * ===============================================
     * Properties
     * ===============================================
     */

    /**
     * The current diagram
     */
    protected final ObjectProperty<Diagram<?>>     diagram  = new SimpleObjectProperty<>(this, "diagram", null);

    /**
     * A list of selected nodes
     */
    protected final ObservableList<SelectableNode> selected = FXCollections.observableArrayList();

    /**
     * The diagrams name bound to the internal representation. Will be null if no diagram was registered.
     */
    protected       StringProperty                 name;

    /**
     * The last actively selected node
     */
    protected final ObjectProperty<ElementNode>    node    = new SimpleObjectProperty<>(this, "node", null);

    /**
     * The joint that is currently selected by the user
     */
    protected final ObjectProperty<Joint>          joint   = new SimpleObjectProperty<>(this, "joint", null);

    /*
     * ===============================================
     * Variables required for drag / drop
     * ===============================================
     */

    protected double mouseX;
    protected double mouseY;

    /*
     * ===============================================
     * Variables required for movement of nodes
     * ===============================================
     */

    protected double offsetX;
    protected double offsetY;

    /*
     * ===============================================
     * Registered listeners
     * ===============================================
     */

    protected ElementsChangedListener listenerElements         = null;
    protected ConnectionsChangedListener listenerConnections   = null;
    protected EventHandler<? super MouseEvent> listenerRelease = null;


    /**
     * Creates a new {@link DiagramPane} with a default set of factories.
     * @param application the {@link FlowDesignFxApplication}
     * @param pane the {@link DiagramsPane} that surrounds this {@link DiagramPane}
     * @param diagramManager the {@link DiagramManager} used for e.g. element creation
     */
    public DiagramPane(FlowDesignFxApplication application, DiagramsPane pane, DiagramManager diagramManager) {
        this.application    = application;
        this.diagramsPane   = pane;
        this.diagramManager = diagramManager;
        Group group = new Group();
        group.getChildren().addAll(groupContent, groupConnections, groupMarker);

        setContent(group);
        setPannable(true);

        setup();
    }

    /**
     * Creates a new {@link DiagramPane} with a default set of factories.
     * @param application the {@link FlowDesignFxApplication}
     * @param pane the {@link DiagramsPane} that surrounds this {@link DiagramPane}
     * @param diagram the {@link Diagram} that will be set initially
     * @param diagramManager the {@link DiagramManager} used for e.g. element creation
     */
    public DiagramPane(FlowDesignFxApplication application, DiagramsPane pane, Diagram diagram, DiagramManager diagramManager) {
        this(application, pane, diagramManager);

        setPannable(true);

        this.diagram.setValue(diagram);
    }

    /**
     * Registers an mouse handler for a given node. This is necessary as the listeners have to be removed again
     * if the node is removed
     * @param node the {@link SelectableNode} that owns the handler
     * @param handler the handler itself
     * @return Returns the handler again.
     */
    private EventHandler<MouseEvent> addMouseHandler(SelectableNode node, EventHandler<MouseEvent> handler) {
        List<EventHandler<MouseEvent>> list = mouseHandlers.get(node);

        if (list == null) {
            list = new ArrayList<>();
            mouseHandlers.put(node, list);
        }

        list.add(handler);

        return handler;
    }

    /**
     * Removes an mouse handler of an node. This should only be called if the node is entirely removed.
     * Not calling this method will result in a memory leak.
     * @param node the {@link SelectableNode} that owns the handler
     * @param handler the handler itself
     */
    private void removeMouseHandler(SelectableNode node, EventHandler<MouseEvent> handler) {
        List<EventHandler<MouseEvent>> list = mouseHandlers.get(node);

        if (list != null) {
            list.remove(handler);

            if (list.size() == 0) {
                mouseHandlers.remove(node);
            }
        }
    }

    /**
     * Creates the property that displays if a certain {@link SelectableNode} is actually selected.
     * The property also manages pseudo-class creation / removal for the given node.
     * @param node the {@link SelectableNode} to check for
     * @return Returns the created property.
     */
    private BooleanProperty createSelectedProperty(SelectableNode node) {
        BooleanProperty selected = new SimpleBooleanProperty();
        selected.bind(Bindings.createBooleanBinding(() -> this.selected.contains(node), this.selected));
        selected.addListener((observable, oldValue, newValue) -> {
            if (node instanceof Node) {
                ((Node) node).pseudoClassStateChanged(PseudoClass.getPseudoClass("activeSelected"), newValue);
            }
        });

        return selected;
    }

    /**
     * Adds an {@link Element} to this {@link DiagramPane}.
     * This will use the registered <code>diagramManager</code>
     * to create matching {@link ElementNode}s.
     * @param element the {@link Element} to add
     */
    private void addElement(Element element) {
        ElementNode node = diagramManager.createNode(getDiagram(), element);
        if (node != null) {
            node.setDiagramPane(this, createSelectedProperty(node));
            node.addEventHandler(MouseEvent.MOUSE_PRESSED, addMouseHandler(node, event -> {
                this.selected.clear();
                this.selected.add(node);

                offsetX = event.getX();
                offsetY = event.getY();

                event.consume();
            }));

            node.addEventHandler(MouseEvent.MOUSE_DRAGGED, addMouseHandler(node,event -> {
                for (Node child : groupContent.getChildren()) {
                    if (child instanceof ElementNode && this.selected.contains(child)) {
                        Bounds bounds = node.getBoundsInParent();

                        System.out.println((getHvalue() * getViewportBounds().getWidth()));

                        System.out.println(getHvalue());
                        System.out.println(getViewportBounds().getWidth());

                        ((ElementNode) child).realX.set(event.getX() - offsetX + bounds.getMinX() + getHvalue()*getViewportBounds().getWidth());
                        ((ElementNode) child).realY.set(event.getY() - offsetY + bounds.getMinY() + getVvalue()*getViewportBounds().getHeight());

                        System.out.println((event.getX() - offsetX + bounds.getMinX() + getHvalue() * getViewportBounds().getWidth()));
                    }
                }
            }));

            groupContent.getChildren().add(node);
        }
    }

    /**
     * Adds a {@link Connection} to this {@link DiagramPane}.
     * Depending on the type of connection different nodes will be
     * created.
     * @param connection the {@link Connection} to register
     */
    private void addConnection(Connection connection) {
        ConnectionNode node;

        // TODO make using factories and map lookup
        if (connection instanceof FlowConnection) {
            node = new ArrowConnectionNode(application, connection);

        } else if (connection instanceof DependencyConnection) {
            node = new CircleConnectionNode(connection);

        } else {
            node = null;
        }

        if (node != null) {
            node.setDiagramPane(this, createSelectedProperty(node));
            node.addEventHandler(MouseEvent.MOUSE_PRESSED, addMouseHandler(node, event -> {
                this.selected.clear();
                this.selected.add(node);

                event.consume();
            }));

            groupConnections.getChildren().add(node);
        }
    }

    /**
     * Internal setup and handler creation.
     */
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

        addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            groupMarker.getChildren().stream().filter(node -> node instanceof ConnectionNode).forEach(node -> {
                ((ConnectionNode) node).setEndX(event.getX());
                ((ConnectionNode) node).setEndY(event.getY());
            });
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
            selected.clear();
            node.set(null);
            event.consume();
        });

        selected.addListener((ListChangeListener<SelectableNode>) c -> {
            while(c.next()) {
                c.getAddedSubList().stream()
                        .filter(node1 -> node1 instanceof ElementNode)
                        .forEach(node1 -> DiagramPane.this.node.set((ElementNode) node1));
            }
        });

        listenerRelease = event -> {
            groupMarker.getChildren().clear();
        };

        addEventFilter(MouseEvent.MOUSE_RELEASED, listenerRelease);
    }

    /**
     * Internally map the given {@link JointNode} to its {@link Joint} for later lookup.
     * @param jointNode the {@link JointNode} to register
     */
    void registerJointNode(JointNode jointNode) {
        jointNodes.put(jointNode.getJoint(), jointNode);
    }

    /**
     * Unregisters an {@link JointNode}.
     * @param jointNode the {@link JointNode} to unregister
     */
    void unregisterJointNode(JointNode jointNode) {
        jointNodes.remove(jointNode.getJoint(), jointNode);
    }

    /**
     * Gets the {@link JointNode} if a {@link Joint}.
     * @param joint the {@link Joint} to look up
     * @return Returns the {@link JointNode} or null if none was registered.
     */
    JointNode getJointNode(Joint joint) {
        return jointNodes.get(joint);
    }

    /**
     * Gets the containing {@link DiagramsPane}.
     * @return Returns the pane.
     */
    public DiagramsPane getDiagramsPane() {
        return diagramsPane;
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

        groupMarker.getChildren().add(node);
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

        groupMarker.getChildren().remove(node);
    }

    /**
     * Gets the name.
     * @return Returns the name.
     */
    public String getName() {
        return name.get();
    }

    /**
     * Sets the name. This might be restricted by data implementation.
     * @param name the new name
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Gets the name property.
     * @return Returns the property.
     */
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

    /**
     * Gets the diagram property.
     * @return Returns the property.
     */
    public ObjectProperty<Diagram<?>> diagramProperty() {
        return diagram;
    }

    /**
     * Gets the currently selected {@link Node}.
     * @return Returns the node or null if none is set.
     */
    public ElementNode getNode() {
        return node.get();
    }

    /**
     * Sets the currently selected {@link ElementNode}.
     * @param node the new node
     */
    public void setNode(ElementNode node) {
        this.node.set(node);
    }

    /**
     * Gets the node property.
     * @return Returns the property.
     */
    public ReadOnlyObjectProperty<ElementNode> nodeProperty() {
        return node;
    }

    /**
     * Gets the currently selected {@link Joint}.
     * @return Returns the {@link Joint} or null if none is set.
     */
    public Joint getJoint() {
        return joint.get();
    }

    /**
     * Sets the currently selected {@link Joint}.
     * @param joint the new {@link Joint}
     */
    void setJoint(Joint joint) {
        this.joint.set(joint);
    }

    /**
     * Gets the {@link Joint} property.
     * @return Returns the property.
     */
    ObjectProperty<Joint> jointProperty() {
        return joint;
    }
}
