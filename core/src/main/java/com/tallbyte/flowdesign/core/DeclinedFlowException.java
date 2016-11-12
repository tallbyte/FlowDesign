package com.tallbyte.flowdesign.core;

/**
 * Created on 2016-10-27.
 */
public class DeclinedFlowException extends FlowException {
    public DeclinedFlowException() {
        super();
    }

    public DeclinedFlowException(String message) {
        super(message);
    }

    public DeclinedFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeclinedFlowException(Throwable cause) {
        super(cause);
    }

    protected DeclinedFlowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
