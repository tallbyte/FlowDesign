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
public class MultiStream extends FlowAction {

    private int min;
    private int max;
    private FlowAction action;

    public MultiStream(int min, int max, FlowAction action) {
        this(0, 0, min, max, action);
    }

    public MultiStream(int start, int end, int min, int max, FlowAction action) {
        super(start, end);
        this.min = min;
        this.max = max;
        this.action = action;
    }

    /**
     * Gets the minimum number of occurrences.
     *
     * @return Returns the number.
     */
    public int getMin() {
        return min;
    }

    /**
     * Gets the maximum number of occurrences.
     *
     * @return Returns the number.
     */
    public int getMax() {
        return max;
    }

    /**
     * Gets the enclosed {@link FlowAction}.
     *
     * @return Returns the action.
     */
    public FlowAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("{");
        if (action != null) {
            builder.append(action.toString());
        }
        builder.append("}");

        return builder.toString();
    }

    @Override
    public FlowAction getChildAt(int location) {
        if (location >= start && location <= end+1) {
            FlowAction child  = this.action.getChildAt(location);

            if (child != null) {
                return child;
            }

            return this;
        }

        return null;
    }
}
