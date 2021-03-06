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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-09)<br/>
 */
public class OperationalUnit extends FlowDiagramElement {


    public static final String JOINT_GROUP_IN  = "in";
    public static final String JOINT_GROUP_OUT = "out";

    public static final String JOINT_GROUP_DEP_IN  = "depIn";
    public static final String JOINT_GROUP_DEP_OUT = "depOut";

    public static final String PROPERTY_REFERENCE     = "reference";
    public static final String PROPERTY_REFERENCE_FIT = "referenceFit";
    public static final String PROPERTY_STATE_ACCESS  = "stateAccess";
    public static final String PROPERTY_STATE         = "state";

    protected ReferenceHandler referenceHandler = new ReferenceHandler(
            PROPERTY_TEXT, PROPERTY_REFERENCE, Diagram.PROPERTY_NAME, Diagram.PROPERTY_PROJECT,
            new ReferenceHolder() {
        @Override
        public void setText(String text) {
            OperationalUnit.this.setText(text);
        }

        @Override
        public String getText() {
            return OperationalUnit.this.getText();
        }

        @Override
        public Diagram getDiagram() {
            return OperationalUnit.this.getDiagram();
        }

        @Override
        public void setReference(Diagram reference) {
            OperationalUnit.this.setInternalReference(reference);
        }

        @Override
        public Diagram getReference() {
            return OperationalUnit.this.getReference();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            OperationalUnit.this.addPropertyChangeListener(listener);
        }
    });

    protected Diagram                reference    = null;
    protected boolean                referenceFit = true;
    protected boolean                stateAccess  = true;
    protected String                 state        = "";

    protected PropertyChangeListener listener     = null;

    /**
     * Creats an new {@link OperationalUnit}.
     */
    public OperationalUnit() {
        addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals(PROPERTY_REFERENCE)) {
                if (evt.getOldValue() != null) {
                    ((Diagram) evt.getOldValue()).removePropertyChangeListener(listener);
                }

                if (evt.getOldValue() != evt.getNewValue()) {
                    calculateReferenceFit();
                }

                if (evt.getNewValue() != null) {
                    listener = evtData -> {
                        if (evtData.getPropertyName().equals("dataTypeIn")) {
                            calculateReferenceFit();
                        }

                        if (evtData.getPropertyName().equals("dataTypeOut")) {
                            calculateReferenceFit();
                        }
                    };

                    ((Diagram) evt.getNewValue()).addPropertyChangeListener(listener);
                }
            }
        });

        getInputGroup().getJoint(0).addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("dataType")) {
                calculateReferenceFit();
            }
        });

        getOutputGroup().getJoint(0).addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("dataType")) {
                calculateReferenceFit();
            }
        });
    }

    private void calculateReferenceFit() {
        boolean fit;

        if (reference instanceof FlowDiagram) {
            fit = ((FlowDiagram) reference).getDataTypeIn().equals(getInputGroup().getJoint(0).getDataType())
                    && ((FlowDiagram) reference).getDataTypeOut().equals(getOutputGroup().getJoint(0).getDataType());

        } else {
            fit = true;
        }

        setInternalReferenceFit(fit);
    }

    @Override
    protected Iterable<JointGroup<?>> createJointGroups() {
        return new ArrayList<JointGroup<?>>() {{
            add(new JointGroup<>(OperationalUnit.this, JOINT_GROUP_IN     , 1, 1, element -> new FlowJoint(element, JointType.INPUT , 1, 0), 1));
            add(new JointGroup<>(OperationalUnit.this, JOINT_GROUP_OUT    , 1, 1, element -> new FlowJoint(element, JointType.OUTPUT, 0, 0), 1));
            add(new JointGroup<>(OperationalUnit.this, JOINT_GROUP_DEP_IN , 1, 1, element -> new DependencyJoint(element, JointType.INPUT , 1, 0), 1));
            add(new JointGroup<>(OperationalUnit.this, JOINT_GROUP_DEP_OUT, 1, 1, element -> new DependencyJoint(element, JointType.OUTPUT, 0, 1), 1));
        }};
    }

    @SuppressWarnings("unchecked")
    public JointGroup<FlowJoint> getInputGroup() {
        return getJointGroup(JOINT_GROUP_IN);
    }

    @SuppressWarnings("unchecked")
    public JointGroup<FlowJoint> getOutputGroup() {
        return getJointGroup(JOINT_GROUP_OUT);
    }

    public JointGroup<?> getDependencyInputGroup() {
        return getJointGroup(JOINT_GROUP_DEP_IN);
    }

    public JointGroup<?> getDependencyOutputGroup() {
        return getJointGroup(JOINT_GROUP_DEP_OUT);
    }

    @Override
    protected void setDiagram(Diagram diagram) {
        super.setDiagram(diagram);

        referenceHandler.setDiagram(diagram);
    }

    private void setInternalReference(Diagram diagram) {
        Diagram old = this.reference;
        this.reference = diagram;
        this.changeSupport.firePropertyChange(PROPERTY_REFERENCE, old, diagram);
    }

    public void setReference(Diagram diagram) {
        throw new UnsupportedOperationException("This method only exists because a setter is required (looking at you, JavaFX)");
    }

    public Diagram getReference() {
        return reference;
    }

    private void setInternalReferenceFit(boolean referenceFit) {
        boolean old = this.referenceFit;
        this.referenceFit = referenceFit;
        this.changeSupport.firePropertyChange(PROPERTY_REFERENCE_FIT, old, referenceFit);
    }

    public void setReferenceFit(boolean referenceFit) {
        throw new UnsupportedOperationException("This method only exists because a setter is required (looking at you, JavaFX)");
    }

    public boolean isReferenceFit() {
        return referenceFit;
    }

    public void setStateAccess(boolean stateAccess) {
        boolean old = this.stateAccess;
        this.stateAccess = stateAccess;
        this.changeSupport.firePropertyChange(PROPERTY_STATE_ACCESS, old, stateAccess);
    }

    public boolean isStateAccess() {
        return stateAccess;
    }

    public void setState(String state) {
        String old = this.state;
        this.state = state;
        this.changeSupport.firePropertyChange(PROPERTY_STATE, old, state);
    }

    public String getState() {
        return state;
    }
}
