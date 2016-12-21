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

package com.tallbyte.flowdesign.javafx;

import com.tallbyte.flowdesign.data.Diagram;
import com.tallbyte.flowdesign.data.Project;
import com.tallbyte.flowdesign.javafx.pane.NewProjectPane;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceString;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-21)<br/>
 */
public class PopupHandler {

    private final FlowDesignFxApplication application;
    private final List<PopupPreparer>     preparers  = new ArrayList<>();

    public PopupHandler(FlowDesignFxApplication application) {
        this.application = application;

        preparers.add(application.getColorHandler());
        preparers.add(new GeneralPopupPreparer());
    }

    public <T> Dialog<T> setupSimpleDialog(Dialog<T> dialog, String title, String itemName) {
        dialog.setGraphic(null);
        dialog.setTitle(getResourceString(title));
        dialog.setContentText(getResourceString(itemName));
        dialog.setHeaderText(null);

        for (PopupPreparer preparer : preparers) {
            preparer.prepare(dialog);
        }

        return dialog;
    }

    public <T> Dialog<T> setupDialog(Dialog<T> dialog) {
        dialog.setGraphic(null);
        dialog.setHeaderText(null);

        for (PopupPreparer preparer : preparers) {
            preparer.prepare(dialog);
        }

        return dialog;
    }

    public Stage setupManualDialog(Stage stage, Parent root, String title, int height) {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.setTitle(getResourceString(title));
        stage.setHeight(height);

        return setupStage(stage);
    }

    public Stage setupStage(Stage stage) {
        for (PopupPreparer preparer : preparers) {
            preparer.prepare(stage);
        }

        return stage;
    }

    public Popup setupPopup(Popup popup) {
        for (PopupPreparer preparer : preparers) {
            preparer.prepare(popup);
        }

        return popup;
    }

    public Dialog<String> createRenameDiagramDialog(Diagram diagram) {
        Dialog<String> dialog = new Dialog<>();

        ButtonType typeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().add(typeOk);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("dialogPaneContent");

        Label     labelName = new Label();
        TextField fieldName = new TextField();
        gridPane.add(labelName, 0, 0);
        gridPane.add(fieldName, 1, 0);

        dialog.getDialogPane().lookupButton(typeOk).disableProperty().bind(
                Bindings.createBooleanBinding(()
                                -> diagram.getProject().getDiagram(fieldName.getText()) != null,
                        fieldName.textProperty()
                ).or(fieldName.textProperty().isEmpty())
        );

        Platform.runLater(fieldName::requestFocus);

        labelName.setText(getResourceString("popup.rename.field.name"));
        dialog.setTitle(getResourceString("popup.rename.title"));

        dialog.getDialogPane().setContent(gridPane);
        setupDialog(dialog);
        dialog.setResultConverter(param -> {
            if (param == typeOk) {
                return fieldName.getText();
            }
            return null;
        });

        return dialog;
    }

    public Dialog<String> createCreateDiagramDialog(Class<? extends Diagram> clazz, Project project) {
        Dialog<String> dialog = new Dialog<>();

        ButtonType typeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(typeOk);

        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("dialogPaneContent");

        Label     labelName = new Label();
        TextField fieldName = new TextField();
        gridPane.add(labelName, 0, 0);
        gridPane.add(fieldName, 1, 0);

        dialog.getDialogPane().lookupButton(typeOk).disableProperty().bind(
                Bindings.createBooleanBinding(() -> {
                        Diagram diagram = project.getDiagram(fieldName.getText());

                        return diagram != null && clazz.equals(diagram.getClass());
                    },
                    fieldName.textProperty()
                ).or(fieldName.textProperty().isEmpty())
        );

        Platform.runLater(fieldName::requestFocus);

        labelName.setText(getResourceString("popup.new."+clazz.getSimpleName()+".field.name"));
        dialog.setTitle(getResourceString("popup.new."+clazz.getSimpleName()+".title"));

        dialog.getDialogPane().setContent(gridPane);
        setupDialog(dialog);
        dialog.setResultConverter(param -> {
            if (param == typeOk) {
                return fieldName.getText();
            }
            return null;
        });

        return dialog;
    }

    public Dialog<String> createCreateProjectDialog() {
        Dialog<String> dialog = new Dialog<>();

        ButtonType typeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(typeOk);

        try {
            NewProjectPane pane = new NewProjectPane();

            pane.getStyleClass().add("dialogPaneContent");

            dialog.getDialogPane().lookupButton(typeOk).disableProperty().bind(
                    pane.okProperty().not()
            );

            pane.onOpen();

            dialog.getDialogPane().setContent(pane);
            setupDialog(dialog);
            dialog.setResultConverter(param -> {
                if (param == typeOk) {
                    return pane.getName();
                }
                return null;
            });
        } catch (javafx.fxml.LoadException e) {
            e.printStackTrace(); // TODO
        }

        return dialog;
    }

}
