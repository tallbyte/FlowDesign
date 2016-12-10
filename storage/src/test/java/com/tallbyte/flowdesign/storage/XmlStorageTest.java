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

import com.tallbyte.flowdesign.core.JointJoinException;
import com.tallbyte.flowdesign.core.Project;
import com.tallbyte.flowdesign.core.environment.Actor;
import com.tallbyte.flowdesign.core.environment.EnvironmentDiagram;
import com.tallbyte.flowdesign.storage.xml.XmlStorage;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Created by michael on 09.12.16.
 */
public class XmlStorageTest {

    @Test
    public void testStorageHandler() throws IOException, JointJoinException {
        StorageHandler storageHandler = new StorageHandler();

        storageHandler.serialize("xml", "/tmp/test.xml", getNewProject());

        XmlStorage xml = storageHandler.getStorage(XmlStorage.class);

        xml.serialize(storageHandler.getMetadata(), "/tmp/metadata.xml");
        storageHandler.setMetadata(
                xml.deserialize("/tmp/metadata.xml", StorageMetadata.class)
        );

        StorageMetadata       metadata = storageHandler.getMetadata();
        StorageMetadata.Entry entry    = null;
        for (StorageMetadata.Entry e : metadata.getRecentlyUsed()) {
            entry = e;
        }

        Project project = storageHandler.deserialize(
                entry.getType(),
                entry.getPath(),
                Project.class
        );

        Assert.assertTrue(project != null);
    }

    /**
     * This test shall test whether the written serialized data
     * after a second serialization with a deserialization in between
     * is the same as after the first serialization
     *
     * @throws IOException
     */
    @Test
    public void testSimpleSerializationDeserializationSerialization() throws IOException {
        File file = File.createTempFile("testSimpleSerializationDeserializationSerialization", "xml");

        try {
            String fileContent;

            Project project = new Project("test 123");

            project.addDiagram(new EnvironmentDiagram("Env"));


            XmlStorage storage = new XmlStorage();

            storage.serialize(project, file);
            fileContent = loadFileContent(file);

            project = storage.deserialize(file, Project.class);
            storage.serialize(project, file);

            Assert.assertEquals(fileContent, loadFileContent(file));

        } finally {
            Assert.assertTrue(file.delete());
        }
    }

    protected String loadFileContent(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    protected Project getNewProject() throws JointJoinException {
        Project            project = new Project();
        EnvironmentDiagram diagram = new EnvironmentDiagram("TestDiagram");

        Actor actor = new Actor();

        diagram.addElement(actor);
        actor.getJoints().iterator().next().join(
                diagram.getRoot().getJoints().iterator().next()
        );

        project.addDiagram(diagram);
        return project;
    }
}
