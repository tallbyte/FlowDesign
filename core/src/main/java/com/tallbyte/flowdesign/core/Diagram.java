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
    protected List<Element>                 elements = new ArrayList<>();

    protected Element                       root;

    protected Project                       project = null;

    protected List<ElementsChangedListener> listeners = new ArrayList<>();
    protected PropertyChangeSupport         changeSupport;

    /**
     * Creates a new {@link Diagram}
     * @param name the name
     * @param root the root {@link Element} required for analysis
     */
    public Diagram(String name, Element root) {
        this.name = name;
        this.root = root;

        elements.add(root);
        this.changeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Sets the name.
     * @param name the new name
     */
    public void setName(String name) {
        if (project != null) {
            project.notifyNameChange(this, this.name, name);
        }

        this.name = name;
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
        this.project = project;
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
     */
    public void addElement(Element element) {
        this.elements.add(element);

        for (ElementsChangedListener listener : listeners) {
            listener.onElementsChanged(element, true);
        }
    }
    /**
     * Removes the given {@link Element} from the internal list.
     * This will also call all {@link ElementsChangedListener}.
     * @param element the @{link Element} to remove
     */

    public void removeElement(Element element) {
        this.elements.remove(element);

        for (ElementsChangedListener listener : listeners) {
            listener.onElementsChanged(element, false);
        }
    }

    /**
     * Gets all added {@link Element}s.
     * @return Returns an {@link Iterable} containing the {@link Element}s.
     */
    public Iterable<Element> getElements() {
        return elements;
    }

    /**
     * Registers an {@link ElementsChangedListener}.
     * @param listener the {@link ElementsChangedListener} to register
     */
    public void addElementsChangedListener(ElementsChangedListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregisters an {@link ElementsChangedListener}.
     * @param listener the {@link ElementsChangedListener} to unregister
     */
    public void removeElementsChangedListener(ElementsChangedListener listener) {
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

    @Override
    public String toString() {
        return name;
    }
}
