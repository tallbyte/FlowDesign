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
import com.tallbyte.flowdesign.javafx.diagram.factory.DiagramImageFactory;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Map;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-08)<br/>
 */
public interface DiagramHandler<T extends Diagram> {

    /**
     * Creates a new {@link Element} based on the given name and its coordinates.
     * @param diagram the {@link Diagram} to create the {@link Element} in
     * @param element the base {@link Element}
     * @param x the x coordinate
     * @param y the y coordinate
     * @return Returns the created {@link Element} or null if none could be created.
     */
    Element createElement(T diagram, String element, double x, double y);

    /**
     * Removes the given {@link Element} from the given {@link Diagram}.
     * @param diagram the {@link Diagram} to remove from
     * @param element the {@link Element} to remove
     */
    void removeElement(T diagram, Element element);

    /**
     * Creates an {@link ElementNode} for a given {@link Element}.
     * @param element the {@link Element} to create the node for
     * @return Returns the created {@link ElementNode} or null if none could be created.
     */
    ElementNode createNode(Element element);

    /**
     * Returns a map of all supported element "names" mapped to their image factory.
     * @return Returns the map.
     */
    Map<String, DiagramImageFactory> getSupportedElements();

    /**
     * Checks if a certain element "name" is user-createable.
     * @param name the name
     * @return Returns true if it is user-createable, else false.
     */
    boolean isUserCreateable(String name);

    /**
     * Creates a new {@link Diagram} with a given name.
     * @param name the supposed name
     * @return Returns the created {@link Diagram}.
     */
    T createDiagram(String name);

    /**
     * Gets the modifiable properties
     * @param diagram the {@link Diagram} to create for
     * @return Returns a list of such properties.
     */
    ObservableList<Property<?>> getDiagramProperties(T diagram);

}
