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
import java.util.*;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-07)<br/>
 * <br/>
 * This class describes an abstract element inside of a {@link Diagram}.
 * The {@link Element} is resizeable and movable. It may also contain {@link Joint}s
 * which can be used to connect it to other {@link Element}s.
 */
public abstract class Element {

    protected       Diagram            diagram;
    protected final Map<String, Joint> joints = new HashMap<>();

    protected double x          = 0;
    protected double y          = 0;
    protected double width      = 100;
    protected double height     = 100;
    protected boolean deletable = true;

    private PropertyChangeSupport changeSupport;

    /**
     * Creats a new {@link Element} with a given set of {@link Joint}s.
     */
    public Element() {
        changeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Adds a {@link Joint}.
     * @param joint the {@link Joint} to add
     */
    protected void addJoint(Joint joint) {
        joints.put(joint.getLocation(), joint);
    }

    /**
     * Gets the upper-left x coordinate.
     * @return Returns the coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the upper-left x coordinate.
     * @param x the coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the upper-left y coordinate.
     * @return Returns the coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the upper-left y coordinate.
     * @param y the coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Gets the width.
     * @return Returns the width.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the width.
     * @param width the width
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Gets the height.
     * @return Returns the height.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the height.
     * @param height the height
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Sets whether or not this {@link Element} is deletable or not.
     * @return Returns true if deletable, else false.
     */
    public boolean isDeletable() {
        return deletable;
    }

    /**
     * Sets whether or not this {@link Element} is deletable or not.
     * @param deletable the deletable state
     */
    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    /**
     * Gets the {@link Diagram} this {@link Element} is in.
     * @return Returns the {@link Diagram}.
     */
    public Diagram getDiagram() {
        return diagram;
    }

    /**
     * Sets the {@link Diagram} of this {@link Element}. For internal use only.
     * @param diagram the new {@link Diagram}
     */
    void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }

    /**
     * Gets a specific {@link Joint} based on his location. The available locations
     * are usually defined as constants in the corresponding {@link Element} class.
     * @param location the location of the {@link Joint}
     * @return Returns the {@link Joint} or null if none exists for the specified location.
     */
    public Joint getJoint(String location) {
        return joints.get(location);
    }

    /**
     * Gets the registered {@link Joint}s. The returned list is unmodifiable.
     * @return Returns an {@link Iterable} containing the {@link Joint}s.
     */
    public Iterable<Joint> getJoints() {
        return joints.values();
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
}
