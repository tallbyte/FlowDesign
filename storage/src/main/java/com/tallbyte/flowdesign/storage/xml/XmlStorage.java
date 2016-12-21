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

import com.tallbyte.flowdesign.data.*;
import com.tallbyte.flowdesign.data.environment.*;
import com.tallbyte.flowdesign.data.environment.System;
import com.tallbyte.flowdesign.data.flow.*;
import com.tallbyte.flowdesign.data.mask.MaskDiagram;
import com.tallbyte.flowdesign.data.mask.Rectangle;
import com.tallbyte.flowdesign.data.mask.SelfReference;
import com.tallbyte.flowdesign.data.ui.storage.ApplicationChangelog;
import com.tallbyte.flowdesign.data.ui.storage.ApplicationChangelogEntry;
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistory;
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistoryEntry;
import com.tallbyte.flowdesign.storage.Serializer;
import com.tallbyte.flowdesign.storage.Storage;
import com.tallbyte.flowdesign.storage.UnknownIdentifierException;
import com.tallbyte.flowdesign.storage.xml.environment.*;
import com.tallbyte.flowdesign.storage.xml.flow.*;
import com.tallbyte.flowdesign.storage.xml.mask.XmlRectangleSerializer;
import com.tallbyte.flowdesign.storage.xml.mask.XmlMaskDiagramSerializer;
import com.tallbyte.flowdesign.storage.xml.mask.XmlSelfReferenceSerializer;

import javax.xml.stream.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 09.12.16.
 */
public class XmlStorage implements Storage<XmlStorage, XMLStreamReader, XMLStreamWriter, XmlDeserializationHelper, XmlSerializationHelper> {

    public static final char MARKER_RESOURCE = '!';

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
        register(Project                    .class, new XmlProjectSerializer());
        register(DataType                   .class, new XmlDataTypeSerializer());

        register(ProjectStorageHistory      .class, new XmlProjectStorageHistorySerializer());
        register(ProjectStorageHistoryEntry .class, new XmlProjectStorageHistoryEntrySerializer());

        register(ApplicationChangelog       .class, new XmlApplicationChangelogSerializer());
        register(ApplicationChangelogEntry  .class, new XmlApplicationChangelogEntrySerializer());

        // Connection related serializer
        register(Connection                 .class, new XmlConnectionSerializer<>());
        register(Joint                      .class, new XmlJointSerializer<>());
        register(DependencyConnection       .class, new XmlDependencyConnectionSerializer());
        register(DependencyJoint            .class, new XmlDependencyJointSerializer());
        register(FlowConnection             .class, new XmlFlowConnectionSerializer());
        register(FlowJoint                  .class, new XmlFlowJointSerializer());

        // EnvironmentDiagram and elements
        register(EnvironmentDiagram         .class, new XmlEnvironmentDiagramSerializer());
        register(Actor                      .class, new XmlActorSerializer());
        register(Adapter                    .class, new XmlAdapterSerializer());
        register(Resource                   .class, new XmlResourceSerializer());
        register(System                     .class, new XmlSystemSerializer());

        // FlowDiagram and elements
        register(FlowDiagram                .class, new XmlFlowDiagramSerializer());
        register(Start                      .class, new XmlStartSerializer());
        register(Portal                     .class, new XmlPortalSerializer());
        register(Split                      .class, new XmlSplitSerializer());
        register(Join                       .class, new XmlJoinSerializer());
        register(ResourceAccess             .class, new XmlResourceAccessSerializer());
        register(StateAccess                .class, new XmlStateAccessSerializer());
        register(Operation                  .class, new XmlOperationSerializer());
        register(End                        .class, new XmlEndSerializer());

        // MaskDiagram and elements
        register(MaskDiagram                .class, new XmlMaskDiagramSerializer());
        register(Rectangle                  .class, new XmlRectangleSerializer());
        register(SelfReference              .class, new XmlSelfReferenceSerializer());
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
    public void serialize(Object serializable, String fileOut) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileOut)) {
            serialize(serializable, fos);
        }
    }


    /**
     * Adds indent to the output depending on the configuration of this {@link XmlStorage}
     *
     * @param serializable The serializable to serialize
     * @param outputStream The {@link OutputStream} to to write to
     * @throws IOException If serialization failed
     */
    public void serialize(Object serializable, OutputStream outputStream) throws IOException {
        if (indentation) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            serializeImpl(serializable, baos);

            try {
                Transformer transformer;
                transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
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
    public <T> T deserialize(String fileIn, Class<T> type) throws IOException {
        if (fileIn.length() > 0 && fileIn.charAt(0) == MARKER_RESOURCE) {
            return deserialize(getClass().getResourceAsStream(fileIn.substring(1)), type);
        }

        try (FileInputStream fis = new FileInputStream(fileIn)) {
            return deserialize(fis, type);
        }
    }

    /**
     * @param input The {@link InputStream} to read from
     * @param type The type enforcement on the deserialized serializable
     * @param <T> Type being enforced
     * @return The deserialized serializable
     * @throws IOException If deserialization failed or the type enforcement failed
     */
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
        return deserialize(source, Object.class);
    }

    @Override
    public Object deserialize(String fileIn) throws IOException {
        return deserialize(fileIn, Object.class);
    }

    /**
     * @param inputStream The {@link InputStream} to read from
     * @return The deserialized serializable
     * @throws IOException If deserialization failed
     */
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
