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
import com.tallbyte.flowdesign.core.Joint;
import com.tallbyte.flowdesign.core.JointType;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-07)<br/>
 */
public class Actor extends EnvironmentDiagramElement {

    public static final String JOINT_LEFT  = "left";
    public static final String JOINT_RIGHT = "right";

    public Actor() {
        addJoint(new Joint(this, JOINT_LEFT , JointType.DEPENDENCY, 0));
        addJoint(new Joint(this, JOINT_RIGHT, JointType.DEPENDENCY, 0));
    }

}
