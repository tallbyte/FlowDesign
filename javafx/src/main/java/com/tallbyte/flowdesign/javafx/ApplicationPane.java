package com.tallbyte.flowdesign.javafx.pane;

import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-26)<br/>
 */
public class ApplicationPane extends BorderPane {

    public ApplicationPane() throws LoadException {
        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/applicationPane.fxml") );
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new LoadException("Could not load "+getClass().getSimpleName());
        }
    }

}
