package com.tallbyte.flowdesign.core__;

import com.tallbyte.flowdesign.core.FlowException;
import com.tallbyte.flowdesign.core__.element.LinearFlow;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created on 2016-10-27.
 */
public class NumberParser extends LinearFlow<String, Number> {

    private final NumberFormat format;

    public NumberParser() {
        this(Locale.getDefault());
    }

    public NumberParser(Locale locale) {
        this.format = NumberFormat.getInstance(locale);
    }

    @Override
    public Number invoke(String input) throws FlowException {
        try {
            return format.parse(input);
        } catch (ParseException e) {
            throw new FlowException(e);
        }
    }
}
