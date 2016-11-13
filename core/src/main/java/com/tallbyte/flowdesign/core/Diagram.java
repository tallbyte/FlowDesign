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
import java.util.List;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-07)<br/>
 */
public abstract class Diagram {

    protected String                        name;
    protected List<Element>                 elements;

    protected Element                       root;

    protected Project                       project = null;

    protected List<ElementsChangedListener> listeners = new ArrayList<>();
    protected PropertyChangeSupport         changeSupport;

    public Diagram(String name, Element root) {
        this.name = name;
        this.root = root;

        this.changeSupport = new PropertyChangeSupport(this);
    }

    public void setName(String name) {
        if (project != null) {
            project.notifyNameChange(this, this.name, name);
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Element getRoot() {
        return root;
    }

    void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public void addElementsChangedListener(ElementsChangedListener listener) {
        listeners.add(listener);
    }

    public void removeElementsChangedListener(ElementsChangedListener listener) {
        listeners.remove(listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public String toString() {
        return name;
    }
}
