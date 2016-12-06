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

import com.tallbyte.flowdesign.core.environment.Connection;

import java.util.HashSet;
import java.util.Set;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-05)<br/>
 */
public class Joint {

    private       Element                       element;
    private       Joint                         remote;

    private final JointLocation                 location;
    private final Class<? extends Element>      baseClass;
    private final Set<JointLocation>            acceptedLocations;
    private final Set<Class<? extends Element>> acceptedElements;
    private final boolean                       forceOut;

    public Joint(JointLocation location,
                 Class<? extends Element> baseClass,
                 Set<JointLocation> acceptedLocations,
                 Set<Class<? extends Element>> acceptedElements,
                 boolean forceOut) {

        this.location           = location;
        this.baseClass          = baseClass;
        this.acceptedLocations  = new HashSet<>(acceptedLocations);
        this.acceptedElements   = new HashSet<>(acceptedElements);
        this.forceOut           = forceOut;
    }

    public Element getElement() {
        return element;
    }

    void setElement(Element element) {
        this.element = element;
    }

    public Class<? extends Element> getBaseClass() {
        return baseClass;
    }

    public JointLocation getLocation() {
        return location;
    }

    public boolean isOutput() {
        return forceOut || acceptedLocations.size() == 0;
    }

    public boolean canJoin(Joint remote) {
        return acceptedLocations.contains(remote.getLocation())
                && (acceptedElements.size() == 0 || acceptedElements.contains(remote.getBaseClass()));
    }

    public Connection join(Joint remote) throws JointJoinException {
        if (!canJoin(remote)) {
            throw new JointJoinException("can not join " + remote + " and " + this);
        }

        Connection connection = new Connection(this, remote);

        if (element != null) {
            if (element.getDiagram().addConnection(connection)) {
                this.remote = remote;

                return connection;
            }
        }

        return null;
    }

    public void disjoin() {
        if (remote != null) {
            element.getDiagram().removeConnection(new Connection(this, remote));
            remote = null;
        }
    }
}
