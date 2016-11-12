package com.tallbyte.flowdesign.core__.element;

import com.tallbyte.flowdesign.core.DeclinedFlowException;
import com.tallbyte.flowdesign.core.FlowException;
import com.tallbyte.flowdesign.core.InvalidElementFlowException;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Created on 2016-10-27.
 */
public abstract class LinearFlow<I, O> implements Flow<I, O> {

    protected Flow<? super O, ?> next;

    @Override
    public <T extends Flow<? super O, ?>> T addNext(T flow) throws DeclinedFlowException {
        if (next == null) {
            this.next = flow;
            return flow;
        }
        throw new DeclinedFlowException();
    }

    @Override
    public <T extends Flow<? super O, ?>> T removeNext(T flow) throws InvalidElementFlowException {
        if (Objects.equals(next, flow)) {
            this.next = null;
            return flow;
        }
        throw new InvalidElementFlowException();
    }

    @Override
    public Collection<Flow<? super O, ?>> getNext() {
        if (next != null) {
            return Collections.singletonList(next);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Object invokeChain(I input) throws FlowException {
        if (next != null) {
            return next.invokeChain(invoke(input));
        } else {
            return invoke(input);
        }
    }

    @Override
    public int getNextMinimum() {
        return 1;
    }

    @Override
    public int getNextMaximum() {
        return 1;
    }

    @Override
    public int getNextCount() {
        return next != null ? 1 : 0;
    }
}
