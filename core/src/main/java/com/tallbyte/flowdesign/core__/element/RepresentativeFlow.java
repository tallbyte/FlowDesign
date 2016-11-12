package com.tallbyte.flowdesign.core__.element;

import com.tallbyte.flowdesign.core.FlowException;
import com.tallbyte.flowdesign.core.InvalidElementFlowException;

/**
 * Created on 2016-10-27.
 */
public class RepresentativeFlow<I, O> extends LinearFlow<I, O> {

    protected Flow<? super I, ?> chain;

    public RepresentativeFlow() {

    }

    @Override
    public O invoke(I input) throws FlowException {
        try {
            return (O)chain.invokeChain(input);

        } catch (ClassCastException | NullPointerException cce) {
            throw new FlowException(cce);

        }
    }

    @Override
    public boolean hasRepresentation() {
        return true;
    }

    @Override
    public <T extends Flow<? super I, ?>> T setRepresentation(T representation) {
        this.chain = representation;
        return representation;
    }

    @Override
    public Flow<? super I, ?> getRepresentation() {
        if (chain != null) {
            return chain;
        }
        throw new InvalidElementFlowException();
    }

}
