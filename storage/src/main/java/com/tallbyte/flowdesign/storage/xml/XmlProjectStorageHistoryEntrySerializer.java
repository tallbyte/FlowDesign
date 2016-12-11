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

import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistoryEntry;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by michael on 11.12.16.
 */
public class XmlProjectStorageHistoryEntrySerializer implements XmlSerializer<ProjectStorageHistoryEntry> {

    public static final String ATTRIBUTE_TYPE           = "type";
    public static final String ATTRIBUTE_PATH           = "path";
    public static final String ATTRIBUTE_PROJECT_NAME   = "projectName";
    public static final String ATTRIBUTE_TIME_MILLIS    = "timeMillis";

    @Override
    public void serialize(XMLStreamWriter writer, ProjectStorageHistoryEntry entry, XmlSerializationHelper helper) throws IOException {
        try {
            writer.writeAttribute(ATTRIBUTE_TYPE,           entry.getType());
            writer.writeAttribute(ATTRIBUTE_PATH,           entry.getPath());
            writer.writeAttribute(ATTRIBUTE_PROJECT_NAME,   entry.getProjectName());
            writer.writeAttribute(ATTRIBUTE_TIME_MILLIS,    Long.toString(entry.getTimeMillis()));

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public ProjectStorageHistoryEntry instantiate() {
        // not going to happen here
        return null;
    }

    @Override
    public ProjectStorageHistoryEntry deserialize(XMLStreamReader reader, ProjectStorageHistoryEntry serializable, XmlDeserializationHelper helper) throws IOException {
        try {
            Map<String, String> attributes = helper.getAttributes(reader);

            return new ProjectStorageHistoryEntry(
                    attributes.get(ATTRIBUTE_TYPE),
                    attributes.get(ATTRIBUTE_PATH),
                    attributes.get(ATTRIBUTE_PROJECT_NAME),
                    Long.valueOf(
                            attributes.get(ATTRIBUTE_TIME_MILLIS)
                    )
            );
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
