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

import com.tallbyte.flowdesign.data.Diagram;
import com.tallbyte.flowdesign.data.ReferenceHandler;
import com.tallbyte.flowdesign.data.ReferenceHolder;
import com.tallbyte.flowdesign.data.environment.EnvironmentDiagramElement;

import java.beans.PropertyChangeListener;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-21)<br/>
 */
public class FlowComment extends FlowDiagramElement {

    protected ReferenceHandler referenceHandler = new ReferenceHandler("text", "reference", "name", "project", new ReferenceHolder() {
        @Override
        public void setText(String text) {
            FlowComment.this.setText(text);
        }

        @Override
        public String getText() {
            return FlowComment.this.getText();
        }

        @Override
        public Diagram getDiagram() {
            return FlowComment.this.getDiagram();
        }

        @Override
        public void setReference(Diagram reference) {
            FlowComment.this.setInternalReference(reference);
        }

        @Override
        public Diagram getReference() {
            return FlowComment.this.getReference();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            FlowComment.this.addPropertyChangeListener(listener);
        }
    });

    protected Diagram reference;

    public FlowComment() {
        setColor("#FFFFFF0F");
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

}
