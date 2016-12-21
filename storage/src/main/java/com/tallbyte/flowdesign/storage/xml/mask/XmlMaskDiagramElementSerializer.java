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

package com.tallbyte.flowdesign.storage.xml.mask;

import com.tallbyte.flowdesign.data.mask.MaskDiagramElement;
import com.tallbyte.flowdesign.storage.xml.XmlDeserializationHelper;
import com.tallbyte.flowdesign.storage.xml.XmlElementSerializer;
import com.tallbyte.flowdesign.storage.xml.XmlSerializationHelper;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Created by michael on 21.12.16.
 */
public abstract class XmlMaskDiagramElementSerializer<T extends MaskDiagramElement> extends XmlElementSerializer<T> {

    @Override
    protected void writeAttributes(XMLStreamWriter writer, T element, XmlSerializationHelper helper) throws XMLStreamException {
        super.writeAttributes(writer, element, helper);
    }

    @Override
    protected void readAttributes(XMLStreamReader reader, T element, XmlDeserializationHelper helper) throws XMLStreamException {
        super.readAttributes(reader, element, helper);
    }
}
