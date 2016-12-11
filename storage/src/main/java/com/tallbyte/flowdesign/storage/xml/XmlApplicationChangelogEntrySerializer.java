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

import com.tallbyte.flowdesign.data.ui.storage.ApplicationChangelogEntry;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 11.12.16.
 */
public class XmlApplicationChangelogEntrySerializer implements XmlSerializer<ApplicationChangelogEntry> {

    public static final String ATTRIBUTE_TIME_MILLIS    = "timeMillis";
    public static final String ATTRIBUTE_COMMIT         = "commit";
    public static final String ATTRIBUTE_SUMMARY        = "summary";
    public static final String ELEMENT_DETAILS          = "details";
    public static final String ELEMENT_DETAIL           = "detail";

    @Override
    public void serialize(XMLStreamWriter writer, ApplicationChangelogEntry entry, XmlSerializationHelper helper) throws IOException {
        try {
            writer.writeAttribute(ATTRIBUTE_TIME_MILLIS, Long.toString(entry.getTimeMillis()));
            writer.writeAttribute(ATTRIBUTE_COMMIT,      entry.getCommit());
            writer.writeAttribute(ATTRIBUTE_SUMMARY,     entry.getSummary());

            writer.writeStartElement(ELEMENT_DETAILS);
            for (String detail : entry.getDetailed()) {
                writer.writeStartElement(ELEMENT_DETAIL);
                writer.writeCharacters(detail);
                writer.writeEndElement();
            }
            writer.writeEndElement();

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public ApplicationChangelogEntry instantiate() {
        // not here
        return null;
    }

    @Override
    public ApplicationChangelogEntry deserialize(XMLStreamReader reader, ApplicationChangelogEntry serializable, XmlDeserializationHelper helper) throws IOException {
        try {
            Map<String, String> attributes = helper.getAttributes(reader);
            List<String>        details    = new ArrayList<>();

            helper.fastForwardToElementStart(reader, ELEMENT_DETAILS);
            helper.foreachElementStartUntil (reader, ELEMENT_DETAILS, () -> {
                details.add(
                        helper.getCharacters(reader)
                );
            });



            return new ApplicationChangelogEntry(
                    Long.valueOf(
                            attributes.get(ATTRIBUTE_TIME_MILLIS)
                    ),
                    attributes.get(ATTRIBUTE_COMMIT),
                    attributes.get(ATTRIBUTE_SUMMARY),
                    details
            );
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
}
