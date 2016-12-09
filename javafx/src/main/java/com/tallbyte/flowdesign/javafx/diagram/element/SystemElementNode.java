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

import com.tallbyte.flowdesign.core.environment.System;
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
public class SystemElementNode extends ElementNode {

    private final System system;

    public SystemElementNode(System element, DiagramImage content) {
        super(element, content, Pos.CENTER);

        this.system = element;
    }

    @Override
    protected void setup() {
        super.setup();

        JointNode left = addJoint(system.getJoint(System.JOINT_LEFT));
        left.centerXProperty().bind(Bindings.createDoubleBinding(() -> 0.0).add(left.radiusProperty()));
        left.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));

        JointNode right = addJoint(system.getJoint(System.JOINT_RIGHT));
        right.centerXProperty().bind(widthProperty().subtract(right.radiusProperty()).subtract(widthExtend));
        right.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));

        JointNode top = addJoint(system.getJoint(System.JOINT_TOP));
        top.centerXProperty().bind(widthProperty().subtract(widthExtend).multiply(0.5));
        top.centerYProperty().bind(Bindings.createDoubleBinding(() -> 0.0).add(top.radiusProperty()));

        JointNode bottom = addJoint(system.getJoint(System.JOINT_BOTTOM));
        bottom.centerXProperty().bind(widthProperty().subtract(widthExtend).multiply(0.5));
        bottom.centerYProperty().bind(heightProperty().subtract(bottom.radiusProperty()).subtract(heightExtend));
    }
}
