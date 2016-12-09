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
import com.tallbyte.flowdesign.core.Joint;
import com.tallbyte.flowdesign.core.Project;
import com.tallbyte.flowdesign.core.environment.Actor;
import com.tallbyte.flowdesign.core.environment.System;
import com.tallbyte.flowdesign.storage.Serializer;
import com.tallbyte.flowdesign.storage.UnknownIdentifierException;

import javax.xml.stream.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 09.12.16.
 */
public class XmlStorage {

    protected Map<String, Serializer<?, XMLStreamReader, XMLStreamWriter, XmlDeserializationHelper, XmlSerializationHelper>> serializers = new HashMap<>();
    protected Map<Class, String> identifiers = new HashMap<>();

    protected XmlSerializationHelper   helperSerialization;
    protected XmlDeserializationHelper helperDeserialization;

    public XmlStorage() {
        this.helperSerialization = new XmlSerializationHelper(
                this::resolveIdentifier,
                this::resolvedSerialization
        );

        this.helperDeserialization = new XmlDeserializationHelper(
                this::instantiate,
                this::deserialize
        );

        // register default entries
        register(Project            .class, new XmlProjectSerializer());

        register(Connection         .class, new XmlConnectionSerializer());
        register(Joint              .class, new XmlJointSerializer());

        register(EnvironmentDiagram .class, new XmlEnvironmentDiagramSerializer());
        register(Actor              .class, new XmlActorSerializer());
        register(System             .class, new XmlSystemSerializer());
    }

    protected String toTagName(Class<?> type) {
        char[] name = type.getSimpleName().toCharArray();
        name[0] = Character.toLowerCase(name[0]);
        return new String(name);
    }

    public <T> XmlStorage register(Class<T> type, Serializer<T, XMLStreamReader, XMLStreamWriter, XmlDeserializationHelper, XmlSerializationHelper> serializer) {
        return register(type, toTagName(type), serializer);
    }

    public <T> XmlStorage register(Class<T> type, String tagName, Serializer<T, XMLStreamReader, XMLStreamWriter, XmlDeserializationHelper, XmlSerializationHelper> serializer) {
        this.identifiers.put(type, tagName);
        this.serializers.put(tagName, serializer);
        return this;
    }

    public void serialize(Object serializable, OutputStream outputStream) throws IOException {
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter  writer  = factory.createXMLStreamWriter(outputStream, StandardCharsets.UTF_8.name());

            writer.writeStartDocument();
            helperSerialization.getSerializationResolver().serialize(
                    writer,
                    serializable,
                    helperSerialization
            );
            writer.writeEndDocument();


        } catch (XMLStreamException e) {
            throw new IOException(e);
        }

    }

    public Object deserialize(InputStream inputStream) throws IOException {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader  = factory.createXMLStreamReader(inputStream, StandardCharsets.UTF_8.name());

            helperDeserialization.fastForwardToElementStart(reader);
            return helperDeserialization.getDeserializationResolver().deserialize(
                    reader,
                    Object.class,
                    helperDeserialization
            );

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    protected String resolveIdentifier(Object serializable) {
        return identifiers.get(serializable.getClass());
    }

    protected <T> void resolvedSerialization(XMLStreamWriter writer, T serializable, XmlSerializationHelper helper) throws IOException {
        String identifier = helper.getIdentifierResolver().resolveIdentifier(serializable);
        Serializer<T, XMLStreamReader, XMLStreamWriter, XmlDeserializationHelper, XmlSerializationHelper>
                serializer = (Serializer<T, XMLStreamReader, XMLStreamWriter, XmlDeserializationHelper, XmlSerializationHelper>)
                serializers.get(identifier);

        if (serializer != null) {
            try {
                writer.writeStartElement(identifier);
                serializer.serialize(
                        writer,
                        serializable,
                        helper
                );
                writer.writeEndElement();

            } catch (XMLStreamException e) {
                throw new IOException(e);
            }

        } else {
            throw new IOException(
                    "No serializer for serializable: "+serializable.getClass().getSimpleName() +
                            " and identifier: "+identifier
            );
        }
    }

    protected Object instantiate(String identifier) throws UnknownIdentifierException {
        Serializer serializer = serializers.get(identifier);

        if (serializer == null) {
            throw new UnknownIdentifierException(identifier);
        }

        return serializer.instantiate();
    }

    protected <T> T deserialize(XMLStreamReader reader, T emptyInstance, Class<T> type, XmlDeserializationHelper helper) throws IOException {
        // since its optional, one needs to check whether to instantiate one
        if (emptyInstance == null) {
            emptyInstance = helper.getInstantiationResolver().instantiate(reader.getLocalName(), type);
        }

        Serializer<T, XMLStreamReader, XMLStreamWriter, XmlDeserializationHelper, XmlSerializationHelper>
                serializer = (Serializer<T, XMLStreamReader, XMLStreamWriter, XmlDeserializationHelper, XmlSerializationHelper>)
                serializers.get(reader.getLocalName());

        if (serializer != null) {
            return serializer.deserialize(
                    reader,
                    emptyInstance,
                    helper
            );
        } else {
            throw new IOException("No serializer for localName: "+reader.getLocalName());
        }
    }
}
