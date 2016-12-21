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

import com.tallbyte.flowdesign.javafx.pane.DiagramMenu;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-21)<br/>
 */
public class DiagramShortcutManager {

    private final Shortcut goToReference;
    private final Shortcut openSuggestions;
    private final Shortcut addFlow;
    private final Shortcut addDependency;

    public DiagramShortcutManager(DiagramMenu menu) {
        goToReference   = new Shortcut(menu.getItemGoToReference());
        openSuggestions = new Shortcut(menu.getItemOpenSuggestions());
        addFlow         = new Shortcut(menu.getItemAddFlow());
        addDependency   = new Shortcut(menu.getItemAddDependency());
    }

    public void reset() {
        goToReference.setActionEvent(null);
        openSuggestions.setActionEvent(null);
        addFlow.setActionEvent(null);
        addDependency.setActionEvent(null);
    }

    public Shortcut getGoToReference() {
        return goToReference;
    }

    public Shortcut getOpenSuggestions() {
        return openSuggestions;
    }

    public Shortcut getAddFlow() {
        return addFlow;
    }

    public Shortcut getAddDependency() {
        return addDependency;
    }
}
