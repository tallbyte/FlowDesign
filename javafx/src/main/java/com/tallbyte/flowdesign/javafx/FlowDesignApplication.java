package com.tallbyte.flowdesign.javafx;

import com.tallbyte.flowdesign.javafx.pane.ApplicationPane;
import javafx.application.Application;
import javafx.scene.Scene;
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
        Scene scene = new Scene(new ApplicationPane());
        primaryStage.setScene(scene);

        primaryStage.show();
    }

}
