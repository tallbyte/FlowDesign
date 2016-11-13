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

    protected ObservableValue<Diagram, String> name;
    protected List<Element>                    elements;

    protected Element                       root;

    protected Project                       project = null;

    protected List<ElementsChangedListener> listeners = new ArrayList<>();
    protected PropertyChangeSupport         changeSupport;

    public Diagram(String name, Element root) {
        this.name = new ObservableValue<>(this, name);
        this.root = root;

        this.changeSupport = new PropertyChangeSupport(this);
    }

    /**
     * @param name The new name of this {@link Diagram}
     * @return itself
     */
    public Diagram setName(String name) {
        this.name.setValue(name);
        return this;
    }

    /**
     * @return The name of this {@link Diagram}
     */
    public String getName() {
        return this.name.getValue();
    }

    /**
     * Adds a {@link ValueChangedListener} that is being informed if the name changes
     *
     * @param listener {@link ValueChangedListener} to add
     * @return itself
     */
    public Diagram addNameChangedListener(ValueChangedListener<? super Diagram, ? super String> listener) {
        name.addListener(listener);
        return this;
    }

    /**
     * @param listener The {@link ValueChangedListener} to remove
     * @return Whether the removal was successfully and therefore the {@link ValueChangedListener} was registered before
     */
    public boolean removeNameChangedListener(ValueChangedListener<? super Diagram, ? super String> listener) {
        return name.removeListener(listener);
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
        return getName();
    }
}
