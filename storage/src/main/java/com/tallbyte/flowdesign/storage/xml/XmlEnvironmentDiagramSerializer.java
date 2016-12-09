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
import com.tallbyte.flowdesign.core.Element;
import com.tallbyte.flowdesign.core.environment.EnvironmentDiagram;
import com.tallbyte.flowdesign.core.environment.EnvironmentDiagramElement;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Map;

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
            helper.getAssignedIdMap().putAll(
                    serializeTypes(
                            writer,
                            diagram.getElements(),
                            helper.getIdentifierResolver()
                    )
            );

            // write elements
            writer.writeStartElement(ELEMENT_NAME_ELEMENTS);
            for (Element element : diagram.getElements()) {
                helper.getSerializationResolver().serialize(
                        writer,
                        element,
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
            EnvironmentDiagram  diagram    = new EnvironmentDiagram(
                    attributes.get(ATTRIBUTE_NAME)
            );

            // instantiate all the entities
            helper.getAssignedIdMap().putAll(
                    deserializeTypes(
                            reader,
                            EnvironmentDiagramElement.class,
                            helper
                    )
            );


            // load all the elements
            helper.fastForwardToElementStart(reader, ELEMENT_NAME_ELEMENTS);
            helper.foreachElementStartUntil (reader, ELEMENT_NAME_ELEMENTS, () -> {
                diagram.addElement(
                        helper.getDeserializationResolver().deserialize(
                                reader,
                                EnvironmentDiagramElement.class,
                                helper
                        )
                );
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
