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

import com.tallbyte.flowdesign.core.environment.Actor;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * Created by michael on 09.12.16.
 */
public class XmlActorSerializer extends XmlElementSerializer<Actor> {

    @Override
    public void serialize(XMLStreamWriter writer, Actor serializable, XmlSerializationHelper helper) throws IOException {
        super.serialize(writer, serializable, helper);
    }

    @Override
    public Actor instantiate() {
        return new Actor();
    }

    @Override
    public Actor deserialize(XMLStreamReader reader, Actor actor, XmlDeserializationHelper helper) throws IOException {
        super.deserialize(reader, actor, helper);
        return actor;
    }
}
