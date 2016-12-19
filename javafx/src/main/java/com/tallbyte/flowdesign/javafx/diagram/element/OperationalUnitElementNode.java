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

import com.tallbyte.flowdesign.data.flow.Operation;
import com.tallbyte.flowdesign.data.flow.OperationalUnit;
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
public class OperationalUnitElementNode extends ElementNode {

    private final OperationalUnit operation;

    public OperationalUnitElementNode(OperationalUnit element, DiagramImage content) {
        super(element, content, Pos.CENTER);

        this.operation = element;
    }

    @Override
    protected void setup() {
        super.setup();

        JointNode input0 = addJoint(operation.getJoint(Operation.JOINT_INPUT0));
        input0.centerXProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
        input0.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));

        JointNode output0 = addJoint(operation.getJoint(Operation.JOINT_OUTPUT0));
        output0.centerXProperty().bind(widthProperty().subtract(widthExtend));
        output0.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));

        JointNode dependencyInput = addJoint(operation.getJoint(Operation.JOINT_DEPENDENCY_INPUT));
        dependencyInput.centerXProperty().bind(widthProperty().subtract(widthExtend).multiply(0.5));
        dependencyInput.centerYProperty().bind(Bindings.createDoubleBinding(() -> 0.0));

        JointNode dependencyOutput = addJoint(operation.getJoint(Operation.JOINT_DEPENDENCY_OUTPUT));
        dependencyOutput.centerXProperty().bind(widthProperty().subtract(widthExtend).multiply(0.5));
        dependencyOutput.centerYProperty().bind(heightProperty().subtract(heightExtend));
    }
}
