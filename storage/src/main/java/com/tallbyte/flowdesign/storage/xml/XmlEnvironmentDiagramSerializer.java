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

import com.tallbyte.flowdesign.core.Connection;
import com.tallbyte.flowdesign.core.EnvironmentDiagram;
import com.tallbyte.flowdesign.core.environment.EnvironmentDiagramElement;
import com.tallbyte.flowdesign.core.environment.System;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Queue;

/**
 * Created by michael on 05.12.16.
 */
public class XmlEnvironmentDiagramSerializer extends XmlDiagramSerializer implements XmlSerializer<EnvironmentDiagram> {


    public static final String ELEMENT_NAME_ELEMENTS    = "elements";
    public static final String ELEMENT_NAME_CONNECTIONS = "connections";
    public static final String ATTRIBUTE_NAME           = "name";


    @Override
    public void serialize(XMLStreamWriter writer, EnvironmentDiagram diagram, XmlSerializationHelper helper) throws IOException {
        try {
            // write diagram related attributes
            writer.writeAttribute(ATTRIBUTE_NAME, diagram.getName());

            // update the id map
            Queue<Map.Entry<String, EnvironmentDiagramElement>> queue = serializeTypes(
                    writer,
                    diagram.getElements(),
                    helper.getIdentifierResolver()
            );

            queue.forEach(e -> helper.getAssignedIdMap().put(
                    e.getValue(),
                    e.getKey()
            ));


            // write elements
            writer.writeStartElement(ELEMENT_NAME_ELEMENTS);
            while (!queue.isEmpty()) {
                helper.getSerializationResolver().serialize(
                        writer,
                        queue.poll().getValue(),
                        helper
                );
            }
            writer.writeEndElement();


            // write connections
            writer.writeStartElement(ELEMENT_NAME_CONNECTIONS);
            for (Connection connection : diagram.getConnections()) {
                helper.getSerializationResolver().serialize(
                        writer,
                        connection,
                        helper
                );
            }
            writer.writeEndElement();

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public EnvironmentDiagram instantiate() {
        // not happening here
        return null;
    }

    @Override
    public EnvironmentDiagram deserialize(XMLStreamReader reader, EnvironmentDiagram serializable, XmlDeserializationHelper helper) throws IOException {
        try {
            // load the diagram attributes
            Map<String, String> attributes = helper.getAttributes(reader);

            // instantiate all the entities
            Queue<Map.Entry<String, EnvironmentDiagramElement>> queue = deserializeTypes(
                    reader,
                    EnvironmentDiagramElement.class,
                    helper
            );

            // prepare the AssignedIdMap
            helper.getAssignedIdMap().clear();
            queue.forEach(e -> helper.getAssignedIdMap().put(
                    e.getKey(),
                    e.getValue()
            ));


            EnvironmentDiagram  diagram    = new EnvironmentDiagram(
                    attributes.get(ATTRIBUTE_NAME),
                    (System) queue.peek().getValue()
            );

            // load all the elements with proper values
            helper.fastForwardToElementStart(reader, ELEMENT_NAME_ELEMENTS);
            helper.foreachElementStartUntil (reader, ELEMENT_NAME_ELEMENTS, () -> {
                EnvironmentDiagramElement e = helper.getDeserializationResolver().deserialize(
                        reader,
                        queue.poll().getValue(),
                        EnvironmentDiagramElement.class,
                        helper
                );

                // do not add the root element twice (first time through constructor)
                if (!diagram.getRoot().equals(e)) {
                    diagram.addElement(e);
                }
            });

            // build all the connections
            helper.fastForwardToElementStart(reader, ELEMENT_NAME_CONNECTIONS);
            helper.foreachElementStartUntil (reader, ELEMENT_NAME_CONNECTIONS, () -> {
                // the deserialize call alone does all the work, nothing to do anymore
                helper.getDeserializationResolver().deserialize(
                        reader,
                        Connection.class,
                        helper
                );
            });

            return diagram;

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
