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

import com.tallbyte.flowdesign.data.Element;
import com.tallbyte.flowdesign.storage.DeserializationResolver;
import com.tallbyte.flowdesign.storage.InstantiationResolver;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 09.12.16.
 */
public class XmlDeserializationHelper {

    protected final InstantiationResolver instantiationResolver;
    protected final DeserializationResolver<XMLStreamReader, XmlDeserializationHelper> deserializationResolver;

    protected final Map<String, Element> assignedIds = new HashMap<>();

    public XmlDeserializationHelper(InstantiationResolver instantiationResolver, DeserializationResolver<XMLStreamReader, XmlDeserializationHelper> deserializationResolver) {
        this.instantiationResolver   = instantiationResolver;
        this.deserializationResolver = deserializationResolver;
    }

    /**
     * @return The {@link Map} of ids and their assigned {@link Element}
     */
    public Map<String, Element> getAssignedIdMap() {
        return assignedIds;
    }

    /**
     * @return The {@link InstantiationResolver} to use to instantiate serializabels
     */
    public InstantiationResolver getInstantiationResolver() {
        return instantiationResolver;
    }

    /**
     * @return The {@link DeserializationResolver} to use to deserialize serializables
     */
    public DeserializationResolver<XMLStreamReader, XmlDeserializationHelper> getDeserializationResolver() {
        return deserializationResolver;
    }

    /**
     * Sets the position of the {@link XMLStreamReader} to
     * the next {@link XMLStreamConstants#START_ELEMENT}
     *
     * @param reader {@link XMLStreamReader} to fast forward
     * @throws XMLStreamException If reading failed
     */
    public void fastForwardToElementStart(XMLStreamReader reader) throws XMLStreamException {
        // fast forward to the start of next element
        while (reader.hasNext()) {
            switch (reader.next()) {
                //case XMLStreamConstants.END_ELEMENT:
                case XMLStreamConstants.START_ELEMENT:
                    return;
            }
        }
    }

    /**
     * Sets the position of the {@link XMLStreamReader} to
     * the next {@link XMLStreamConstants#START_ELEMENT}
     * and checks whether the {@link XMLStreamReader#getLocalName()}
     * matches with the given localName
     *
     * @param reader {@link XMLStreamReader} to fast forward
     * @param localName Local name to check for
     * @throws XMLStreamException If reading failed or if the local name mismatches
     */
    public void fastForwardToElementStart(XMLStreamReader reader, String localName) throws XMLStreamException {
        // to the next element
        fastForwardToElementStart(reader);

        // check whether the requested localName matches
        if (!localName.equals(reader.getLocalName())) {
            throw new XMLStreamException(
                    "Unexpected element tag: "+reader.getLocalName() +
                            " expected: " + localName +
                            " at line: " + reader.getLocation().getLineNumber()
            );
        }
    }

    /**
     * Calls {@link ThrowableCallback#call()} for each {@link XMLStreamConstants#START_ELEMENT}
     * until the {@link XMLStreamConstants#END_ELEMENT}s localName equals to the given one
     *
     * @param reader {@link XMLStreamReader} to read from
     * @param localNameEnd The localName to end the loop
     * @param callback {@link ThrowableCallback} to call for each element start
     * @param <T> Type of possible exception in the callback
     * @throws XMLStreamException If reading failed
     * @throws T If the {@link ThrowableCallback} encountered an error
     */
    public <T extends Exception> void foreachElementStartUntil(XMLStreamReader reader, String localNameEnd, ThrowableCallback<T> callback) throws XMLStreamException, T {
        connectionLoop:
        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    // inform about the element start
                    callback.call();

                    // for unknown reason, this is also called in the middle of the document... me does not understand
                    // but at least reader.hasNext() returns only false on the read document end - I believe
                    /*
                case XMLStreamConstants.END_DOCUMENT:
                    System.out.println("foreachElementStartUntil: END_DOCUMENT for localName="+reader.getLocalName());
                    System.out.flush();
                    break connectionLoop;
                    */

                case XMLStreamConstants.END_ELEMENT:
                    if (!reader.isCharacters() && localNameEnd.equals(reader.getLocalName())) {
                        /*
                        System.out.println("foreachElementStartUntil: break for localName="+reader.getLocalName());
                        System.out.flush();
                        */
                        // list processed
                        break connectionLoop;
                    }
                    break;

                default:
                    break;
            }
        }
        /*
        System.out.println("foreachElementStartUntil: while ended, localName="+reader.getLocalName()+", hasNext="+reader.hasNext());
        System.out.flush();
        */
    }

    /**
     * Reads all available attributes from the current state of the
     * given {@link XMLStreamReader}
     *
     * @param reader {@link XMLStreamReader} to read the attributes from
     * @return A new {@link Map} with all attributes available
     * @throws XMLStreamException If reading the attributes failed
     */
    public Map<String, String> getAttributes(XMLStreamReader reader) throws XMLStreamException {
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < reader.getAttributeCount(); ++i) {
            map.put(
                    reader.getAttributeLocalName(i),
                    reader.getAttributeValue(i)
            );
        }

        return map;
    }

    /**
     * Reads the text before the next element start the next element end, therefore
     * the position is expected at {@link XMLStreamConstants#ATTRIBUTE} or
     * {@link XMLStreamConstants#START_ELEMENT}
     *
     * @param reader {@link XMLStreamReader} to read from
     * @return The read characters
     * @throws XMLStreamException If reading the text failed
     */
    public String getCharacters(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    throw new XMLStreamException(
                            "Unexpected element: "+reader.getLocalName()+
                                    " expected characters" +
                                    " at line: "+reader.getLocation().getLineNumber()
                    );

                case XMLStreamConstants.END_ELEMENT:
                    throw new XMLStreamException(
                            "Unexpected element end: "+reader.getLocalName()+
                                    " expected characters" +
                                    " at line: "+reader.getLocation().getLineNumber()
                    );

                case XMLStreamConstants.CHARACTERS:
                    return reader.getText();

                default:
                    // nothing to do?
                    break;
            }
        }

        throw new XMLStreamException(
                "End of stream, expected characters" +
                        " at line: "+reader.getLocation().getLineNumber()
        );
    }

    @FunctionalInterface
    public interface ThrowableCallback<T extends Throwable> {

        void call() throws T;
    }
}
