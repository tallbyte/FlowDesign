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

import java.io.IOException;

/**
 * Created by michael on 05.12.16.
 */
@FunctionalInterface
public interface SerializationResolver<W, WH> {

    /**
     * @param write The write handle to write to
     * @param serializable The serializable to resolve the {@link Serializer} for
     * @param helper Implementation specific helper class
     * @throws IOException If writing failed
     */
    void serialize(W write, Object serializable, WH helper) throws IOException;
}
