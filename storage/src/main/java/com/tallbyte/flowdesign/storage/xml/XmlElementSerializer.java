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

import com.tallbyte.flowdesign.core.Element;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by michael on 09.12.16.
 */
public abstract class XmlElementSerializer<T extends Element> implements XmlSerializer<T> {

    public static final String ATTRIBUTE_X          = "x";
    public static final String ATTRIBUTE_Y          = "y";
    public static final String ATTRIBUTE_WIDTH      = "width";
    public static final String ATTRIBUTE_HEIGHT     = "height";
    public static final String ATTRIBUTE_DELETABLE  = "deletable";

    @Override
    public void serialize(XMLStreamWriter writer, T serializable, XmlSerializationHelper helper) throws IOException {
        try {
            // Double#toString is locale independent
            writer.writeAttribute(ATTRIBUTE_X,          Double.toString(serializable.getX()));
            writer.writeAttribute(ATTRIBUTE_Y,          Double.toString(serializable.getY()));
            writer.writeAttribute(ATTRIBUTE_WIDTH,      Double.toString(serializable.getWidth()));
            writer.writeAttribute(ATTRIBUTE_HEIGHT,     Double.toString(serializable.getHeight()));

            writer.writeAttribute(ATTRIBUTE_DELETABLE,  Boolean.toString(serializable.isDeletable()));

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public T deserialize(XMLStreamReader reader, T serializable, XmlDeserializationHelper helper) throws IOException {
        try {
            Map<String, String> attributes = helper.getAttributes(reader);

            // Double#valueOf is locale independent
            serializable.setX       (Double.valueOf(attributes.get(ATTRIBUTE_X)));
            serializable.setY       (Double.valueOf(attributes.get(ATTRIBUTE_Y)));
            serializable.setWidth   (Double.valueOf(attributes.get(ATTRIBUTE_WIDTH)));
            serializable.setHeight  (Double.valueOf(attributes.get(ATTRIBUTE_HEIGHT)));

            serializable.setDeletable(Boolean.valueOf(attributes.get(ATTRIBUTE_DELETABLE)));

            return serializable;

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
