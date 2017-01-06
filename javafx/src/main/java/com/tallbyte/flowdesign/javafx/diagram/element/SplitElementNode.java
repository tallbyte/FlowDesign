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
import com.tallbyte.flowdesign.data.Element;
import com.tallbyte.flowdesign.data.FlowJoint;
import com.tallbyte.flowdesign.data.flow.Split;
import com.tallbyte.flowdesign.javafx.FlowDesignFxApplication;
import com.tallbyte.flowdesign.javafx.ShortcutGroup;
import com.tallbyte.flowdesign.javafx.Shortcuts;
import com.tallbyte.flowdesign.javafx.diagram.ElementNode;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-08)<br/>
 */
public class SplitElementNode extends ElementNode<DiagramImage> {

    private final Split split;

    public SplitElementNode(Split element, DiagramImage content) {
        super(element, content, Pos.BOTTOM_CENTER);

        this.split = element;
    }

    @Override
    protected void setup() {
        super.setup();

        addJointsAcrossRectangleCentered(new JointGroupHandler(split.getInputGroup(), 0.5, 0.8), false, 0.0);
        addJointsAcrossRectangleCentered(new JointGroupHandler(split.getOutputGroup(), 0.5, 0.8), false, 1.0);

        /*JointNode input = addJoint(split.getJoint(Split.JOINT_INPUT));
        input.centerXProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
        input.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));

        JointNode output = addJoint(split.getJoint(Split.JOINT_OUTPUT));
        output.centerXProperty().bind(widthProperty().subtract(widthExtend));
        output.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));*/
    }

    @Override
    public void registerShortcuts(ShortcutGroup group) {
        super.registerShortcuts(group);

        group.getShortcut(Shortcuts.SHORTCUT_MOVE_LEFT).setAction(event -> {
            for (Connection c : split.getInputGroup().getJoint(0).getIncoming()) {
                diagramPane.requestSelection(c);
            }
            event.consume();
        });

        group.getShortcut(Shortcuts.SHORTCUT_MOVE_RIGHT).setAction(event -> {
            List<Connection> list = split.getOutputGroup().getJoint(0)
                    .getOutgoing().stream().collect(Collectors.toList());

            if (list.size() > 0) {
                list.sort((o1, o2) -> (int) (o1.getTarget().getElement().getY() - o2.getTarget().getElement().getY()));

                diagramPane.requestSelection(list.get(Math.min(list.size()-1, 1)));
            }
            event.consume();
        });

        group.getShortcut(Shortcuts.SHORTCUT_MOVE_UP).setAction(event -> {
            List<Connection> list = split.getOutputGroup().getJoint(0)
                    .getOutgoing().stream().collect(Collectors.toList());

            if (list.size() > 0) {
                list.sort((o1, o2) -> (int) (o1.getTarget().getElement().getY() - o2.getTarget().getElement().getY()));

                diagramPane.requestSelection(list.get(0));
            }
            event.consume();
        });

        group.getShortcut(Shortcuts.SHORTCUT_MOVE_DOWN).setAction(event -> {
            List<Connection> list = split.getOutputGroup().getJoint(0)
                    .getOutgoing().stream().collect(Collectors.toList());

            if (list.size() > 0) {
                list.sort((o1, o2) -> (int) (o2.getTarget().getElement().getY() - o1.getTarget().getElement().getY()));

                diagramPane.requestSelection(list.get(0));
            }
            event.consume();
        });
    }
}
