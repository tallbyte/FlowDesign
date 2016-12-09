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

/**
 * Created by michael on 09.12.16.
 *
 * Instantiates a {@link Element} for its given type-identifier.
 * The type-identifier of an {@link Element} can be retrieved by
 * calling {@link IdentifierResolver#resolveIdentifier(Object)}
 */
@FunctionalInterface
public interface InstantiationResolver {

    /**
     * @param identifier Identifier to instantiate an {@link Element} for
     * @return A new instance of the {@link Element} for the given identifier
     * @throws UnknownIdentifierException If the given identifier is unknown
     */
    Object instantiate(String identifier) throws UnknownIdentifierException;

    /**
     *
     * @param identifier Identifier to instantiate an {@link Element} for
     * @param type Class to force type compatibility
     * @param <T> Enforced type
     * @return A new instance of the {@link Element} for the given identifier
     * @throws UnknownIdentifierException If the given identifier is unknown or if the actual type differs
     */
    default <T> T instantiate(String identifier, Class<T> type) throws UnknownIdentifierException {
        Object instance = instantiate(identifier);

        if (type.isInstance(instance)) {
            return type.cast(instance);
        }

        // act as if there is no such identifier
        throw new UnknownIdentifierException(identifier);
    }
}
