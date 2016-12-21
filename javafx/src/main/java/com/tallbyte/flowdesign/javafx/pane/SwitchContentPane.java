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

package com.tallbyte.flowdesign.javafx.pane;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.BorderPane;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-11)<br/>
 */
public abstract class SwitchContentPane extends BorderPane {

    private BooleanProperty ok = new SimpleBooleanProperty(this, "ok", false);

    public void onOk() {

    }

    public void onOpen(SwitchPane pane) {

    }

    public void onOpen() {

    }

    public String getTitle() {
        return "";
    }

    public abstract boolean hasOk();

    public abstract boolean hasCancel();

    public boolean getOk() {
        return ok.get();
    }

    public void setOk(boolean ok) {
        this.ok.set(ok);
    }

    public BooleanProperty okProperty() {
        return ok;
    }
}
