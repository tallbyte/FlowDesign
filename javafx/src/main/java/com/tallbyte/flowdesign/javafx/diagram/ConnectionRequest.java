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

package com.tallbyte.flowdesign.javafx.diagram;

import com.tallbyte.flowdesign.data.Element;
import com.tallbyte.flowdesign.data.Connection;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-03)<br/>
 */
public class ConnectionRequest {

    private final Element source;

    /**
     * Creates a new {@link ConnectionRequest}.
     * @param source the source {@link Element}
     */
    public ConnectionRequest(Element source) {
        this.source = source;
    }

    /**
     * Gets the {@link Element} that shall be the source of the new {@link Connection}.
     * @return Returns the source.
     */
    public Element getSource() {
        return source;
    }
}
