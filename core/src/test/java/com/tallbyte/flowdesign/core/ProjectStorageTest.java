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

package com.tallbyte.flowdesign.core;

import com.tallbyte.flowdesign.core.storage.ProjectStorage;
import com.tallbyte.flowdesign.data.Project;
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistoryEntry;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;

/**
 * Created by michael on 11.12.16.
 */
public class ProjectStorageTest {

    @Test
    public void testHistory() throws IOException{
        Path path = Files.createTempDirectory("flowdesign.junit.testHistory.");

        List<String> names = Arrays.asList(
                "Test3",
                "Test2",
                "Test1"
        );

        List<String> paths = Arrays.asList(
                path.resolve("test3.xml").toString(),
                path.resolve("test2.xml").toString(),
                path.resolve("test1.xml").toString()
        );

        paths.forEach(System.out::println);

        Project project3 = new Project(names.get(0));
        Project project2 = new Project(names.get(1));
        Project project1 = new Project(names.get(2));


        ProjectStorage storage = new ProjectStorage();

        storage.serialize(paths.get(0), project3);
        storage.serialize(paths.get(1), project2);
        storage.serialize(paths.get(2), project1);

        String pathHistory = path.resolve("history.xml").toString();
        storage.serializeHistory  (pathHistory);
        storage.deserializeHistory(pathHistory);

        // do it twice to be sure serialization of deserialized history works
        storage.serializeHistory  (pathHistory);
        storage.deserializeHistory(pathHistory);


        Assert.assertTrue(paths.size() == storage.getHistory().getSize());

        int index = 0;
        for (ProjectStorageHistoryEntry entry : storage.getHistory().getEntries()) {
            Assert.assertTrue(paths.get(index).equals(entry.getPath()));
            Assert.assertTrue(names.get(index).equals(entry.getProjectName()));
            index++;
        }

        // cleanup
        deleteDirectoryRecursive(path);
    }

    public void deleteDirectoryRecursive(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }


            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
