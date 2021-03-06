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

import com.tallbyte.flowdesign.data.flow.FlowDiagram;

import java.beans.PropertyChangeListener;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-21)<br/>
 */
public class ReferenceHandler {

    protected Diagram diagram;

    protected String                  propertyProject;
    protected ReferenceHolder         holder;

    protected DiagramsChangedListener listenerDiagrams;
    protected PropertyChangeListener  listenerProject;
    protected PropertyChangeListener  listenerName;

    public ReferenceHandler(
            String propertyText,
            String propertyReference,
            String propertyName,
            String propertyProject,
            ReferenceHolder holder) {

        this.propertyProject = propertyProject;
        this.holder          = holder;

        holder.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals(propertyText)) {
                initText((String) evt.getOldValue(), (String) evt.getNewValue());
            }

            if (evt.getPropertyName().equals(propertyReference)) {
                if (evt.getOldValue() != null) {
                    ((Diagram) evt.getOldValue()).removePropertyChangeListener(listenerName);
                }

                if (evt.getNewValue() != null) {
                    listenerName = evtName -> {
                        if (evtName.getPropertyName().equals(propertyName)) {
                            holder.setText((String) evtName.getNewValue());
                        }
                    };

                    ((Diagram) evt.getNewValue()).addPropertyChangeListener(listenerName);
                }
            }
        });
    }

    /**
     * Sets the {@link Diagram}.
     *
     * @param diagram the new diagram
     */
    public void setDiagram(Diagram diagram) {
        if (this.diagram != null) {
            this.diagram.removePropertyChangeListener(listenerProject);
        }

        this.diagram = diagram;

        if (diagram != null) {
            listenerProject = evt -> {
                if (evt.getPropertyName().equals(propertyProject)) {
                    initProject((Project) evt.getOldValue(), (Project) evt.getNewValue());
                }
            };

            diagram.addPropertyChangeListener(listenerProject);

            initProject(null, diagram.getProject());
            initText(null, holder.getText());
        }
    }

    /**
     * Handler for text changes.
     *
     * @param oldText the old text
     * @param newText the new text
     */
    private void initText(String oldText, String newText) {
        Diagram diagram = holder.getDiagram();

        if (diagram != null) {
            Project project = diagram.getProject();

            if (project != null) {
                Diagram d = project.getDiagram(newText);

                if (d instanceof FlowDiagram) {
                    holder.setReference(d);
                } else {
                    holder.setReference(null);
                }
            }
        }
    }

    /**
     * Handler for {@link Project} changes.
     *
     * @param oldValue the old {@link Project}
     * @param newValue the new {@link Project}
     */
    private void initProject(Project oldValue, Project newValue) {
        if (oldValue != null) {
            oldValue.removeDiagramsChangedListener(listenerDiagrams);
        }

        if (newValue != null) {
            listenerDiagrams = (diagramChanged, added) -> {
                if (!added && diagramChanged == holder.getReference()) {
                    holder.setReference(null);
                }

                if (added && diagramChanged.getName().equals(holder.getText())
                        && diagramChanged instanceof FlowDiagram) {

                    holder.setReference(diagramChanged);
                }
            };

            initText(null, holder.getText());
            newValue.addDiagramsChangedListener(listenerDiagrams);
        }
    }
}
