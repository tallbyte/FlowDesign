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

import com.tallbyte.flowdesign.core.notation.IllegalCharacterException;
import com.tallbyte.flowdesign.core.notation.IllegalNotationException;
import com.tallbyte.flowdesign.core.notation.actions.FlowAction;
import com.tallbyte.flowdesign.core.notation.actions.Tupel;
import com.tallbyte.flowdesign.core.notation.actions.TupelContainment;

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

    private boolean ended = false;
    private boolean added = false;

    private TupelNotationRule() {
        super("[\\,\\)\\*]");
    }

    public TupelNotationRule(char c) throws IllegalCharacterException, IllegalNotationException {
        this();

        if (c != '(') {
            throw new IllegalCharacterException("tupel must start with an open parenthesis");
        }

        builder.append(c);
    }

    @Override
    public boolean handleCharacter(char c) throws IllegalCharacterException, IllegalNotationException {
        if (!ended && c == ')') {
            ended = true;
        } else if (ended && c != '*') {
            throw new IllegalCharacterException("already ended "+c);
        }

        if (c == ',') {
            added = false;
        }

        if (!ended && added) {
            throw new IllegalNotationException("tupel content should be separated by comma");
        }

        super.handleCharacter(c);

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
    public void insert(FlowNotationRule rule) throws IllegalNotationException {
        if (!(rule instanceof TupelNotationRule) && !(rule instanceof TypeNotationRule)) {
            throw new IllegalNotationException("tupel can only contain tupels or types");
        }

        added = true;

        super.insert(rule);
    }

    @Override
    public FlowAction doBuild() throws IllegalNotationException {
        if (!ended) {
            throw new IllegalNotationException("not closed");
        }

        List<TupelContainment> list = new ArrayList<>();
        for (FlowNotationRule rule : childs) {
            list.add(0, (TupelContainment) rule.build());
        }

        return new Tupel(repeat, list);
    }
}
