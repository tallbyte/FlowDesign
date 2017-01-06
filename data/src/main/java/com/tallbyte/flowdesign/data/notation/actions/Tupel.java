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

package com.tallbyte.flowdesign.data.notation.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public class Tupel extends TupelContainment {

    private List<TupelContainment> types;
    private boolean                repeated;

    public Tupel(boolean repeated, List<TupelContainment> types) {
        this(0, 0, repeated, types);
    }

    public Tupel(boolean repeated, TupelContainment... types) {
        this(0, 0, repeated, types);
    }

    public Tupel(int start, int end, boolean repeated, List<TupelContainment> types) {
        super(start, end);

        this.types = types;
        this.repeated = repeated;
    }

    public Tupel(int start, int end, boolean repeated, TupelContainment... types) {
        super(start, end);

        this.types = new ArrayList<>();
        for (TupelContainment type : types) {
            this.types.add(type);
        }
        this.repeated = repeated;
    }

    public TupelContainment getType(int i) {
        return types.get(i);
    }

    public int getTypeAmount() {
        return types.size();
    }

    public List<TupelContainment> getTypes() {
        return Collections.unmodifiableList(types);
    }

    public boolean isRepeated() {
        return repeated;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("(");
        for (int i = 0 ; i < types.size() ; ++i) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(types.get(i).toString());
        }
        builder.append(")");
        if (repeated) {
            builder.append("*");
        }

        return builder.toString();
    }
}
