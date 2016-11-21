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

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by michael on 21.11.16.
 */
public class DatatypeTest {

    @Test
    public void testFullClassName() {
        assertEquals(
                List.class.getName(),
                new Datatype(
                        List.class
                ).getFullClassName()
        );

        assertEquals(
                List.class.getName(),
                new Datatype(
                        List.class.getName()
                ).getFullClassName()
        );
    }

    @Test
    public void testPackageName() {
        assertEquals(
                List.class.getPackage().getName(),
                new Datatype(
                        List.class
                ).getPackageName()
        );

        assertEquals(
                List.class.getPackage().getName(),
                new Datatype(
                        List.class.getName()
                ).getPackageName()
        );

        assertEquals(
                null,
                new Datatype(
                        List.class.getSimpleName()
                ).getPackageName()
        );
    }

    @Test
    public void testClassName() {
        assertEquals(
                List.class.getSimpleName(),
                new Datatype(
                        List.class
                ).getClassName()
        );

        assertEquals(
                List.class.getSimpleName(),
                new Datatype(
                        List.class.getName()
                ).getClassName()
        );

        assertEquals(
                List.class.getSimpleName(),
                new Datatype(
                        List.class.getSimpleName()
                ).getClassName()
        );
    }

    @Test
    public void testDisplayNameNormal() {
        final String expected = "List";

        assertEquals(
                expected,
                new Datatype(
                        List.class
                ).getDisplayName()
        );

        assertEquals(
                expected,
                new Datatype(
                        List.class.getName()
                ).getDisplayName()
        );

        assertEquals(
                expected,
                new Datatype(
                        List.class.getSimpleName()
                ).getDisplayName()
        );
    }

    @Test
    public void testDisplayNameSingleGeneric() {
        checkDisplayName("List<Integer>", false, List.class, Integer.class);
        checkDisplayName("java.util.List<java.lang.Integer>", true, List.class, Integer.class);

    }

    @Test
    public void testDisplayNameMultiGeneric() {
        checkDisplayName("Map<Integer, String>", false, Map.class, Integer.class, String.class);
        checkDisplayName("java.util.Map<java.lang.Integer, java.lang.String>", true,  Map.class, Integer.class, String.class);
    }

    @Test
    public void testEquals() {
        Datatype list               = new Datatype(List.class);
        Datatype listInteger        = new Datatype(List.class, Integer.class);
        Datatype map                = new Datatype(Map.class);
        Datatype mapInteger         = new Datatype(Map.class, Integer.class);
        Datatype mapIntegerString   = new Datatype(Map.class, Integer.class, String.class);

        assertTrue(
                list.equals(
                        listInteger,
                        true,
                        false
                )
        );

        assertTrue(
                list.equals(
                        listInteger,
                        true,
                        true
                )
        );

        assertTrue(
                map.equals(
                        mapInteger,
                        true,
                        true
                )
        );

        assertTrue(
                map.equals(
                        mapInteger,
                        true,
                        false
                )
        );

        assertTrue(
                map.equals(
                        mapIntegerString,
                        true,
                        true
                )
        );

        assertTrue(
                map.equals(
                        mapIntegerString,
                        true,
                        false
                )
        );

        assertTrue(
                mapInteger.equals(
                        mapIntegerString,
                        true,
                        true
                )
        );

        assertTrue(
                mapInteger.equals(
                        mapIntegerString,
                        true,
                        false
                )
        );


        checkEquals(List.class, list);
        checkEquals(List.class, listInteger);
        checkEquals(Map.class, map);
        checkEquals(Map.class, mapInteger);
        checkEquals(Map.class, mapIntegerString);


        assertFalse(list.equals(listInteger));
        assertFalse(list.equals(listInteger, false, false));

        assertFalse(list.equals(map));
        assertFalse(list.equals(map, true, true));

        assertFalse(map.equals(mapInteger));
        assertFalse(map.equals(mapInteger, false, false));

        assertFalse(map.equals(mapIntegerString));
        assertFalse(map.equals(mapIntegerString, false, false));


        assertFalse(mapInteger.equals(mapIntegerString));
        assertFalse(mapInteger.equals(mapIntegerString, false, false));
    }

    public void checkEquals(Class<?> clazz, Datatype type) {
        assertTrue(type.equals((Object)clazz));
        assertTrue(type.equals(clazz, true));

        assertFalse(type.equals((Object)clazz.getSimpleName()));
        assertTrue (type.equals((Object)clazz.getName()));
        assertTrue (type.equals(clazz.getName(), false));
        assertTrue (type.equals(clazz.getSimpleName(), true));
        assertFalse(type.equals(clazz.getSimpleName(), false));
    }

    protected void checkDisplayName(String expected, boolean includePackagePrefix, Class base, Class...generics) {

        assertEquals(
                expected,
                new Datatype(
                        base,
                        generics
                ).getDisplayName(includePackagePrefix)
        );

        assertEquals(
                expected,
                new Datatype(
                        base.getName(),
                        generics
                ).getDisplayName(includePackagePrefix)
        );

        assertEquals(
                expected,
                new Datatype(
                        base.getName(),
                        Arrays
                                .stream(generics)
                                .map(Class::getName)
                                .collect(Collectors.toList())
                                .toArray(new String[generics.length])
                ).getDisplayName(includePackagePrefix)
        );

        assertEquals(
                expected,
                new Datatype(
                        base,
                        Arrays
                                .stream(generics)
                                .map(Datatype::new)
                                .collect(Collectors.toList())
                                .toArray(new Datatype[generics.length])
                ).getDisplayName(includePackagePrefix)
        );

        assertEquals(
                expected,
                new Datatype(
                        base.getName(),
                        Arrays
                                .stream(generics)
                                .map(Datatype::new)
                                .collect(Collectors.toList())
                                .toArray(new Datatype[generics.length])
                ).getDisplayName(includePackagePrefix)
        );


        if (!includePackagePrefix) {
            assertEquals(
                    expected,
                    new Datatype(
                            base.getSimpleName(),
                            generics
                    ).getDisplayName(false)
            );

            assertEquals(
                    expected,
                    new Datatype(
                            base.getName(),
                            Arrays
                                    .stream(generics)
                                    .map(Class::getSimpleName)
                                    .collect(Collectors.toList())
                                    .toArray(new String[generics.length])
                    ).getDisplayName(false)
            );

            assertEquals(
                    expected,
                    new Datatype(
                            base.getSimpleName(),
                            Arrays
                                    .stream(generics)
                                    .map(Class::getName)
                                    .collect(Collectors.toList())
                                    .toArray(new String[generics.length])
                    ).getDisplayName(false)
            );

            assertEquals(
                    expected,
                    new Datatype(
                            base.getSimpleName(),
                            Arrays
                                    .stream(generics)
                                    .map(Class::getSimpleName)
                                    .collect(Collectors.toList())
                                    .toArray(new String[generics.length])
                    ).getDisplayName(false)
            );


            assertEquals(
                    expected,
                    new Datatype(
                            base.getSimpleName(),
                            Arrays
                                    .stream(generics)
                                    .map(Datatype::new)
                                    .collect(Collectors.toList())
                                    .toArray(new Datatype[generics.length])
                    ).getDisplayName(false)
            );
        }

    }
}
