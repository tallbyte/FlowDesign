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

package com.tallbyte.flowdesign.storage.xml;

import com.tallbyte.flowdesign.data.Element;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 09.12.16.
 */
public abstract class XmlElementSerializer<T extends Element> implements XmlSerializer<T> {

    public static final String ATTRIBUTE_X          = "x";
    public static final String ATTRIBUTE_Y          = "y";
    public static final String ATTRIBUTE_WIDTH      = "width";
    public static final String ATTRIBUTE_HEIGHT     = "height";
    public static final String ATTRIBUTE_TEXT       = "text";
    public static final String ATTRIBUTE_COLOR      = "color";
    public static final String ATTRIBUTE_DELETABLE  = "deletable";


    /**
     * @param attributes {@link Map} to put attributes into
     * @param element {@link Element} to load attributes from
     * @param helper {@link XmlSerializationHelper} assisting
     * @return Given {@link Map}
     * @throws XMLStreamException If attribute composing failed
     */
    protected Map<String, String> saveAttributes(Map<String, String> attributes, T element, XmlSerializationHelper helper) throws XMLStreamException {
        // Double#toString is locale independent
        attributes.put(ATTRIBUTE_X,          Double.toString(element.getX()));
        attributes.put(ATTRIBUTE_Y,          Double.toString(element.getY()));
        attributes.put(ATTRIBUTE_WIDTH,      Double.toString(element.getWidth()));
        attributes.put(ATTRIBUTE_HEIGHT,     Double.toString(element.getHeight()));
        attributes.put(ATTRIBUTE_TEXT,       element.getText());
        attributes.put(ATTRIBUTE_COLOR,      element.getColor());
        attributes.put(ATTRIBUTE_DELETABLE,  Boolean.toString(element.isDeletable()));

        return attributes;
    }

    /**
     * @param attributes {@link Map} to load attributes from
     * @param element {@link Element} to store loaded attributes into
     * @param helper {@link XmlDeserializationHelper} assisting
     * @throws XMLStreamException If attribute loading failed
     */
    protected void loadAttributes(Map<String, String> attributes, T element, XmlDeserializationHelper helper) throws XMLStreamException {
        // Double#valueOf is locale independent
        element.setX       (Double.valueOf(attributes.get(ATTRIBUTE_X)));
        element.setY       (Double.valueOf(attributes.get(ATTRIBUTE_Y)));
        element.setWidth   (Double.valueOf(attributes.get(ATTRIBUTE_WIDTH)));
        element.setHeight  (Double.valueOf(attributes.get(ATTRIBUTE_HEIGHT)));
        element.setText    (               attributes.get(ATTRIBUTE_TEXT));
        element.setColor   (               attributes.get(ATTRIBUTE_COLOR));

        element.setDeletable(Boolean.valueOf(attributes.get(ATTRIBUTE_DELETABLE)));
    }

    @Override
    public void serialize(XMLStreamWriter writer, T serializable, XmlSerializationHelper helper) throws IOException {
        try {
            helper.setAttributes(
                    writer,
                    saveAttributes(
                            new HashMap<>(),
                            serializable,
                            helper
                    )
            );

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public T deserialize(XMLStreamReader reader, T serializable, XmlDeserializationHelper helper) throws IOException {
        try {
            loadAttributes(
                    helper.getAttributes(reader),
                    serializable,
                    helper
            );

            return serializable;

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
