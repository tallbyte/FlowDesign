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

import com.tallbyte.flowdesign.data.DataType;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by michael on 21.11.16.
 */
public class DataTypeTest {

    @Test
    public void testFullClassName() {
        assertEquals(
                List.class.getName(),
                new DataType(
                        List.class
                ).getFullClassName()
        );

        assertEquals(
                List.class.getName(),
                new DataType(
                        List.class.getName()
                ).getFullClassName()
        );
    }

    @Test
    public void testPackageName() {
        assertEquals(
                List.class.getPackage().getName(),
                new DataType(
                        List.class
                ).getPackageName()
        );

        assertEquals(
                List.class.getPackage().getName(),
                new DataType(
                        List.class.getName()
                ).getPackageName()
        );

        assertEquals(
                null,
                new DataType(
                        List.class.getSimpleName()
                ).getPackageName()
        );
    }

    @Test
    public void testClassName() {
        assertEquals(
                List.class.getSimpleName(),
                new DataType(
                        List.class
                ).getClassName()
        );

        assertEquals(
                List.class.getSimpleName(),
                new DataType(
                        List.class.getName()
                ).getClassName()
        );

        assertEquals(
                List.class.getSimpleName(),
                new DataType(
                        List.class.getSimpleName()
                ).getClassName()
        );
    }

    @Test
    public void testDisplayNameNormal() {
        final String expected = "List";

        assertEquals(
                expected,
                new DataType(
                        List.class
                ).getDisplayName()
        );

        assertEquals(
                expected,
                new DataType(
                        List.class.getName()
                ).getDisplayName()
        );

        assertEquals(
                expected,
                new DataType(
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
        DataType list               = new DataType(List.class);
        DataType listInteger        = new DataType(List.class, Integer.class);
        DataType map                = new DataType(Map.class);
        DataType mapInteger         = new DataType(Map.class, Integer.class);
        DataType mapIntegerString   = new DataType(Map.class, Integer.class, String.class);

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

    public void checkEquals(Class<?> clazz, DataType type) {
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
                new DataType(
                        base,
                        generics
                ).getDisplayName(includePackagePrefix)
        );

        assertEquals(
                expected,
                new DataType(
                        base.getName(),
                        generics
                ).getDisplayName(includePackagePrefix)
        );

        assertEquals(
                expected,
                new DataType(
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
                new DataType(
                        base,
                        Arrays
                                .stream(generics)
                                .map(DataType::new)
                                .collect(Collectors.toList())
                                .toArray(new DataType[generics.length])
                ).getDisplayName(includePackagePrefix)
        );

        assertEquals(
                expected,
                new DataType(
                        base.getName(),
                        Arrays
                                .stream(generics)
                                .map(DataType::new)
                                .collect(Collectors.toList())
                                .toArray(new DataType[generics.length])
                ).getDisplayName(includePackagePrefix)
        );


        if (!includePackagePrefix) {
            assertEquals(
                    expected,
                    new DataType(
                            base.getSimpleName(),
                            generics
                    ).getDisplayName(false)
            );

            assertEquals(
                    expected,
                    new DataType(
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
                    new DataType(
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
                    new DataType(
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
                    new DataType(
                            base.getSimpleName(),
                            Arrays
                                    .stream(generics)
                                    .map(DataType::new)
                                    .collect(Collectors.toList())
                                    .toArray(new DataType[generics.length])
                    ).getDisplayName(false)
            );
        }

    }
}
