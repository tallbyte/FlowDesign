package com.tallbyte.flowdesign.javafx.pane;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceBundle;

/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-11)<br/>
 */
public class SwitchPane extends BorderPane {

    @FXML private HBox hBoxButtons;

    private ObjectProperty<SwitchContentPane> defaultContent = new SimpleObjectProperty<>(this, "defaultContent", null);
    private ObjectProperty<SwitchContentPane> content        = new SimpleObjectProperty<>(this, "content", null);
    private StringProperty                    title          = new SimpleStringProperty(this, "title", null);

    public SwitchPane() throws LoadException {
        FXMLLoader loader = new FXMLLoader( getClass().getResource("/fxml/switchPane.fxml") );
        loader.setController(this);
        loader.setRoot(this);
        loader.setResources(getResourceBundle());

        try {
            loader.load();
        } catch (IOException e) {
            throw new LoadException("Could not load "+getClass().getSimpleName(), e);
        }

        content.set(defaultContent.get());

        content.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                hBoxButtons.getChildren().clear();

                if (newValue.hasOk()) {
                    Button buttonOk = new Button();
                    buttonOk.getStyleClass().add("buttonOk");
                    buttonOk.setText("OK");
                    buttonOk.setDefaultButton(true);
                    buttonOk.setOnAction(event -> newValue.onOk());
                    buttonOk.disableProperty().bind(newValue.okProperty().not());

                    hBoxButtons.getChildren().add(buttonOk);
                }

                if (newValue.hasCancel()) {
                    Button buttonCancel = new Button();
                    buttonCancel.getStyleClass().add("buttonCancel");
                    buttonCancel.setText("Cancel");
                    buttonCancel.setOnAction(event -> content.set(defaultContent.get()));

                    hBoxButtons.getChildren().add(buttonCancel);
                }

                hBoxButtons.setVisible(newValue.hasOk() || newValue.hasCancel());
                hBoxButtons.setManaged(newValue.hasOk() || newValue.hasCancel());

                newValue.onOpen(this);
                setCenter(newValue);
                setTitle(newValue.getTitle());
            }
        });

    }

    public SwitchContentPane getDefaultContent() {
        return defaultContent.get();
    }

    public void setDefaultContent(SwitchContentPane defaultContent) {
        this.defaultContent.set(defaultContent);
    }

    public ObjectProperty<SwitchContentPane> defaultContentProperty() {
        return defaultContent;
    }

    public SwitchContentPane getContent() {
        return content.get();
    }

    public void setContent(SwitchContentPane content) {
        this.content.set(content);
    }

    public ObjectProperty<SwitchContentPane> contentProperty() {
        return content;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }
}
