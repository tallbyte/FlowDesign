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

package com.tallbyte.flowdesign.core.environment;

import com.tallbyte.flowdesign.core.Element;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-11)<br/>
 */
public class Connection {

    private boolean toSystem;
    private Element element;

    private PropertyChangeSupport changeSupport;

    public Connection(Element element) {
        this(false, element);
    }

    public Connection(boolean toSystem, Element element) {
        this.toSystem = toSystem;
        this.element  = element;

        this.changeSupport = new PropertyChangeSupport(this);
    }

    public boolean isToSystem() {
        return toSystem;
    }

    public void setToSystem(boolean toSystem) {
        this.toSystem = toSystem;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }
}