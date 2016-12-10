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

package com.tallbyte.flowdesign.storage;

import com.tallbyte.flowdesign.data.Diagram;

import java.io.IOException;

/**
 * Created by michael on 05.12.16.
 */
public interface DiagramSerializer<D extends Diagram, R, W, RH, WH> {

    /**
     * @param writer The write handle to use for writing
     * @param diagram The {@link Diagram} to serialize
     * @param helper Implementation specific helper class for serialization
     * @throws IOException If writing and/or serialization failed
     */
    void serialize(W writer, D diagram, WH helper) throws IOException;

    /**
     * @param reader The read handle
     * @param helper Implementation specific helper clas for deserialization
     * @return The deserialized {@link Diagram}
     * @throws IOException If reading and/or deserialization failed
     */
    D deserialize(R reader, RH helper)  throws IOException;
}
