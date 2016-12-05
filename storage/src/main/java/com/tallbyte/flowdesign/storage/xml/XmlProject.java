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

import com.tallbyte.flowdesign.core.Diagram;
import com.tallbyte.flowdesign.core.Element;
import com.tallbyte.flowdesign.core.EnvironmentDiagram;
import com.tallbyte.flowdesign.core.Project;
import com.tallbyte.flowdesign.storage.DiagramSerializer;
import com.tallbyte.flowdesign.storage.ElementSerializer;
import com.tallbyte.flowdesign.storage.ProjectSerializer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * No element registered shall call {@link XMLStreamWriter#writeStartElement(String)}
 * or a similar function to start and/or end a xml-element. This will all be handled
 * by this class
 */
public class XmlProject implements ProjectSerializer<XMLStreamReader, XMLStreamWriter> {

    public static final String ELEMENT_NAME_PROJECT         = "project";
    public static final String ELEMENT_NAME_DIAGRAM         = "diagram";
    public static final String ELEMENT_NAME_ELEMENT         = "element";
    public static final String ELEMENT_NAME_TYPE            = "type";
    public static final String ATTRIBUTE_PROJECT_NAME       = "name";
    public static final String ATTRIBUTE_ELEMENT_TYPE       = "type";
    public static final String ATTRIBUTE_ELEMENT_IDENTIFIER = "id";

    protected Map<String, DiagramSerializer<Diagram, XMLStreamReader, XMLStreamWriter>> diagramSerializer = new HashMap<>();
    protected Map<String, ElementSerializer<Element, XMLStreamReader, XMLStreamWriter>> elementSerializers = new HashMap<>();

    protected Map<XMLStreamWriter, Map<Element, String>> elementToIdentifierResolver = new ConcurrentHashMap<>();

    @Override
    public void serialize(Project project, OutputStream outputStream) throws IOException {
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter  writer  = factory.createXMLStreamWriter(outputStream, StandardCharsets.UTF_8.name());

            writer.writeStartDocument();

            // >> Project related start
            writer.writeStartElement(ELEMENT_NAME_PROJECT);
            writer.writeAttribute(ATTRIBUTE_PROJECT_NAME, project.getName());

            // write all the diagrams // TODO
            for (Diagram diagram : project.getDiagrams(EnvironmentDiagram.class)) {
                writeDiagram(writer, diagram);
            }

            writer.writeEndElement();
            // << Project related end

            writer.writeEndDocument();


        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Project deserialize(InputStream inputStream) throws IOException {
        return null;
    }

    /**
     * Retrieves the {@link DiagramSerializer} for the given {@link Diagram} and
     * serializes it
     *
     * @param writer {@link XMLStreamWriter} to write to
     * @param diagram {@link Diagram} to serialize
     * @throws IOException If serialization failed
     */
    protected void writeDiagram(XMLStreamWriter writer, Diagram diagram) throws IOException {
        // retrieve the serializer for the given diagram
        String                                                       type       = diagram.getClass().getSimpleName();
        DiagramSerializer<Diagram, XMLStreamReader, XMLStreamWriter> serializer = diagramSerializer.get(type);

        // unknown diagram type
        if (serializer == null) {
            throw new IOException("Unknown diagram type: "+diagram.getClass());
        }

        Map<Element, String> identifiers = new HashMap<>();
        this.elementToIdentifierResolver.put(writer, identifiers);

        try {
            writer.writeStartElement(ELEMENT_NAME_DIAGRAM);
            writer.writeAttribute(ATTRIBUTE_ELEMENT_TYPE, type);


            /*
             * Give all the Elements in the current Diagram
             * an id, so they can be pointed at each other by
             * that id
             */
            int id = 0;
            for (Element element : diagram.getElements()) {
                // write types of the links so they can be loaded on deserialize
                writer.writeStartElement(ELEMENT_NAME_TYPE);
                writer.writeAttribute(ATTRIBUTE_ELEMENT_TYPE,       element.getClass().getSimpleName());
                writer.writeAttribute(ATTRIBUTE_ELEMENT_IDENTIFIER, ""+id);
                writer.writeEndElement();

                identifiers.put(element, ""+(id++));

            }

            // actual serialization
            serializer.serialize(writer, diagram, this::writeElement);


            writer.writeEndElement();

        } catch (XMLStreamException e) {
            throw new IOException(e);

        } finally {
            // be sure to cleanup
            this.elementToIdentifierResolver.remove(writer);
        }
    }

    /**
     * Retrieves the {@link ElementSerializer} for the given {@link Element}
     * and serializes it
     *
     * @param writer {@@link XMLStreamWriter} to write to
     * @param element The {@link Element} to serialize
     * @throws IOException If serialization failed
     */
    protected void writeElement(XMLStreamWriter writer, Element element) throws IOException {
        // retrieve the serializer for the given element
        String                                                       type       = element.getClass().getSimpleName();
        ElementSerializer<Element, XMLStreamReader, XMLStreamWriter> serializer = elementSerializers.get(type);

        // unknown element type?
        if (serializer == null) {
            throw new IOException("Unknown element type: "+element.getClass());
        }

        try {
            // current identifiers
            Map<Element, String> identifiers = elementToIdentifierResolver.get(writer);

            writer.writeStartElement(ELEMENT_NAME_ELEMENT);
            writer.writeAttribute(ATTRIBUTE_ELEMENT_TYPE,       type);
            writer.writeAttribute(ATTRIBUTE_ELEMENT_IDENTIFIER, identifiers.get(element));

            // actual serialization
            serializer.serialize(writer, element, this::writeElement, identifiers::get);

            writer.writeEndElement();

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public <T extends Diagram> void registerDiagramSerializer(Class<T> type, DiagramSerializer<T, XMLStreamReader, XMLStreamWriter> serializer) {
        diagramSerializer.put(
                type.getClass().getSimpleName(),
                (DiagramSerializer<Diagram, XMLStreamReader, XMLStreamWriter>) serializer // cast is fine
        );
    }

    @Override
    public <T extends Element> void registerElementSerializer(Class<T> type, ElementSerializer<T, XMLStreamReader, XMLStreamWriter> serializer) {
        elementSerializers.put(
                type.getClass().getSimpleName(),
                (ElementSerializer<Element, XMLStreamReader, XMLStreamWriter>) serializer // cast is fine
        );
    }
}
