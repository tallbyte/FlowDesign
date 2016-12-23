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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public abstract class FlowNotationRuleBase implements FlowNotationRule {

    protected FlowAction             compiled = null;
    protected String                 allowed;
    protected StringBuilder          builder = new StringBuilder();
    protected List<FlowNotationRule> childs  = new ArrayList<>();

    public FlowNotationRuleBase(String allowed) {
        this.allowed = allowed;
    }

    @Override
    public void insert(FlowNotationRule rule) throws IllegalNotationException {
        if (compiled != null) {
            throw new IllegalStateException("already compiled");
        }

        if (canHaveChildren()) {
            childs.add(rule);
        } else {
            throw new IllegalNotationException("can not have children");
        }
    }

    @Override
    public boolean handleCharacter(char c) throws IllegalCharacterException, IllegalNotationException {
        if (compiled != null) {
            throw new IllegalStateException("already compiled");
        }

        if (Pattern.matches(allowed, c+"")) {
            builder.append(c);
        } else {
            throw new IllegalCharacterException("character "+c+" not allowed here");
        }

        return false;
    }

    protected abstract FlowAction doBuild() throws IllegalNotationException;

    @Override
    public FlowAction build() throws IllegalNotationException {
        if (compiled == null) {
            compiled = doBuild();
        }

        return compiled;
    }
}
