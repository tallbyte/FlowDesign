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
    protected PropertyChangeListener  listenerProject;
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
                Diagram diagram = getDiagram();

                if (diagram != null) {
                    Project project = diagram.getProject();

                    if (project != null) {
                        Diagram d = project.getDiagram((String) evt.getNewValue());

                        if (d instanceof FlowDiagram) {
                            setInternalReference(d);
                        } else {
                            setInternalReference(null);
                        }
                    }
                }
            }

            if (evt.getPropertyName().equals("reference")) {
                if (evt.getOldValue() != null) {
                    ((Diagram) evt.getOldValue()).removePropertyChangeListener(listenerName);
                }

                if (evt.getNewValue() != null) {
                    listenerName = evtName -> {
                        if (evtName.getPropertyName().equals("name")) {
                            setText((String) evtName.getNewValue());
                        }
                    };

                    ((Diagram) evt.getNewValue()).addPropertyChangeListener(listenerName);
                }
            }
        });
    }

    @Override
    protected void setDiagram(Diagram diagram) {
        if (this.diagram != null) {
            this.diagram.removePropertyChangeListener(listenerProject);
        }

        if (diagram != null) {
            listenerProject = evt -> {
                if (evt.getPropertyName().equals("project")) {
                    if (evt.getOldValue() != null) {
                        ((Project) evt.getOldValue()).removeDiagramsChangedListener(listenerDiagrams);
                    }

                    if (evt.getNewValue() != null) {
                        listenerDiagrams = (diagramChanged, added) -> {
                            if (!added && diagramChanged == this.reference) {
                                setInternalReference(null);
                            }

                            if (added && diagramChanged.getName().equals(getText())) {
                                setInternalReference(diagramChanged);
                            }
                        };

                        ((Project) evt.getNewValue()).addDiagramsChangedListener(listenerDiagrams);
                    }


                }
            };

            diagram.addPropertyChangeListener(listenerProject);
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
}
