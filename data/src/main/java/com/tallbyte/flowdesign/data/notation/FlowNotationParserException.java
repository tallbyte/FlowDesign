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

package com.tallbyte.flowdesign.data.notation;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-23)<br/>
 */
public class FlowNotationParserException extends Exception {

    private final int x;
    private final int y;

    public FlowNotationParserException(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public FlowNotationParserException(String message, int x, int y) {
        super(buildMessage(message, x, y));

        this.x = x;
        this.y = y;
    }

    public FlowNotationParserException(String message, Throwable cause, int x, int y) {
        super(buildMessage(message, x, y), cause);

        this.x = x;
        this.y = y;
    }

    public FlowNotationParserException(Throwable cause, int x, int y) {
        super(buildMessage("", x, y), cause);

        this.x = x;
        this.y = y;
    }

    public FlowNotationParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int x, int y) {
        super(buildMessage(message, x, y), cause, enableSuppression, writableStackTrace);

        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private static String buildMessage(String message, int x, int y) {
        return String.format("%s%s[%d,%d]", message, (message == null || message.isEmpty() ? "" : " at "), x, y);
    }
}
