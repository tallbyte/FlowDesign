/*
 * FlowDesign
 * Copyright (C) 2016 Tallbyte <copyright at tallbyte.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tallbyte.flowdesign.data;

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
