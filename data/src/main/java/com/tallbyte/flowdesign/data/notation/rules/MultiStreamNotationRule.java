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

package com.tallbyte.flowdesign.data.notation.rules;

import com.tallbyte.flowdesign.data.notation.IllegalCharacterException;
import com.tallbyte.flowdesign.data.notation.IllegalNotationException;
import com.tallbyte.flowdesign.data.notation.actions.FlowAction;
import com.tallbyte.flowdesign.data.notation.actions.MultiStream;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public class MultiStreamNotationRule extends FlowNotationRuleBase {

    private boolean ended = false;

    private MultiStreamNotationRule(int i) {
        super("[\\}]", i);
    }

    public MultiStreamNotationRule(char c, int i) throws IllegalCharacterException, IllegalNotationException {
        this(i);

        if (c != '{') {
            throw new IllegalCharacterException("multistream must start with an open curly braces");
        }

        builder.append(c);
    }

    @Override
    public boolean handleCharacter(char c, int i) throws IllegalCharacterException, IllegalNotationException {
        if (c == '}') {
            ended = true;
        }

        super.handleCharacter(c, i);

        return true;
    }

    @Override
    public boolean canHaveChildren() {
        return true;
    }

    @Override
    public void insert(FlowNotationRule rule) throws IllegalNotationException {
        if (childs.size() == 1) {
            throw new IllegalNotationException("only one containing element allowed");
        }

        super.insert(rule);
    }

    @Override
    public boolean isFinished(int i, int len) {
        return ended;
    }

    @Override
    public FlowAction doBuild() throws IllegalNotationException {
        if (!ended) {
            throw new IllegalNotationException("not closed");
        }

        if (childs.size() == 0) {
            throw new IllegalNotationException("no content");
        }

        if (childs.size() > 1) {
            throw new IllegalNotationException("only one containing element allowed");
        }

        return new MultiStream(start, last, 0, 0, childs.get(0).build());
    }
}
