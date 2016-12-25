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

import com.tallbyte.flowdesign.data.DataType;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public class Type extends TupelContainment{

    private DataType type;
    private String   name;
    private boolean  repeated;

    public Type(int start, int end, DataType type, String name, boolean repeated) {
        super(start, end);
        this.type     = type;
        this.name     = name;
        this.repeated = repeated;
    }

    public DataType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isRepeated() {
        return repeated;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(name);
        if (!name.isEmpty() && type != null && !type.getClassName().isEmpty()) {
            builder.append(":");
        }
        if (type != null) {
            builder.append(type.getClassName());
        }
        if (repeated) {
            builder.append("*");
        }

        return builder.toString();
    }
}
