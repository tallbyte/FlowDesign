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
import com.tallbyte.flowdesign.data.notation.rules.*;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public class SimpleFlowNotationParser implements FlowNotationParser {

    private List<FlowNotationRuleFactory> factories = new ArrayList<>();

    public SimpleFlowNotationParser() {
        factories.add(MultiStreamNotationRule::new);
        factories.add(TupelNotationRule::new);
        factories.add(TypeNotationRule::new);
    }

    private FlowNotationRule findRule(char c, int i) throws IllegalNotationException {
        for (FlowNotationRuleFactory factory : factories) {
            try {
                return factory.create(c, i);
            } catch (Exception e) { /*ignore*/ }
        }

        throw new IllegalNotationException("could not find rule matching "+c);
    }

    private boolean checkEnd(Stack<?> stack ,int i, int x, int y, String string) throws FlowNotationParserException {
        if (stack.size() == 1) {
            if (i < string.length() - 1) {
                throw new FlowNotationParserException("statement already closed", x, y);
            } else {
                if (i != string.length() -1) {
                    throw new FlowNotationParserException("statement already closed", x, y);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public FlowAction parse(String string) throws FlowNotationParserException {
        if (string == null || string.isEmpty()) {
            return null;
        }

        Stack<FlowNotationRule> stack = new Stack<>();
        int x = 0;
        int y = 0;

        try {
            // push first
            stack.push(findRule(string.charAt(0), 0));

            for (int i = 1; i < string.length() ; ++i) {
                char c = string.charAt(i);

                // ignore whitespace
                if (!Character.isWhitespace(c)) {

                    /*
                     * Main logic
                     */

                    FlowNotationRule top = stack.peek();

                    boolean downwards = false;
                    boolean reapply   = false;
                    try {
                        // try to add a new character to the existing rule
                        downwards = top.handleCharacter(c, i);

                    } catch (IllegalCharacterException e) {
                        if (top.canHaveChildren()) {
                            // existing rule refuses to use the character, so try to create an embedded rule
                            FlowNotationRule rule = null;
                            try {
                                rule = findRule(c, i);
                                stack.push(rule);
                            } catch (IllegalNotationException ie) {
                                downwards = true;
                            }

                            // inserting might not be allowed at all times -> out of try block
                            if (rule != null) {
                                top.insert(rule);
                            }

                        } else {
                            downwards = true;
                        }

                        // character could not be applied -> has to be done one layer below
                        reapply = true;
                    }

                    // we go downwards in the stack
                    if (downwards) {
                        // check if we could return
                        if (checkEnd(stack, i, x, y, string)) {
                            return top.build();
                        }

                        // don't return, but proceed
                        top.build();
                        stack.pop();

                        // if an element was ended because of a bad character, the character was still unprocessed
                        if (reapply) {
                            if (stack.peek().handleCharacter(c, i)) {
                                // now check again if ending is required
                                if (checkEnd(stack, i, x, y, string)) {
                                    return stack.peek().build();
                                }

                                // finally remove from the stack
                                stack.pop();
                            }
                        }
                    }
                }

                /*
                 * Location updating
                 */
                x = x + 1;
                if (Character.toString(c).equals(System.lineSeparator())) {
                    x = 0;
                    y = y + 1;
                }
            }
        } catch (EmptyStackException e) {
            throw new FlowNotationParserException("more characters expected", x, y);

        } catch (FlowNotationParserException e) {
            throw e;

        } catch (Exception e) {
            throw new FlowNotationParserException(e.getMessage(), e, x, y);
        }

        try {
            if (checkEnd(stack, string.length()-1, x, y, string)) {
                return stack.peek().build();
            }

            throw new FlowNotationParserException("statement unfinished", x, y);
        } catch (IllegalNotationException e) {
            throw new FlowNotationParserException("unfinished statement", x, y);
        }
    }
}
