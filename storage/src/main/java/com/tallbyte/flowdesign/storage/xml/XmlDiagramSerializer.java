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
import com.tallbyte.flowdesign.storage.IdentifierResolver;
import com.tallbyte.flowdesign.storage.InstantiationResolver;
import com.tallbyte.flowdesign.storage.UnknownIdentifierException;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 09.12.16.
 *
 * Basic serializer helping with serializing the type definitions
 * of the holding elements
 */
public abstract class XmlDiagramSerializer {

    public static final String ELEMENT_NAME_TYPES           = "types";
    public static final String ELEMENT_NAME_TYPE            = "type";
    public static final String ATTRIBUTE_ELEMENT_TYPE       = "type";
    public static final String ATTRIBUTE_ELEMENT_IDENTIFIER = "id";

    /**
     * Writes a xml-tag named {@value #ELEMENT_NAME_TYPES} to the {@link XMLStreamWriter},
     * having a id to type-identifier pair for each given {@link Element}. The returned {@link Map}
     * hold all assigned ids. The {@link #deserializeTypes(XMLStreamReader, Class, InstantiationResolver)}
     * is the counter part of this method, allowing one to deserialize and instantiate those given {@link Element}s
     *
     * @param writer {@link XMLStreamWriter} to write to
     * @param elements {@link Element}s to write the type-identifier for
     * @param identifierResolver {@link IdentifierResolver} to use to resolve the type-identifiers
     * @param <E> Type of {@link Element}s to write
     * @return A {@link Map} holding all assigned ids
     * @throws XMLStreamException If writing failed
     */
    protected <E extends Element> Map<E, String> serializeTypes(XMLStreamWriter writer, Iterable<? extends E> elements, IdentifierResolver identifierResolver) throws XMLStreamException {
        // map holding the given indices
        Map<E, String> map = new HashMap<>();

        writer.writeStartElement(ELEMENT_NAME_TYPES);

        int index = 0;
        for (E e : elements) {
            String indexString = ""+(index++);
            map.put(e, indexString);

            /*
             * actually writing the id is not required, since the index inside the types-tag is the same,
             * but for safety and stability reasons - and for easier understanding while looking
             * at the xml file - its explicitly included
             */
            writer.writeStartElement(ELEMENT_NAME_TYPE);
            writer.writeAttribute(ATTRIBUTE_ELEMENT_TYPE,       identifierResolver.resolveIdentifier(e));
            writer.writeAttribute(ATTRIBUTE_ELEMENT_IDENTIFIER, indexString);
            writer.writeEndElement();
        }

        writer.writeEndElement();

        return map;
    }

    /**
     * Reads the xml-tag names {@value #ELEMENT_NAME_TYPES} with all its definitions from the given
     * {@link XMLStreamReader}. The {@link XMLStreamReader} must currently point with
     * {@link XMLStreamReader#getEventType()} at {@link XMLStreamConstants#START_ELEMENT} of the types-tag. The
     * returned {@link Map} contains all instantiated {@link Element}s accessible through their in
     * {@link #serializeTypes(XMLStreamWriter, Iterable, IdentifierResolver)} assigned id
     *
     * @param reader {@link XMLStreamReader} to read from
     * @param type Type of {@link Element} to ensure compatibility for
     * @param helper {@link XmlDeserializationHelper} to use
     * @param <E> Type of {@link Element}
     * @return {@link Map} with instantiated {@link Element}s accessible through their assigned id
     * @throws IOException If there was an error while deserialization
     * @throws XMLStreamException If reading failed
     */
    protected <E extends Element> Map<String, E> deserializeTypes(XMLStreamReader reader, Class<E> type, XmlDeserializationHelper helper) throws IOException, XMLStreamException {
        Map<String, E> map = new HashMap<>();

        // current tag required to be the types-tag
        if (reader.getEventType() != XMLStreamConstants.START_ELEMENT || !ELEMENT_NAME_TYPES.equals(reader.getLocalName())) {
            throw new IOException(
                    "Required element: "+ELEMENT_NAME_TYPES +
                            " but found: "+reader.getLocalName() +
                            " at line: "+reader.getLocation().getLineNumber()
            );
        }

        nextLoop:
        while (reader.hasNext()) {
            switch (reader.next()) {
                // the only allowed element to start is the type definition tag
                case XMLStreamConstants.START_ELEMENT:
                    if (!ELEMENT_NAME_TYPE.equals(reader.getLocalName())) {
                        throw new IOException(
                                "Unexpected element, expected "+ELEMENT_NAME_TYPE +
                                        " but found: "+reader.getLocalName() +
                                        " at line: "+reader.getLocation().getLineNumber()
                        );
                    }
                    break;

                /*
                 * Load the identifier and index, instantiate the Element and check for
                 * compatibility before adding it to the map
                 */
                case XMLStreamConstants.ATTRIBUTE:
                    try {
                        // load all the attributes
                        Map<String, String> attributes = helper.getAttributes(reader);

                        // load the map with the type
                        map.put(
                                attributes.get(ATTRIBUTE_ELEMENT_IDENTIFIER),
                                helper.getInstantiationResolver().instantiate(
                                        attributes.get(ATTRIBUTE_ELEMENT_TYPE),
                                        type
                                )
                        );

                    } catch (UnknownIdentifierException e) {
                        throw new IOException("Failed to instantiate element at line: "+reader.getLocation().getLineNumber());
                    }

                    break;


                case XMLStreamConstants.END_ELEMENT:
                    if (ELEMENT_NAME_TYPES.equals(reader.getLocalName())) {
                        // done with reading
                        break nextLoop;
                    }

                default:
                    // nothing to do?
            }
        }


        // end the types-tag
        reader.next();
        return map;
    }
}
