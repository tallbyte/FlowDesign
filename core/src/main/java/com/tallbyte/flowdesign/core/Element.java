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
 */
public abstract class Element {

    private       Diagram diagram;
    private final List<Joint> joints;

    private double x          = 0;
    private double y          = 0;
    private double width      = 100;
    private double height     = 100;
    private boolean deletable = true;

    private PropertyChangeSupport changeSupport;

    /**
     * Creates a new {@link Element}.
     */
    public Element() {
        this(new ArrayList<>());
    }

    /**
     * Creats a new {@link Element} with a given set of {@link Joint}s.
     * @param joints the available {@link Joint}s
     */
    public Element(List<Joint> joints) {
        this.joints   = Collections.unmodifiableList(new ArrayList<>(joints));
        this.joints.forEach(joint -> joint.setElement(this));

        changeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Creats a new {@link Element} with a given set of {@link Joint}s.
     * @param joints the available {@link Joint}s
     */
    public Element(Joint... joints) {
        this.joints   = Collections.unmodifiableList(Arrays.asList(joints));
        this.joints.forEach(joint -> joint.setElement(this));

        changeSupport = new PropertyChangeSupport(this);
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

    public Diagram getDiagram() {
        return diagram;
    }

    void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }

    /**
     * Gets the registered {@link Joint}s. The returned list is unmodifiable.
     * @return Returns a list containing the {@link Joint}s.
     */
    public List<Joint> getJoints() {
        return joints;
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

    protected static List<Joint> generateDefaultJoints(Class<? extends Element> baseClass) {
        List<Joint> list = new ArrayList<>();

        Set<JointLocation> acceptedLocations = new HashSet<>();
        for (JointLocation loc : JointLocation.values()) {
            acceptedLocations.add(loc);
        }

        for (JointLocation loc : JointLocation.values()) {
            list.add(new Joint(loc, baseClass, acceptedLocations, new HashSet<>(), true));
        }

        return list;
    }

}
