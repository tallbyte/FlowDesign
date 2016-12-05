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

import com.tallbyte.flowdesign.core.Diagram;
import com.tallbyte.flowdesign.core.Element;

import java.io.IOException;

/**
 * Created by michael on 05.12.16.
 */
public interface DiagramSerializer<D extends Diagram, R, W> {

    /**
     * @param write The write handle to use for writing
     * @param diagram The {@link Diagram} to serialize
     * @param elementSerializer The {@link ElementSerializationResolver} to call to serialize
     *                          {@link Element}s nested in the {@link Diagram} to serialize
     * @throws IOException If writing and/or serialization failed
     */
    void serialize(W write, D diagram, ElementSerializationResolver<W> elementSerializer) throws IOException;

    /**
     * @param read The read handle
     * @param elementDeserializer The {@link ElementDeserializationResolver} to call to deserialize
     *                            {@link Element}s nested in the {@link Diagram}
     * @return The deserialized {@link Diagram}
     * @throws IOException If reading and/or deserialization failed
     */
    D deserialize(R read, ElementDeserializationResolver<R> elementDeserializer)  throws IOException;
}
