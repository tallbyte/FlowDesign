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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 13.11.16.
 */
public class ObservableValue<S, V> {

    protected final S source;
    protected V value;

    protected List<ValueChangedListener<? super S, ? super V>> listeners;

    public ObservableValue(S source) {
        this(source, null);
    }

    public ObservableValue(S source, V value) {
        this.source = source;
        this.value  = value;

        this.listeners  = new ArrayList<>();
    }

    /**
     * @return The current value
     */
    public V getValue() {
        return value;
    }

    /**
     * @param newValue The new value to set
     * @return The value that has been set
     */
    public V setValue(V newValue) {
        setValue(newValue, false);
        return getValue();
    }

    /**
     * Notifies all {@link ValueChangedListener}s about the new value
     *
     * @param newValue The new value
     * @param allowCancellation Whether to ask {@link ValueChangedListener} whether to cancel this change
     * @return Whether the value has been set to the given value
     */
    public boolean setValue(V newValue, boolean allowCancellation) {
        if (allowCancellation) {
            if (firePreChange(getValue(), newValue)) {
                // change cancelled
                return false;
            }
        }

        // actual change
        V oldValue = getValue();
        this.value = newValue;

        // notify listeners
        fireChanged(oldValue, newValue);

        return true;
    }

    /**
     * @param oldValue The old value
     * @param newValue The new value
     * @return Whether cancellation has been requested
     */
    protected boolean firePreChange(V oldValue, V newValue) {
        for (ValueChangedListener<? super S, ? super V> listener : listeners) {
            if (listener.onPreChange(source, oldValue, newValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param oldValue The old value
     * @param newValue The new value
     */
    protected void fireChanged(V oldValue, V newValue) {
        for (ValueChangedListener<? super S, ? super V> listener : listeners) {
            listener.onChanged(source, oldValue, newValue);
        }
    }

    /**
     * @param listener The {@link ValueChangedListener} to inform on changes
     * @return itself
     */
    public ObservableValue<S, V> addListener(ValueChangedListener<? super S, ? super V> listener) {
        this.listeners.add(listener);
        return this;
    }

    /**
     * @param listener {@link ValueChangedListener} to no longer inform about changes
     * @return Whether the removal was successfully and therefore the {@link ValueChangedListener} was registered before
     */
    public boolean removeListener(ValueChangedListener<? super S, ? super V> listener) {
        return this.listeners.remove(listener);
    }
}
