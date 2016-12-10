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

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import static com.tallbyte.flowdesign.storage.StorageHandler.STORAGE_TYPE_XML;

/**
 * Created by michael on 10.12.16.
 */
public class StorageMetadata {

    public static final int SIZE_RECENTLY_USED = 10;

    protected Queue<Entry> recentlyUsed = new LinkedList<>();

    protected int    recentlyUsedSize;
    protected String defaultStorageType;

    public StorageMetadata() {
        this(STORAGE_TYPE_XML, SIZE_RECENTLY_USED);
    }

    public StorageMetadata(String defaultStorageType, int recentlyUsedSize) {
        this.defaultStorageType = defaultStorageType;
        this.recentlyUsedSize   = recentlyUsedSize;
    }

    /**
     * @return The current default {@link Storage} type
     */
    public String getDefaultStorageType() {
        return defaultStorageType;
    }

    /**
     * @param defaultStorageType The new default {@link Storage} type
     * @return itself
     */
    public StorageMetadata setDefaultStorageType(String defaultStorageType) {
        this.defaultStorageType = defaultStorageType;
        return this;
    }

    /**
     * @return The size of the recently used {@link Queue}
     */
    public int getRecentlyUsedSize() {
        return recentlyUsedSize;
    }

    /**
     * @param recentlyUsedSize The new size of the recently used {@link Queue}
     * @return itself
     */
    public StorageMetadata setRecentlyUsedSize(int recentlyUsedSize) {
        this.recentlyUsedSize = recentlyUsedSize;
        return this;
    }

    /**
     * @param type The type of the {@link Storage} just used
     * @param path The path just passed to the {@link Storage}
     * @return itself
     */
    public StorageMetadata pushRecentlyUsed(String type, String path) {
        // prevent multiple entries with the same value pair
        for (Entry entry : recentlyUsed) {
            if (Objects.equals(entry.getType(), type) && Objects.equals(entry.getPath(), path)) {
                recentlyUsed.remove(entry);
                break;
            }
        }

        // reduce the size to the limit
        while (recentlyUsed.size() >= recentlyUsedSize) {
            recentlyUsed.poll();
        }

        // add the new entry
        recentlyUsed.add(new Entry(type, path));
        return this;
    }

    /**
     * The {@link Queue} holds {@link Entry}s where with the type of the
     * {@link Storage} used, the path passed to the {@link Storage} and
     * the time in milliseconds of the occurrence.
     * he last entry is the most recently used one, while the first
     * entry is the oldest entry
     *
     * @return A {@link Queue} with the recently used type/path entries
     */
    public Queue<Entry> getRecentlyUsed() {
        return recentlyUsed;
    }

    public static class Entry {
        private String type;
        private String path;
        private long timeMillis;

        public Entry(String type, String path) {
            this(type, path, System.currentTimeMillis());
        }

        public Entry(String type, String path, long timeMillis) {
            this.type       = type;
            this.path       = path;
            this.timeMillis = timeMillis;
        }

        /**
         * @return The {@link Storage} type for the path of this {@link Entry}
         */
        public String getType() {
            return type;
        }

        /**
         * @return The path to pass to the {@link Storage} of this {@link Entry}
         */
        public String getPath() {
            return path;
        }

        /**
         * @return The unix timestamp in milliseconds when this entry was created
         */
        public long getTimeMillis() {
            return timeMillis;
        }
    }
}
