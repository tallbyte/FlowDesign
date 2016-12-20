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

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-09)<br/>
 */
public class OperationalUnit extends FlowDiagramElement {

    public static final String JOINT_INPUT0            = "input0";
    public static final String JOINT_OUTPUT0           = "output0";
    public static final String JOINT_DEPENDENCY_INPUT  = "dependencyInput";
    public static final String JOINT_DEPENDENCY_OUTPUT = "dependencyOutput";

    protected String input  = "";
    protected String output = "";

    protected Diagram reference = null;

    protected DiagramsChangedListener listenerDiagrams;
    protected PropertyChangeListener  listenerName;

    /**
     * Creats an new {@link OperationalUnit}.
     */
    public OperationalUnit() {
        addJoint(new FlowJoint(this, JOINT_INPUT0 , JointType.INPUT, 1));
        addJoint(new FlowJoint(this, JOINT_OUTPUT0, JointType.OUTPUT, 1));
        addJoint(new DependencyJoint(this, JOINT_DEPENDENCY_INPUT , JointType.INPUT , 0));
        addJoint(new DependencyJoint(this, JOINT_DEPENDENCY_OUTPUT, JointType.OUTPUT, 0));

        addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("text")) {
                setReference(getDiagram().getProject().getDiagram((String) evt.getNewValue()));
            }

            if (evt.getPropertyName().equals("reference")) {
                listenerName = evtName -> {
                    if (evtName.getPropertyName().equals("name")) {

                    }
                };

                reference.getProject().addDiagramsChangedListener(listenerDiagrams);
                System.out.println("ref="+evt.getNewValue());
            }
        });
    }

    @Override
    protected void setDiagram(Diagram diagram) {
        if (this.diagram != null) {
            this.diagram.getProject().removeDiagramsChangedListener(listenerDiagrams);
        }

        if (diagram != null) {
            listenerDiagrams = (diagramChanged, added) -> {
                if (!added && diagramChanged == this.reference) {
                    setReference(null);
                }
            };

            diagram.addPropertyChangeListener(listenerName);
        }

        super.setDiagram(diagram);
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    private void setReference(Diagram diagram) {
        this.changeSupport.firePropertyChange("reference", this.reference, diagram);
        this.reference = diagram;
    }

    public Diagram getReference() {
        return reference;
    }
}
