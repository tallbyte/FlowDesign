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

import com.tallbyte.flowdesign.data.environment.*;
import com.tallbyte.flowdesign.data.environment.System;
import com.tallbyte.flowdesign.data.mask.MaskComment;
import com.tallbyte.flowdesign.javafx.diagram.element.*;
import com.tallbyte.flowdesign.javafx.diagram.factory.*;
import com.tallbyte.flowdesign.javafx.diagram.image.*;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-08)<br/>
 */
public class EnvironmentDiagramHandler extends DiagramHandlerBase<EnvironmentDiagram, EnvironmentDiagramElement, DiagramImage> {

    public EnvironmentDiagramHandler() {
        addEntries("System", System.class,
                System::new,
                EllipseDiagramImage::new,
                SystemElementNode::new
        );
        addEntries("Actor", Actor.class,
                Actor::new,
                StickmanDiagramImage::new,
                ActorElementNode::new
        );
        addEntries("Resource", Resource.class,
                Resource::new,
                SystemResourceDiagramImage::new,
                ResourceElementNode::new
        );
        addEntries("Adapter", Adapter.class,
                Adapter::new,
                AdapterDiagramImage::new,
                AdapterElementNode::new
        );
        addEntries("EnvironmentComment", EnvironmentComment.class,
                EnvironmentComment::new,
                CommentDiagramImage::new,
                EnvironmentCommentElementNode::new
        );
    }

    @Override
    protected EnvironmentDiagram createNewDiagramInstance(String name) {
        return new EnvironmentDiagram(name);
    }
}
