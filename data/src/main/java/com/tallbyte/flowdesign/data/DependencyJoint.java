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

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-12)<br/>
 */
public class DependencyJoint extends Joint {
    /**
     * Creates a new {@link Joint} based on given configuration.
     *
     * @param element  the containing {@link Element}
     * @param type     the type
     * @param maxIn   the maximum amount of incoming
     *                 connections or 0 for infinite
     * @param maxOut   the maximum amount of outgoing
     *                 connections or 0 for infinite
     */
    public DependencyJoint(Element element, JointType type, int maxIn, int maxOut) {
        super(element, type, maxIn, maxOut);
    }

    @Override
    public boolean canJoin(Joint target) {
        return target instanceof DependencyJoint && super.canJoin(target);
    }

    @Override
    protected Connection createConnection(Joint target) {
        return new DependencyConnection(this, target);
    }
}
