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

import com.tallbyte.flowdesign.data.DataType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-16)<br/>
 */
public class DataTypeHolder {

    private final List<DataType> types = new ArrayList<>();

    public DataTypeHolder() {
        addDataType("String");
        addDataType("StringUtils");
        addDataType("Object");
        addDataType("Car");
    }

    public void addDataType(String name) {
        addDataType(new DataType(name));
    }

    public void addDataType(DataType type) {
        types.add(type);
    }

    public void removeDataType(DataType type) {
        types.remove(type);
    }

    public Iterable<DataType> getDataTypes() {
        return Collections.unmodifiableList(types);
    }

    public Iterable<DataType> getDataTypes(String prefix) {
        if (prefix == null) {
            return new ArrayList<>();
        }

        return types.stream().filter(
                type -> type.getClassName().toLowerCase().startsWith(prefix.toLowerCase())
        ).collect(Collectors.toList());

    }

}
