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

import com.tallbyte.flowdesign.core.ApplicationManager;
import com.tallbyte.flowdesign.javafx.pane.ApplicationPane;
import com.tallbyte.flowdesign.javafx.pane.SwitchPane;
import com.tallbyte.flowdesign.javafx.pane.WelcomePane;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.tallbyte.flowdesign.javafx.Shortcuts.*;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-26)<br/>
 */
public class FlowDesignFxApplication extends Application {

    private ShortcutManager     shortcutManager;
    private ApplicationManager  applicationManager;
    private ColorHandler        colorHandler;
    private PopupHandler        popupHandler;
    private List<Stage>         mainStages = new ArrayList<>();

    protected Point2D totalCenter;

    @Override
    public void start(Stage primaryStage) throws Exception {
        calculateTotalCenter();

        this.shortcutManager    = new ShortcutManager();
        this.applicationManager = new ApplicationManager();
        this.colorHandler       = new ColorHandler(this);
        this.popupHandler       = new PopupHandler(this);

        setupShortcuts();

        SwitchPane  switchPane  = new SwitchPane();
        WelcomePane defaultPane = new WelcomePane(this);
        switchPane.setDefaultContent(defaultPane);
        switchPane.setContent(defaultPane);

        primaryStage.titleProperty().bind(switchPane.titleProperty());
        Scene scene = new Scene(switchPane);
        primaryStage.setScene(scene);
        primaryStage.setWidth(700);
        primaryStage.setHeight(430);
        popupHandler.setupStage(primaryStage);

        primaryStage.show();
        totalCenter(primaryStage);
    }

    protected void calculateTotalCenter() {

        // dont trust for linux
        // Screen primary = Screen.getPrimary();
        double maxX = 0;
        double maxY = 0;

        for (Screen screen : Screen.getScreens()) {
            maxX = Math.max(maxX, screen.getVisualBounds().getMaxX());
            maxY = Math.max(maxY, screen.getVisualBounds().getMaxY());
        }

        double centerX = maxX / 2; // primary.getVisualBounds().getMinX() + primary.getBounds().getWidth()  / 2;
        double centerY = maxY / 2; // primary.getVisualBounds().getMinY() + primary.getBounds().getHeight() / 2;

        this.totalCenter = new Point2D(centerX, centerY);
    }

    protected void totalCenter(Stage stage) {
        stage.setX(totalCenter.getX() - stage.getWidth()  / 2);
        stage.setY(totalCenter.getY() - stage.getHeight() / 2);
        stage.centerOnScreen();
    }

    private void setupShortcuts() {
        ShortcutGroup groupElements = new ShortcutGroup(GROUP_DIAGRAM_ELEMENTS);
        groupElements.addShortcut(new Shortcut(SHORTCUT_GO_TO_REFERENCE, SHORTCUT_GO_TO_REFERENCE));
        groupElements.addShortcut(new Shortcut(SHORTCUT_OPEN_SUGGESTIONS, SHORTCUT_OPEN_SUGGESTIONS));
        groupElements.addShortcut(new Shortcut(SHORTCUT_APPLY_ACTION, SHORTCUT_APPLY_ACTION));
        groupElements.addShortcut(new Shortcut(SHORTCUT_ADD_FLOW, SHORTCUT_ADD_FLOW));
        groupElements.addShortcut(new Shortcut(SHORTCUT_ADD_DEPENDENCY, SHORTCUT_ADD_DEPENDENCY));
        groupElements.addShortcut(new Shortcut(SHORTCUT_MOVE_UP, SHORTCUT_MOVE_UP));
        groupElements.addShortcut(new Shortcut(SHORTCUT_MOVE_RIGHT, SHORTCUT_MOVE_RIGHT));
        groupElements.addShortcut(new Shortcut(SHORTCUT_MOVE_DOWN, SHORTCUT_MOVE_DOWN));
        groupElements.addShortcut(new Shortcut(SHORTCUT_MOVE_LEFT, SHORTCUT_MOVE_LEFT));

        ShortcutGroup groupDiagram = new ShortcutGroup(GROUP_DIAGRAM);
        groupDiagram.addShortcut(new Shortcut(SHORTCUT_REMOVE_SELECTED, SHORTCUT_REMOVE_SELECTED));

        shortcutManager.addGroup(groupElements);
        shortcutManager.addGroup(groupDiagram);
    }

    public ShortcutManager getShortcutManager() {
        return shortcutManager;
    }

    public ApplicationManager getApplicationManager() {
        return applicationManager;
    }

    public ColorHandler getColorHandler() {
        return colorHandler;
    }

    public PopupHandler getPopupHandler() {
        return popupHandler;
    }

    public ApplicationPane openApplication() throws javafx.fxml.LoadException {
        ApplicationPane pane = new ApplicationPane(this);

        Stage stage = new Stage();
        stage.setTitle("FlowDesign");
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setWidth(1600);
        stage.setHeight(900);
        popupHandler.setupStage(stage);
        stage.show();
        totalCenter(stage);

        mainStages.add(stage);
        stage.setOnCloseRequest(event -> mainStages.remove(stage));

        return pane;
    }

    public List<Stage> getMainStages() {
        return Collections.unmodifiableList(mainStages);
    }
}
