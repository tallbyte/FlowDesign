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

package com.tallbyte.flowdesign.javafx.diagram;

import com.tallbyte.flowdesign.core.EnvironmentDiagram;
import com.tallbyte.flowdesign.javafx.diagram.factory.ActorDiagramImageFactory;
import com.tallbyte.flowdesign.javafx.diagram.factory.SystemDiagramImageFactory;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-11-05)<br/>
 */
public class FactoryPane extends GridPane {

    protected final Map<Class<?>, List<FactoryNode>> factoryNodes = new HashMap<>();

    /**
     * Creates a new {@link FactoryPane} with a default text.
     */
    public FactoryPane() {
        getChildren().add(new Label("Please select a diagram"));
    }

    /**
     * Sets the {@link DiagramPane} this {@link FactoryPane} should reflect.
     * @param diagramPane the new {@link DiagramPane}
     */
    public void setDiagramPane(DiagramPane diagramPane) {
        factoryNodes.put(EnvironmentDiagram.class, new ArrayList<FactoryNode>() {{
            add(new FactoryNode(new SystemDiagramImageFactory(), "System"));
            add(new FactoryNode(new ActorDiagramImageFactory(), "Actor"));
        }});

        diagramPane.diagramProperty().addListener((observable, oldValue, newValue) -> {
            getChildren().clear();

            if (newValue != null) {
                List<FactoryNode> list = factoryNodes.get(newValue.getClass());

                if (list != null) {
                    for (int i = 0 ; i < list.size() ; ++i) {
                        FactoryNode child = list.get(i);

                        GridPane.setColumnIndex(child, i % 2);
                        GridPane.setRowIndex(child, i / 2);

                        getChildren().add(child);
                    }
                }

            }
        });
    }


}
