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
 */
public class Project {

    private Map<Class<? extends Diagram>, List<Diagram>> diagrams  = new HashMap<>();
    private Map<String,                   Diagram>       nameMap   = new HashMap<>();
    private List<DiagramsChangedListener>                listeners = new ArrayList<>();

    private String                                       name;

    private PropertyChangeSupport                        changeSupport;

    public Project() {
        this("default");
    }

    public Project(String name) {
        this.name = name;

        this.changeSupport = new PropertyChangeSupport(this);
    }

    protected void callListeners(Diagram diagram, boolean added) {
        for (DiagramsChangedListener listener : listeners) {
            listener.onDiagramsChanged(diagram, added);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Diagram getDiagram(String name) {
        return nameMap.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends Diagram> Iterable<T> getDiagrams(Class<T> diagramType) {
        Iterable<T> list = (Iterable<T>) diagrams.get(diagramType);

        return list == null ? new ArrayList<>() : list;
    }

    public void addDiagramsChangedListener(DiagramsChangedListener listener) {
        listeners.add(listener);
    }

    public void removeDiagramsChangedListener(DiagramsChangedListener listener) {
        listeners.remove(listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    void notifyNameChange(Diagram diagram, String oldName, String newName) {
        nameMap.remove(oldName);
        nameMap.put(newName, diagram);
    }

}
