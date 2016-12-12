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
import com.tallbyte.flowdesign.data.DependencyConnection;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * Created by michael on 12.12.16.
 */
public class XmlDependencyConnectionSerializer extends XmlConnectionSerializer<DependencyConnection> {

    @Override
    public void serialize(XMLStreamWriter writer, DependencyConnection connection, XmlSerializationHelper helper) throws IOException {
        super.serialize(writer, connection, helper);
    }

    @Override
    public DependencyConnection instantiate() {
        return super.instantiate();
    }

    @Override
    public DependencyConnection deserialize(XMLStreamReader reader, Connection serializable, XmlDeserializationHelper helper) throws IOException {
        return super.deserialize(reader, serializable, helper);
    }
}
