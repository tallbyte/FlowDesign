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

package com.tallbyte.flowdesign.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-21)<br/>
 */
public class Shortcut {

    protected final String                                    name;
    protected final String                                    keyCombo;
    protected final ObjectProperty<EventHandler<ActionEvent>> action = new SimpleObjectProperty<>(this, "action", null);

    public Shortcut(String name, String keyCombo) {
        this.name     = name;
        this.keyCombo = keyCombo;
    }

    public String getName() {
        return name;
    }

    public String getKeyCombo() {
        return keyCombo;
    }

    public void setAction(EventHandler<ActionEvent> action) {
        this.action.set(action);
    }

    public EventHandler<ActionEvent> getAction() {
        return action.get();
    }

    public ObjectProperty<EventHandler<ActionEvent>> actionProperty() {
        return action;
    }
}
