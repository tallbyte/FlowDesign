package com.tallbyte.flowdesign.core;

/**
 * Created on 2016-10-27.
 */
public class InvalidElementFlowException extends FlowException {
    public InvalidElementFlowException() {
        super();
    }

    public InvalidElementFlowException(String message) {
        super(message);
    }

    public InvalidElementFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidElementFlowException(Throwable cause) {
        super(cause);
    }

    protected InvalidElementFlowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
