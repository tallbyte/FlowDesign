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

import java.util.HashMap;
import java.util.Map;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-27)<br/>
 */
public class ShortcutGroup {

    private final String                name;
    private final Map<String, Shortcut> shortcuts = new HashMap<>();

    public ShortcutGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Shortcut getShortcut(String name) {
        return shortcuts.get(name);
    }

    public void addShortcut(Shortcut shortcut) {
        this.shortcuts.putIfAbsent(shortcut.getName(), shortcut);
    }

    public Iterable<Shortcut> getShortcuts() {
        return shortcuts.values();
    }
}
