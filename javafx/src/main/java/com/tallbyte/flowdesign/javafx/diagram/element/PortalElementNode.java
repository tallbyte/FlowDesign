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

import com.tallbyte.flowdesign.data.Connection;
import com.tallbyte.flowdesign.data.FlowJoint;
import com.tallbyte.flowdesign.data.flow.Portal;
import com.tallbyte.flowdesign.javafx.FlowDesignFxApplication;
import com.tallbyte.flowdesign.javafx.ShortcutGroup;
import com.tallbyte.flowdesign.javafx.Shortcuts;
import com.tallbyte.flowdesign.javafx.diagram.ElementNode;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import javafx.geometry.Pos;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-08)<br/>
 */
public class PortalElementNode extends ElementNode<DiagramImage> {

    private final Portal portal;

    public PortalElementNode(Portal element, DiagramImage content) {
        super(element, content, Pos.CENTER);

        this.portal = element;
    }

    @Override
    protected void setup() {
        super.setup();

        addJointsAcrossRectangleCentered(new JointGroupHandler(portal.getInputGroup(), 0.5, 0.8), false, 0.0);
        addJointsAcrossRectangleCentered(new JointGroupHandler(portal.getOutputGroup(), 0.5, 0.8), false, 1.0);

        /*JointNode input = addJoint(portal.getJoint(Portal.JOINT_INPUT));
        input.centerXProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
        input.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));

        JointNode output = addJoint(portal.getJoint(Portal.JOINT_OUTPUT));
        output.centerXProperty().bind(widthProperty().subtract(widthExtend));
        output.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));*/
    }

    @Override
    public void registerShortcuts(ShortcutGroup group) {
        super.registerShortcuts(group);

        group.getShortcut(Shortcuts.SHORTCUT_MOVE_LEFT).setAction(event -> {
            for (Connection c : portal.getInputGroup().getJoint(0).getIncoming()) {
                diagramPane.requestSelection(c);
            }
        });

        group.getShortcut(Shortcuts.SHORTCUT_MOVE_RIGHT).setAction(event -> {
            for (Connection c : portal.getOutputGroup().getJoint(0).getOutgoing()) {
                diagramPane.requestSelection(c);
            }
        });
    }
}
