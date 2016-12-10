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
import com.tallbyte.flowdesign.storage.IdentifierResolver;
import com.tallbyte.flowdesign.storage.SerializationResolver;

import javax.xml.stream.XMLStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 09.12.16.
 */
public class XmlSerializationHelper {

    protected final IdentifierResolver identifierResolver;
    protected final SerializationResolver<XMLStreamWriter, XmlSerializationHelper> serializationResolver;

    protected final Map<Element, String> assignedIds = new HashMap<>();

    public XmlSerializationHelper(IdentifierResolver identifierResolver, SerializationResolver<XMLStreamWriter, XmlSerializationHelper> serializationResolver) {
        this.identifierResolver     = identifierResolver;
        this.serializationResolver  = serializationResolver;
    }

    /**
     * @return The {@link Map} of {@link Element}s and their assigned id
     */
    public Map<Element, String> getAssignedIdMap() {
        return assignedIds;
    }

    /**
     * @return The {@link IdentifierResolver} to use
     */
    public IdentifierResolver getIdentifierResolver() {
        return identifierResolver;
    }

    /**
     * @return The {@link SerializationResolver} to use
     */
    public SerializationResolver<XMLStreamWriter, XmlSerializationHelper> getSerializationResolver() {
        return serializationResolver;
    }
}
