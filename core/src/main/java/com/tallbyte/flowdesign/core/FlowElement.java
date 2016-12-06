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

import java.util.Collections;
import java.util.List;

/**
 * Created by michael on 21.11.16.
 */
public class FlowElement extends Element {

    protected DataType input;
    protected DataType output;
    protected String name;
    protected String commentary;
    protected List<FlowElement> representation;

    public FlowElement(String name, List<FlowElement> representation) {
        this.name           = name;
        this.representation = representation;
    }

    public FlowElement(DataType input, DataType output, String name) {
        this.input  = input;
        this.output = output;
        this.name   = name;
    }

    /**
     * @return The required input-{@link DataType}
     */
    public DataType getInput() {
        if (isRepresentative() && !representation.isEmpty()) {
            return representation.get(0).getInput();
        }
        return input; // null if representative and empty
    }

    /**
     * @return The {@link DataType} of the output
     */
    public DataType getOutput() {
        if (isRepresentative() && !representation.isEmpty()) {
            return representation.get(representation.size()-1).getOutput();
        }
        return output; // null if representative and empty
    }

    /**
     * @return The name of this {@link FlowElement}
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The new name of this {@link FlowElement}
     * @return itself
     */
    public FlowElement setName(String name) {
        // TODO notify listeners
        this.name = name;
        return this;
    }

    /**
     * @return Additional commentary for this {@link FlowElement} or null
     */
    public String getCommentary() {
        return commentary;
    }

    /**
     * @param commentary The new commentary for  this {@link FlowElement} or null
     * @return itself
     */
    public FlowElement setCommentary(String commentary) {
        // TODO notify listeners
        this.commentary = commentary;
        return this;
    }

    /**
     * @return Whether this {@link FlowElement} is representative an thus
     *         represents this {@link FlowElement} summaries multiple other
     *         {@link FlowElement} by its first input {@link DataType} and
     *         last output {@link DataType}
     */
    public boolean isRepresentative() {
        return representation != null;
    }

    /**
     * @param element The {@link FlowElement} to add to re representative chain
     * @return The given {@link FlowElement}
     * @throws DeclinedFlowException If adding failed
     */
    public FlowElement addToRepresentation(FlowElement element) throws DeclinedFlowException {
        if (!isRepresentative()) {
            throw new DeclinedFlowException("Not a representative "+getClass().getSimpleName());
        }

        if (getOutput() != null && !getOutput().equals(element.getInput())) {
            throw new DeclinedFlowException("DataType incompatible");
        }

        // TODO notify listeners
        representation.add(element);
        return element;
    }

    /**
     * @param element The {@link FlowElement} to remove
     * @return The given {@link FlowElement}
     * @throws DeclinedFlowException If the removal failed
     */
    public FlowElement removeFromRepresentation(FlowElement element) throws DeclinedFlowException {
        if (!isRepresentative()) {
            throw new DeclinedFlowException("Not a representative "+getClass().getSimpleName());
        }

        int index = representation.indexOf(element);

        if (index < 0) {
            throw new DeclinedFlowException("Unknown "+getClass().getSimpleName()+": "+element);
        }

        if (index > 0 && (index+1) < representation.size()) {
            // neither first nor last --> need to check DataType consistency
            FlowElement before = representation.get(index-1);
            FlowElement after  = representation.get(index+1);

            if (!before.getOutput().equals(after.getInput())) {
                throw new DeclinedFlowException("DataType mismatch of the chain");
            }
        }

        // TODO notify listeners
        representation.remove(index);
        return element;
    }

    /**
     * @return The {@link FlowElement}s represented by this {@link FlowElement}
     */
    public Iterable<FlowElement> getRepresentation() {
        return isRepresentative() ? representation : Collections.emptyList();
    }
}