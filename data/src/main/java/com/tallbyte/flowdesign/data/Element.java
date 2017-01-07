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

import java.beans.*;
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
public abstract class Element implements DiagramContent {

    public static final String PROPERTY_TEXT      = "text";
    public static final String PROPERTY_COLOR     = "color";
    public static final String PROPERTY_X         = "x";
    public static final String PROPERTY_Y         = "y";
    public static final String PROPERTY_WIDTH     = "width";
    public static final String PROPERTY_HEIGHT    = "height";
    public static final String PROPERTY_DELETABLE = "deletable";


    protected Diagram                    diagram;
    protected Map<String, JointGroup<?>> jointGroups;

    protected String text       = "Label";
    protected String color      = null;
    protected double x          = 0;
    protected double y          = 0;
    protected double width      = 75;
    protected double height     = 75;
    protected boolean deletable = true;

    protected PropertyChangeSupport       changeSupport;

    /**
     * Creates a new {@link Element}.
     */
    public Element() {
        changeSupport = new PropertyChangeSupport(this);

        HashMap<String, JointGroup<?>> map = new HashMap<>();
        for (JointGroup group : createJointGroups()) {
            map.put(group.getName(), group);
        }
        this.jointGroups = Collections.unmodifiableMap(map);
    }

    protected Iterable<JointGroup<?>> createJointGroups() {
        return new ArrayList<>();
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
        this.changeSupport.firePropertyChange(PROPERTY_TEXT, old, text);
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
        this.changeSupport.firePropertyChange(PROPERTY_COLOR, old, color);

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
        this.changeSupport.firePropertyChange(PROPERTY_X, old, x);
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
        this.changeSupport.firePropertyChange(PROPERTY_Y, old, y);
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
        this.changeSupport.firePropertyChange(PROPERTY_WIDTH, old, width);
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
        this.changeSupport.firePropertyChange(PROPERTY_HEIGHT, old, height);
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
        this.changeSupport.firePropertyChange(PROPERTY_DELETABLE, old, deletable);
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
     * Gets all available {@link JointGroup}s names.
     * @return Returns the names
     */
    public Iterable<String> getJointGroups() {
        return jointGroups.keySet();
    }

    /**
     * Gets a {@link JointGroup} registered for a certain name.
     * @param name the name
     * @return Returns the group or null if none was found.
     */
    public JointGroup getJointGroup(String name) {
        return jointGroups.get(name);
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
