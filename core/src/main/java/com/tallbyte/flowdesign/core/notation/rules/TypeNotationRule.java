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

package com.tallbyte.flowdesign.core.notation.rules;

import com.sun.org.apache.xml.internal.utils.StringBufferPool;
import com.tallbyte.flowdesign.core.notation.IllegalCharacterException;
import com.tallbyte.flowdesign.core.notation.IllegalNotationException;
import com.tallbyte.flowdesign.core.notation.actions.FlowAction;
import com.tallbyte.flowdesign.core.notation.actions.Tupel;
import com.tallbyte.flowdesign.core.notation.actions.Type;
import com.tallbyte.flowdesign.data.DataType;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public class TypeNotationRule extends FlowNotationRuleBase {

    private boolean repeat    = false;
    private boolean nameFound = false;
    private StringBuilder name = new StringBuilder();
    private StringBuilder type = new StringBuilder();
    private TypeNotationRule() {
        super("[a-zA-Z\\:\\*]");
    }

    public TypeNotationRule(char c) throws IllegalCharacterException, IllegalNotationException {
        this();

        if (!Character.isLetter(c)) {
            throw new IllegalCharacterException("first character if type must be uppercase or lowercase letter");
        }

        handleCharacter(c);
    }

    @Override
    public boolean handleCharacter(char c) throws IllegalCharacterException, IllegalNotationException {
        super.handleCharacter(c);

        if (builder.length() == 1 && Character.isUpperCase(c)) {
            nameFound = true;
        }

        if (c == ':') {
            if (nameFound) {
                throw new IllegalNotationException("name already specified");
            } else {
                nameFound = true;
            }
        } else {
            if (nameFound) {
                type.append(c);
            } else {
                name.append(c);
            }
        }

        if (c == '*') {
            repeat = true;
        }

        // we never know when a name ends
        return c == '*';
    }

    @Override
    public void insert(FlowNotationRule rule) throws IllegalNotationException {
        throw new IllegalNotationException("type can not have nested");
    }

    @Override
    public boolean canHaveChildren() {
        return false;
    }

    @Override
    public FlowAction doBuild() throws IllegalNotationException {
        return new Tupel(repeat, new Type(new DataType(type.toString()), name.toString()));
    }

}
