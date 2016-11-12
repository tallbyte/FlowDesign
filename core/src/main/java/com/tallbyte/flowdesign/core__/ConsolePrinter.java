package com.tallbyte.flowdesign.core__;

import com.tallbyte.flowdesign.core.FlowException;
import com.tallbyte.flowdesign.core__.element.LinearFlow;

/**
 * Created on 2016-10-27.
 */
public class ConsolePrinter extends LinearFlow<Object, Void> {
    @Override
    public Void invoke(Object input) throws FlowException {
        System.out.println(input != null ? (input.getClass()+": "+input) : null);
        return null;
    }
}
