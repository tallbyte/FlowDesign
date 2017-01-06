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

package com.tallbyte.flowdesign.data.notation;

import com.tallbyte.flowdesign.data.notation.actions.FlowAction;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public interface FlowNotationParser {

    /**
     * Parses a string an tries to create a matching
     * {@link FlowAction} out of it.
     *
     * @param string the string to parse
     * @return Returns the parsed {@link FlowAction}.
     * @throws FlowNotationParserException Is thrown if the given string does not match syntax.
     */
    public FlowAction parse(String string) throws FlowNotationParserException;

}
