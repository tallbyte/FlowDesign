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

import com.tallbyte.flowdesign.core.Project;
import com.tallbyte.flowdesign.core.environment.EnvironmentDiagram;
import com.tallbyte.flowdesign.storage.xml.XmlStorage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by michael on 09.12.16.
 */
public class TestMain {

    public static void main(String[] args) throws IOException {
        Project project = new Project("test 123");

        project.addDiagram(new EnvironmentDiagram("Env"));

        XmlStorage storage = new XmlStorage();

        try (FileOutputStream fos = new FileOutputStream("/tmp/test1234.xml")) {
            storage.serialize(project, fos);
        }

        Object value;
        try (FileInputStream fis = new FileInputStream("/tmp/test1234.xml")) {
            value = storage.deserialize(fis);
        }

        try (FileOutputStream fos = new FileOutputStream("/tmp/test1234v2.xml")) {
            storage.serialize(value, fos);
        }
    }
}
