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

package com.tallbyte.flowdesign.storage.xml.environment;

import com.tallbyte.flowdesign.data.environment.EnvironmentComment;

/**
 * Created by michael on 21.12.16.
 */
public class XmlEnvironmentCommentSerializer extends XmlEnvironmentDiagramElementSerializer<EnvironmentComment> {

    @Override
    public EnvironmentComment instantiate() {
        return new EnvironmentComment();
    }
}
