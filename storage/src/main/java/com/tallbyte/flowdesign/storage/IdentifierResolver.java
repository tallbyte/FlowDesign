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
 * Resolves the type-identifier for the given serializable for serialization.
 * The type-identifier is later used in {@link InstantiationResolver}
 * to instantiate the same type of serializable on deserialization
 */
@FunctionalInterface
public interface IdentifierResolver {

    /**
     * @param serializable The serializable to resolve the identifier for
     * @return The identifier for the given {@link Element}
     */
    String resolveIdentifier(Object serializable);
}