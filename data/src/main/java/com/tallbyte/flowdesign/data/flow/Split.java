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

package com.tallbyte.flowdesign.data.flow;

import com.tallbyte.flowdesign.data.*;
import com.tallbyte.flowdesign.data.notation.FlowNotationParserException;
import com.tallbyte.flowdesign.data.notation.actions.FlowAction;
import com.tallbyte.flowdesign.data.notation.actions.Tupel;
import com.tallbyte.flowdesign.data.notation.actions.TupelContainment;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-09)<br/>
 */
public class Split extends FlowDiagramElement {

    private final static Map<Joint, PropertyChangeListener> LISTENER_MAP = new HashMap<>();

    public static final String JOINT_GROUP_IN  = "in";
    public static final String JOINT_GROUP_OUT = "out";

    /**
     * Creats an new {@link Split}.
     */
    public Split() {
    }

    @Override
    protected Iterable<JointGroup<?>> createJointGroups() {
        JointGroup<FlowJoint> groupIn = new JointGroup<>(Split.this, JOINT_GROUP_IN , 1, 1, element -> new FlowJoint(element, JointType.INPUT , 1, 0), 1);

        groupIn.addJointsChangedListener((joint, added) -> {
            if (added) {
                PropertyChangeListener listener = evt -> {
                    if (evt.getPropertyName().equals("dataType")) {
                        genOutput();
                    }
                };

                joint.addPropertyChangeListener(listener);
                LISTENER_MAP.put(joint, listener);

            } else {
                joint.removePropertyChangeListener(LISTENER_MAP.remove(joint));
            }
        });

        for (Joint joint : groupIn.getJoints()) {
            PropertyChangeListener listener = evt -> {
                if (evt.getPropertyName().equals("dataType")) {
                    genOutput();
                }
            };

            joint.addPropertyChangeListener(listener);
            LISTENER_MAP.put(joint, listener);
        }

        return new ArrayList<JointGroup<?>>() {{
            add(groupIn);
            add(new JointGroup<>(Split.this, JOINT_GROUP_OUT, 1, 1,
                    element -> new FlowJoint(element, JointType.OUTPUT, 0, 0, new ConnectionTextExtractor() {
                        @Override
                        public String setConnection(FlowJoint joint, FlowConnection connection, String text) {
                            System.out.println(("---" + text));
                            try {
                                FlowAction action = joint.getParser().parse(text);

                                if (action instanceof Tupel) {
                                    int index = joint.getOutgoingIndex(connection);

                                    if (index >= 0 && index < ((Tupel) action).getTypeAmount()) {
                                        return new Tupel(0, 0, false, ((Tupel) action).getType(index)).toString();
                                    } else {
                                        return new Tupel(0, 0, false).toString();
                                    }
                                }
                            } catch (FlowNotationParserException e) {
                                // ignore
                            }

                            return new Tupel(0, 0, false).toString();
                        }

                        @Override
                        public String setJoint(FlowJoint joint, FlowConnection connection, String text) {
                            return ((FlowJoint) getInputGroup().getJoint(0)).getDataType();
                        }
                    }
                    ), 1)
            );
        }};
    }

    private void genOutput() {
        System.out.println("++++"+((FlowJoint) getInputGroup().getJoint(0)).getDataType());
        getOutputGroup().getJoints().stream()
                .filter(j -> j instanceof FlowJoint)
                .forEach(j -> ((FlowJoint)j).setDataType(((FlowJoint) getInputGroup().getJoint(0)).getDataType())
                );
    }

    public JointGroup<?> getInputGroup() {
        return getJointGroup(JOINT_GROUP_IN);
    }

    public JointGroup<?> getOutputGroup() {
        return getJointGroup(JOINT_GROUP_OUT);
    }

}
