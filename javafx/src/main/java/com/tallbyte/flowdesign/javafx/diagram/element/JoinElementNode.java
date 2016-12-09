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

import com.tallbyte.flowdesign.core.flow.Join;
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
public class JoinElementNode extends ElementNode {

    private final Join join;

    public JoinElementNode(Join element, DiagramImage content) {
        super(element, content, Pos.BOTTOM_CENTER);

        this.join = element;
    }

    @Override
    protected void setup() {
        super.setup();

        JointNode input0 = addJoint(join.getJoint(Join.JOINT_INPUT0));
        input0.centerXProperty().bind(Bindings.createDoubleBinding(() -> 0.0).add(input0.radiusProperty()));
        input0.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.1));

        JointNode input1 = addJoint(join.getJoint(Join.JOINT_INPUT1));
        input1.centerXProperty().bind(Bindings.createDoubleBinding(() -> 0.0).add(input1.radiusProperty()));
        input1.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.9));

        JointNode output = addJoint(join.getJoint(Join.JOINT_OUTPUT));
        output.centerXProperty().bind(widthProperty().subtract(output.radiusProperty()).subtract(widthExtend));
        output.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));
    }
}
