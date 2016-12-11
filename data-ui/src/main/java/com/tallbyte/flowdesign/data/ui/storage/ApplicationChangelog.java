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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by michael on 11.12.16.
 */
public class ApplicationChangelog {

    protected List<ApplicationChangelogEntry> entries = new ArrayList<>();

    protected boolean modifiable;

    public ApplicationChangelog() {
        this.modifiable = true;
    }

    /**
     * Makes this {@link ApplicationChangelog} unmodifiable
     */
    public void makeUnmodifiable() {
        if (modifiable) {
            this.entries    = Collections.unmodifiableList(this.entries);
            this.modifiable = false;
        }
    }

    /**
     * @return Whether this {@link ApplicationChangelog} can be modified
     */
    public boolean isModifiable() {
        return modifiable;
    }

    /**
     * @param entry {@link ApplicationChangelogEntry}
     * @throws UnsupportedOperationException If this {@link ApplicationChangelog} is unmodifiable
     */
    public void add(ApplicationChangelogEntry entry) throws UnsupportedOperationException {
        this.entries.add(entry);
    }

    /**
     * @return An {@link Iterable} over all added {@link ApplicationChangelogEntry}s
     */
    public Iterable<ApplicationChangelogEntry> getEntries() {
        return entries;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof ApplicationChangelog) {
            ApplicationChangelog other = (ApplicationChangelog)obj;
            return Objects.equals(this.isModifiable(), other.isModifiable())
                && Objects.equals(this.getEntries(),   other.getEntries());
        }

        return false;
    }
}
