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
import com.tallbyte.flowdesign.core.environment.EnvironmentDiagram;
import com.tallbyte.flowdesign.core.Joint;
import com.tallbyte.flowdesign.core.Project;
import com.tallbyte.flowdesign.core.environment.Actor;
import com.tallbyte.flowdesign.core.environment.System;
import com.tallbyte.flowdesign.storage.Serializer;
import com.tallbyte.flowdesign.storage.Storage;
import com.tallbyte.flowdesign.storage.UnknownIdentifierException;

import javax.xml.stream.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 09.12.16.
 */
public class XmlStorage implements Storage<XmlStorage, OutputStream, InputStream, XMLStreamReader, XMLStreamWriter, XmlDeserializationHelper, XmlSerializationHelper> {

    protected Map<String, Serializer<?, XMLStreamReader, XMLStreamWriter, XmlDeserializationHelper, XmlSerializationHelper>> serializers = new HashMap<>();
    protected Map<Class, String> identifiers = new HashMap<>();

    protected XmlSerializationHelper   helperSerialization;
    protected XmlDeserializationHelper helperDeserialization;

    protected boolean indentation;

    public XmlStorage() {
        this(true);
    }

    public XmlStorage(boolean indentation) {
        this.indentation = indentation;

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

    /**
     * @param type The class to get the identifier for
     * @return The identifier for the given class
     */
    protected String getIdentifier(Class<?> type) {
        char[] name = type.getSimpleName().toCharArray();
        name[0] = Character.toLowerCase(name[0]);
        return new String(name);
    }

    @Override
    public <T> XmlStorage register(Class<T> type, Serializer<T, XMLStreamReader, XMLStreamWriter, XmlDeserializationHelper, XmlSerializationHelper> serializer) {
        return register(type, getIdentifier(type), serializer);
    }

    /**
     * @param type The type to register the identifier for
     * @param identifier The identifier to register the {@link Serializer} for
     * @param serializer The {@link Serializer} to register
     * @param <T> Type being registered
     * @return itself
     */
    public <T> XmlStorage register(Class<T> type, String identifier, Serializer<T, XMLStreamReader, XMLStreamWriter, XmlDeserializationHelper, XmlSerializationHelper> serializer) {
        this.identifiers.put(type, identifier);
        this.serializers.put(identifier, serializer);
        return this;
    }

    /**
     * @param serializable The serializable to serialize
     * @param destination The {@link File} to write to
     * @throws IOException If serialization failed
     */
    public void serialize(Object serializable, File destination) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            serialize(serializable, fos);
        }
    }

    @Override
    public void serialize(Object serializable, OutputStream outputStream) throws IOException {
        if (indentation) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            serializeImpl(serializable, baos);

            try {
                Transformer transformer;
                transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(
                        new StreamSource(
                                new ByteArrayInputStream(baos.toByteArray())
                        ),
                        new StreamResult(
                                outputStream
                        )
                );
            } catch (TransformerException e) {
                throw new IOException(e);
            }

        } else {
            serializeImpl(serializable, outputStream);
        }
    }

    /**
     * @param serializable The serializable to serialize
     * @param outputStream The {@link OutputStream} to to write to
     * @throws IOException If serialization failed
     */
    private void serializeImpl(Object serializable, OutputStream outputStream) throws IOException {
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

    /**
     * @param source The {@link File} to read from
     * @param type The type to enforce the serializable to be
     * @param <T> The type being enforced
     * @return The deserialized serializable
     * @throws IOException If deserialization failed or on type mismatch
     */
    public <T> T deserialize(File source, Class<T> type) throws IOException {
        try (FileInputStream fis = new FileInputStream(source)) {
            return deserialize(fis, type);
        }
    }

    @Override
    public <T> T deserialize(InputStream input, Class<T> type) throws IOException {
        Object value = deserialize(input);

        if (type.isInstance(value)) {
            return type.cast(value);

        } else {
            throw new IOException(
                    "Type enforcement failed, expected "+type +
                            " but got "+(value != null ? value.getClass():null)
            );
        }
    }

    /**
     * @param source The {@link File} to read from
     * @return The deserialized serializable
     * @throws IOException If deserialization failed or on type mismatch
     */
    public Object deserialize(File source) throws IOException {
        try (FileInputStream fis = new FileInputStream(source)) {
            return deserialize(fis);
        }
    }

    @Override
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

    /**
     * @param serializable The serializable to resolve the identifier for
     * @return The identifier for the given serializable or null
     */
    protected String resolveIdentifier(Object serializable) {
        return identifiers.get(serializable.getClass());
    }

    /**
     * Tries to retrieve the matching serializer for the given serializable
     * and writes preparation data to the {@link XMLStreamWriter} before
     * letting the {@link Serializer} serialize the serializable
     *
     * @param writer {@link XMLStreamWriter} to write to
     * @param serializable Serializable to find the {@link Serializer} for and to serialize
     * @param helper {@link XmlSerializationHelper} assisting
     * @param <T> Type of the serializable
     * @throws IOException If serialization failed
     */
    protected <T> void resolvedSerialization(XMLStreamWriter writer, T serializable, XmlSerializationHelper helper) throws IOException {
        String identifier = helper.getIdentifierResolver().resolveIdentifier(serializable);

        @SuppressWarnings("unchecked") // legit cast
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

    /**
     * @param identifier The identifier to instantiate for
     * @return A new instance for the given identifier
     * @throws UnknownIdentifierException If there is no such given identifier
     */
    protected Object instantiate(String identifier) throws UnknownIdentifierException {
        Serializer serializer = serializers.get(identifier);

        if (serializer == null) {
            throw new UnknownIdentifierException(identifier);
        }

        return serializer.instantiate();
    }

    /**
     * Tries to retrieve the {@link Serializer} for the current tag on the given
     * {@link XMLStreamReader}, then lets the {@link Serializer} deserialize the
     * current tag
     *
     * @param reader {@link XMLStreamReader} to deserialize from
     * @param emptyInstance Optional empty instance to fill with deserialized data or null to create a new instance
     * @param type Type to enforce the deserialized instance to be
     * @param helper {@link XmlDeserializationHelper} assisting
     * @param <T> Type of the serializable
     * @return The deserialized serializable
     * @throws IOException If deserialization failed
     */
    protected <T> T deserialize(XMLStreamReader reader, T emptyInstance, Class<T> type, XmlDeserializationHelper helper) throws IOException {
        // since its optional, one needs to check whether to instantiate one
        if (emptyInstance == null) {
            emptyInstance = helper.getInstantiationResolver().instantiate(reader.getLocalName(), type);
        }

        @SuppressWarnings("unchecked") // legit cast
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
