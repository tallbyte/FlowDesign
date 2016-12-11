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

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.tallbyte.flowdesign.data.Project;
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistory;
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistoryEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-11)<br/>
 */
public class ApplicationManager {

    private File                 storageFile;

    private ProjectStorage       storage = new ProjectStorage();
    private Map<Project, String> paths   = new HashMap<>();

    public ApplicationManager() throws IOException {
        setApplicationStoragePath();
        createApplicationStoragePath();
        checkApplicationStoragePath();

        loadHistory();
    }

    private void setApplicationStoragePath() {
        storageFile = new File(System.getProperty("user.home")+ System.getProperty("file.separator")+".flowDesign");
    }

    private void createApplicationStoragePath() throws IOException {
        if (!storageFile.exists() || !storageFile.isDirectory()) {
            System.out.println(storageFile.getPath());
            if (!storageFile.mkdir()) {
                throw new IOException("could not create "+storageFile.getPath());
            }
        }
    }

    private void checkApplicationStoragePath() throws IOException {
        if (!storageFile.canRead() || ! storageFile.canWrite()) {
            throw new IOException("can not read/write to "+storageFile);
        }
    }

    private void loadHistory() throws IOException {
        File fileHistory = new File(storageFile, "history.xml");

        if (fileHistory.exists() && fileHistory.canRead()) {
            storage.deserializeHistory(fileHistory.getPath());
        }
    }

    private void saveHistory() throws IOException {
        storage.serializeHistory(new File(storageFile, "history.xml").getPath());
    }

    public void saveProject(Project project) throws IOException, NoPathSpecifiedException {
        String path = paths.get(project);

        if (path != null) {
            saveProject(project, path);
        } else {
            throw new NoPathSpecifiedException("could not find a suitable save location");
        }
    }

    public void saveProject(Project project, String path) throws IOException {
        storage.serialize(path, project);

        saveHistory();
    }

    public Project loadProject(ProjectStorageHistoryEntry entry) throws IOException, ProjectNotFoundException {
        try {
            return storage.deserialize(entry.getPath(), Project.class);
        } catch (FileNotFoundException e) {
            throw new ProjectNotFoundException("no project at path " + entry.getPath());
        }
    }

    public Project loadProject(String path) throws IOException, ProjectNotFoundException {
        try {
            return storage.deserialize(path, Project.class);
        } catch (FileNotFoundException e) {
            throw new ProjectNotFoundException("no project at path " + path);
        }
    }

    public ProjectStorageHistory getHistory() {
        return storage.getHistory();
    }

}
