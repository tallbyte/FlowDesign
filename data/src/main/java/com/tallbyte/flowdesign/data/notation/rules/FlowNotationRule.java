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

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public interface FlowNotationRule {

    /**
     * Tries to add a new character to this {@link FlowNotationRule}.
     *
     * @param c the caracter to add
     * @param i the index the parser is at
     * @return Returns true if this {@link FlowNotationRule} ended with the given character.
     * @throws IllegalCharacterException Is thrown if the given character is not allowed.
     * @throws IllegalNotationException Is thrown if illegal syntax was detected.
     */
    boolean handleCharacter(char c, int i) throws IllegalCharacterException, IllegalNotationException;

    /**
     * Inserts an child {@link FlowNotationRule}.
     *
     * @param rule the child
     * @throws IllegalNotationException Is thrown if illegal syntax was detected.
     */
    void insert(FlowNotationRule rule) throws IllegalNotationException;

    /**
     * Checks whether or not this {@link FlowNotationRule} can have children.
     * @return
     */
    boolean canHaveChildren();

    /**
     * Checks whether or not this {@link FlowNotationRule} is finished.
     *
     * @param i the current index of the parser
     * @param len the total length of the parsed string
     * @return Returns true if is finished, else false.
     */
    boolean isFinished(int i, int len);

    /**
     * Tries to build this {@link FlowNotationRule}.
     *
     * @return Returns the created {@link FlowAction}.
     * @throws IllegalNotationException Is thrown if illegal syntax was detected.
     */
    FlowAction build() throws IllegalNotationException;

}
