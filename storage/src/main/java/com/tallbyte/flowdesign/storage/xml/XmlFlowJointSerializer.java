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

import com.tallbyte.flowdesign.data.FlowJoint;
import com.tallbyte.flowdesign.data.Joint;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by michael on 12.12.16.
 */
public class XmlFlowJointSerializer extends XmlJointSerializer<FlowJoint> {

    public static final String ATTRIBUTE_DATA_TYPE = "dataType";

    @Override
    public void serialize(XMLStreamWriter writer, FlowJoint joint, XmlSerializationHelper helper) throws IOException {
        super.serialize(writer, joint, helper);

        try {
            if (joint.getDataType() != null) {
                writer.writeAttribute(ATTRIBUTE_DATA_TYPE, joint.getDataType());
            }
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }


    @Override
    public FlowJoint deserialize(XMLStreamReader reader, Joint serializable, XmlDeserializationHelper helper) throws IOException {
        try {
            Map<String, String> attributes  = helper.getAttributes(reader);
            FlowJoint           joint       = super.deserialize(reader, serializable, helper);

            joint.setDataType(attributes.get(ATTRIBUTE_DATA_TYPE));
            return joint;

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
