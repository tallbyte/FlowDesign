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

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * Created by michael on 11.12.16.
 */
public class ProjectStorageHistory {

    public static final int DEFAULT_SIZE_LIMIT = 10;

    protected Queue<ProjectStorageHistoryEntry> entries;
    protected int sizeLimit;

    public ProjectStorageHistory() {
        this(DEFAULT_SIZE_LIMIT);
    }

    public ProjectStorageHistory(int sizeLimit) {
        this.entries    = new LinkedList<>();
        this.sizeLimit  = sizeLimit;
    }

    /**
     * @return The current size / amount of known entries
     */
    public int getSize() {
        return entries.size();
    }

    /**
     * If {@link ProjectStorageHistoryEntry}s are added
     * and the size limit has been reached, the oldest
     * entry is being removed
     *
     * @return The current size limit
     */
    public int getSizeLimit() {
        return sizeLimit;
    }

    /**
     * @param sizeLimit The new size limit
     */
    public void setSizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
        this.limitEntries();
    }

    /**
     * Removes entries until the size matches
     * the size limit
     */
    protected void limitEntries() {
        while (sizeLimit >= 0 && entries.size() > sizeLimit) {
            entries.poll();
        }
    }

    /**
     * @param entry The new {@link ProjectStorageHistoryEntry} to add
     */
    public void add(ProjectStorageHistoryEntry entry) {
        entries.add(entry);
        limitEntries();
    }

    /**
     * The first entry in the {@link Iterable} is the one added fist,
     * while the last entry in the {@link Iterable} is the one added
     * most recently.
     *
     * @return An {@link Iterable} over all known {@link ProjectStorageHistoryEntry}
     */
    public Iterable<ProjectStorageHistoryEntry> getEntries() {
        return entries;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof ProjectStorageHistory) {
            ProjectStorageHistory other = (ProjectStorageHistory)obj;
            return Objects.equals(this.getSizeLimit(), other.getSizeLimit())
                && Objects.equals(this.getEntries(),   other.getEntries());
        }

        return false;
    }
}
