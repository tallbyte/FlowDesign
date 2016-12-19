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

package com.tallbyte.flowdesign.javafx.property;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-19)<br/>
 */
public class ColorProperty extends SimpleObjectProperty<Color> {

    public ColorProperty() {
    }

    public ColorProperty(Color initialValue) {
        super(initialValue);
    }

    public ColorProperty(Object bean, String name) {
        super(bean, name);
    }

    public ColorProperty(Object bean, String name, Color initialValue) {
        super(bean, name, initialValue);
    }
}
