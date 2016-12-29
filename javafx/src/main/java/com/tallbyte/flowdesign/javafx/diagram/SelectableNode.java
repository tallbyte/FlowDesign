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

import com.tallbyte.flowdesign.data.flow.FlowDiagram;
import com.tallbyte.flowdesign.javafx.Action;
import com.tallbyte.flowdesign.javafx.ShortcutGroup;
import com.tallbyte.flowdesign.javafx.Shortcuts;
import com.tallbyte.flowdesign.javafx.popup.ActionPopup;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-19)<br/>
 */
public abstract class SelectableNode extends Group {

    /**
     * Gets whether this {@link SelectableNode} is selected or not.
     * @return Returns true if it is, else false.
     */
    public abstract boolean isSelected();

    /**
     * Gets the selected property
     * @return Returns the property.
     */
    public abstract ReadOnlyBooleanProperty selectedProperty();

    /**
     * Register all shortcuts.
     * @param group the containing group
     */
    public void registerShortcuts(ShortcutGroup group) {
        group.getShortcut(Shortcuts.SHORTCUT_APPLY_ACTION).setAction(event -> {
            Bounds bounds = localToScreen(getBoundsInLocal());
            if (bounds != null) {
                List<Action> list = getCurrentActions();
                if (list.size() > 0) {
                    ActionPopup popup = new ActionPopup(list);
                    popup.show(this, bounds.getMinX()+10, bounds.getMinY()-100);
                }
            }
        });
    }

    /**
     * Gets a list of {@link Action}s currently available for the element.
     * @return Returns the list.
     */
    public List<Action> getCurrentActions() {
        return new ArrayList<>();
    }

    /**
     * Gets the modifiable properties
     * @return Returns a list of such properties.
     */
    public abstract ObservableList<Property<?>> getNodeProperties();


}
