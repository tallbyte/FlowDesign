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
 * - julian (2016-12-28)<br/>
 */
public interface ConnectionTextExtractor {

    /**
     * Create the text for the {@link FlowConnection} out of the type of the {@link FlowJoint}.
     *
     * @param joint the source {@link FlowJoint}
     * @param connection the target {@link FlowConnection}
     * @param text the text to apply
     * @return Returns the generated text.
     */
    String setConnection(FlowJoint joint, FlowConnection connection, String text);

    /**
     * Create the text for the {@link FlowJoint} out of the type of the {@link FlowConnection}.
     *
     * @param joint the target {@link FlowJoint}
     * @param connection the source {@link FlowConnection}
     * @param text the text to apply
     * @return Returns the generated text.
     */
    String setJoint(FlowJoint joint, FlowConnection connection, String text);

}
