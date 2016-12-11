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
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-26)<br/>
 */
public class FlowDesignApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ApplicationManager manager = new ApplicationManager();

        SwitchPane  switchPane  = new SwitchPane();
        WelcomePane defaultPane = new WelcomePane(manager);
        switchPane.setDefaultContent(defaultPane);
        switchPane.setContent(defaultPane);

        primaryStage.titleProperty().bind(switchPane.titleProperty());
        Scene scene = new Scene(switchPane);
        primaryStage.getIcons().add(new Image("/images/realIcon.png"));
        primaryStage.setScene(scene);
        primaryStage.setWidth(700);
        primaryStage.setHeight(430);

        primaryStage.show();
    }

}
