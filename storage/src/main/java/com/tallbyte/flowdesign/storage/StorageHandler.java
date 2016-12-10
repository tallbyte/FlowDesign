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

import com.tallbyte.flowdesign.storage.xml.XmlStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 10.12.16.
 */
public class StorageHandler {

    public static final String STORAGE_TYPE_XML = "xml";

    protected Map<Class, Storage> storages = new HashMap<>();
    protected Map<String, Class>  types    = new HashMap<>();

    protected StorageMetadata metadata;

    public StorageHandler() {
        this(true);
    }

    public StorageHandler(boolean addDefaultStorages) {
        if (addDefaultStorages) {
            addStorage(STORAGE_TYPE_XML, new XmlStorage());
        }

        this.metadata = new StorageMetadata();
    }

    /**
     * @return {@link StorageMetadata} being updated automatically
     */
    public StorageMetadata getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The new {@link StorageMetadata} to update
     * @return itself
     */
    public StorageHandler setMetadata(StorageMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * @param type The {@link Storage} type identifier
     * @param path The path to pass to the {@link Storage} to serialize to - the formatting and
     *             interpretation of the path depends on the {@link Storage} implementation. The path
     *             does explicitly not need to be a file path
     * @param serializable The serializable to serialize
     * @throws IOException If serialization failed or the given type is unknown
     */
    public void serialize(String type, String path, Object serializable) throws IOException {
        Storage storage = this.storages.get(
                types.get(
                        type
                )
        );

        if (storage == null) {
            throw new IOException("Unknown type: "+type);
        }

        storage.serialize(serializable, path);
        metadata.pushRecentlyUsed(type, path);
    }

    /**
     * @param type The {@link Storage} type identifier
     * @param path The path to pass to the {@link Storage} to deserialize from - the formatting and
     *             interpretation of the path depends on the {@link Storage} implementation. The path
     *             does explicitly not need to be a file path
     * @return The deserialized serializable
     * @throws IOException If deserialization failed or the given type is unknown
     */
    public Object deserialize(String type, String path) throws IOException {
        Storage storage = this.storages.get(
                types.get(
                        type
                )
        );

        if (storage == null) {
            throw new IOException("Unknown storage: "+type);
        }

        Object ob = storage.deserialize(path);
        metadata.pushRecentlyUsed(type, path);
        return ob;
    }

    /**
     *
     * @param type The {@link Storage} type identifier
     * @param path the path to pass to the {@link Storage} to deserialize from - the formatting and
     *             interpretation of the path depends on the {@link Storage} implementation. The path
     *             does explicitly not need to be a file path
     * @param classType The enforced class type
     * @param <T> Type being enforced
     * @return The deserialized serializable
     * @throws IOException If deserialization failed, the given type is unknown or the type enforcement failed
     */
    public <T> T deserialize(String type, String path, Class<T> classType) throws IOException {
        Storage<?,?,?,?,?> storage = this.storages.get(
                types.get(
                        type
                )
        );

        if (storage == null) {
            throw new IOException("Unknown storage: "+type);
        }

        T value = storage.deserialize(path, classType);
        metadata.pushRecentlyUsed(type, path);
        return value;
    }

    /**
     * @param type Type to add the {@link Storage} for
     * @param storage {@link Storage} to add
     * @return itself
     */
    public StorageHandler addStorage(String type, Storage storage) {
        this.storages.put(storage.getClass(), storage);
        this.types  .put(type,               storage.getClass());
        return this;
    }

    /**
     * @param type The type to remove the {@link Storage} for
     * @return The {@link Storage} once registered for the given type or null if unknown
     */
    public Storage removeStorage(String type) {
        return storages.remove(
                types.remove(
                        type
                )
        );
    }

    /**
     * @return An {@link Iterable} over all added storage types
     */
    public Iterable<String> getStorageTypes() {
        return types.keySet();
    }
}
