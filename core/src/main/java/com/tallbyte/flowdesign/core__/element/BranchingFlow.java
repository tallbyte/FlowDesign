package com.tallbyte.flowdesign.core__.element;

import com.tallbyte.flowdesign.core.DeclinedFlowException;
import com.tallbyte.flowdesign.core.InvalidElementFlowException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016-10-27.
 */
public abstract class BranchingFlow<I, O> implements Flow<I, O> {

    protected final List<Flow<? super O, ?>> next;

    public BranchingFlow() {
        this.next = new ArrayList<>();
    }

    @Override
    public <T extends Flow<? super O, ?>> T addNext(T flow) throws DeclinedFlowException {
        if (getNextCount() < getNextMaximum()) {
            this.next.add(flow);
            return flow;
        }
        throw new DeclinedFlowException();
    }

    @Override
    public <T extends Flow<? super O, ?>> T removeNext(T flow) throws InvalidElementFlowException {
        if (next.remove(flow)) {
            return flow;
        }
        throw new InvalidElementFlowException();
    }

    @Override
    public Iterable<Flow<? super O, ?>> getNext() {
        return next;
    }

    @Override
    public int getNextMinimum() {
        return 1;
    }

    @Override
    public int getNextCount() {
        return next.size();
    }
}
