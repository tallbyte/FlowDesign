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
import com.tallbyte.flowdesign.javafx.ShortcutGroup;
import com.tallbyte.flowdesign.javafx.pane.DiagramsPane;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.tallbyte.flowdesign.javafx.Shortcuts.*;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-05)<br/>
 */
public class DiagramPane extends ScrollPane {

    public static final double ZOOM_CHANGE_FACTOR = 1.1;

    /*
     * ===============================================
     * Layout related
     * ===============================================
     */

    /**
     * This is the group that will contain all {@link ElementNode}s.
     */
    protected final Group                                                groupContent     = new Group();

    /**
     * This is the group that will contain all {@link ConnectionNode}s.
     */
    protected final Group                                                groupConnections = new Group();

    /**
     * This is the group that will contain markers like the interactive drag-line for connection creation.
     */
    protected final Group                                                groupMarker      = new Group();

    /*
     * ===============================================
     * Dependencies
     * ===============================================
     */

    protected final FlowDesignFxApplication                              application;

    protected final DiagramManager                                       diagramManager;
    protected final DiagramsPane                                         diagramsPane;

    /*
     * ===============================================
     * Lookup maps
     * ===============================================
     */

    protected final Map<Joint, JointNode>                                                             jointNodes       = new HashMap<>();
    protected final Map<DiagramContent, SelectableNode>                                               nodes            = new HashMap<>();
    protected final Map<SelectableNode, List<Pair<EventType<MouseEvent>, EventHandler<MouseEvent>>>>  mouseHandlers    = new HashMap<>();

    /*
     * ===============================================
     * Properties
     * ===============================================
     */

    /**
     * The current diagram.
     */
    protected final ObjectProperty<Diagram<?>>     diagram  = new SimpleObjectProperty<>(this, "diagram", null);

    /**
     * A list of selected nodes.
     */
    protected final ObservableList<SelectableNode> selected = FXCollections.observableArrayList();

    /**
     * The diagrams name bound to the internal representation. Will be null if no diagram was registered.
     */
    protected       StringProperty                 name;
    protected       ObservableList<Property<?>>    properties;

    /**
     * The last actively selected node.
     */
    protected final ObjectProperty<SelectableNode> node    = new SimpleObjectProperty<>(this, "node", null);

    /**
     * The joint that is currently selected by the user.
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


    /*
     * ===============================================
     * Zoom and movement variables
     * ===============================================
     */
    protected BooleanProperty scaleEnabled = new SimpleBooleanProperty(false);
    protected DoubleProperty  scale        = new SimpleDoubleProperty(1);
    protected DoubleProperty  translateX   = new SimpleDoubleProperty(0);
    protected DoubleProperty  translateY   = new SimpleDoubleProperty(0);



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

        // TODO dirty, cleanup >> start
        Pane  outer = new Pane();
        Pane  inner = new Pane();

        inner.setMaxSize(0, 0);
        inner.setPrefSize(0, 0);

        outer.setMaxSize    (Double.MAX_VALUE, Double.MAX_VALUE);
        outer.setPrefSize   (Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);

        inner.getChildren().addAll(groupConnections, groupContent, groupMarker);
        outer.getChildren().add(inner);



        DoubleProperty mouseX = new SimpleDoubleProperty(getWidth()  / 2.);
        DoubleProperty mouseY = new SimpleDoubleProperty(getHeight() / 2.);

        Consumer<Double> setInnerTranslateX = newX -> {
            inner.setTranslateX(
                    getWidth() / 2.
                - inner.getWidth() / 2. // zero
                + newX
            );
        };

        Consumer<Double> setInnerTranslateY = newY -> {
            inner.setTranslateY(
                    getHeight() / 2.
                - inner.getHeight() / 2. // zero
                + newY
            );
        };

        translateX.addListener((observable, oldValue, newValue) -> setInnerTranslateX.accept(newValue.doubleValue()));
        translateY.addListener((observable, oldValue, newValue) -> setInnerTranslateY.accept(newValue.doubleValue()));

        inner.scaleXProperty().bind(scale);
        inner.scaleYProperty().bind(scale);


        DoubleProperty x = new SimpleDoubleProperty();
        DoubleProperty y = new SimpleDoubleProperty();

