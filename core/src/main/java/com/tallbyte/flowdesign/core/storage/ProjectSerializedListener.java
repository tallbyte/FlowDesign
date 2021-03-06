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

package com.tallbyte.flowdesign.core.storage;

import com.tallbyte.flowdesign.data.Project;
import com.tallbyte.flowdesign.storage.Storage;

/**
 * Created by michael on 11.12.16.
 */
@FunctionalInterface
public interface ProjectSerializedListener {

    /**
     * @param type The {@link Storage} type that has been used
     * @param path The path being passed to the {@link Storage}
     * @param project The {@link Project} that has been serialized
     */
    void onProjectSerialized(String type, String path, Project project);
}
