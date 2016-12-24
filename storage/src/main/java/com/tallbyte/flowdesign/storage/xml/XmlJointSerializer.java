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
import com.tallbyte.flowdesign.data.Joint;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by michael on 09.12.16.
 */
public class XmlJointSerializer<T extends Joint> implements XmlSerializer<T> {

    public static final String ATTRIBUTE_LOCATION = "location";
    public static final String ATTRIBUTE_ENTITY   = "entity";

    @Override
    public void serialize(XMLStreamWriter writer, T joint, XmlSerializationHelper helper) throws IOException {
        try {
            //writer.writeAttribute(ATTRIBUTE_LOCATION, joint.getLocation());
            writer.writeAttribute(ATTRIBUTE_ENTITY,   helper.getAssignedIdMap().get(joint.getElement()));

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public T instantiate() {
        // no joint instances created inside this class
        return null;
    }

    @Override
    public T deserialize(XMLStreamReader reader, Joint serializable, XmlDeserializationHelper helper) throws IOException {
        try {
            Map<String, String> attributes = helper.getAttributes(reader);

            String location = attributes.get(ATTRIBUTE_LOCATION);
            String entityId = attributes.get(ATTRIBUTE_ENTITY);

            Element element = helper.getAssignedIdMap().get(entityId);

            if (element == null) {
                throw new IOException(
                        "Failed to find entity: "+entityId +
                                " at line: "+reader.getLocation().getLineNumber()
                );
            }

            /* for (Joint j : element.getJoints()) {
                if (j.getLocation().equals(location)) {
                    return (T)j;
                }
            }*/

            throw new IOException(
                    "Failed to find requested joint for location: "+location +
                            " at line: "+reader.getLocation().getLineNumber()
            );

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