        EventHandler<MouseEvent> eventHandlerPressed = event -> {
            x.set(event.getSceneX());
            y.set(event.getSceneY());
        };

        EventHandler<MouseEvent> eventHandlerDragged = event -> {
            if (event.isSecondaryButtonDown()) {
                double dx = x.get() - event.getSceneX();
                double dy = y.get() - event.getSceneY();

                x.set(event.getSceneX());
                y.set(event.getSceneY());

                translateX.set(translateX.get() - dx);
                translateY.set(translateY.get() - dy);
            }
        };

        outer.addEventHandler(MouseEvent.MOUSE_PRESSED, eventHandlerPressed);
        outer.addEventHandler(MouseEvent.MOUSE_DRAGGED, eventHandlerDragged);



        addEventHandler(ScrollEvent.SCROLL, event -> {
            if (scaleEnabled.getValue()) {
                double scaleChange;

                if (event.getDeltaY() > 0) {
                    scaleChange = ZOOM_CHANGE_FACTOR;
                } else {
                    scaleChange = 1. / ZOOM_CHANGE_FACTOR;
                }

                System.out.println("mouseX="+mouseX.get()+", to-center-x="+(mouseX.get() - (getWidth()  / 2.)));

                double tx = translateX.get();
                double ty = translateY.get();

                double mcx = mouseX.get() - getWidth()  / 2.;
                double mcy = mouseY.get() - getHeight() / 2.;

                double dmx = (mcx * scaleChange) - mcx;
                double dmy = (mcy * scaleChange) - mcy;

                translateX  .set(tx * scaleChange - dmx);
                translateY  .set(ty * scaleChange - dmy);




                scale       .set(scale      .get() * scaleChange);
            }
        });

        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SHIFT) {
                scaleEnabled.set(true);
            }

            if (event.isShiftDown() && event.getCode() == KeyCode.SPACE) {
                translateX.set(0);
                translateY.set(0);
                scale.set(1);
            }
        });

        addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.SHIFT) {
                scaleEnabled.set(false);
            }
        });

        outer.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            mouseX.set(event.getX());
            mouseY.set(event.getY());
        });

        translateX.set(0);
        translateY.set(0);
        scale.set(1);

        setInnerTranslateX.accept(0.);
        setInnerTranslateY.accept(0.);

        widthProperty().addListener((observable, oldValue, newValue) -> {
            translateX.set(translateX.get() + (newValue.doubleValue() - oldValue.doubleValue()) / 4.);
        });

        heightProperty().addListener((observable, oldValue, newValue) -> {
            translateY.set(translateY.get() + (newValue.doubleValue() - oldValue.doubleValue()) / 4.);
        });

        parentProperty().addListener((observable, oldValue, newValue) -> {
            setInnerTranslateX.accept(translateX.get());
            setInnerTranslateY.accept(translateY.get());
        });

        outer.prefWidthProperty() .bind(widthProperty());
        outer.prefHeightProperty().bind(heightProperty());
        outer.maxWidthProperty ().bind(widthProperty());
        outer.maxHeightProperty().bind(heightProperty());
        // TODO dirty, cleanup >> end


        setContent(outer);
        setPannable(true);


        setup();
    }

    /**
     * @param paneSpaceX X coordinate on the {@link DiagramPane}
     * @return The x coordinate on the {@link Diagram}
     */
    public double toDiagramSpaceX(double paneSpaceX) {
        return (paneSpaceX - translateX.get() - getWidth() / 2.) / scale.get();
    }

    /**
     * @param paneSpaceY Y coordinate on the {@link DiagramPane}
     * @return The y coordinate on the {@link Diagram}
     */
    public double toDiagramSpaceY(double paneSpaceY) {
        return (paneSpaceY - translateY.get() - getHeight() / 2.) / scale.get();
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
    private EventHandler<MouseEvent> addMouseHandler(SelectableNode node, EventType<MouseEvent> type, EventHandler<MouseEvent> handler) {
        List<Pair<EventType<MouseEvent>, EventHandler<MouseEvent>>> list = mouseHandlers.get(node);

        if (list == null) {
            list = new ArrayList<>();
            mouseHandlers.put(node, list);
        }

        list.add(new Pair<>(type, handler));

        return handler;
    }

    /**
     * Removes all mouse handler of an node. This should only be called if the node is entirely removed.
     * Not calling this method will result in a memory leak.
     * @param node the {@link SelectableNode} that owns the handler
     */
    private void removeMouseHandlers(SelectableNode node) {
        List<Pair<EventType<MouseEvent>, EventHandler<MouseEvent>>> list = mouseHandlers.get(node);

        if (list != null && node != null) {
            for (Pair<EventType<MouseEvent>, EventHandler<MouseEvent>> pair : list) {
                node.removeEventHandler(pair.getKey(), pair.getValue());
                node.removeEventFilter(pair.getKey(), pair.getValue());
            }
        }

        mouseHandlers.remove(node);
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
            if (node != null) {
                node.pseudoClassStateChanged(PseudoClass.getPseudoClass("activeSelected"), newValue);
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
            nodes.put(element, node);

            node.setDiagramPane(this, createSelectedProperty(node));
            node.addEventFilter(MouseEvent.MOUSE_PRESSED, addMouseHandler(node, MouseEvent.MOUSE_PRESSED, event -> {
                requestSelection(node);

                offsetX = event.getX();
                offsetY = event.getY();

                node.requestFocus();
            }));
            node.addEventHandler(MouseEvent.MOUSE_PRESSED, addMouseHandler(node, MouseEvent.MOUSE_PRESSED, event -> {
                requestSelection(node);

                offsetX = event.getX();
                offsetY = event.getY();

                node.requestFocus();

                event.consume();
            }));

            node.addEventHandler(MouseEvent.MOUSE_DRAGGED, addMouseHandler(node, MouseEvent.MOUSE_DRAGGED, event -> {
                for (Node child : groupContent.getChildren()) {
                    if (child instanceof ElementNode && this.selected.contains(child)) {
                        Bounds bounds = node.getBoundsInParent();

                        //System.out.println((getHvalue() * getViewportBounds().getWidth()));

                        //System.out.println(getHvalue());
                        //System.out.println(getViewportBounds().getWidth());

                        ((ElementNode) child).realX.set(event.getX() - offsetX + bounds.getMinX());
                        ((ElementNode) child).realY.set(event.getY() - offsetY + bounds.getMinY());

                        //System.out.println((event.getX() - offsetX + bounds.getMinX() + getHvalue() * getViewportBounds().getWidth()));
                    }
                }
            }));

            groupContent.getChildren().add(node);
        }
    }

    /**
     * Removes an {@link Element} from this {@link DiagramPane}.
     * @param element the {@link Element} to remove
     */
    private void removeElement(Element element) {
        nodes.remove(element);

        for (Node node : groupContent.getChildrenUnmodifiable()) {
            if (node instanceof ElementNode && ((ElementNode) node).getElement().equals(element)) {
                groupContent.getChildren().remove(node);
                removeMouseHandlers((ElementNode) node);
                ((ElementNode) node).remove();
                break;
            }
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
            node = new FlowConnectionNode((FlowConnection) connection);

        } else if (connection instanceof DependencyConnection) {
            node = new DependencyConnectionNode((DependencyConnection) connection);

        } else {
            node = null;
        }

        if (node != null) {
            nodes.put(connection, node);

            node.setDiagramPane(this, createSelectedProperty(node));
            node.addEventHandler(MouseEvent.MOUSE_PRESSED, addMouseHandler(node, MouseEvent.MOUSE_PRESSED, event -> {
                requestSelection(node);

                node.requestFocus();
                node.toFront();

                event.consume();
            }));

            groupConnections.getChildren().add(node);
        }
    }

    /**
     * Removes a {@link Connection} to this {@link DiagramPane}.
     * @param connection the {@link Connection} to remove
     */
    private void removeConnection(Connection connection) {
        nodes.remove(connection);

        for (Node node : groupConnections.getChildrenUnmodifiable()) {
            if (node instanceof ConnectionNode && ((ConnectionNode) node).getConnection().equals(connection)) {
                groupConnections.getChildren().remove(node);
                removeMouseHandlers((ConnectionNode) node);
                ((ConnectionNode) node).remove();
                break;
            }
        }
    }

    /**
     * Internal setup and handler creation.
     */
    private void setup() {
        getStyleClass().add("diagramNode");

        selected.addListener((ListChangeListener<SelectableNode>) c -> {
            while (c.next()) {
                c.getAddedSubList().forEach(node::set);
            }
        });

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
                    name = JavaBeanStringPropertyBuilder.create().bean(newValue).name(Diagram.PROPERTY_NAME).build();
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Could not create properties. This should never happen?!");
                }

                // set the properties
                properties = diagramManager.getDiagramProperties(newValue);

                listenerElements = (element, added) -> {
                    if (added) {
                        addElement(element);
                    } else {
                        removeElement(element);
                    }
                };
                listenerConnections = (connection, added) -> {
                    if (added) {
                        addConnection(connection);
                    } else {
                        removeConnection(connection);
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
                ((ConnectionNode) node).setEndX(toDiagramSpaceX(event.getX()));
                ((ConnectionNode) node).setEndY(toDiagramSpaceY(event.getY()));
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

                Element e = diagramManager.createElement(getDiagram(), event.getDragboard().getString(),
                        toDiagramSpaceX(mouseX-b.getMinX()-event.getDragboard().getDragViewOffsetX()),
                        toDiagramSpaceY(mouseY-b.getMinY()-event.getDragboard().getDragViewOffsetY())
                );

                if (e != null) {
                    requestSelection(e);
                    event.consume();
                }
            }
        });

        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {

            }
        });

        setOnMousePressed(event -> {
            selected.clear();
            node.set(null);
            requestFocus();
            event.consume();
        });

        selected.addListener((ListChangeListener<SelectableNode>) c -> {
            while(c.next()) {
                c.getAddedSubList().stream()
                        .filter(node1 -> node1 instanceof ElementNode)
                        .forEach(node1 -> DiagramPane.this.node.set((ElementNode) node1));
            }
        });

        node.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.requestFocus();
            } else {
                requestFocus();
            }

            reapplyShortcuts();
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
     * Gets the currently selected {@link SelectableNode}.
     * @return Returns the node or null if none is set.
     */
    public SelectableNode getNode() {
        return node.get();
    }

    /**
     * Sets the currently selected {@link ElementNode}.
     * @param node the new node
     */
    public void setNode(SelectableNode node) {
        this.node.set(node);
    }

    /**
     * Gets the node property.
     * @return Returns the property.
     */
    public ReadOnlyObjectProperty<SelectableNode> nodeProperty() {
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

    /**
     * Gets the diagram current properties.
     *
     * @return Retruns a list of them.
     */
    public ObservableList<Property<?>> getDiagramProperties() {
        return properties;
    }

    /**
     * Resets the shortcuts groups and re-registers all shortcuts.
     */
    protected void reapplyShortcuts() {
        ShortcutGroup groupElements = application.getShortcutManager().getShortcutGroup(GROUP_DIAGRAM_ELEMENTS);
        groupElements.reset();
        SelectableNode currentNode = getNode();
        if (currentNode != null) {
            currentNode.registerShortcuts(groupElements);
        }

        ShortcutGroup groupDiagram = application.getShortcutManager().getShortcutGroup(GROUP_DIAGRAM);
        groupDiagram.reset();
        groupDiagram.getShortcut(SHORTCUT_REMOVE_SELECTED).setAction(event -> {
            // copy to be able to remove
            List<SelectableNode> copy = new ArrayList<>(selected);

            for (SelectableNode node : copy) {
                if (node instanceof ElementNode) {
                    diagramManager.removeElement(getDiagram(), ((ElementNode) node).getElement());
                } else if (node instanceof ConnectionNode) {
                    try {
                        ((ConnectionNode) node).getConnection().getSource().disjoin(((ConnectionNode) node).getConnection().getTarget());
                    } catch (JointJoinException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Handler method that is called when opening the diagram.
     */
    public void onOpen() {
        reapplyShortcuts();
    }

    /**
     * Requests that a certain {@link SelectableNode} is being selected.
     * @param node the node to select
     */
    protected void requestSelection(SelectableNode node) {
        this.node.set(node);
        this.selected.clear();

        if (node != null) {
            this.selected.add(node);
        }
    }

    /**
     * Requests that a certain {@link DiagramContent} is being selected.
     * @param content the content to select
     */
    public void requestSelection(DiagramContent content) {
        requestSelection(nodes.get(content));
    }

    /**
     * Gets the application.
     * @return Returns the application.
     */
    public FlowDesignFxApplication getApplication() {
        return application;
    }
}
