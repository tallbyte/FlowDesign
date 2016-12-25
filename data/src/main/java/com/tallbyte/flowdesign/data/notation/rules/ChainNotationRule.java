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
import com.tallbyte.flowdesign.data.notation.actions.Chain;
import com.tallbyte.flowdesign.data.notation.actions.FlowAction;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public class ChainNotationRule extends FlowNotationRuleBase {

    private boolean ended         = false;

    private boolean splitRequired = false;
    private boolean split         = false;

    private ChainNotationRule(int i) {
        super("[\\/]", i);
    }

    @Override
    public boolean handleCharacter(char c, int i) throws IllegalCharacterException, IllegalNotationException {
        if (c == '/') {
            if (!split) {
                splitRequired = false;
                split = true;
            } else {
                throw new IllegalNotationException("chain can only contain one split");
            }

        }

        return super.handleCharacter(c, i);
    }

    @Override
    public boolean canHaveChildren() {
        return true;
    }

    @Override
    public void insert(FlowNotationRule rule) throws IllegalNotationException {
        if (rule instanceof ChainNotationRule) {
            throw new IllegalNotationException("chain can not contain chains");
        }

        if (splitRequired) {
            throw new IllegalNotationException("chain elements need to be separated by a /");
        }

        if (childs.size() > 1) {
            throw new IllegalNotationException("split can only contain two elements");
        }
        split         = false;
        splitRequired = true;

        super.insert(rule);

        ended = childs.size() == 2;
    }

    @Override
    public FlowAction doBuild() throws IllegalNotationException {
        if (!ended) {
            throw new IllegalNotationException("not closed");
        }

        return new Chain(start, last, childs.get(0).build(), childs.get(1).build());
    }
}
