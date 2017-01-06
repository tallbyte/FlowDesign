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

package com.tallbyte.flowdesign.javafx.diagram;

import com.tallbyte.flowdesign.data.Element;
import com.tallbyte.flowdesign.data.flow.*;
import com.tallbyte.flowdesign.javafx.diagram.element.*;
import com.tallbyte.flowdesign.javafx.diagram.factory.DiagramImageFactory;
import com.tallbyte.flowdesign.javafx.diagram.image.*;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-08)<br/>
 */
public class FlowDiagramHandler extends DiagramHandlerBase<FlowDiagram, FlowDiagramElement, DiagramImage> {

    public FlowDiagramHandler() {
        addEntries("Start", Start.class,
                Start::new,
                StartDiagramImage::new,
                StartElementNode::new,
                false
        );
        addEntries("End", End.class,
                End::new,
                EndDiagramImage::new,
                EndElementNode::new,
                false
        );
        addEntries("Join", Join.class,
                Join::new,
                VerticalStickDiagramImage::new,
                JoinElementNode::new
        );
        addEntries("Operation", Operation.class,
                Operation::new,
                OperationalUnitDiagramImage::new,
                OperationalUnitElementNode::new
        );
        addEntries("Split", Split.class,
                Split::new,
                SplitDiagramImage::new,
                SplitElementNode::new
        );
        addEntries("Portal", Portal.class,
                Portal::new,
                PortalDiagramImage::new,
                PortalElementNode::new
        );
        addEntries("Comment", FlowComment.class,
                FlowComment::new,
                CommentDiagramImage::new,
                FlowCommentElementNode::new
        );
    }

    @Override
    protected FlowDiagram createNewDiagramInstance(String name) {
        return new FlowDiagram(name);
    }

    @Override
    public FlowDiagram createDiagram(String name) {
        FlowDiagram diagram = super.createDiagram(name);

        setToPrefSize(diagram.getEnd());

        diagram.getEnd().setX(300);

        return diagram;
    }
}
