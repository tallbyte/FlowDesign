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

import com.tallbyte.flowdesign.data.notation.FlowNotationParser;
import com.tallbyte.flowdesign.data.notation.FlowNotationParserException;
import com.tallbyte.flowdesign.data.notation.SimpleFlowNotationParser;
import com.tallbyte.flowdesign.data.notation.actions.FlowAction;
import com.tallbyte.flowdesign.data.notation.actions.Type;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-12)<br/>
 */
public class FlowJoint extends Joint {

    private final static FlowNotationParser PARSER = new SimpleFlowNotationParser();

    private JointValidator validator;

    private String  dataType;
    private boolean valid;

    /**
     * Creates a new {@link Joint} based on given configuration.
     * This will create an default {@link JointValidator} that
     * always returns true.
     *
     * @param element  the containing {@link Element}
     * @param type     the type
     * @param maxIn    the maximum amount of incoming
     *                 connections or 0 for infinite
     * @param maxOut   the maximum amount of outgoing
     *                 connections or 0 for infinite
     */
    public FlowJoint(Element element, JointType type, int maxIn, int maxOut) {
        this(element, type, maxIn, maxOut, joint -> {
            try {
                FlowAction f = PARSER.parse(joint.getDataType());

                return !(f instanceof Type);
            } catch (FlowNotationParserException e) {
                return false;
            }
        });
    }

    /**
     * Creates a new {@link Joint} based on given configuration.
     *
     * @param element  the containing {@link Element}
     * @param type     the type
     * @param maxIn    the maximum amount of incoming
     *                 connections or 0 for infinite
     * @param maxOut   the maximum amount of outgoing
     *                 connections or 0 for infinite
     * @param validator the {@link JointValidator} used to check
     *                  if the content of this {@link Joint} matches
     *                  rules.
     */
    public FlowJoint(Element element, JointType type, int maxIn, int maxOut, JointValidator validator) {
        super(element, type, maxIn, maxOut);

        this.validator = validator;
        setValid(validator.isValid(this));
    }

    /**
     * Gets a {@link FlowNotationParser} suitable for parsing this {@link FlowJoint}s
     * content.
     * @return Returns the parser.
     */
    public FlowNotationParser getParser() {
        return PARSER;
    }

    /**
     * Gets the current type as a string.
     * @return Returns the type.
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Sets the current string type.
     * @param dataType the new type
     */
    public void setDataType(String dataType) {
        String old = this.dataType;
        this.dataType = dataType;
        setValid(validator.isValid(this));
        changeSupport.firePropertyChange("dataType", old, dataType);
    }

    /**
     * Checks if this {@link FlowJoint} has valid type.
     * @return Returns true if it has, else false.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Sets the current valid value.
     * @param valid current value valid?
     */
    protected void setValid(boolean valid) {
        boolean old = this.valid;
        this.valid = valid;
        changeSupport.firePropertyChange("valid", old, valid);
    }

    @Override
    public boolean canJoin(Joint target) {
        return target instanceof FlowJoint && super.canJoin(target);
    }

    @Override
    protected Connection createConnection(Joint target) {
        return new FlowConnection(this, (FlowJoint) target);
    }
}
