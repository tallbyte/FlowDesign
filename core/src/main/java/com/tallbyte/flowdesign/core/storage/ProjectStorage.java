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
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistory;
import com.tallbyte.flowdesign.data.ui.storage.ProjectStorageHistoryEntry;
import com.tallbyte.flowdesign.storage.Storage;
import com.tallbyte.flowdesign.storage.StorageHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 11.12.16.
 */
public class ProjectStorage {

    public static final String DEFAULT_STORAGE_TYPE = StorageHandler.STORAGE_TYPE_XML;

    protected String         storageType;
    protected StorageHandler storageHandler;

    protected ProjectStorageHistory history;

    protected List<ProjectSerializedListener> listeners;

    public ProjectStorage() {
        this(DEFAULT_STORAGE_TYPE);
    }

    public ProjectStorage(String storageType) {
        this.storageType    = storageType;
        this.storageHandler = new StorageHandler(true);

        this.history        = new ProjectStorageHistory();
        this.listeners      = new ArrayList<>();

        registerListener(this::onProjectSerializedNotifyHistory);
    }

    /**
     * @return The {@link ProjectStorageHistory} of this {@link ProjectStorage}
     */
    public ProjectStorageHistory getHistory() {
        return history;
    }

    /**
     * @param history The new {@link ProjectStorageHistory} of this {@link ProjectStorage}
     */
    public void setHistory(ProjectStorageHistory history) {
        this.history = history;
    }

    /**
     * @return The {@link Storage} type used for (de-)serialization
     */
    public String getStorageType() {
        return storageType;
    }

    /**
     * @param storageHandler The new {@link StorageHandler} to use
     */
    public void setStorageHandler(StorageHandler storageHandler) {
        this.storageHandler = storageHandler;
    }

    /**
     * @return The {@link StorageHandler} used by this {@link ProjectStorage}
     */
    public StorageHandler getStorageHandler() {
        return storageHandler;
    }

    /**
     * @param storageType The new {@link Storage} type to use for (de-)serialization
     */
    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    /**
     * Just raw serialization with the current storage type,
     * without notifying any specific listener
     *
     * @param path Path to pass to the {@link Storage}
     * @param serializable Serializable to serialize
     * @return The storage type that has been used
     * @throws IOException If serialization failed
     */
    public String serialize(String path, Object serializable) throws IOException {
        String type = getStorageType();
        this.storageHandler.serialize(
                type,
                path,
                serializable
        );
        return type;
    }

    /**
     * Serializes the given {@link Project} and notifies the registered
     * {@link ProjectSerializedListener}s
     *
     * @param path Path to pass to the {@link Storage}
     * @param project The {@link Project} to serialize
     * @return The storage type that has been used
     * @throws IOException If serialization failed
     */
    public String serialize(String path, Project project) throws IOException {
        String type = this.serialize(path, (Object)project);
        this.notifyProjectSerializedListeners(type, path, project);
        return type;
    }

    /**
     * Serializes the current {@link ProjectStorageHistory}
     * to the given path
     *
     * @param path Path to serialize to
     * @return The storage type that has been used
     * @throws IOException If serialization failed
     */
    public String serializeHistory(String path) throws IOException {
        return this.serialize(
                path,
                getHistory()
        );
    }

    /**
     * @param path The path to pass to the {@link Storage}
     * @param serializableType The type of the serializable cast to
     * @param <T> The serializable type
     * @return The deserialized serializable
     * @throws IOException If deserialization failed
     */
    public <T> T deserialize(String path, Class<T> serializableType) throws IOException {
        return storageHandler.deserialize(
                getStorageType(),
                path,
                serializableType
        );
    }

    /**
     * Deserializes a {@link ProjectStorageHistory} from the given path
     * and sets it as the new {@link ProjectStorageHistory} of this
     * {@link ProjectStorage}
     *
     * @param path The path to deserialize the {@link ProjectStorageHistory} from
     * @throws IOException If deserialization failed
     */
    public void deserializeHistory(String path) throws IOException {
        setHistory(
                deserialize(
                        path,
                        ProjectStorageHistory.class
                )
        );
    }

    /**
     * @param type The type that has been used to serialize the {@link Project}
     * @param path The path that has been passed to the {@link Storage}
     * @param project The {@link Project} that has been serialized
     */
    protected void notifyProjectSerializedListeners(String type, String path, Project project) {
        listeners.forEach(l -> l.onProjectSerialized(type, path, project));
    }

    /**
     * Notifies the {@link ProjectStorageHistory} about a successful
     * {@link Project} serialization
     *
     * @param type The type of {@link Storage} that as been used
     * @param path The path that has been passed to the {@link Storage}
     * @param project The {@link Project} that has been serialized
     */
    protected void onProjectSerializedNotifyHistory(String type, String path, Project project) {
        this.history.add(
                new ProjectStorageHistoryEntry(
                        type,
                        path,
                        project.getName()
                )
        );
    }


    /**
     * @param listener {@link ProjectSerializedListener} to notify after a {@link Project}s
     *                 has been serialized successfully
     */
    public void registerListener(ProjectSerializedListener listener) {
        this.listeners.add(listener);
    }

    /**
     * @param listener The {@link ProjectSerializedListener} to no longer notify
     * @return Whether the {@link ProjectSerializedListener} was known
     */
    public boolean removeListener(ProjectSerializedListener listener) {
        return this.listeners.remove(listener);
    }
}
