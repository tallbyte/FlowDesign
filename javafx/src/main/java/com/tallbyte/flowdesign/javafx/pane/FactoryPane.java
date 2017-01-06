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

package com.tallbyte.flowdesign.javafx.pane;

import com.tallbyte.flowdesign.data.Diagram;
import com.tallbyte.flowdesign.javafx.diagram.DiagramManager;
import com.tallbyte.flowdesign.javafx.diagram.FactoryNode;
import javafx.beans.value.ChangeListener;
import javafx.css.*;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceString;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-05)<br/>
 */
public class FactoryPane extends GridPane {

    private static final List<CssMetaData<? extends Styleable, ?>> STYLABLES;
    private static final CssMetaData<FactoryPane, Color>           DEFAULT_COLOR = new CssMetaData<FactoryPane, Color>(
            "-fx-default-color",
            StyleConverter.getColorConverter(),
            Color.BLACK ) {

        @Override
        public boolean isSettable(FactoryPane styleable) {
            return !styleable.defaultColorProperty().isBound();
        }

        @Override
        public StyleableProperty<Color> getStyleableProperty(FactoryPane styleable) {
            return styleable.defaultColorProperty();
        }
    };

    static {
        List<CssMetaData<? extends Styleable, ?>> list = new ArrayList<>(getClassCssMetaData());
        list.add(DEFAULT_COLOR);
        STYLABLES = Collections.unmodifiableList(list);
    }

    protected StyleableObjectProperty<Color> defaultColor = new SimpleStyleableObjectProperty<>(DEFAULT_COLOR);

    protected ChangeListener<Diagram> listener = null;

    /**
     * Creates a new {@link FactoryPane} with a default text.
     */
    public FactoryPane() {
        getChildren().add(new Label(getResourceString("pane.factory.default")));

        getStyleClass().add("factoryPane");
    }

    public void setup(DiagramsPane pane) {
        DiagramManager diagramManager = pane.getDiagramManager();

        pane.diagramProperty().addListener((o, oldPane, newPane) -> {
            if (listener != null && oldPane != null) {
                oldPane.diagramProperty().removeListener(listener);
            }

            if (newPane != null) {
                listener = (observable, oldValue, newValue) -> {
                    getChildren().clear();

                    if (newValue != null) {
                        if (diagramManager.isSupporting(newValue)) {
                            List<FactoryNode> list = diagramManager
                                    .getSupportedElements(newValue)
                                    .entrySet().stream()
                                    .filter(entry -> diagramManager.isUserCreateable(newValue, entry.getKey()))
                                    .map(factory
                                            -> new FactoryNode(
                                                factory.getValue(),
                                                factory.getKey(),
                                                getResourceString("pane.factory.node."+factory.getKey(), factory.getKey()),
                                                defaultColorProperty()
                                            )
                                    ).collect(Collectors.toList());
                            Collections.sort(list, (o1, o2) -> o1.getName().compareTo(o2.getName()));

                            for (int i = 0 ; i < list.size() ; ++i) {
                                FactoryNode child = list.get(i);

                                GridPane.setColumnIndex(child, i % 2);
                                GridPane.setRowIndex(child, i / 2);

                                getChildren().add(child);
                                GridPane.setHalignment(child, HPos.CENTER);
                                GridPane.setValignment(child, VPos.BOTTOM);
                            }
                        } else {
                            getChildren().add(new Label(getResourceString("pane.factory.unsupported")));
                        }
                    } else {
                        getChildren().add(new Label(getResourceString("pane.factory.default")));
                    }
                };

                listener.changed(newPane.diagramProperty(), null, newPane.getDiagram());

                newPane.diagramProperty().addListener(listener);
            }
        });
    }

    public Color getDefaultColor() {
        return defaultColor.get();
    }

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor.set(defaultColor);
    }

    public StyleableObjectProperty<Color> defaultColorProperty() {
        return defaultColor;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return STYLABLES;
    }

}
