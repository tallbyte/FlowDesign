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

package com.tallbyte.flowdesign.javafx.diagram.element;

import com.tallbyte.flowdesign.data.DependencyJoint;
import com.tallbyte.flowdesign.data.Diagram;
import com.tallbyte.flowdesign.data.FlowJoint;
import com.tallbyte.flowdesign.data.JointJoinException;
import com.tallbyte.flowdesign.data.flow.FlowDiagram;
import com.tallbyte.flowdesign.data.flow.Operation;
import com.tallbyte.flowdesign.data.flow.OperationalUnit;
import com.tallbyte.flowdesign.javafx.ShortcutGroup;
import com.tallbyte.flowdesign.javafx.Shortcuts;
import com.tallbyte.flowdesign.javafx.control.AutoSizeTextField;
import com.tallbyte.flowdesign.javafx.diagram.ElementNode;
import com.tallbyte.flowdesign.javafx.diagram.image.DiagramImage;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-08)<br/>
 */
public class OperationalUnitElementNode extends ElementNode {

    protected final OperationalUnit   operation;

    protected       StringProperty    state;
    protected       ObjectProperty<?> reference;

    public OperationalUnitElementNode(OperationalUnit element, DiagramImage content) {
        super(element, content, Pos.CENTER);

        setOnMousePressed(event -> requestFocus());

        this.operation = element;
    }

    @Override
    public void registerShortcuts(ShortcutGroup group) {
        super.registerShortcuts(group);

        group.getShortcut(Shortcuts.SHORTCUT_GO_TO_REFERENCE).setAction(event -> {
            Object reference = this.reference.get();
            if (reference instanceof FlowDiagram) {
                diagramPane.getDiagramsPane().addDiagram((FlowDiagram) reference);
            }
        });
        group.getShortcut(Shortcuts.SHORTCUT_ADD_FLOW).setAction(event -> {
            double x = getRealX() + getRealWidth() + 300;
            double y = getRealY();

            Operation operation = new Operation();
            operation.setX(x);
            operation.setY(y);
            operation.setWidth(getRealWidth());
            operation.setHeight(getRealHeight());

            FlowDiagram diagram = (FlowDiagram) diagramPane.getDiagram();
            diagram.addElement(operation);

            try {
                this.operation.getOutputGroup().getJoint(0).join(operation.getInputGroup().getJoint(0));
                operation.getInputGroup().getJoint(0).setDataType(this.operation.getOutputGroup().getJoint(0).getDataType());
            } catch (JointJoinException e) {
                // just try
            }
        });
    }

    private void setupProperties() {
        try {
            state     = JavaBeanStringPropertyBuilder.create().bean(element).name("state").build();
            reference = JavaBeanObjectPropertyBuilder.create().bean(element).name("reference").build();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not create properties. This should never happen?!", e);
        }

        reference.addListener((observable, oldValue, newValue) -> {
            pseudoClassStateChanged(PseudoClass.getPseudoClass("referenced"), newValue != null);
        });
        pseudoClassStateChanged(PseudoClass.getPseudoClass("referenced"), reference.getValue() != null);
    }

    @Override
    protected void addDefaultProperties() {
        super.addDefaultProperties();

        setupProperties();

        properties.add(state);
    }

    public String getState() {
        return state.get();
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public StringProperty stateProperty() {
        return state;
    }

    @Override
    protected void setup() {
        super.setup();

        TextField textField = setupText(new AutoSizeTextField(), state, "nodeTextHolder", Pos.BOTTOM_RIGHT);
        textField.setAlignment(Pos.BASELINE_LEFT);
        textField.getStyleClass().add("nodeStateTextHolder");
        textField.layoutXProperty().bind(wrap.widthProperty().multiply(0.8));
        textField.layoutYProperty().bind(wrap.heightProperty().multiply(0.7));
        textField.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, new Insets(0))));
        textField.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
            double rx = (1+Math.cos(2*Math.PI*0.125))*0.5*getRealWidth();
            double rw = getRealWidth()*0.25;

            rx -= rw*0.75;

            return rx + rw;
        }, wrap.widthProperty()));
        textField.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
            double ry = (1+Math.sin(2*Math.PI*0.125))*0.5*getRealHeight();
            double rh = getRealHeight()*0.25;

            ry -= rh*0.75;
            ry += rh*0.5;
            ry -= textField.getHeight()*0.5;

            return ry;
        }, wrap.heightProperty(), textField.heightProperty()));
        getChildren().add(textField);

        addJointsAcrossCircleCentered(new JointGroupHandler(operation.getInputGroup(), 0.5, 0.3));
        addJointsAcrossCircleCentered(new JointGroupHandler(operation.getOutputGroup(), 0.0, 0.3));

        addJointsAcrossCircleCentered(new JointGroupHandler(operation.getDependencyInputGroup(), 0.75, 0.0));
        addJointsAcrossCircleCentered(new JointGroupHandler(operation.getDependencyOutputGroup(), 0.25, 0.0));

        /*JointNode input0 = addJoint(operation.getJoint(Operation.JOINT_INPUT0));
        input0.centerXProperty().bind(Bindings.createDoubleBinding(() -> 0.0));
        input0.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));

        JointNode output0 = addJoint(operation.getJoint(Operation.JOINT_OUTPUT0));
        output0.centerXProperty().bind(widthProperty().subtract(widthExtend));
        output0.centerYProperty().bind(heightProperty().subtract(heightExtend).multiply(0.5));

        JointNode dependencyInput = addJoint(operation.getJoint(Operation.JOINT_DEPENDENCY_INPUT));
        dependencyInput.centerXProperty().bind(widthProperty().subtract(widthExtend).multiply(0.5));
        dependencyInput.centerYProperty().bind(Bindings.createDoubleBinding(() -> 0.0));

        JointNode dependencyOutput = addJoint(operation.getJoint(Operation.JOINT_DEPENDENCY_OUTPUT));
        dependencyOutput.centerXProperty().bind(widthProperty().subtract(widthExtend).multiply(0.5));
        dependencyOutput.centerYProperty().bind(heightProperty().subtract(heightExtend));**/
    }
}
