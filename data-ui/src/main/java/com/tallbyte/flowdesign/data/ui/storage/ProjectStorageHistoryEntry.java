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

package com.tallbyte.flowdesign.data.ui.storage;

import java.util.Objects;

/**
 * Created by michael on 11.12.16.
 */
public class ProjectStorageHistoryEntry {

    protected final String type;
    protected final String path;
    protected final String projectName;
    protected final long timeMillis;

    public ProjectStorageHistoryEntry(String type, String path, String projectName) {
        this(type, path, projectName, System.currentTimeMillis());
    }

    public ProjectStorageHistoryEntry(String type, String path, String projectName, long timeMillis) {
        this.type           = type;
        this.path           = path;
        this.projectName    = projectName;
        this.timeMillis     = timeMillis;
    }

    /**
     * @return The type of the storage that has been used
     */
    public String getType() {
        return type;
    }

    /**
     * @return The path passed to the storage
     */
    public String getPath() {
        return path;
    }

    /**
     * @return The name of the project that has been serialized
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @return The time in milliseconds of the serialization (unix epoch)
     */
    public long getTimeMillis() {
        return timeMillis;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof ProjectStorageHistoryEntry) {
            ProjectStorageHistoryEntry other = ((ProjectStorageHistoryEntry)obj);
            return Objects.equals(this.getPath(),        other.getType())
                && Objects.equals(this.getPath(),        other.getPath())
                && Objects.equals(this.getProjectName(), other.getProjectName())
                && Objects.equals(this.getTimeMillis(),  other.getTimeMillis());
        }

        return false;
    }
}
