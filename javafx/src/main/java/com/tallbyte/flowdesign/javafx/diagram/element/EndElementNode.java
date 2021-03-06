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
import com.tallbyte.flowdesign.data.flow.End;
import com.tallbyte.flowdesign.data.flow.FlowDiagram;
import com.tallbyte.flowdesign.javafx.ShortcutGroup;
import com.tallbyte.flowdesign.javafx.Shortcuts;
import com.tallbyte.flowdesign.javafx.diagram.ElementNode;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import com.tallbyte.flowdesign.javafx.diagram.image.EndDiagramImage;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.geometry.Pos;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-08)<br/>
 */
public class EndElementNode extends ElementNode<DiagramImage> {

    private final End end;

    public EndElementNode(End element, EndDiagramImage content) {
        super(element, content, Pos.BOTTOM_CENTER);

        this.end = element;

        try {
            content.uiProperty().bind(JavaBeanBooleanPropertyBuilder.create().bean(element.getDiagram()).name(FlowDiagram.PROPERTY_UI).build());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not create properties. This should never happen?!", e);
        }
    }

    @Override
    protected void setup() {
        super.setup();

        addJointsAcrossCircleCentered(new JointGroupHandler(end.getInputGroup(), 0.5, 0.0));

        /*JointNode input = addJoint(end.getJoint(End.JOINT_INPUT));
        input.centerXProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
        input.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));*/
    }

    @Override
    public void registerShortcuts(ShortcutGroup group) {
        super.registerShortcuts(group);

        group.getShortcut(Shortcuts.SHORTCUT_MOVE_LEFT).setAction(event -> {
            for (Connection c : end.getInputGroup().getJoint(0).getIncoming()) {
                diagramPane.requestSelection(c);
            }
            event.consume();
        });
    }
}
