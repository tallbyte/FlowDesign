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

package com.tallbyte.flowdesign.data;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-12)<br/>
 */
public class FlowConnection extends Connection<FlowJoint> {
    /**
     * Creates a new {@link Connection} between two {@link Joint}s.
     *
     * @param source the source {@link Joint}
     * @param target the target {@link Joint}
     */
    public FlowConnection(FlowJoint source, FlowJoint target) {
        super(source, target);
    }

    @Override
    public void setText(String text) {
        super.setText(text);

        source.setDataType(text);
        target.setDataType(text);
    }
}
