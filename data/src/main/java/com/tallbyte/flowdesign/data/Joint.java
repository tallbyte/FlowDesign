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
import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-05)<br/>
 * <br/>
 * A {@link Joint} describes a connection-point used to connect two
 * {@link Element}s. Some {@link Joint}s may be defined as input, others
 * as output.
 */
public abstract class Joint {

    private final Element                       element;

    private final List<Connection>              outgoing = new ArrayList<>();
    private final List<Connection>              incoming = new ArrayList<>();

    private final JointType                     type;
    private final int                           maxIn;
    private final int                           maxOut;

    protected PropertyChangeSupport changeSupport;

    /**
     * Creates a new {@link Joint} based on given configuration.
     * @param element the containing {@link Element}
     * @param type the type
     * @param maxOut the maximum amount of outgoing
     *               connections or 0 for infinite
     *               (for OUTPUT type only)
     */
    public Joint(Element element,
                 JointType type,
                 int maxIn,
                 int maxOut) {

        this.element  = element;
        this.type     = type;
        this.maxIn    = maxIn;
        this.maxOut   = maxOut;

        changeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Gets the containing {@link Element}.
     * @return Returns the {@link Element}.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Gets whether or not this {@link Joint} is suitable for incoming connections.
     * @return Returns true if connections to this {@link Joint} can be made, else false.
     */
    public boolean isInput() {
        return type == JointType.INPUT || type == JointType.INPUT_OUTPUT;
    }

    /**
     * Gets whether or not this {@link Joint} is suitable for outgoing connections.
     * This will not check whether the maximum amount of outgoing connections
     * is already reached.
     * @return Returns true if connections can be made from this {@link Joint}.
     */
    public boolean isOutput() {
        return type == JointType.OUTPUT || type == JointType.INPUT_OUTPUT;
    }

    /**
     * Check whether this {@link Joint} can be connected to the given {@link Joint}, assuming this
     * one is source and the given one is target.
     * @param target the target {@link Joint}
     * @return Returns true if a connection can be made, else false.
     */
    public boolean canJoin(Joint target) {
        return (outgoing.size() < maxOut || maxOut == 0)
                && (target.incoming.size() < target.maxIn || target.maxIn == 0)
                && target.getElement() != getElement()
                && (
                        (type == JointType.INPUT_OUTPUT && target.type == JointType.INPUT_OUTPUT)
                    ||  (type == JointType.OUTPUT       && target.type == JointType.INPUT       )
                );
    }

    /**
     * Internal notify method.
     * @param connection the connection
     */
    private void notifyJoin(Connection connection) {
        incoming.add(connection);
    }

    /**
     * Creates a new {@link Connection} between the two {@link Joint}s.
     * @param target the target {@link Joint}
     * @return Returns the new {@link Connection}.
     */
    protected abstract Connection createConnection(Joint target);

    /**
     * Joins two {@link Joint}s. This assumes this {@link Joint} is source and the given one is target.
     * @param target the target {@link Joint}
     * @return Returns the created {@link Connection}.
     * @throws JointJoinException if the restrictions forbid to create an connection.
     * See <code>#canJoin()</code> for more information.
     */
    public Connection join(Joint target) throws JointJoinException {
        if (!isOutput()) {
            throw new JointJoinException("can not join " + target + " and " + this + ": source no output");
        }

        if (!target.isInput()) {
            throw new JointJoinException("can not join " + target + " and " + this + ": target no input");
        }

        if (!canJoin(target)) {
            throw new JointJoinException("can not join " + target + " and " + this + ": joints do not match");
        }

        Connection connection = createConnection(target);

        for (Connection c : outgoing) {
            if (c.getTarget() == target) {
                throw new JointJoinException("can not join " + this + " and " + target + ": target already joined");
            }
        }

        if (element.getDiagram().addConnection(connection)) {
            target.notifyJoin(connection);
            outgoing.add(connection);
            return connection;
        } else {
            throw new JointJoinException("can not join " + target + " and " + this + ": already joined");
        }
    }

    /**
     * Internal notify method.
     * @param connection the connection
     */
    private void notifyDisjoin(Connection connection) {
        incoming.remove(connection);
    }

    /**
     * Removes the incoming connection if none is present.
     */
    public void disjoin(Joint target) throws JointJoinException {
        Connection connection = null;

        for (Connection c : outgoing) {
            if (c.getTarget() == target) {
                connection = c;
                break;
            }
        }

        if (connection != null) {
            outgoing.remove(connection);
            target.notifyDisjoin(connection);
            element.getDiagram().removeConnection(connection);

        } else {
            throw new JointJoinException("can not disjoin " + this + " and " + target + ": not connected");
        }
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
