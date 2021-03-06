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
import com.tallbyte.flowdesign.data.JointGroup;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Created by michael on 09.12.16.
 */
public class XmlJointSerializer<T extends Joint> implements XmlSerializer<T> {

    public static final String ATTRIBUTE_GROUP  = "group";
    public static final String ATTRIBUTE_INDEX  = "index";
    public static final String ATTRIBUTE_ENTITY = "entity";

    @Override
    public void serialize(XMLStreamWriter writer, T joint, XmlSerializationHelper helper) throws IOException {
        try {
            //writer.writeAttribute(ATTRIBUTE_LOCATION, joint.getLocation());
            writer.writeAttribute(ATTRIBUTE_ENTITY,   helper.getAssignedIdMap().get(joint.getElement()));

            for (String groupName: joint.getElement().getJointGroups()) {
                int index = 0;
                for (Object j : joint.getElement().getJointGroup(groupName).getJoints()) {
                    if (Objects.equals(j, joint)) {

                        writer.writeAttribute(ATTRIBUTE_INDEX, Integer.toString(index));
                        writer.writeAttribute(ATTRIBUTE_GROUP, groupName);

                        return; // done
                    }
                    ++index;
                }
            }

            throw new IOException("Could not resolve index of joint "+joint);


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

            int    index    = Integer.valueOf(attributes.get(ATTRIBUTE_INDEX));
            String group    = attributes.get(ATTRIBUTE_GROUP);
            String entityId = attributes.get(ATTRIBUTE_ENTITY);

            Element element = helper.getAssignedIdMap().get(entityId);

            if (element == null) {
                throw new IOException(
                        "Failed to find entity: "+entityId +
                                " at line: "+reader.getLocation().getLineNumber()
                );
            }

            JointGroup<?> jointGroup = element.getJointGroup(group);

            if (jointGroup == null) {
                throw new IOException("Failed to find requested joint group of name: "+group +
                        " at line: "+reader.getLocation().getLineNumber()
                );
            }

            if (index < 0 || index >= jointGroup.getMaxJoints()) {
                throw new IOException("Invalid index="+index+" where max="+jointGroup.getMaxJoints() +
                        " at line: "+reader.getLocation().getLineNumber()
                );
            }

            return (T)jointGroup.getJoint(index);

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
