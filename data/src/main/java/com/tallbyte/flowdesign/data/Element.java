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

import com.tallbyte.flowdesign.data.flow.Join;

import java.beans.*;
import java.util.*;
import java.util.stream.Collectors;

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

    protected                                   Diagram      diagram;
    protected final Map<Class<? extends Joint>, List<Joint>> joints = new HashMap<>();

    protected String text       = "Label";
    protected String color      = null;
    protected double x          = 0;
    protected double y          = 0;
    protected double width      = 75;
    protected double height     = 75;
    protected boolean deletable = true;

    protected List<JointsChangedListener> listenersJoints = new ArrayList<>();
    protected PropertyChangeSupport       changeSupport;

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
        List<Joint> list = joints.get(joint.getClass());

        if (list == null) {
            list = new ArrayList<>();
            joints.put(joint.getClass(), list);
        }

        list.add(joint);
    }

    /**
     * Removes a {@link Joint}.
     * @param joint the {@link Joint} to remove
     */
    protected void removeJoint(Joint joint) {
        List<Joint> list = joints.get(joint.getClass());

        if (list != null) {
            list.remove(joint);
            if (list.size() == 0) {
                joints.remove(joint.getClass());
            }

        }
    }

    /**
     * Gets the description text
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the description text.
     * @param text the new text
     */
    public void setText(String text) {
        String old = this.text;
        this.text = text;
        this.changeSupport.firePropertyChange("text", old, text);
    }

    /**
     * Gets the desired color in web-format.
     * @return Returns the color.
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the description color.
     * @param color the new color in web-format
     */
    public void setColor(String color) {
        String old = this.color;
        this.color = color;
        this.changeSupport.firePropertyChange("color", old, color);

        // TODO check for format maybe?
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
        double old = this.x;
        this.x = x;
        this.changeSupport.firePropertyChange("x", old, x);
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
        double old = this.y;
        this.y = y;
        this.changeSupport.firePropertyChange("y", old, y);
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
        double old = this.width;
        this.width = width;
        this.changeSupport.firePropertyChange("width", old, width);
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
        double old = this.height;
        this.height = height;
        this.changeSupport.firePropertyChange("height", old, height);
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
        boolean old = this.deletable;
        this.deletable = deletable;
        this.changeSupport.firePropertyChange("deletable", old, deletable);
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
    protected void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }

    /**
     * Gets the registered {@link Joint}s. The returned list is unmodifiable.
     * @param clazz the type of {@link Joint}s
     * @return Returns an {@link Iterable} containing the {@link Joint}s.
     */
    public Iterable<Joint> getJoints(Class<? extends Joint> clazz) {
        return Collections.unmodifiableList(joints.get(clazz));
    }

    /**
     * Gets the registered {@link Joint}s. The returned list is unmodifiable.
     * Only input joints are returned.
     * @param clazz the type of {@link Joint}s
     * @return Returns an {@link Iterable} containing the {@link Joint}s.
     */
    public Iterable<Joint> getInputJoints(Class<? extends Joint> clazz) {
        return Collections.unmodifiableList(joints.get(clazz).stream().filter(Joint::isInput).collect(Collectors.toList()));
    }

    /**
     * Gets the registered {@link Joint}s. The returned list is unmodifiable.
     * Only output joints are returned.
     * @param clazz the type of {@link Joint}s
     * @return Returns an {@link Iterable} containing the {@link Joint}s.
     */
    public Iterable<Joint> getOutputJoints(Class<? extends Joint> clazz) {
        return Collections.unmodifiableList(joints.get(clazz).stream().filter(Joint::isOutput).collect(Collectors.toList()));
    }

    /**
     * Gets the registered {@link Joint}s. The returned list is unmodifiable.
     * @param clazz the type of {@link Joint}s
     * @param in is the requested joint input?
     * @param out is the requested joint output?
     * @param index the index of the node
     * @return Returns an {@link Iterable} containing the {@link Joint}s.
     */
    public Joint getJoints(Class<? extends Joint> clazz, boolean in, boolean out, int index) {
        int i = 0;
        for (Joint joint : Collections.unmodifiableList(joints.get(clazz))) {
            if ((in == joint.isInput()) && (out == joint.isOutput()) && index == i) {
                return joint;
            }
            ++i;
        }

        throw new JointNotFoundException("");
    }

    /**
     * Registers an {@link JointsChangedListener}.
     * @param listener the {@link JointsChangedListener} to register
     */
    public void addJointsChangedListener(JointsChangedListener listener) {
        listenersJoints.add(listener);
    }

    /**
     * Unregisters an {@link JointsChangedListener}.
     * @param listener the {@link JointsChangedListener} to unregister
     */
    public void removeJointsChangedListener(JointsChangedListener listener) {
        listenersJoints.remove(listener);
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
