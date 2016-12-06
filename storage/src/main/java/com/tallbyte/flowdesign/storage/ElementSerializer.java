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

import com.tallbyte.flowdesign.core.Element;

import java.io.IOException;
import java.util.function.Function;

/**
 * Created by michael on 05.12.16.
 */
public interface ElementSerializer<E extends Element, R, W> {

    /**
     * @param write The write handle to use for writing
     * @param element The {@link Element} to serialize
     * @param elementSerializer The {@link ElementSerializationResolver} to call to serialize
     *                          {@link Element}s nested in the {@link Element} to serialize
     * @param elementResolver The {@link Function} that resolves linked - and thus not nested - {@link Element}s
     *                        to an unique identifier
     * @throws IOException If writing and/or serialization failed
     */
    void serialize(W write, E element, ElementSerializationResolver<W> elementSerializer, Function<Element, String> elementResolver) throws IOException;

    /**
     * @return A new empty instance of the type this {@link ElementSerializer} is responsible for
     */
    E instantiate();

    /**
     * @param read The read handle
     * @param element Target {@link Element} to write read values to
     * @param elementDeserializer The {@link ElementDeserializationResolver} to call to deserialize
     *                            {@link Element}s nested in the {@link Element}
     * @param elementResolver The {@link Function} t hat resolves identifiers of linked - and thus not nested -
     *                        {@link Element}s
     * @return The deserialized {@link Element}
     * @throws IOException If reading and/or deserialization failed
     */
    E deserialize(R read, E element, ElementDeserializationResolver<R> elementDeserializer, Function<String, Element> elementResolver)  throws IOException;
}
