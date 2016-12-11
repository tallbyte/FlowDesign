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

import com.tallbyte.flowdesign.data.Joint;
import com.tallbyte.flowdesign.data.JointType;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-09)<br/>
 */
public class Join extends FlowDiagramElement {

    public static final String JOINT_INPUT0 = "input0";
    public static final String JOINT_INPUT1 = "input1";
    public static final String JOINT_OUTPUT = "output";

    /**
     * Creats an new {@link Join}.
     */
    public Join() {
        addJoint(new Joint(this, JOINT_INPUT0, JointType.INPUT, 1));
        addJoint(new Joint(this, JOINT_INPUT1, JointType.INPUT, 1));
        addJoint(new Joint(this, JOINT_OUTPUT, JointType.OUTPUT, 0));
    }

}