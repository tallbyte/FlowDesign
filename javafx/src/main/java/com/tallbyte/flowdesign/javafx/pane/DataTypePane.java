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

import com.tallbyte.flowdesign.data.DataType;
import com.tallbyte.flowdesign.data.DataTypesChangedListener;
import com.tallbyte.flowdesign.data.Diagram;
import com.tallbyte.flowdesign.data.Project;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceBundle;
import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceString;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-11)<br/>
 */
public class DataTypePane extends BorderPane {

    @FXML private TextField          textFieldSearch;

    @FXML private Button             buttonAdd;
    @FXML private Button             buttonRemove;

    @FXML private ListView<DataType> listTypes;

    protected ObservableList<DataType> listData;
    protected Project                  project       = null;
    protected DataTypesChangedListener listenerTypes = null;

    public DataTypePane() throws LoadException {
        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/dataTypePane.fxml") );
        loader.setController(this);
        loader.setRoot(this);
        loader.setResources(getResourceBundle());

        try {
            loader.load();
        } catch (IOException e) {
            throw new LoadException("Could not load "+getClass().getSimpleName(), e);
        }

        listData = FXCollections.observableArrayList();

        FilteredList<DataType> filter = new FilteredList<>(listData, dataType -> true);
        listTypes.setItems(filter);



        buttonAdd.setDisable(true);
        buttonRemove.setDisable(true);

        textFieldSearch.textProperty().addListener(observable -> {
            filter.setPredicate(dataType -> dataType.getClassName().toLowerCase().startsWith(textFieldSearch.getText().toLowerCase()));
        });

        listTypes.setCellFactory(new Callback<ListView<DataType>, ListCell<DataType>>() {
            @Override
            public ListCell<DataType> call(ListView<DataType> param) {
                return new ListCell<DataType>() {
                    @Override
                    protected void updateItem(DataType item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new DataTypeEntry(item));
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void onAdd() {
        if (project.getDataTypeHolder().addDataType(new DataType(textFieldSearch.getText()))) {
            textFieldSearch.setText("");
        }
    }

    @FXML
    private void onRemove() {
        project.getDataTypeHolder().removeDataType(listTypes.getSelectionModel().getSelectedItem());
        listTypes.getSelectionModel().select(null);
    }

    public void setup(ApplicationPane pane) {
        buttonAdd.disableProperty()
                .bind(textFieldSearch.textProperty().isEmpty()
                      .or(pane.projectProperty().isNull())
        );
        buttonRemove.disableProperty()
                .bind(listTypes.getSelectionModel().selectedItemProperty().isNull()
                      .or(pane.projectProperty().isNull())
        );

        pane.projectProperty().addListener((o, oldProject, newProject) -> {
            if (listenerTypes != null && oldProject != null) {
                oldProject.getDataTypeHolder().removeDataTypesChangedListener(listenerTypes);
            }

            project = newProject;

            if (newProject != null) {
                listData.clear();

                listenerTypes = (dataType, added) -> {
                    if (added) {
                        listData.add(dataType);
                    } else {
                        listData.remove(dataType);
                    }
                };

                for (DataType type : newProject.getDataTypeHolder().getDataTypes()) {
                    listData.add(type);
                }

                newProject.getDataTypeHolder().addDataTypesChangedListener(listenerTypes);
            }
        });
    }
}
