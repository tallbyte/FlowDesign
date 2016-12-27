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
import javafx.fxml.LoadException;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-09)<br/>
 */
public class OperationalUnit extends FlowDiagramElement {

    protected ReferenceHandler referenceHandler = new ReferenceHandler("text", "reference", "name", "project", new ReferenceHolder() {
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

    protected Diagram reference;
    protected String  state = "";

    public static final String JOINT_GROUP_IN  = "in";
    public static final String JOINT_GROUP_OUT = "out";

    public static final String JOINT_GROUP_DEP_IN  = "depIn";
    public static final String JOINT_GROUP_DEP_OUT = "depOut";

    /**
     * Creats an new {@link OperationalUnit}.
     */
    public OperationalUnit() {
    }

    @Override
    protected Iterable<JointGroup<?>> createJointGroups() {
        return new ArrayList<JointGroup<?>>() {{
            add(new JointGroup<>(OperationalUnit.this, JOINT_GROUP_IN     , 1, 1, element -> new FlowJoint(element, JointType.INPUT , 1, 0), 1));
            add(new JointGroup<>(OperationalUnit.this, JOINT_GROUP_OUT    , 1, 1, element -> new FlowJoint(element, JointType.OUTPUT, 0, 2), 1));
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
        this.changeSupport.firePropertyChange("reference", old, diagram);
    }

    public void setReference(Diagram diagram) {
        throw new UnsupportedOperationException("This method only exists because a setter is required (looking at you, JavaFX");
    }

    public Diagram getReference() {
        return reference;
    }

    public void setState(String state) {
        String old = this.state;
        this.state = state;
        this.changeSupport.firePropertyChange("state", old, state);
    }

    public String getState() {
        return state;
    }
}
