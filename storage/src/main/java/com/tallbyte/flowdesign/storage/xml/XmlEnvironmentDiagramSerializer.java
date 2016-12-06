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
import com.tallbyte.flowdesign.core.EnvironmentDiagram;
import com.tallbyte.flowdesign.storage.DiagramSerializer;
import com.tallbyte.flowdesign.storage.ElementDeserializationResolver;
import com.tallbyte.flowdesign.storage.ElementSerializationResolver;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * Created by michael on 05.12.16.
 */
public class XmlEnvironmentDiagramSerializer implements DiagramSerializer<EnvironmentDiagram, XMLStreamReader, XMLStreamWriter> {

    public static final String ATTRIBUTE_NAME   = "name";

    @Override
    public void serialize(XMLStreamWriter write, EnvironmentDiagram diagram, ElementSerializationResolver<XMLStreamWriter> elementSerializer) throws IOException {
        try {
            write.writeAttribute(ATTRIBUTE_NAME, diagram.getName());

            for (Element element : diagram.getElements()) {
                elementSerializer.serialize(write, element);
            }

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public EnvironmentDiagram deserialize(XMLStreamReader read, ElementDeserializationResolver<XMLStreamReader> elementDeserializer) throws IOException {
        return null;
    }
}
