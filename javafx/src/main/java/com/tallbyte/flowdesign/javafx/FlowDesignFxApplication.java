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

import com.tallbyte.flowdesign.core.storage.ApplicationManager;
import com.tallbyte.flowdesign.javafx.pane.ApplicationPane;
import com.tallbyte.flowdesign.javafx.pane.SwitchPane;
import com.tallbyte.flowdesign.javafx.pane.WelcomePane;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceString;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-26)<br/>
 */
public class FlowDesignFxApplication extends Application {

    private ApplicationManager  applicationManager;
    private ColorHandler        colorHandler;
    private List<PopupPreparer> preparers = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.applicationManager = new ApplicationManager();
        this.colorHandler       = new ColorHandler();

        preparers.add(colorHandler);
        preparers.add(new GeneralPopupPreparer());

        SwitchPane  switchPane  = new SwitchPane();
        WelcomePane defaultPane = new WelcomePane(this);
        switchPane.setDefaultContent(defaultPane);
        switchPane.setContent(defaultPane);

        primaryStage.titleProperty().bind(switchPane.titleProperty());
        Scene scene = new Scene(switchPane);
        primaryStage.setScene(scene);
        primaryStage.setWidth(700);
        primaryStage.setHeight(430);
        setupStage(primaryStage);

        primaryStage.show();
    }

    public ApplicationManager getApplicationManager() {
        return applicationManager;
    }

    public ColorHandler getColorHandler() {
        return colorHandler;
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

}
