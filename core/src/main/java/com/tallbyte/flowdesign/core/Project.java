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

package com.tallbyte.flowdesign.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-07)<br/>
 * <br/>
 * The {@link Project} holds {@link Diagram}s of arbitrary type, registered by
 * {@link Class}.
 */
public class Project {

    private Map<Class<? extends Diagram>, List<Diagram>> diagrams  = new HashMap<>();
    private Map<String,                   Diagram>       nameMap   = new HashMap<>();
    private List<DiagramsChangedListener>                listeners = new ArrayList<>();

    private String                                       name;

    private PropertyChangeSupport                        changeSupport;

    /**
     * Creates a new {@link Project} with a default name.
     */
    public Project() {
        this("default");
    }

    /**
     * Creates a new {@link Project} with the given name.
     * @param name the name of the {@link Project}
     */
    public Project(String name) {
        this.name = name;

        this.changeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Calls all {@link DiagramsChangedListener}s.
     * @param diagram the {@link Diagram} to call for
     * @param added was the diagram added or removed?
     */
    protected void callListeners(Diagram diagram, boolean added) {
        for (DiagramsChangedListener listener : listeners) {
            listener.onDiagramsChanged(diagram, added);
        }
    }

    /**
     * Gets the name of this {@link Project}.
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this {@link Project}.
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a {@link Diagram}.
     * This will call all {@link DiagramsChangedListener}s.
     * @param diagram the {@link Diagram} to add
     */
    public void addDiagram(Diagram diagram) {
        if (diagram == null) {
            return;
        }

        List<Diagram> list = diagrams.get(diagram.getClass());

        if (list == null) {
            list = new ArrayList<>();
            diagrams.put(diagram.getClass(), list);
        }

        // add to the local lists
        list.add(diagram);
        nameMap.put(diagram.getName(), diagram);

        // set project
        diagram.setProject(this);

        // call listeners
        callListeners(diagram, true);
    }

    /**
     * Removes a {@link Diagram}.
     * This will call all {@link DiagramsChangedListener}s.
     * @param diagram the {@link Diagram} to remove
     */
    public void removeDiagram(Diagram diagram) {
        if (diagram == null) {
            return;
        }

        // remove from map
        diagrams.get(diagram.getClass()).remove(diagram);

        if (diagrams.get(diagram.getClass()).size() == 0) {
            diagrams.remove(diagram.getClass());
        }

        nameMap.remove(diagram.getName());

        // reset project
        diagram.setProject(null);

        // call listeners
        callListeners(diagram, false);
    }

    /**
     * Gets a {@link Diagram} with a specified name.
     * @param name the name of this {@link Diagram}
     * @return
     */
    public Diagram getDiagram(String name) {
        return nameMap.get(name);
    }

    /**
     * Gets all {@link Diagram}s of a certain type.
     * @param diagramType the type
     * @return Returns an {@link Iterable} containing all {@link Diagram}s with of that type.
     */
    @SuppressWarnings("unchecked")
    public <T extends Diagram> Iterable<T> getDiagrams(Class<T> diagramType) {
        Iterable<T> list = (Iterable<T>) diagrams.get(diagramType);

        return list == null ? new ArrayList<>() : list;
    }

    /**
     * Registers an {@link DiagramsChangedListener}.
     * @param listener the {@link DiagramsChangedListener} to register
     */
    public void addDiagramsChangedListener(DiagramsChangedListener listener) {
        listeners.add(listener);
    }


    /**
     * Unregisters an {@link DiagramsChangedListener}.
     * @param listener the {@link DiagramsChangedListener} to unregister
     */
    public void removeDiagramsChangedListener(DiagramsChangedListener listener) {
        listeners.remove(listener);
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

    /**
     * Notifies this {@link Project} that a {@link Diagram}s name was changed
     * without the need for extra listener objects.
     * @param diagram the changed {@link Diagram}
     * @param oldName the old name
     * @param newName the new name
     */
    void notifyNameChange(Diagram diagram, String oldName, String newName) {
        nameMap.remove(oldName);
        nameMap.put(newName, diagram);
    }

}
