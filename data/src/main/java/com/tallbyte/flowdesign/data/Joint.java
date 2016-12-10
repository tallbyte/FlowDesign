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
public class Joint {

    private final Element                       element;

    private final List<Joint>                   outgoing = new ArrayList<>();
    private       Joint                         incoming;

    private final String                        location;
    private final JointType                     type;
    private final int                           maxOut;

    /**
     * Creates a new {@link Connection} based on given configuration.
     * @param element the containing {@link Element}
     * @param location the location (e.g. name)
     * @param type the type
     * @param maxOut the maximum amount of outgoing
     *               connections or 0 for infinite
     *               (for OUTPUT type only)
     */
    public Joint(Element element,
                 String location,
                 JointType type,
                 int maxOut) {

        this.element  = element;
        this.location = location;
        this.type     = type;
        this.maxOut   = maxOut;
    }

    /**
     * Gets the containing {@link Element}.
     * @return Returns the {@link Element}.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Gets the incoming {@link Joint}.
     * @return Returns the {@link Joint} or null if none is set.
     */
    public Joint getIncoming() {
        return incoming;
    }

    /**
     * Gets the location of this {@link Joint}.
     * The location is some kind of name which can be arbitary,
     * but is unique per {@link Element}.
     * @return Returns the location.
     */
    public String getLocation() {
        return location;
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
                && target.getElement() != getElement()
                && (
                        (type == JointType.INPUT_OUTPUT && target.type == JointType.INPUT_OUTPUT)
                    ||  (type == JointType.OUTPUT       && target.type == JointType.INPUT       )
                );
    }

    /**
     * Checks whether this {@link Joint} can be connected to the given one, assuming the given one is the source.
     * For internal use only.
     * @param source the source {@link Joint}
     * @throws JointJoinException if no connection can be made.
     */
    private void checkJoint(Joint source) throws JointJoinException {
        if (this.incoming != null) {
            throw new JointJoinException("can not join " + this + " and " + source + ": target already joined");
        }
    }

    /**
     * Internal notify method.
     * @param source the source {@link Joint}
     */
    private void notifyJoin(Joint source) {
        this.incoming = source;
    }

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

        Connection connection = new Connection(this, target);

        target.checkJoint(this);

        if (element.getDiagram().addConnection(connection)) {
            target.notifyJoin(this);
            outgoing.add(target);
            return connection;
        } else {
            throw new JointJoinException("can not join " + target + " and " + this + ": already joined");
        }
    }

    /**
     * Internal notify method.
     * @param target the target {@link Joint}
     */
    private void notifyDisjoin(Joint target) {
        outgoing.remove(target);
    }

    /**
     * Removes the incoming connection if none is present.
     */
    public void disjoin() {
        if (incoming != null) {
            element.getDiagram().removeConnection(new Connection(this, incoming));
            notifyDisjoin(incoming);
            incoming = null;
        }
    }
}
