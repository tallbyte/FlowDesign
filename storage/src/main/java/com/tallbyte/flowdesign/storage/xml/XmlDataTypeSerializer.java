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

import com.tallbyte.flowdesign.data.DataType;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 21.12.16.
 */
public class XmlDataTypeSerializer implements XmlSerializer<DataType> {

    public static final String ATTRIBUTE_NAME           = "name";
    public static final String ELEMENT_GENERICS         = "generics";

    @Override
    public void serialize(XMLStreamWriter writer, DataType dataType, XmlSerializationHelper helper) throws IOException {
        try {
            writer.writeAttribute(ATTRIBUTE_NAME, dataType.getFullClassName());

            // serialize the generics
            writer.writeStartElement(ELEMENT_GENERICS);
            for (DataType generic : dataType.getGenerics()) {
                helper.getSerializationResolver().serialize(
                        writer,
                        generic,
                        helper
                );
            }
            writer.writeEndElement();

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public DataType instantiate() {
        return null;
    }

    @Override
    public DataType deserialize(XMLStreamReader reader, DataType dataType, XmlDeserializationHelper helper) throws IOException {
        try {
            Map<String, String> attributes = helper.getAttributes(reader);
            List<DataType>      generics   = new ArrayList<>();


            helper.fastForwardToElementStart(reader, ELEMENT_GENERICS);
            helper.foreachElementStartUntil(reader, ELEMENT_GENERICS, () -> {
                generics.add(
                        helper.getDeserializationResolver().deserialize(
                                reader,
                                DataType.class,
                                helper
                        )
                );
            });

            return new DataType(attributes.get(ATTRIBUTE_NAME), generics.toArray(new DataType[0]));

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
