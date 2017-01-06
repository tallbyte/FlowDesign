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

import com.tallbyte.flowdesign.data.DependencyJoint;
import com.tallbyte.flowdesign.data.Joint;
import com.tallbyte.flowdesign.data.environment.Actor;
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
public class ActorElementNode extends ElementNode<DiagramImage> {

    private final Actor actor;

    public ActorElementNode(Actor element, DiagramImage content) {
        super(element, content, Pos.BOTTOM_CENTER);

        this.actor = element;
    }

    @Override
    protected void setup() {
        super.setup();

        JointNode left = addJoint(actor.getJointGroup().getJoint(0));
        left.centerXProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
        left.centerYProperty().bind(wrap.heightProperty().subtract(heightExtend).multiply(0.4));

        JointNode right = addJoint(actor.getJointGroup().getJoint(1));
        right.centerXProperty().bind(wrap.widthProperty().subtract(widthExtend));
        right.centerYProperty().bind(wrap.heightProperty().subtract(heightExtend).multiply(0.4));
    }
}
