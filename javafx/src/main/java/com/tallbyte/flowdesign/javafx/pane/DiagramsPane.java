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

import com.tallbyte.flowdesign.core.Diagram;
import com.tallbyte.flowdesign.javafx.diagram.DiagramPane;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.HashMap;
import java.util.Map;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-06)<br/>
 */
public class DiagramsPane extends TabPane {

    private final ListProperty<Diagram> diagrams = new SimpleListProperty<>(this, "diagram", FXCollections.observableArrayList());
    private final Map<Diagram, Tab>     tabMap   = new HashMap<>();
    private final Map<Tab, DiagramPane> paneMap  = new HashMap<>();

    private final ObjectProperty<DiagramPane> diagram = new SimpleObjectProperty<>(this, "diagram", null);

    public DiagramsPane() {
        setup();
    }

    public void addDiagram(Diagram diagram) {
        diagrams.add(diagram);
    }

    public void removeDiagram(Diagram diagram) {
        diagrams.remove(diagram);
    }

    public DiagramPane getDiagram() {
        return diagram.get();
    }

    public ReadOnlyObjectProperty<DiagramPane> diagramProperty() {
        return diagram;
    }

    private void setup() {
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("new tab "+paneMap.get(newValue));
            diagram.setValue(paneMap.get(newValue));
        });

        diagrams.addListener((ListChangeListener<Diagram>) c -> {
            while(c.next()) {
                for (Diagram d : c.getAddedSubList()) {
                    Tab         tab  = new Tab();
                    DiagramPane pane = new DiagramPane(d);

                    tab.textProperty().bindBidirectional(pane.nameProperty());
                    tab.setContent(new ScrollPane(pane));

                    paneMap.put(tab, pane);
                    tabMap.put(d, tab);
                    getTabs().add(tab);

                    tab.setOnClosed(event -> {
                        tabMap.remove(d);
                        paneMap.remove(tab);
                    });
                }

                for (Diagram d : c.getRemoved()) {
                    Tab tab = tabMap.remove(d);

                    if (tab != null) {
                        getTabs().remove(tab);
                        paneMap.remove(tab);
                    }
                }
            }

        });
    }

}
