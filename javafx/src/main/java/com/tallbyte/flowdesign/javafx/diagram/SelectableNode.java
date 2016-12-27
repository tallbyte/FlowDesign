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

package com.tallbyte.flowdesign.javafx.diagram;

import com.tallbyte.flowdesign.javafx.ShortcutGroup;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-19)<br/>
 */
public interface SelectableNode {

    /**
     * Gets whether this {@link SelectableNode} is selected or not.
     * @return Returns true if it is, else false.
     */
    boolean isSelected();

    /**
     * Gets the selected property
     * @return Returns the property.
     */
    ReadOnlyBooleanProperty selectedProperty();

    /**
     * Register all shortcuts.
     * @param group the containing group
     */
    default void registerShortcuts(ShortcutGroup group) {
        // do nothing
    }

    /**
     * Gets the modifiable properties
     * @return Returns a list of such properties.
     */
    ObservableList<Property<?>> getNodeProperties();


}
