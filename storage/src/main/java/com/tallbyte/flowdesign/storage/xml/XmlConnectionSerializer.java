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

import com.tallbyte.flowdesign.data.Connection;
import com.tallbyte.flowdesign.data.Joint;
import com.tallbyte.flowdesign.data.JointJoinException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 09.12.16.
 */
public class XmlConnectionSerializer<T extends Connection> implements XmlSerializer<T> {

    public static final String ATTRIBUTE_TEXT = "text";

    @Override
    public void serialize(XMLStreamWriter writer, T connection, XmlSerializationHelper helper) throws IOException {
        try {
            // write the attributes
            helper.setAttributes(
                    writer,
                    saveAttribute(
                            new HashMap<>(),
                            connection,
                            helper
                    )
            );

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }


        // serialize the source joint
        helper.getSerializationResolver().serialize(
                writer,
                connection.getSource(),
                helper
        );

        // serialize the target joint
        helper.getSerializationResolver().serialize(
                writer,
                connection.getTarget(),
                helper
        );
    }

    /**
     * @param map {@link Map} to put attributes to save into
     * @param connection {@link Connection} to read values from
     * @param helper {@link XmlSerializationHelper} assisting
     * @return The {@link Map} instance either given or a replacement to save instead
     */
    protected Map<String, String> saveAttribute(Map<String, String> map, T connection, XmlSerializationHelper helper) {
        map.put(ATTRIBUTE_TEXT, connection.getText());
        return map;
    }

    /**
     * @param attributes {@link Map} of attributes to read from
     * @param connection {@link Connection} to put into
     * @param helper {@link XmlDeserializationHelper} assisting
     * @return The given {@link Connection}
     */
    protected T loadAttributes(Map<String, String> attributes, T connection, XmlDeserializationHelper helper) {
        connection.setText(attributes.get(ATTRIBUTE_TEXT));
        return connection;
    }

    @Override
    public T instantiate() {
        // no new instance is created inside this class
        return null;
    }

    @Override
    public T deserialize(XMLStreamReader reader, Connection serializable, XmlDeserializationHelper helper) throws IOException {
        try {
            Map<String, String> attributes = helper.getAttributes(reader);

            helper.fastForwardToElementStart(reader);
            Joint source = helper.getDeserializationResolver().deserialize(reader, Joint.class, helper);

            helper.fastForwardToElementStart(reader);
            Joint target = helper.getDeserializationResolver().deserialize(reader, Joint.class, helper);

            return loadAttributes(
                    attributes,
                    (T)source.join(target),
                    helper
            );
        } catch (JointJoinException e) {
            throw new IOException("Cannot joint target on source at line: "+reader.getLocation().getLineNumber(), e);

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
