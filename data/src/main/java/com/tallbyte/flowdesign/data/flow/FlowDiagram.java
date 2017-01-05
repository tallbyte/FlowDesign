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

import java.beans.PropertyChangeListener;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-07)<br/>
 * <br/>
 * A {@link FlowDiagram} describes a data flow trough operations etc. .
 */
public class FlowDiagram extends Diagram<FlowDiagramElement> {

    private final Start start;
    private final End   end;

    /**
     * Creates a new {@link FlowDiagram} using name only.
     * @param name the desired name
     */
    public FlowDiagram(String name) {
        this(name, new Start(), new End());
    }

    /**
     * Creates a new {@link FlowDiagram} with a given {@link Start} as root.
     * @param name the desired name
     * @param root the desired root
     * @param end the desired end
     * @throws IllegalArgumentException Is thrown if <code>root</code> is null.
     */
    public FlowDiagram(String name, Start root, End end) {
        super(name, root);

        if (root == null) {
            throw new IllegalArgumentException("root can not be null");
        }

        this.start = root;
        this.end   = end;

        addElement(end);
    }

    @Override
    public Start getRoot() {
        return (Start) super.getRoot();
    }

    @Override
    public boolean addElement(FlowDiagramElement element) {
        if ((element instanceof Start && element != start) || (element instanceof End && element != end)) {
            return false;
        }

        return super.addElement(element);
    }

    @Override
    public void removeElement(FlowDiagramElement element) {
        if (element != start && element != end) {
            super.removeElement(element);
        }
    }

    /**
     * Gets the starting element.
     * @return Returns the start.
     */
    public Start getStart() {
        return start;
    }

    /**
     * Returns the ending element.
     * @return Returns the end.
     */
    public End getEnd() {
        return end;
    }
}
