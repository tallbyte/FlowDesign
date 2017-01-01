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

import com.tallbyte.flowdesign.data.Diagram;
import com.tallbyte.flowdesign.data.Element;
import com.tallbyte.flowdesign.data.environment.EnvironmentDiagram;
import com.tallbyte.flowdesign.data.flow.FlowDiagram;
import com.tallbyte.flowdesign.data.mask.MaskDiagram;
import com.tallbyte.flowdesign.javafx.diagram.factory.DiagramImageFactory;

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
        addHandler(FlowDiagram.class       , new FlowDiagramHandler()       );
        addHandler(MaskDiagram.class       , new MaskDiagramHandler()       );
    }

    public static <T extends Diagram> void addHandler(Class<T> clazz, DiagramHandler<T> handler) {
        HANDLERS.put(clazz, handler);
    }

    @SuppressWarnings("unchecked") // this is safe, as we only allow altering of the handler map trough addHandler()
    private <T extends Diagram> DiagramHandler<T> getHandler(T diagram) {
        return (DiagramHandler<T>) HANDLERS.get(diagram.getClass());
    }

    @SuppressWarnings("unchecked") // this is safe, as we only allow altering of the handler map trough addHandler()
    private <T extends Diagram> DiagramHandler<T> getHandler(Class<T> clazz) {
        return (DiagramHandler<T>) HANDLERS.get(clazz);
    }

    public <T extends Diagram> boolean isSupporting(T diagram) {
        return getHandler(diagram) != null;
    }

    public <T extends Diagram> Element createElement(T diagram, String element, double x, double y) {
        DiagramHandler<T> handler = getHandler(diagram);

        if (handler != null) {
            return handler.createElement(diagram, element, x, y);
        } else {
            throw new IllegalStateException("unsupported diagram type");
        }
    }

    public <T extends Diagram> void removeElement(T diagram, Element element) {
        DiagramHandler<T> handler = getHandler(diagram);

        if (handler != null) {
            handler.removeElement(diagram, element);
        } else {
            throw new IllegalStateException("unsupported diagram type");
        }
    }

    public ElementNode createNode(Diagram diagram, Element element) {
        DiagramHandler<?> handler = getHandler(diagram);

        if (handler != null) {
            return handler.createNode(element);
        } else {
            throw new IllegalStateException("unsupported diagram type");
        }
    }

    public Map<String, DiagramImageFactory> getSupportedElements(Diagram diagram) {
        DiagramHandler<?> handler = getHandler(diagram);

        if (handler != null) {
            return handler.getSupportedElements();
        } else {
            throw new IllegalStateException("unsupported diagram type");
        }
    }

    public Diagram createDiagram(String name, Class<? extends Diagram> clazz) {
        DiagramHandler<?> handler = getHandler(clazz);

        if (handler != null) {
            return handler.createDiagram(name);
        } else {
            throw new IllegalStateException("unsupported diagram type");
        }
    }

    public Iterable<Class<? extends Diagram>> getSupportedDiagramTypes() {
        return HANDLERS.keySet();
    }
}
