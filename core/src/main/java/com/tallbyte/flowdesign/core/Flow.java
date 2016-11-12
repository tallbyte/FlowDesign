package com.tallbyte.flowdesign.core;

/**
 * Created on 2016-10-28.
 */
public interface Flow<I, O> {

    Class<I> getTypeInput();

    default boolean isSuitableInput(Class<?> clazz) {
        return getTypeInput().isAssignableFrom(clazz);
    }

    Class<O> getTypeOutput();

    String getName();

    String getCommentary();


    boolean isRepresentative();

    void addToRepresentation(Flow<?, ?> flow) throws DeclinedFlowException;

    boolean isRepresentationComplete();

    Iterable<Flow<?, ?>> getRepresentation();
}
