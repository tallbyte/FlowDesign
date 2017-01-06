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

package com.tallbyte.flowdesign.data.mask;

import com.tallbyte.flowdesign.data.Diagram;
import com.tallbyte.flowdesign.data.ReferenceHandler;
import com.tallbyte.flowdesign.data.ReferenceHolder;

import java.beans.PropertyChangeListener;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-21)<br/>
 */
public class MaskComment extends MaskDiagramElement {

    public static final String PROPERTY_REFERENCE = "reference";

    protected ReferenceHandler referenceHandler = new ReferenceHandler(
            PROPERTY_TEXT, PROPERTY_REFERENCE, Diagram.PROPERTY_NAME, Diagram.PROPERTY_PROJECT,
            new ReferenceHolder() {
        @Override
        public void setText(String text) {
            MaskComment.this.setText(text);
        }

        @Override
        public String getText() {
            return MaskComment.this.getText();
        }

        @Override
        public Diagram getDiagram() {
            return MaskComment.this.getDiagram();
        }

        @Override
        public void setReference(Diagram reference) {
            MaskComment.this.setInternalReference(reference);
        }

        @Override
        public Diagram getReference() {
            return MaskComment.this.getReference();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            MaskComment.this.addPropertyChangeListener(listener);
        }
    });

    protected Diagram reference;

    public MaskComment() {
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
        this.changeSupport.firePropertyChange(PROPERTY_REFERENCE, old, diagram);
    }

    public void setReference(Diagram diagram) {
        throw new UnsupportedOperationException("This method only exists because a setter is required (looking at you, JavaFX");
    }

    public Diagram getReference() {
        return reference;
    }

}
