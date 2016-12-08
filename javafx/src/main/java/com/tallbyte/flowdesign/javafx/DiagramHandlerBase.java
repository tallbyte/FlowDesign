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

import com.tallbyte.flowdesign.core.Diagram;
import com.tallbyte.flowdesign.core.Element;
import com.tallbyte.flowdesign.javafx.diagram.factory.DiagramImageFactory;
import com.tallbyte.flowdesign.javafx.diagram.factory.DiagramNodeFactory;
import com.tallbyte.flowdesign.javafx.diagram.factory.ElementFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-08)<br/>
 */
public abstract class DiagramHandlerBase<T extends Diagram> implements DiagramHandler<T> {

    protected final Map<String, ElementFactory>                        elementFactories = new HashMap<>();
    protected final Map<Class<? extends Element>, DiagramImageFactory> imageFactories   = new HashMap<>();
    protected final Map<Class<? extends Element>, DiagramNodeFactory>  nodeFactories    = new HashMap<>();

    protected final ObjectProperty<T> diagram = new SimpleObjectProperty<>(this, "diagram", null);

    @Override
    public void setDiagram(T diagram) {
        this.diagram.set(diagram);
    }

    @Override
    public T getDiagram() {
        return diagram.get();
    }

    @Override
    public ObjectProperty<T> diagramProperty() {
        return diagram;
    }

}
