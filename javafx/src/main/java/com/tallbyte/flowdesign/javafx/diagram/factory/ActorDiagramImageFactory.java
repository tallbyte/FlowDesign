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

package com.tallbyte.flowdesign.javafx.diagram.factory;

import com.tallbyte.flowdesign.core.Element;
import com.tallbyte.flowdesign.core.environment.Actor;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import com.tallbyte.flowdesign.javafx.diagram.image.StickmanDiagramImage;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-04)<br/>
 */
public class ActorDiagramImageFactory implements DiagramImageFactory {

    @Override
    public String getName() {
        return "Actor";
    }

    @Override
    public Class<? extends Element> getTargetClass() {
        return Actor.class;
    }

    @Override
    public DiagramImage createDiagramImage() {
        return new StickmanDiagramImage();
    }

}
