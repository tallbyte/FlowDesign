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

package com.tallbyte.flowdesign.data;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-11)<br/>
 * <br/>
 * A {@link Connection} describes a connection between two {@link Joint}s, which
 * basically means a connection between two {@link Element}s.
 */
public class Connection {

    private final Joint source;
    private final Joint target;

    /**
     * Creates a new {@link Connection} between two {@link Joint}s.
     * @param source the source {@link Joint}
     * @param target the target {@link Joint}
     */
    public Connection(Joint source, Joint target) {
        this.source = source;
        this.target = target;

    }

    /**
     * Gets the source {@link Joint}.
     * @return Returns the {@link Joint}.
     */
    public Joint getSource() {
        return source;
    }

    /**
     * Gets the target {@link Joint}.
     * @return Returns the {@link Joint}.
     */
    public Joint getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Connection) {
            Connection con = (Connection) obj;

            return (con.getTarget() == getTarget() && con.getSource() == getSource())
                    || (con.getTarget() == getSource() && con.getSource() == getTarget());
        }

        return false;
    }
}