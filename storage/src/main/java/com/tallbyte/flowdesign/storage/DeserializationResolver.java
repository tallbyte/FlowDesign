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

/**
 * Created by michael on 05.12.16.
 */
@FunctionalInterface
public interface DeserializationResolver<R, RH> {

    /**
     * Resolves the {@link Serializer} and deserializes
     * the next {@link Element}
     *
     * @param read The read handle to read from
     * @param type {@link Class} to enforce
     * @param helper Implementation specific helper class
     * @return The deserialized instance
     * @throws IOException If reading failed
     */
    default <T> T deserialize(R read, Class<T> type, RH helper) throws IOException {
        return deserialize(read, null, type, helper);
    }

    /**
     * Resolves the {@link Serializer} and deserializes
     * the next {@link Element}
     *
     * @param read The read handle to read from
     * @param emptyInstance Optional empty instance to fill with values or null
     * @param type {@link Class} to enforce
     * @param helper Implementation specific helper class
     * @return The deserialized instance
     * @throws IOException If reading failed
     */
    <T> T deserialize(R read, T emptyInstance, Class<T> type, RH helper) throws IOException;
}
