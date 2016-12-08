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
import com.tallbyte.flowdesign.core.EnvironmentDiagram;
import com.tallbyte.flowdesign.javafx.diagram.DiagramNode;
import com.tallbyte.flowdesign.javafx.diagram.DiagramPane;
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
public class DiagramManager {

    private final static Map<Class<? extends Diagram>, DiagramHandler<? extends Diagram>> HANDLERS = new HashMap<>();

    static {
        addHandler(EnvironmentDiagram.class, new EnvironmentDiagramHandler());
    }

    private final ObjectProperty<Diagram<?>> diagram = new SimpleObjectProperty<>(this, "diagram", null);
    private       DiagramHandler<?>       handler = null;

    public DiagramManager() {
        diagramProperty().addListener((observable, oldValue, newValue) -> {
            setHandler(newValue);
        });
    }

    public static <T extends Diagram> void addHandler(Class<T> clazz, DiagramHandler<T> handler) {
        HANDLERS.put(clazz, handler);
    }

    @SuppressWarnings("unchecked") // this is safe, as we only allow altering of the handler map trough addHandler()
    private <T extends Diagram> void setHandler(Diagram diagram) {
        DiagramHandler<T> handler = (DiagramHandler<T>) HANDLERS.get(diagram.getClass());
        handler.setDiagram((T) diagram);

        this.handler = handler;
    }

    public void setDiagram(Diagram<?> diagram) {
        this.diagram.set(diagram);
    }

    public Diagram<?> getDiagram() {
        return diagram.get();
    }

    public ObjectProperty<Diagram<?>> diagramProperty() {
        return diagram;
    }

    public void createElement(String element, double x, double y) {
        if (handler != null) {
            handler.createElement(element, x, y);
        } else {
            throw new IllegalStateException("unsupported diagram type");
        }
    }

    public DiagramNode createNode(DiagramPane pane, Element element) {
        if (handler != null) {
            return handler.createNode(pane, element);
        } else {
            throw new IllegalStateException("unsupported diagram type");
        }
    }
}
