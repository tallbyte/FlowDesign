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

package com.tallbyte.flowdesign.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-07)<br/>
 * <br/>
 * A {@link Diagram} contains {@link Element}s of specifiable type. It
 * allows allows to specify certain listeners to monitor {@link Element}
 * or {@link Connection} removal or addition.
 */
public abstract class Diagram<E extends Element> {

    public static final String PROPERTY_NAME    = "name";
    public static final String PROPERTY_PROJECT = "project";

    protected String                           name;
    protected List<E>                          elements    = new ArrayList<>();
    protected List<Connection>                 connections = new ArrayList<>();

    protected E                                root;

    protected Project                          project = null;

    protected List<ElementsChangedListener>    listenersElements    = new ArrayList<>();
    protected List<ConnectionsChangedListener> listenersConnections = new ArrayList<>();
    protected PropertyChangeSupport            changeSupport;

    /**
     * Creates a new {@link Diagram}
     * @param name the name
     * @param root the root {@link Element} required for analysis
     */
    public Diagram(String name, E root) {
        this.name = name;
        this.root = root;

        addElement(root);

        this.changeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Sets the name.
     * @param name the new name
     * @throws IllegalArgumentException Is thrown if the name is already used in the projcet
     */
    public void setName(String name) {
        // check if the name is already in use
        Project project = getProject();
        if (project != null) {
            for (Diagram d : project.getDiagrams(getClass())) {
                if (name.equals(d.getName())) {
                    throw new IllegalArgumentException("diagram name already used in project");
                }
            }

            project.notifyNameChange(this, this.name, name);
        }

        String old = this.name;
        this.name = name;
        this.changeSupport.firePropertyChange(PROPERTY_NAME, old, name);
    }

    /**
     * Gets the name of this {@link Diagram}.
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the root {@link Element} of this {@link Diagram}.
     * The root may be used for further analysis or reflection.
     * @return Returns the root {@link Element}.
     */
    public Element getRoot() {
        return root;
    }

    /**
     * Sets the {@link Project} of this {@link Diagram}.
     * For internal use only.
     * @param project the new {@link Project}
     */
    void setProject(Project project) {
        Project old = this.project;
        this.project = project;
        this.changeSupport.firePropertyChange(PROPERTY_PROJECT, old, project);
    }

    /**
     * Gets the {@link Project} of this {@link Diagram}.
     * @return Returns the {@link Project} or null if none is set.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Adds the given {@link Element} to the internal list.
     * This will also call all {@link ElementsChangedListener}.
     * @param element the @{link Element} to add
     * @return Returns true if the element was added else false.
     */
    public boolean addElement(E element) {
        if (element == null) {
            return false;
        }

        if (this.elements.contains(element)) {
            return false;
        }

        this.elements.add(element);
        element.setDiagram(this);

        for (ElementsChangedListener listener : listenersElements) {
            listener.onElementsChanged(element, true);
        }

        return true;
    }
    /**
     * Removes the given {@link Element} from the internal list.
     * This will also call all {@link ElementsChangedListener}.
     * @param element the @{link Element} to remove
     */

    public void removeElement(E element) {
        List<Connection> remove = new ArrayList<>();
        for (Connection c : getConnections()) {
            if (c.getSource().getElement() == element || c.getTarget().getElement() == element) {
                remove.add(c);
            }
        }

        for (Connection connection : remove) {
            try {
                connection.getSource().disjoin(connection.getTarget());
            } catch (JointJoinException e) {
                e.printStackTrace();
            }
        }

        this.elements.remove(element);
        element.setDiagram(null);

        for (ElementsChangedListener listener : listenersElements) {
            listener.onElementsChanged(element, false);
        }
    }

    /**
     * Adds the given {@link Connection} to the internal list.
     * This will also call all {@link ConnectionsChangedListener}.
     * @param connection the @{link Connection} to add
     */
    boolean addConnection(Connection connection) {
        // some pre-checking
        if (!connections.contains(connection)
                && connection.getTarget() != connection.getSource()) {

            this.connections.add(connection);

            for (ConnectionsChangedListener listener : listenersConnections) {
                listener.onConnectionsChanged(connection, true);
            }

            return true;
        }

        return false;
    }

    /**
     * Removes the given {@link Connection} from the internal list.
     * This will also call all {@link ConnectionsChangedListener}.
     * @param connection the @{link Connection} to remove
     */

    void removeConnection(Connection connection) {
        this.connections.remove(connection);

        for (ConnectionsChangedListener listener : listenersConnections) {
            listener.onConnectionsChanged(connection, false);
        }
    }

    /**
     * Gets all added {@link Connection}s.
     * @return Returns an {@link Iterable} containing the {@link Connection}s.
     */
    public Iterable<Connection> getConnections() {
        return Collections.unmodifiableCollection(connections);
    }

    /**
     * Gets all added {@link Element}s.
     * @return Returns an {@link Iterable} containing the {@link Element}s.
     */
    public Iterable<E> getElements() {
        return Collections.unmodifiableCollection(elements);
    }

    /**
     * Registers an {@link ElementsChangedListener}.
     * @param listener the {@link ElementsChangedListener} to register
     */
    public void addElementsChangedListener(ElementsChangedListener listener) {
        listenersElements.add(listener);
    }

    /**
     * Unregisters an {@link ElementsChangedListener}.
     * @param listener the {@link ElementsChangedListener} to unregister
     */
    public void removeElementsChangedListener(ElementsChangedListener listener) {
        listenersElements.remove(listener);
    }

    /**
     * Registers an {@link ConnectionsChangedListener}.
     * @param listener the {@link ConnectionsChangedListener} to register
     */
    public void addConnectionsChangedListener(ConnectionsChangedListener listener) {
        listenersConnections.add(listener);
    }

    /**
     * Unregisters an {@link ConnectionsChangedListener}.
     * @param listener the {@link ConnectionsChangedListener} to unregister
     */
    public void removeConnectionsChangedListener(ConnectionsChangedListener listener) {
        listenersConnections.remove(listener);
    }

    /**
     * Registers an {@link PropertyChangeListener}.
     * @param listener the {@link PropertyChangeListener} to register
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Unregisters an {@link PropertyChangeListener}.
     * @param listener the {@link PropertyChangeListener} to unregister
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Diagram) {
            return obj.getClass() == getClass()
                    && ((Diagram) obj).getName().toLowerCase().equals(getName().toLowerCase());
        }

        return false;
    }
}

