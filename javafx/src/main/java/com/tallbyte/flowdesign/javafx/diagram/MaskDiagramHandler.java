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

import com.tallbyte.flowdesign.data.flow.*;
import com.tallbyte.flowdesign.data.mask.*;
import com.tallbyte.flowdesign.javafx.diagram.element.*;
import com.tallbyte.flowdesign.javafx.diagram.image.*;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-08)<br/>
 */
public class MaskDiagramHandler extends DiagramHandlerBase<MaskDiagram, MaskDiagramElement> {

    public MaskDiagramHandler() {
        addEntries("Rectangle", Rectangle.class,
                Rectangle::new,
                RectangleDiagramImage::new,
                RectangleElementNode::new
        );
        addEntries("SelfReference", SelfReference.class,
                SelfReference::new,
                SelfReferenceDiagramImage::new,
                SelfReferenceElementNode::new
        );
        addEntries("MaskComment", MaskComment.class,
                MaskComment::new,
                RectangleDiagramImage::new,
                MaskCommentElementNode::new
        );
    }

    @Override
    protected MaskDiagram createNewDiagramInstance(String name) {
        return new MaskDiagram(name);
    }
}
