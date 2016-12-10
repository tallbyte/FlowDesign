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

package com.tallbyte.flowdesign.data.flow;

import com.tallbyte.flowdesign.data.DataType;
import com.tallbyte.flowdesign.data.DeclinedFlowException;
import com.tallbyte.flowdesign.data.Element;

import java.util.Collections;
import java.util.List;

/**
 * Created by michael on 21.11.16.
 */
public class Operation extends Element {

    protected DataType input;
    protected DataType output;
    protected String name;
    protected String commentary;
    protected List<Operation> representation;

    public Operation(String name, List<Operation> representation) {
        this.name           = name;
        this.representation = representation;
    }

    public Operation(DataType input, DataType output, String name) {
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
     * @return The name of this {@link Operation}
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The new name of this {@link Operation}
     * @return itself
     */
    public Operation setName(String name) {
        // TODO notify listeners
        this.name = name;
        return this;
    }

    /**
     * @return Additional commentary for this {@link Operation} or null
     */
    public String getCommentary() {
        return commentary;
    }

    /**
     * @param commentary The new commentary for  this {@link Operation} or null
     * @return itself
     */
    public Operation setCommentary(String commentary) {
        // TODO notify listeners
        this.commentary = commentary;
        return this;
    }

    /**
     * @return Whether this {@link Operation} is representative an thus
     *         represents this {@link Operation} summaries multiple other
     *         {@link Operation} by its first input {@link DataType} and
     *         last output {@link DataType}
     */
    public boolean isRepresentative() {
        return representation != null;
    }

    /**
     * @param element The {@link Operation} to add to re representative chain
     * @return The given {@link Operation}
     * @throws DeclinedFlowException If adding failed
     */
    public Operation addToRepresentation(Operation element) throws DeclinedFlowException {
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
     * @param element The {@link Operation} to remove
     * @return The given {@link Operation}
     * @throws DeclinedFlowException If the removal failed
     */
    public Operation removeFromRepresentation(Operation element) throws DeclinedFlowException {
        if (!isRepresentative()) {
            throw new DeclinedFlowException("Not a representative "+getClass().getSimpleName());
        }

        int index = representation.indexOf(element);

        if (index < 0) {
            throw new DeclinedFlowException("Unknown "+getClass().getSimpleName()+": "+element);
        }

        if (index > 0 && (index+1) < representation.size()) {
            // neither first nor last --> need to check DataType consistency
            Operation before = representation.get(index-1);
            Operation after  = representation.get(index+1);

            if (!before.getOutput().equals(after.getInput())) {
                throw new DeclinedFlowException("DataType mismatch of the chain");
            }
        }

        // TODO notify listeners
        representation.remove(index);
        return element;
    }

    /**
     * @return The {@link Operation}s represented by this {@link Operation}
     */
    public Iterable<Operation> getRepresentation() {
        return isRepresentative() ? representation : Collections.emptyList();
    }
}
