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

package com.tallbyte.flowdesign.storage.xml.flow;

import com.tallbyte.flowdesign.data.Connection;
import com.tallbyte.flowdesign.data.flow.End;
import com.tallbyte.flowdesign.data.flow.FlowDiagram;
import com.tallbyte.flowdesign.data.flow.FlowDiagramElement;
import com.tallbyte.flowdesign.data.flow.Start;
import com.tallbyte.flowdesign.storage.xml.XmlDeserializationHelper;
import com.tallbyte.flowdesign.storage.xml.XmlDiagramSerializer;
import com.tallbyte.flowdesign.storage.xml.XmlSerializationHelper;
import com.tallbyte.flowdesign.storage.xml.XmlSerializer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Queue;

/**
 * Created by michael on 12.12.16.
 */
public class XmlFlowDiagramSerializer extends XmlDiagramSerializer implements XmlSerializer<FlowDiagram> {
    @Override
    public void serialize(XMLStreamWriter writer, FlowDiagram diagram, XmlSerializationHelper helper) throws IOException {
        try {
            // write diagram related attributes
            serializeAttributes(writer, diagram, helper);

            // write elements
            helper.getAssignedIdMap().clear();
            serializeElements(writer, diagram.getElements(), helper);

            // write connections
            serializeConnections(writer, diagram.getConnections(), helper);

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public FlowDiagram instantiate() {
        // not happening here
        return null;
    }

    @Override
    public FlowDiagram deserialize(XMLStreamReader reader, FlowDiagram serializable, XmlDeserializationHelper helper) throws IOException {
        try {
            // load the diagram attributes
            Map<String, String> attributes = helper.getAttributes(reader);

            helper.getAssignedIdMap().clear();
            Queue<Map.Entry<String, FlowDiagramElement>> queue = deserializeElementTypes(
                    reader,
                    FlowDiagramElement.class,
                    helper
            );

            // load all the elements with proper values
            deserializeElements(reader, queue, FlowDiagramElement.class, helper);

            Start start = null;
            End   end   = null;

            for (Map.Entry<String, FlowDiagramElement> entry : queue) {
                if (entry.getValue() instanceof Start) {
                    start = (Start) entry.getValue();
                }

                if (entry.getValue() instanceof End) {
                    end = (End) entry.getValue();
                }
            }

            FlowDiagram diagram = new FlowDiagram(
                    attributes.get(ATTRIBUTE_NAME),
                    start,
                    end
            );

            queue.stream()
                    .map(Map.Entry::getValue)
                    .forEach(diagram::addElement);

            // build all the connections
            deserializeConnections(reader, Connection.class, helper);

            return diagram;

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
