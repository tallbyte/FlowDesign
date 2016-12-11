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

import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistory;
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistoryEntry;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by michael on 11.12.16.
 */
public class XmlProjectStorageHistorySerializer implements XmlSerializer<ProjectStorageHistory> {

    public static final String ATTRIBUTE_SIZE_LIMIT = "sizeLimit";
    public static final String ELEMENT_ENTRIES      = "entries";

    @Override
    public void serialize(XMLStreamWriter writer, ProjectStorageHistory history, XmlSerializationHelper helper) throws IOException {
        try {
            writer.writeAttribute(ATTRIBUTE_SIZE_LIMIT, Integer.toString(history.getSizeLimit()));

            writer.writeStartElement(ELEMENT_ENTRIES);
            for (ProjectStorageHistoryEntry entry : history.getEntries()) {
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
    public ProjectStorageHistory instantiate() {
        return new ProjectStorageHistory();
    }

    @Override
    public ProjectStorageHistory deserialize(XMLStreamReader reader, ProjectStorageHistory history, XmlDeserializationHelper helper) throws IOException {
        try {
            Map<String, String> attributes = helper.getAttributes(reader);

            history.setSizeLimit(Integer.valueOf(attributes.get(ELEMENT_ENTRIES)));

            helper.fastForwardToElementStart(reader, ELEMENT_ENTRIES);
            helper.foreachElementStartUntil (reader, ELEMENT_ENTRIES, () -> {
                    history.add(
                            helper.getDeserializationResolver().deserialize(
                                    reader,
                                    ProjectStorageHistoryEntry.class,
                                    helper
                            )
                    );
            });

            return history;

        } catch (XMLStreamException e) {
            throw new IOException();
        }
    }
}
