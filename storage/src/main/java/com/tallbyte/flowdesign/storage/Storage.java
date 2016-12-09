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

/**
 * Created by michael on 09.12.16.
 */
public interface Storage<S extends Storage, O, I, OE extends Throwable, IE extends Throwable, R, W, RH, WH> {

    /**
     * @param serializable The serializable to serialize
     * @param output The output to to serialize to
     * @throws OE If serialization failed
     */
    void serialize(Object serializable, O output) throws OE;

    /**
     * @param input The input to deserialize from
     * @return The deserialized serializable
     * @throws IE If deserialization failed
     */
    Object deserialize(I input) throws IE;

    /**
     * @param input The input to deserialize from
     * @param type The class to enforce the return type to be
     * @param <T> The type that is enforced
     * @return The deserialized serializable
     * @throws IE If deserialization failed or type enforcement failed
     */
    <T> T deserialize(I input, Class<T> type) throws IE;

    /**
     * @param type The type to register the {@link Serializer} for
     * @param serializer {@link Serializer} to register for the given type
     * @param <T> Type
     * @return itself
     */
    <T> S register(Class<T> type, Serializer<T, R, W, RH, WH> serializer);
}
