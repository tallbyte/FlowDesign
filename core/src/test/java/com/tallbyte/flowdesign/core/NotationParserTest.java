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

package com.tallbyte.flowdesign.core;

import com.tallbyte.flowdesign.core.notation.FlowNotationParser;
import com.tallbyte.flowdesign.core.notation.FlowNotationParserException;
import com.tallbyte.flowdesign.core.notation.SimpleFlowNotationParser;
import com.tallbyte.flowdesign.core.notation.actions.FlowAction;
import com.tallbyte.flowdesign.core.notation.actions.MultiStream;
import com.tallbyte.flowdesign.core.notation.actions.Tupel;
import com.tallbyte.flowdesign.core.notation.actions.Type;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Stack;

import static junit.framework.TestCase.*;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public class NotationParserTest {

    private final FlowNotationParser parser = new SimpleFlowNotationParser();

    private void assertDeepEqual(String toParse, Class<?>... clazz) throws FlowNotationParserException {
        Stack<FlowAction> stack = new Stack<>();

        stack.push(parser.parse(toParse));

        int i = 0;
        while (!stack.isEmpty()) {
            if (i >= clazz.length) {
                throw new RuntimeException("clazz array too small? fix your test...");
            }

            FlowAction action = stack.pop();
            assertEquals(clazz[i], action.getClass());

            if (action instanceof MultiStream) {
                stack.push(((MultiStream) action).getAction());
            } else if (action instanceof Tupel) {
                ((Tupel) action).getTypes().forEach(stack::push);
            }

            // increment array
            ++i;
        }

        assertEquals(clazz.length, i);

    }

    private void printAction(FlowAction action) {
        if (action instanceof Tupel) {
            System.out.println("Tupel{"+((Tupel) action).getTypes().size()+"}");
        }

        if (action instanceof MultiStream) {
            System.out.println("Tupel{"+action.getClass()+"}");
        }

        if (action instanceof Type) {
            System.out.println("Tupel{"+((Type) action).getType().getClassName()+","+((Type) action).getName()+"}");
        }
    }

    @Test
    public void testNotation() throws FlowNotationParserException {
        assertDeepEqual("{(String, Integer)*}",
                MultiStream.class,
                    Tupel.class,
                        Tupel.class, Type.class,
                        Tupel.class, Type.class
        );
        assertDeepEqual("{(String, Integer)}",
                MultiStream.class,
                    Tupel.class,
                        Tupel.class, Type.class,
                        Tupel.class, Type.class
        );
        assertDeepEqual("{(String*, Integer*)}",
                MultiStream.class,
                    Tupel.class,
                        Tupel.class, Type.class,
                        Tupel.class, Type.class
        );
        assertDeepEqual("(String*, Integer*)",
                Tupel.class,
                    Tupel.class, Type.class,
                    Tupel.class, Type.class
        );
        assertDeepEqual("(String*, Integer*)*",
                Tupel.class,
                    Tupel.class, Type.class,
                    Tupel.class, Type.class
        );
        assertDeepEqual("(String)*",
                Tupel.class,
                    Tupel.class, Type.class
        );
        assertDeepEqual("{(String)*}",
                MultiStream.class,
                    Tupel.class,
                        Tupel.class, Type.class
        );
        assertDeepEqual("{((String,Object*),(List,HashMap)*)}",
                MultiStream.class,
                    Tupel.class,
                        Tupel.class,
                            Tupel.class, Type.class,
                            Tupel.class, Type.class,
                        Tupel.class,
                            Tupel.class, Type.class,
                        Tupel.class, Type.class
        );
        assertDeepEqual("(String)*",
                Tupel.class,
                    Tupel.class, Type.class
        );
        assertDeepEqual("(String)",
                Tupel.class,
                    Tupel.class, Type.class
        );
        assertDeepEqual("String",
                Tupel.class, Type.class);
        assertDeepEqual("String*",
                Tupel.class, Type.class);
        assertDeepEqual("(String*,sdf,(Object,List))*",
                Tupel.class,
                    Tupel.class, Type.class,
                    Tupel.class, Type.class,
                    Tupel.class,
                        Tupel.class, Type.class,
                        Tupel.class, Type.class
        );
        assertDeepEqual("(name:String,(Object,List))*",
                Tupel.class,
                    Tupel.class, Type.class,
                    Tupel.class,
                        Tupel.class, Type.class,
                        Tupel.class, Type.class
        );

    }

    @Test(expected = FlowNotationParserException.class)
    public void testAlreadyClosed() throws FlowNotationParserException {
        parser.parse("{name:StringBuilder}*");
    }

    @Test(expected = FlowNotationParserException.class)
    public void testUnfinished() throws FlowNotationParserException {
        parser.parse("{name:StringBuilder");
    }

    @Test(expected = FlowNotationParserException.class)
    public void testOpenEnd() throws FlowNotationParserException {
        parser.parse("(String)*}");
    }

    @Test(expected = FlowNotationParserException.class)
    public void testNested() throws FlowNotationParserException {
        parser.parse("({String})*");
    }

    @Test
    public void testMissingCharacters() throws FlowNotationParserException {
        parser.parse("");
    }

}
