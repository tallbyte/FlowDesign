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

package com.tallbyte.flowdesign.javafx.diagram.element;

import com.tallbyte.flowdesign.data.FlowJoint;
import com.tallbyte.flowdesign.data.flow.Operation;
import com.tallbyte.flowdesign.data.flow.Split;
import com.tallbyte.flowdesign.javafx.diagram.ElementNode;
import com.tallbyte.flowdesign.javafx.diagram.JointNode;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-08)<br/>
 */
public class SplitElementNode extends ElementNode {

    private final Split split;

    public SplitElementNode(Split element, DiagramImage content) {
        super(element, content, Pos.BOTTOM_CENTER);

        this.split = element;
    }

    @Override
    protected void setup() {
        super.setup();

        addJointsAcrossRectangleCentered(new JointGroup(split, FlowJoint.class, true, false, 0.5, 0.8), false, 0.0);
        addJointsAcrossRectangleCentered(new JointGroup(split, FlowJoint.class, false, true, 0.5, 0.8), false, 1.0);

        /*JointNode input = addJoint(split.getJoint(Split.JOINT_INPUT));
        input.centerXProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
        input.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));

        JointNode output = addJoint(split.getJoint(Split.JOINT_OUTPUT));
        output.centerXProperty().bind(widthProperty().subtract(widthExtend));
        output.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));*/
    }
}
