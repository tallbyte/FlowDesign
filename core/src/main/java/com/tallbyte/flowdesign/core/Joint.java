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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-05)<br/>
 */
public class Joint {

    private final Element                       element;

    private final List<Joint>                   outgoing = new ArrayList<>();
    private       Joint                         incoming;

    private final String                        location;
    private final JointType                     type;
    private final int                           maxOut;

    public Joint(Element element,
                 String location,
                 JointType type,
                 int maxOut) {

        this.element  = element;
        this.location = location;
        this.type     = type;
        this.maxOut   = maxOut;
    }

    public Element getElement() {
        return element;
    }

    public Joint getIncoming() {
        return incoming;
    }

    public String getLocation() {
        return location;
    }

    public boolean isInput() {
        return type == JointType.INPUT || type == JointType.DEPENDENCY;
    }

    public boolean isOutput() {
        return type == JointType.OUTPUT || type == JointType.DEPENDENCY;
    }

    public boolean canJoin(Joint remote) {
        return (outgoing.size() < maxOut || maxOut == 0)
                && (
                        (type == JointType.DEPENDENCY && remote.type == JointType.DEPENDENCY)
                    ||  (type == JointType.OUTPUT     && remote.type == JointType.INPUT     )
                );
    }

    private void notifyJoin(Joint source) throws JointJoinException {
        if (this.incoming != null) {
            throw new JointJoinException("can not join " + this + " and " + source + ": target already joined");
        }

        this.incoming = source;
    }

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

        if (element != null) {
            if (element.getDiagram().addConnection(connection)) {
                target.notifyJoin(this);
                outgoing.add(target);
                return connection;
            }
        }

        return null;
    }

    private void notifyDisjoin(Joint target) {
        outgoing.remove(target);
    }

    public void disjoin() {
        if (incoming != null) {
            element.getDiagram().removeConnection(new Connection(this, incoming));
            notifyDisjoin(incoming);
            incoming = null;
        }
    }
}
