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

import com.tallbyte.flowdesign.storage.StorageMetadata;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by michael on 10.12.16.
 */
public class XmlStorageMetadataEntrySerializer implements XmlSerializer<StorageMetadata.Entry> {

    public static final String ATTRIBUTE_TYPE = "type";
    public static final String ATTRIBUTE_PATH = "path";
    public static final String ATTRIBUTE_TIME = "timeMillis";

    @Override
    public void serialize(XMLStreamWriter writer, StorageMetadata.Entry serializable, XmlSerializationHelper helper) throws IOException {
        try {
            writer.writeAttribute(ATTRIBUTE_TYPE, serializable.getType());
            writer.writeAttribute(ATTRIBUTE_PATH, serializable.getPath());
            writer.writeAttribute(ATTRIBUTE_TIME, Long.toString(serializable.getTimeMillis()));

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public StorageMetadata.Entry instantiate() {
        // not instantiated here
        return null;
    }

    @Override
    public StorageMetadata.Entry deserialize(XMLStreamReader reader, StorageMetadata.Entry serializable, XmlDeserializationHelper helper) throws IOException {
        try {
            Map<String, String> attributes = helper.getAttributes(reader);

            return new StorageMetadata.Entry(
                    attributes.get(ATTRIBUTE_TYPE),
                    attributes.get(ATTRIBUTE_PATH),
                    Long.valueOf(
                            attributes.get(ATTRIBUTE_TIME)
                    )
            );

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
