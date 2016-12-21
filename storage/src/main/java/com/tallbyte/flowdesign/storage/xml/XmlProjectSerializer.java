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

import com.tallbyte.flowdesign.data.DataType;
import com.tallbyte.flowdesign.data.Diagram;
import com.tallbyte.flowdesign.data.environment.EnvironmentDiagram;
import com.tallbyte.flowdesign.data.Project;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Map;

/**
 * No element registered shall call {@link XMLStreamWriter#writeStartElement(String)}
 * or a similar function to start and/or end a xml-element. This will all be handled
 * by this class
 */
public class XmlProjectSerializer implements XmlSerializer<Project> {

    public static final String ELEMENT_DIAGRAMS     = "diagrams";
    public static final String ELEMENT_DATA_TYPES   = "dataTypes";
    public static final String ATTRIBUTE_NAME       = "name";



    @Override
    public void serialize(XMLStreamWriter writer, Project project, XmlSerializationHelper helper) throws IOException {
        try {
            writer.writeAttribute(ATTRIBUTE_NAME, project.getName());

            // serialize all DataType definitions
            writer.writeStartElement(ELEMENT_DATA_TYPES);
            for (DataType dataType : project.getDataTypeHolder().getDataTypes()) {
                helper.getSerializationResolver().serialize(
                        writer,
                        dataType,
                        helper
                );
            }
            writer.writeEndElement();


            // serialize all diagrams
            writer.writeStartElement(ELEMENT_DIAGRAMS);
            for (Class<? extends Diagram> type : project.getDiagramTypes()) {
                for (Diagram<?> diagram : project.getDiagrams(type)) {
                    helper.getSerializationResolver().serialize(
                            writer,
                            diagram,
                            helper
                    );
                }
            }
            writer.writeEndElement();


        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Project instantiate() {
        return new Project();
    }

    @Override
    public Project deserialize(XMLStreamReader reader, Project project, XmlDeserializationHelper helper) throws IOException {
        try {
            Map<String, String> attributes = helper.getAttributes(reader);

            project.setName(attributes.get(ATTRIBUTE_NAME));


            project.getDataTypeHolder().clear();

            // deserialize all DataType definitions
            helper.fastForwardToElementStart(reader, ELEMENT_DATA_TYPES);
            helper.foreachElementStartUntil(reader, ELEMENT_DATA_TYPES, () -> {
                project.getDataTypeHolder().addDataType(
                        helper.getDeserializationResolver().deserialize(
                                reader,
                                DataType.class,
                                helper
                        )
                );
            });

            // deserialize all Diagrams
            helper.fastForwardToElementStart(reader, ELEMENT_DIAGRAMS);
            helper.foreachElementStartUntil(reader, ELEMENT_DIAGRAMS, () -> {
                project.addDiagram(
                        helper.getDeserializationResolver().deserialize(
                            reader,
                            Diagram.class,
                            helper
                        )
                );
            });


        } catch (XMLStreamException e) {
            throw new IOException(e);
        }

        return project;
    }
}
