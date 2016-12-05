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
import com.tallbyte.flowdesign.core.Project;

import java.io.*;

/**
 * Created by michael on 05.12.16.
 */
public interface ProjectSerializer<R, W> {

    /**
     * @param project {@link Project} to serialize
     * @param file {@link File} to write to
     * @throws IOException If writing to the given {@link File} failed
     */
    default void serialize(Project project, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            serialize(project, fos);
        }
    }

    /**
     * @param project {@link Project} to serialize
     * @param outputStream Target {@link OutputStream} to write to
     * @throws IOException If writing to the given {@link OutputStream} failed
     */
    void serialize(Project project, OutputStream outputStream) throws IOException;

    /**
     * @param file The {@link File} to read the {@link Project} from
     * @return The loaded {@link Project}
     * @throws IOException If reading from the given {@link File} failed
     */
    default Project deserialize(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return deserialize(fis);
        }
    }

    /**
     * @param inputStream {@link InputStream} to read the {@link Project} from
     * @return The loaded {@link Project}
     * @throws IOException If reading from the given {@link InputStream} failed
     */
    Project deserialize(InputStream inputStream) throws IOException;


    /**
     * @param type The actual {@link Diagram} class to register the {@link DiagramSerializer} for
     * @param serializer The {@link DiagramSerializer} to register
     * @param <T> The {@link Diagram} type
     */
    <T extends Diagram> void registerDiagramSerializer(Class<T> type, DiagramSerializer<T, R, W> serializer);

    /**
     * @param type The actual {@link Element} class to register the {@link ElementSerializer} for
     * @param serializer {@link ElementSerializer} to register
     * @param <T> The {@link Element} type
     */
    <T extends Element> void registerElementSerializer(Class<T> type, ElementSerializer<T, R, W> serializer);
}
