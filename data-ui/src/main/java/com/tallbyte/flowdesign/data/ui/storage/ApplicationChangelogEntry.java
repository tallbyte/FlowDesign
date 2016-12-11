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

import java.util.Collections;
import java.util.List;

/**
 * Created by michael on 11.12.16.
 */
public class ApplicationChangelogEntry {

    protected final long         timeMillis;
    protected final String       commit;
    protected final String       summary;
    protected final List<String> detailed;

    public ApplicationChangelogEntry(long timeMillis, String commit, String summary, List<String> detailed) {
        this.timeMillis = timeMillis;
        this.commit     = commit;
        this.summary    = summary;
        this.detailed   = Collections.unmodifiableList(detailed);
    }

    /**
     * @return The date in milliseconds of this entry
     */
    public long getTimeMillis() {
        return timeMillis;
    }

    /**
     * @return The commit identifier of this entry
     */
    public String getCommit() {
        return commit;
    }

    /**
     * @return A summary of the changes for this entry
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @return An {@link Iterable} over more detailed changes
     */
    public Iterable<String> getDetailed() {
        return detailed;
    }
}
