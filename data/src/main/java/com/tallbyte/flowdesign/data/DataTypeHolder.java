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

import java.util.*;
import java.util.stream.Collectors;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-16)<br/>
 */
public class DataTypeHolder {

    protected final List<DataType>                 types     = new ArrayList<>();
    protected final List<DataTypesChangedListener> listeners = new ArrayList<>();

    public DataTypeHolder() {
        addDataType("String");
        addDataType("Object");
        addDataType("Integer");
        addDataType("Double");
        addDataType("Integer");
        addDataType("Float");
        addDataType("Long");
        addDataType("Byte");
        addDataType("Character");
        addDataType("int");
        addDataType("byte");
        addDataType("char");
        addDataType("double");
        addDataType("float");
        addDataType("long");
        addDataType("StringBuilder");
        addDataType("StringBuffer");
        addDataType("HashMap");
        addDataType("List");
        addDataType("Set");
    }

    public void addDataType(String name) {
        addDataType(new DataType(name));
    }

    public boolean addDataType(DataType type) {
        if (!types.contains(type)) {
            types.add(type);

            for (DataTypesChangedListener listener : listeners) {
                listener.onDataTypesChanged(type, true);
            }

            return true;
        }

        return false;
    }

    public void clear() {
        clear(false);
    }

    public void clear(boolean clearListeners) {
        types.clear();

        if (clearListeners) {
            listeners.clear();
        }
    }

    public void removeDataType(DataType type) {
        types.remove(type);

        for (DataTypesChangedListener listener : listeners) {
            listener.onDataTypesChanged(type, false);
        }
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

    /**
     * Registers an {@link DataTypesChangedListener}.
     * @param listener the {@link DataTypesChangedListener} to register
     */
    public void addDataTypesChangedListener(DataTypesChangedListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregisters an {@link DataTypesChangedListener}.
     * @param listener the {@link DataTypesChangedListener} to unregister
     */
    public void removeDataTypesChangedListener(DataTypesChangedListener listener) {
        listeners.remove(listener);
    }

}
