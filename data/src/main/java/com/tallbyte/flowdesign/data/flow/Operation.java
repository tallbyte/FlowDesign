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

package com.tallbyte.flowdesign.data.flow;

import com.tallbyte.flowdesign.data.DependencyJoint;
import com.tallbyte.flowdesign.data.FlowJoint;
import com.tallbyte.flowdesign.data.JointType;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-09)<br/>
 */
public class Operation extends FlowDiagramElement {

    public static final String JOINT_INPUT0     = "input0";
    public static final String JOINT_OUTPUT1    = "output0";
    public static final String JOINT_DEPENDENCY = "dependency";

    /**
     * Creats an new {@link Operation}.
     */
    public Operation() {
        addJoint(new FlowJoint(this, JOINT_INPUT0    , JointType.INPUT, 1));
        addJoint(new FlowJoint(this, JOINT_OUTPUT1   , JointType.OUTPUT, 1));
        addJoint(new DependencyJoint(this, JOINT_DEPENDENCY, JointType.OUTPUT, 0));
    }

}
