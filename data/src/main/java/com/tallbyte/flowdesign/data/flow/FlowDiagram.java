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

package com.tallbyte.flowdesign.data.flow;

import com.tallbyte.flowdesign.data.Diagram;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-07)<br/>
 * <br/>
 * A {@link FlowDiagram} describes a data flow trough operations etc. .
 */
public class FlowDiagram extends Diagram<FlowDiagramElement> {

    /**
     * Creates a new {@link FlowDiagram} using name only.
     * @param name the desired name
     */
    public FlowDiagram(String name) {
        super(name, new Start());
    }

    /**
     * Creates a new {@link FlowDiagram} with a given {@link Start} as root.
     * @param name the desired name
     * @param root the desired root
     */
    public FlowDiagram(String name, Start root) {
        super(name, root);
    }

    @Override
    public Start getRoot() {
        return (Start) super.getRoot();
    }
}
