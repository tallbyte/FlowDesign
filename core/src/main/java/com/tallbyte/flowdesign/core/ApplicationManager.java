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

import com.tallbyte.flowdesign.core.storage.NoPathSpecifiedException;
import com.tallbyte.flowdesign.core.storage.ProjectNotFoundException;
import com.tallbyte.flowdesign.core.storage.ProjectStorage;
import com.tallbyte.flowdesign.data.Project;
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistory;
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistoryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    /*
     * Storage
     */

    private File                 storageFile;

    private ProjectStorage       storage = new ProjectStorage();
    private Map<Project, String> paths   = new HashMap<>();

    /*
     * Logging
     */


    private Logger               logger  = LogManager.getLogger(getClass());

    public ApplicationManager() throws IOException {
        setApplicationStoragePath();
        createApplicationStoragePath();
        checkApplicationStoragePath();

        logger.info("initializing application");

        loadHistory();
    }

    private void setApplicationStoragePath() {
        storageFile = new File(System.getProperty("user.home")+ System.getProperty("file.separator")+".flowDesign");
    }

    private void createApplicationStoragePath() throws IOException {
        if (!storageFile.exists() || !storageFile.isDirectory()) {
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
        logger.info("saving history");

        storage.serializeHistory(new File(storageFile, "history.xml").getPath());

        logger.info("saved history");
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
        logger.info("attempting to save project "+project.getName());
        storage.serialize(path, project);
        paths.put(project, path);
        logger.info("saved project "+project.getName());

        saveHistory();


    }

    public void serialize(Object target, String path) throws IOException {
        storage.serialize(path, target);
    }

    public Project loadProject(ProjectStorageHistoryEntry entry) throws IOException, ProjectNotFoundException {
        logger.info("attempting to load project from " + entry.getPath());

        try {
            Project project = storage.deserialize(entry.getPath(), Project.class);
            paths.put(project, entry.getPath());

            logger.info("loaded project " + project.getName());

            return project;
        } catch (FileNotFoundException e) {
            throw new ProjectNotFoundException("no project at path " + entry.getPath());
        }
    }

    public Project loadProject(String path) throws IOException, ProjectNotFoundException {
        logger.info("attempting to load project from " + path);

        try {
            Project project = storage.deserialize(path, Project.class);
            paths.put(project, path);

            logger.info("loaded project " + project.getName());

            return project;
        } catch (FileNotFoundException e) {
            throw new ProjectNotFoundException("no project at path " + path);
        }
    }

    public <T> T deserialize(Class<T> clazz, String path) throws IOException {
        return storage.deserialize(path, clazz);
    }

    public ProjectStorageHistory getHistory() {
        return storage.getHistory();
    }
}
