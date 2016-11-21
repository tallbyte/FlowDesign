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

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by michael on 21.11.16.
 */
public class Datatype {

    public static final char SEPARATOR_PACKAGE          = '.';
    public static final char SEPARATOR_GENERIC_BEGIN    = '<';
    public static final char SEPARATOR_GENERIC_END      = '>';
    public static final String SEPARATOR_GENERICS       = ", ";

    protected final String     namePackage;
    protected final String     nameClass;
    protected final Datatype[] generics;

    public Datatype(Class<?> clazz) {
        this(clazz.getName());
    }

    public Datatype(Class<?> clazz, Class<?>...generics) {
        this(clazz.getName(), generics);
    }

    public Datatype(Class<?> clazz, Datatype...generics) {
        this(clazz.getName(), generics);
    }

    public Datatype(String name) {
        this(name, new Datatype[0]);
    }

    public Datatype(String name, String...generics) {
        this(
                name,
                Arrays
                        .stream(generics)
                        .map(Datatype::new)
                        .collect(Collectors.toList())
                        .toArray(new Datatype[generics.length])
        );
    }

    public Datatype(String name, Class<?>...generics) {
        this(
                name,
                Arrays
                        .stream(generics)
                        .map(Datatype::new)
                        .collect(Collectors.toList())
                        .toArray(new Datatype[generics.length])
        );
    }

    public Datatype(String name, Datatype...generics) {
        this.namePackage    = getPackage(name);
        this.nameClass      = getClass(name);
        this.generics       = generics;
    }


    /**
     * @return The package path and class name
     */
    public String getFullClassName() {
        return getPackageName() != null
                ? getPackageName() + SEPARATOR_PACKAGE + getClassName()
                : getClassName();
    }

    /**
     * @return The package path of the package or null
     */
    public String getPackageName() {
        return namePackage;
    }

    /**
     * @return The name only of the class
     */
    public String getClassName() {
        return nameClass;
    }

    /**
     * @return The name of this {@link Datatype} and its generic types without the package prefix
     */
    public String getDisplayName() {
        return getDisplayName(false);
    }

    /**
     * @param includePackagePrefix Whether to include package prefix if available
     * @return The name of this {@link Datatype} and its generic types
     */
    public String getDisplayName(boolean includePackagePrefix) {
        StringBuilder builder = new StringBuilder();

        if (includePackagePrefix) {
            builder.append(getFullClassName());

        } else {
            builder.append(getClassName());
        }

        if (generics.length > 0) {
            builder.append(SEPARATOR_GENERIC_BEGIN);
            builder.append(generics[0].getDisplayName(includePackagePrefix));

            for (int i = 1; i < generics.length; ++i) {
                builder.append(SEPARATOR_GENERICS);
                builder.append(generics[i].getDisplayName(includePackagePrefix));
            }

            builder.append(SEPARATOR_GENERIC_END);
        }

        return builder.toString();
    }

    /**
     * @param fullClassName A full class name including the package path
     * @return The package path or null if none specified in the path
     */
    public static String getPackage(String fullClassName) {
        int index = fullClassName.lastIndexOf(SEPARATOR_PACKAGE);

        if (index > 0) {
            return fullClassName.substring(0, index);

        } else {
            // seems to have not package path
            return null;
        }
    }

    /**
     * @param fullClassName A full class name including the package path or only the class name
     * @return The class name
     */
    public static String getClass(String fullClassName) {
        int index = fullClassName.lastIndexOf(SEPARATOR_PACKAGE);

        if (index > 0 && (index+1) < fullClassName.length()) {
            return fullClassName.substring(index+1);

        } else {
            // seems to be the class name itself
            return fullClassName;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof Class) {
            return equals((Class)obj, false);
        }

        else if (obj instanceof String) {
            return equals((String)obj, false);
        }

        else if (obj instanceof Datatype) {
            // datatype allows full comparision
            return equals((Datatype)obj, false, false);
        }

        return false;
    }

    /**
     * @param name The class to compare to
     * @param ignorePackage Whether to ignore different packages paths
     * @return Whether the given {@link Class} could be a (simplified) representation of this {@link Datatype}
     */
    public boolean equals(Class<?> name, boolean ignorePackage) {
        return equals(
                new Datatype(name),
                true, // cannot be compared with only the base class
                ignorePackage
        );
    }

    /**
     * @param name The name of the class to compare to
     * @param ignorePackage Whether to ignore different package paths
     * @return Whether the given {@link String} could be a (simplified) representation of this {@link Datatype}
     */
    public boolean equals(String name, boolean ignorePackage) {
        return equals(
                new Datatype(name),
                true, // cannot be compared with only the base class
                ignorePackage
        );
    }

    /**
     * @param datatype The {@link Datatype} to compare to
     * @param ignoreGenerics Whether to ignore different generics
     * @param ignorePackage Whether to ignore different package paths
     * @return Whether the given {@link Datatype} is the same or a (simplified) representation of this {@link Datatype}
     */
    public boolean equals(Datatype datatype, boolean ignoreGenerics, boolean ignorePackage) {
        if (datatype == this) {
            return true;
        }

        if (datatype == null) {
            return false;
        }

        // check base class
        if (ignorePackage) {
            if (ignoreGenerics) {
                // ignorePackage, ignoreGenerics
                // without package comparision
                if (!getClassName().equals(datatype.getClassName())) {
                    return false;
                }
            } else {
                // ignorePackage, !ignoreGenerics
                // the display name contains the generics but not the package path
                return getDisplayName().equals(datatype.getDisplayName());
            }

        } else {
            // !ignorePackage
            // including package comparision
            if (!getFullClassName().equals(datatype.getFullClassName())) {
                return false;
            }
        }

        if (!ignoreGenerics) {
            if (generics.length != datatype.generics.length) {
                return false;
            }

            // need to check all generics
            for (int i = 0; i < generics.length; ++i) {
                if (!generics[i].equals(datatype.generics[i], ignoreGenerics, ignorePackage)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append("[package=");
        builder.append(namePackage);
        builder.append(",class=");
        builder.append(nameClass);

        if (generics.length > 0) {
            builder.append(",generics=");
            builder.append(Arrays.deepToString(generics));
        }

        builder.append("]@");
        builder.append(hashCode());
        return builder.toString();
    }
}
