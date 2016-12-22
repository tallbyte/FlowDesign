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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-11)<br/>
 * <br/>
 * A {@link Connection} describes a connection between two {@link Joint}s, which
 * basically means a connection between two {@link Element}s.
 */
public class Connection<J extends Joint> {

    protected final J source;
    protected final J target;

    private String text = "";

    protected PropertyChangeSupport changeSupport;

    /**
     * Creates a new {@link Connection} between two {@link Joint}s.
     * @param source the source {@link Joint}
     * @param target the target {@link Joint}
     */
    public Connection(J source, J target) {
        this.source = source;
        this.target = target;

        changeSupport = new PropertyChangeSupport(this);
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
     * Gets the source {@link Joint}.
     * @return Returns the {@link Joint}.
     */
    public J getSource() {
        return source;
    }

    /**
     * Gets the target {@link Joint}.
     * @return Returns the {@link Joint}.
     */
    public J getTarget() {
        return target;
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
    public boolean equals(Object obj) {
        if (obj instanceof Connection) {
            Connection con = (Connection) obj;

            return (con.getTarget() == getTarget() && con.getSource() == getSource())
                    || (con.getTarget() == getSource() && con.getSource() == getTarget());
        }

        return false;
    }
}
