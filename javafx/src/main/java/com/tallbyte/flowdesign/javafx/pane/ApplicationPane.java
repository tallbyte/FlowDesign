package com.tallbyte.flowdesign.javafx.pane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-10-26)<br/>
 */
public class ApplicationPane extends BorderPane {

    @FXML private Button buttonTest;
    @FXML private ImageView imageView;

    public ApplicationPane() throws LoadException {
        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/applicationPane.fxml") );
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new LoadException("Could not load "+getClass().getSimpleName());
        }

        buttonTest.setOnDragDetected(event -> {
            buttonTest.startFullDrag();
            Dragboard db = buttonTest.startDragAndDrop(TransferMode.COPY);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            db.setDragView(buttonTest.snapshot(params, null));
            db.setDragViewOffsetX(event.getX());
            db.setDragViewOffsetY(event.getY());
            ClipboardContent content = new ClipboardContent();
            content.putString(buttonTest.getId());
            db.setContent(content);
            event.consume();
        });
    }

}
