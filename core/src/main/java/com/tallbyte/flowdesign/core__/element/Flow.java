package com.tallbyte.flowdesign.core__.element;

import com.tallbyte.flowdesign.core.DeclinedFlowException;
import com.tallbyte.flowdesign.core.FlowException;
import com.tallbyte.flowdesign.core.InvalidElementFlowException;

/**
 * Created on 2016-10-27.
 */
public interface Flow<I, O> {

    /**
     * @param flow Follow up {@link Flow} to invoke
     * @return The given {@link Flow}
     * @throws DeclinedFlowException If adding failed
     */
    <T extends Flow<? super O, ?>> T addNext(T flow) throws DeclinedFlowException;

    /**
     * @param flow Follow up {@link Flow} to no longer invoke
     * @return The {@link Flow} that has been removed
     * @throws InvalidElementFlowException If the given {@link Flow} is unknown
     */
    <T extends Flow<? super O, ?>> T removeNext(T flow) throws InvalidElementFlowException;

    /**
     * @return All follow up {@link Flow}s as an {@link Iterable} (might be empty)
     */
    Iterable<Flow<? super O, ?>> getNext();

    /**
     * @return The minimum allowed amount of follow up {@link Flow}s required by this {@link Flow}
     */
    int getNextMinimum();

    /**
     * @return The maximum allowed amount of follow up {@link Flow}s allowed by this {@link Flow}
     */
    int getNextMaximum();

    /**
     * @return The current count of follow up {@link Flow}s
     */
    int getNextCount();

    /**
     * @return Whether follow up {@link Flow}s are allowed
     */
    default boolean isAcceptingNext() {
        return getNextCount() < getNextMaximum();
    }

    /**
     * @return Whether this {@link Flow} is ready to be invoked
     */
    default boolean invokable() {
        return getNextCount() >= getNextMinimum()
            && getNextCount() <= getNextMaximum();
    }

    default boolean hasRepresentation() {
        return false;
    }

    default <T extends Flow<? super I, ?>> T setRepresentation(T representation) throws DeclinedFlowException {
        throw new DeclinedFlowException();
    }

    default Flow<? super I, ?> getRepresentation() throws InvalidElementFlowException {
        throw new InvalidElementFlowException();
    }

    /**
     * @param input Input value
     * @return Processed output value
     * @throws FlowException If processing failed
     */
    O invoke(I input) throws FlowException;

    /**
     * Invokes this {@link Flow} and follow up {@link Flow}s
     *
     * @param input Input value
     * @return Processed output value
     * @throws FlowException If processing failed
     */
    Object invokeChain(I input) throws FlowException;
}
