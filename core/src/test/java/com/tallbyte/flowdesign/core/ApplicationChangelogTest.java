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
import com.tallbyte.flowdesign.data.ui.storage.ApplicationChangelog;
import com.tallbyte.flowdesign.data.ui.storage.ApplicationChangelogEntry;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Created by michael on 11.12.16.
 */
public class ApplicationChangelogTest {

    @Test
    public void testChangelog() throws IOException {
        Path path = FileUtils.createTempDirectory(getClass());


        ApplicationChangelog original = new ApplicationChangelog();

        original.add(new ApplicationChangelogEntry(0, "123abc1", "Test 1", Arrays.asList("Detail 1", "Detail 2", "Detail 3")));
        original.add(new ApplicationChangelogEntry(1, "345abc1", "Test 2", Arrays.asList("Detail 4", "Detail 5", "Detail 6")));
        original.add(new ApplicationChangelogEntry(2, "678abc1", "Test 3", Arrays.asList("Detail 7", "Detail 8", "Detail 9")));


        ProjectStorage storage = new ProjectStorage();

        String pathChangelog = path.resolve("changelog.xml").toString();
        System.out.println(pathChangelog);

        storage.serialize(pathChangelog, original);
        ApplicationChangelog c1 = storage.deserialize(pathChangelog, ApplicationChangelog.class);

        // serialize and deserialize a second time to test whether serialization of deserialized changelogs work
        storage.serialize(pathChangelog, c1);
        ApplicationChangelog c2 = storage.deserialize(pathChangelog, ApplicationChangelog.class);

        Assert.assertTrue(original.equals(c1));
        Assert.assertTrue(original.equals(c2));

        FileUtils.deleteDirectoryRecursive(path);
    }
}
