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

package com.tallbyte.flowdesign.data.notation.actions;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public abstract class FlowAction {

    protected int start;
    protected int end;

    public FlowAction(int start, int end) {
        this.start = start;
        this.end   = end;
    }

    /**
     * Gets the starting index in the original string.
     *
     * @return Returns the index.
     */
    public int getStart() {
        return start;
    }

    /**
     * Gets the ending index in the original string.
     *
     * @return Returns the index.
     */
    public int getEnd() {
        return end;
    }

    /**
     * Gets an child at the specified location of the origin string.
     *
     * @param location the location in the original string
     * @return Returns the {@link FlowAction} or null if none was found.
     */
    public abstract FlowAction getChildAt(int location);
}
