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
public class XmlStorageMetadataSerializer implements XmlSerializer<StorageMetadata> {

    public static final String ELEMENT_ENTRIES                  = "entries";

    public static final String ATTRIBUTE_RECENTLY_USED_SIZE     = "recentlyUsedSize";
    public static final String ATTRIBUTE_DEFAULT_STORAGE_TYPE   = "defaultStorageType";

    @Override
    public void serialize(XMLStreamWriter writer, StorageMetadata serializable, XmlSerializationHelper helper) throws IOException {
        try {
            writer.writeAttribute(ATTRIBUTE_RECENTLY_USED_SIZE,   Integer.toString(serializable.getRecentlyUsedSize()));
            writer.writeAttribute(ATTRIBUTE_DEFAULT_STORAGE_TYPE, serializable.getDefaultStorageType());

            writer.writeStartElement(ELEMENT_ENTRIES);
            for (StorageMetadata.Entry entry : serializable.getRecentlyUsed()) {
                helper.getSerializationResolver().serialize(
                        writer,
                        entry,
                        helper
                );
            }
            writer.writeEndElement();

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public StorageMetadata instantiate() {
        return new StorageMetadata();
    }

    @Override
    public StorageMetadata deserialize(XMLStreamReader reader, StorageMetadata serializable, XmlDeserializationHelper helper) throws IOException {
        try {
            Map<String, String> attributes = helper.getAttributes(reader);

            serializable.setDefaultStorageType(attributes.get(ATTRIBUTE_DEFAULT_STORAGE_TYPE));
            serializable.setRecentlyUsedSize(Integer.valueOf(attributes.get(ATTRIBUTE_RECENTLY_USED_SIZE)));

            // load the entries
            serializable.getRecentlyUsed().clear();
            helper.fastForwardToElementStart(reader, ELEMENT_ENTRIES);
            helper.foreachElementStartUntil (reader, ELEMENT_ENTRIES, () -> {
                serializable.getRecentlyUsed().add(
                        helper.getDeserializationResolver().deserialize(
                                reader,
                                StorageMetadata.Entry.class,
                                helper
                        )
                );
            });

            return serializable;

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
