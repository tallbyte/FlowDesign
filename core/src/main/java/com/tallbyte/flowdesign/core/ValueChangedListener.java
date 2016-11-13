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

/**
 * Created by michael on 13.11.16.
 */
public interface ValueChangedListener<S, V> {


    /**
     * Might notify on upcoming changes before they are executed.
     * The return value can be used to cancel such an action.
     *
     * @param source The source that is going to change
     * @param newValue The value that is going to be the new value
     * @param oldValue The value that is going to be the old value
     * @return True to request to cancel this action
     */
    default boolean onPreChange(S source, V newValue, V oldValue) {
        return false;
    }


    /**
     * Notifies that on a certain source, a value has changed
     *
     * @param source The source where the value has changed
     * @param newValue The new value
     * @param oldValue The old value
     */
    void onChanged(S source, V newValue, V oldValue);
}
