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

import com.tallbyte.flowdesign.data.FlowJoint;
import com.tallbyte.flowdesign.data.JointGroup;
import com.tallbyte.flowdesign.data.JointType;

import java.util.ArrayList;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-09)<br/>
 */
public class End extends FlowDiagramElement {

    public static final String JOINT_GROUP = "in";

    /**
     * Creats an new {@link End}.
     */
    public End() {
        setText("");
    }

    @Override
    protected Iterable<JointGroup<?>> createJointGroups() {
        return new ArrayList<JointGroup<?>>() {{
            add(new JointGroup<>(End.this, JOINT_GROUP , 1, 1, element -> new FlowJoint(element, JointType.INPUT, 1, 0), 1));
        }};
    }

    public JointGroup<?> getInputGroup() {
        return getJointGroup(JOINT_GROUP);
    }

}
