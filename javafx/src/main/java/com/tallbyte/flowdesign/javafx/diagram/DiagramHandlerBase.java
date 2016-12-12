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
import com.tallbyte.flowdesign.javafx.diagram.factory.*;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-08)<br/>
 */
public abstract class DiagramHandlerBase<T extends Diagram<S>, S extends Element> implements DiagramHandler<T> {

    protected final Map<String, ElementFactory<? extends S>>                       elementFactories = new HashMap<>();
    protected final Map<Class<? extends Element>, DiagramImageFactory>             imageFactories   = new HashMap<>();
    protected final Map<Class<? extends Element>, ElementNodeFactory<? extends S>> nodeFactories    = new HashMap<>();
    protected final Map<String, DiagramImageFactory>                               supportedElements= new HashMap<>();

    protected <E extends S> void addEntries(String string, Class<E> clazz,
                                            ElementFactory<E> elementFactory,
                                            DiagramImageFactory imageFactory,
                                            ElementNodeFactory<E> nodeFactory) {

        elementFactories.put(string, elementFactory);
        imageFactories.put(clazz, imageFactory);
        nodeFactories.put(clazz, nodeFactory);
        supportedElements.put(string, imageFactory);
    }

    @Override
    public void createElement(T diagram, String element, double x, double y) {
        ElementFactory<? extends S> factory = elementFactories.get(element);

        if (factory != null) {
            S e = factory.createElement();
            // set location
            e.setX(x);
            e.setY(y);

            // set dimension
            DiagramImageFactory imageFactory = imageFactories.get(e.getClass());
            if (imageFactory != null) {
                DiagramImage image = imageFactory.createDiagramImage();
                e.setWidth(image.getWidth());
                e.setHeight(image.getHeight());
            } else {
                e.setWidth(75);
                e.setHeight(75);
            }

            diagram.addElement(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked") // should be safe because of addEntries()
    public ElementNode createNode(Element element) {
        DiagramImageFactory    imageFactory = imageFactories.get(element.getClass());
        ElementNodeFactory<S> nodeFactory  = (ElementNodeFactory<S>) nodeFactories.get(element.getClass());

        if (imageFactory != null && nodeFactory != null) {
            /*
             * The following cast should be safe as well, as unsupported elements would
             * result in a null-value for nodeFactory
             */
            return nodeFactory.createDiagramNode((S) element, imageFactory.createDiagramImage());
        }

        return null;
    }

    @Override
    public Map<String, DiagramImageFactory> getSupportedElements() {
        return Collections.unmodifiableMap(supportedElements);
    }

    protected abstract T createNewDiagramInstance(String name);

    @Override
    public T createDiagram(String name) {
        T diagram = createNewDiagramInstance(name);

        Element e = diagram.getRoot();
        DiagramImageFactory factory = imageFactories.get(e.getClass());
        if (factory != null) {
            DiagramImage image = factory.createDiagramImage();
            e.setWidth(image.getWidth());
            e.setHeight(image.getHeight());
        }

        return diagram;
    }
}
