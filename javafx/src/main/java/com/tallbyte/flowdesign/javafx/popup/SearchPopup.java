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

package com.tallbyte.flowdesign.javafx.popup;

import com.tallbyte.flowdesign.data.Diagram;
import com.tallbyte.flowdesign.data.DiagramsChangedListener;
import com.tallbyte.flowdesign.javafx.pane.ApplicationPane;
import com.tallbyte.flowdesign.javafx.pane.DiagramsPane;
import com.tallbyte.flowdesign.javafx.pane.SearchEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.controlsfx.glyphfont.FontAwesome;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-16)<br/>
 */
public class SearchPopup extends Popup {

    protected final TextField         textFieldSearch;
    protected final ListView<Diagram> list;

    protected final ApplicationPane   pane;
    protected final DiagramsPane      paneDiagrams;

    protected ObservableList<Diagram> listData;
    protected DiagramsChangedListener listenerDiagrams = null;

    /**
     * Creates a new {@link SearchPopup}.
     */
    public SearchPopup(ApplicationPane pane, DiagramsPane paneDiagrams) {
        this.pane            = pane;
        this.paneDiagrams    = paneDiagrams;
        this.textFieldSearch = new TextField();
        this.list            = new ListView<>();

        listData = FXCollections.observableArrayList();

        FilteredList<Diagram> filter = new FilteredList<>(listData, diagram -> true);
        list.setItems(filter);

        textFieldSearch.textProperty().addListener(observable -> {
            filter.setPredicate(diagram -> diagram.getName().toLowerCase().startsWith(textFieldSearch.getText().toLowerCase()));

            list.getSelectionModel().select(0);
        });

        list.setCellFactory(param -> {
            ListCell<Diagram> cell = new ListCell<Diagram>() {
                @Override
                protected void updateItem(Diagram item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(new SearchEntry(item));
                    }
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    close();
                }
            });

            return cell;
        });

        list.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                close();
                event.consume();

            } else if (event.getCode() == KeyCode.UP && list.getSelectionModel().getSelectedIndices().contains(0)) {
                textFieldSearch.requestFocus();
            }
        });

        addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                hide();
                event.consume();

            }
        });

        textFieldSearch.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP
                    || event.getCode() == KeyCode.DOWN) {

                list.requestFocus();
                list.fireEvent(event);
            }  else if (event.getCode() == KeyCode.ENTER) {
                close();
                event.consume();

            }
        });

        setAutoHide(true);
        setHideOnEscape(true);

        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add("searchPopup");

        BorderPane paneTop = new BorderPane();
        paneTop.getStyleClass().add("searchHeader");

        StackPane paneStack = new StackPane();
        paneStack.getChildren().add(textFieldSearch);
        StackPane.setAlignment(textFieldSearch, Pos.CENTER);

        Button buttonSearch = new Button();
        buttonSearch.setStyle("-fx-font-family: FontAwesome");
        buttonSearch.setText("\uf002");
        buttonSearch.getStyleClass().add("iconButton");
        buttonSearch.getStyleClass().add("searchButton");

        paneStack.getChildren().add(buttonSearch);
        StackPane.setAlignment(buttonSearch, Pos.CENTER_RIGHT);

        paneTop.setCenter(paneStack);

        borderPane.setTop(paneTop);
        borderPane.setCenter(list);

        getContent().add(borderPane);

        setup();
    }
    /**
     * Shows this popup.
     * @param ownerNode the owning {@link Node} of the shown popup
     * @param anchorX the x location in screen coordinates
     * @param anchorY the y location in screen coordinates
     */
    public void show(Node ownerNode, double anchorX, double anchorY) {
        super.show(ownerNode, anchorX, anchorY);

        textFieldSearch.requestFocus();
    }

    /**
     * Closes this {@link SearchPopup} and executes the selected action.
     */
    private void close() {
        Diagram selected = list.getSelectionModel().getSelectedItem();

        if (selected != null) {
            textFieldSearch.setText("");
            paneDiagrams.addDiagram(selected);
        }

        hide();
    }

    private void setup() {
        pane.projectProperty().addListener((o, oldProject, newProject) -> {
            if (listenerDiagrams != null && oldProject != null) {
                oldProject.removeDiagramsChangedListener(listenerDiagrams);
            }

            if (newProject != null) {
                listData.clear();

                listenerDiagrams = (diagram, added) -> {
                    if (added) {
                        listData.add(diagram);
                    } else {
                        listData.remove(diagram);
                    }
                };

                for (Class<? extends Diagram> clazz : newProject.getDiagramTypes()) {
                    for (Diagram diagram : newProject.getDiagrams(clazz)) {
                        listData.add(diagram);
                    }
                }

                newProject.addDiagramsChangedListener(listenerDiagrams);
            }
        });
    }
}
