package com.tallbyte.flowdesign.core__;

import com.tallbyte.flowdesign.core.FlowException;
import com.tallbyte.flowdesign.core__.element.LinearFlow;

import java.util.Scanner;

/**
 * Created on 2016-10-27.
 */
public class LineReader extends LinearFlow<Scanner, String> {

    @Override
    public String invoke(Scanner input) throws FlowException {
        return input.nextLine();
    }
}
