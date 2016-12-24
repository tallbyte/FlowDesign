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

package com.tallbyte.flowdesign.core.notation.actions;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public class MultiStream implements FlowAction {

    private int min;
    private int max;
    private FlowAction action;

    public MultiStream(int min, int max, FlowAction action) {
        this.min = min;
        this.max = max;
        this.action = action;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

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
}
