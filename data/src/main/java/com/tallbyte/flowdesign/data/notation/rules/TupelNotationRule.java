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
import com.tallbyte.flowdesign.data.notation.actions.Tupel;
import com.tallbyte.flowdesign.data.notation.actions.TupelContainment;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public class TupelNotationRule extends FlowNotationRuleBase {

    private boolean repeat = false;

    private boolean ended    = false;
    private boolean added    = false;
    private boolean expected = true;

    private TupelNotationRule(int i) {
        super("[\\,\\)\\*]", i);
    }

    public TupelNotationRule(char c, int i) throws IllegalCharacterException, IllegalNotationException {
        this(i);

        if (c != '(') {
            throw new IllegalCharacterException("tupel must start with an open parenthesis");
        }

        builder.append(c);
    }

    @Override
    public boolean handleCharacter(char c, int i) throws IllegalCharacterException, IllegalNotationException {
        if (!ended && c == ')') {
            ended = true;
        } else if (ended && c != '*') {
            throw new IllegalCharacterException("already ended "+c);
        }

        if (c == ',') {
            added    = false;
            expected = true;
        }

        if (!ended && added) {
            throw new IllegalNotationException("tupel content should be separated by comma");
        }

        super.handleCharacter(c, i);

        if (c == '*') {
            repeat = true;
        }

        return c == '*';
    }

    @Override
    public boolean canHaveChildren() {
        return true;
    }

    @Override
    public boolean isFinished(int i, int len) {
        return repeat;
    }

    @Override
    public void insert(FlowNotationRule rule) throws IllegalNotationException {
        if (!(rule instanceof TupelNotationRule) && !(rule instanceof TypeNotationRule)) {
            throw new IllegalNotationException("tupel can only contain tupels or types");
        }

        expected = false;
        added    = true;

        super.insert(rule);
    }

    @Override
    public FlowAction doBuild() throws IllegalNotationException {
        if (!ended) {
            throw new IllegalNotationException("not closed");
        }

        if (expected) {
            throw new IllegalNotationException("expecting child");
        }

        if (childs.size()==0) {
            throw new IllegalNotationException("at least one containing element required");
        }

        List<TupelContainment> list = new ArrayList<>();
        for (FlowNotationRule rule : childs) {
            list.add((TupelContainment) rule.build());
        }

        return new Tupel(start, last, repeat, list);
    }
}
