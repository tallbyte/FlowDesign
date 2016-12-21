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

package com.tallbyte.flowdesign.data.environment;

import com.tallbyte.flowdesign.data.Diagram;

import java.lang.*;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-07)<br/>
 * <br/>
 * A System-{@link EnvironmentDiagram} describes all dependencies and external actors of
 * an software system.
 */
public class EnvironmentDiagram extends Diagram<EnvironmentDiagramElement> {

    /**
     * Creates a new {@link EnvironmentDiagram} using name only.
     * @param name the desired name
     */
    public EnvironmentDiagram(String name) {
        super(name, null);
    }

    /**
     * Creates a new {@link EnvironmentDiagram} with a given {@link System} as root.
     * @param name the desired name
     * @param root the desired root
     */
    public EnvironmentDiagram(String name, System root) {
        super(name, root);
    }

    @Override
    public System getRoot() {
        return (System) super.getRoot();
    }
}
