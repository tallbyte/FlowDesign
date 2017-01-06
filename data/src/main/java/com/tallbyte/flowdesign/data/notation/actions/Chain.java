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
 * - julian (2016-12-24)<br/>
 */
public class Chain extends FlowAction {

    protected FlowAction first;
    protected FlowAction second;

    public Chain(FlowAction first, FlowAction second) {
        this(0, 0, first, second);
    }

    public Chain(int start, int end, FlowAction first, FlowAction second) {
        super(start, end);

        this.first  = first;
        this.second = second;
    }

    /**
     * Gets the first {@link FlowAction} of this {@link Chain}.
     *
     * @return Returns the action.
     */
    public FlowAction getFirst() {
        return first;
    }

    /**
     * Gets the second {@link FlowAction} of this {@link Chain}.
     *
     * @return Returns the action.
     */
    public FlowAction getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return first.toString()+"/"+second.toString();
    }
}
