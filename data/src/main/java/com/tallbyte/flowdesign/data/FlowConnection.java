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

import com.tallbyte.flowdesign.data.notation.FlowNotationParserException;
import com.tallbyte.flowdesign.data.notation.actions.Chain;
import com.tallbyte.flowdesign.data.notation.actions.FlowAction;

import java.beans.PropertyChangeListener;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-12)<br/>
 */
public class FlowConnection extends Connection<FlowJoint> {

    protected final PropertyChangeListener listenerSource;
    protected final PropertyChangeListener listenerTarget;

    protected       boolean                enableUpdater = true;

    /**
     * Creates a new {@link Connection} between two {@link Joint}s.
     *
     * @param source the source {@link Joint}
     * @param target the target {@link Joint}
     */
    public FlowConnection(FlowJoint source, FlowJoint target) {
        super(source, target);

        listenerSource = evt -> {
            if (evt.getPropertyName().equals("dataType")) {
                updateText();
            }
        };
        listenerTarget = evt -> {
            if (evt.getPropertyName().equals("dataType")) {
                updateText();
            }
        };
        source.addPropertyChangeListener(listenerSource);
        target.addPropertyChangeListener(listenerTarget);

        updateText();
    }

    private void updateText() {
        if (!enableUpdater) {
            return;
        }

        String sourceText = source.getExtractor().setConnection(source, this, source.getDataType());
        String targetText = target.getExtractor().setConnection(target, this, target.getDataType());

        if (sourceText.equals(targetText)) {
            try {
                FlowAction action = source.getParser().parse(sourceText);

                if (action instanceof Chain) {
                    updateJoints(
                            ((Chain) action).getFirst().toString(),
                            ((Chain) action).getSecond().toString()
                    );

                    super.setText(action.toString());
                } else {
                    super.setText(sourceText);
                }
            } catch (FlowNotationParserException e) {
                super.setText(sourceText);
            }

        } else {
            FlowAction actionSource = null;
            FlowAction actionTarget = null;

            try {
                actionSource = source.getParser().parse(sourceText);
            } catch (FlowNotationParserException e) {
                // ignore
            }
            try {
                actionTarget = target.getParser().parse(targetText);
            } catch (FlowNotationParserException e) {
                // ignore
            }

            boolean setAuto = actionSource != null && actionTarget != null;
            if (setAuto) {
                try {
                    super.setText(new Chain(0, 0, actionSource, actionTarget).toString());
                    updateJoints(actionSource.toString(), actionTarget.toString());
                } catch (Exception e) {
                    setAuto = false;
                }
            }

            if (!setAuto) {
                super.setText(sourceText+"/"+targetText);
            }
        }
    }

    private void updateJoints(String text) {
        updateJoints(text, text);
    }

    private void updateJoints(String first, String second) {
        try {
            enableUpdater = false;
            source.setDataType(source.getExtractor().setJoint(source, this, first));
            target.setDataType(target.getExtractor().setJoint(target, this, second));
        } finally {
            enableUpdater = true;
        }
    }

    @Override
    protected void destroy() {
        super.destroy();

        source.removePropertyChangeListener(listenerSource);
        target.removePropertyChangeListener(listenerTarget);
    }

    @Override
    public void setText(String text) {
        super.setText(text);

        updateJoints(text);
    }
}
