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

package com.tallbyte.flowdesign.storage.xml.flow;

import com.tallbyte.flowdesign.data.flow.OperationalUnit;
import com.tallbyte.flowdesign.storage.xml.XmlDeserializationHelper;
import com.tallbyte.flowdesign.storage.xml.XmlSerializationHelper;

import javax.xml.stream.XMLStreamException;
import java.util.Map;

/**
 * Created by michael on 12.12.16.
 */
public abstract class XmlOperationalUnitSerializer<O extends OperationalUnit> extends XmlFlowDiagramElementSerializer<O> {

    public static final String ATTRIBUTE_STATE = "state";

    @Override
    protected Map<String, String> saveAttributes(Map<String, String> attributes, O element, XmlSerializationHelper helper) throws XMLStreamException {
        super.saveAttributes(attributes, element, helper);

        attributes.put(ATTRIBUTE_STATE, element.getState());

        return attributes;
    }

    @Override
    protected void loadAttributes(Map<String, String> attributes, O element, XmlDeserializationHelper helper) throws XMLStreamException {
        super.loadAttributes(attributes, element, helper);

        element.setState(attributes.get(ATTRIBUTE_STATE));
    }

}
