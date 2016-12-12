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

package com.tallbyte.flowdesign.data.environment;

import com.tallbyte.flowdesign.data.DependencyJoint;
import com.tallbyte.flowdesign.data.JointType;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-07)<br/>
 * <br/>
 * A {@link Resource} describes a software-system in terms of diagram-element.
 */
public class Resource extends EnvironmentDiagramElement {

    public static final String JOINT_LEFT   = "left";
    public static final String JOINT_RIGHT  = "right";

    public static final String JOINT_TOP    = "top";
    public static final String JOINT_BOTTOM = "bottom";

    /**
     * Create a new {@link Resource}.
     */
    public Resource() {
        addJoint(new DependencyJoint(this, JOINT_LEFT  , JointType.INPUT_OUTPUT, 0));
        addJoint(new DependencyJoint(this, JOINT_RIGHT , JointType.INPUT_OUTPUT, 0));

        addJoint(new DependencyJoint(this, JOINT_TOP   , JointType.INPUT_OUTPUT, 0));
        addJoint(new DependencyJoint(this, JOINT_BOTTOM, JointType.INPUT_OUTPUT, 0));
    }

}
