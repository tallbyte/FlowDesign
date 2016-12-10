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

/**
 * Created by michael on 09.12.16.
 */
public class XmlConnectionSerializer implements XmlSerializer<Connection> {

    @Override
    public void serialize(XMLStreamWriter writer, Connection connection, XmlSerializationHelper helper) throws IOException {
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

    @Override
    public Connection instantiate() {
        // no new instance is created inside this class
        return null;
    }

    @Override
    public Connection deserialize(XMLStreamReader reader, Connection serializable, XmlDeserializationHelper helper) throws IOException {
        try {
            helper.fastForwardToElementStart(reader);
            Joint source = helper.getDeserializationResolver().deserialize(reader, Joint.class, helper);

            helper.fastForwardToElementStart(reader);
            Joint target = helper.getDeserializationResolver().deserialize(reader, Joint.class, helper);

            return source.join(target);
        } catch (JointJoinException e) {
            throw new IOException("Cannot joint target on source at line: "+reader.getLocation().getLineNumber(), e);

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
