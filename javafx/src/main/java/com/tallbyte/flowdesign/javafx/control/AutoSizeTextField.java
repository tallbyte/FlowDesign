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

package com.tallbyte.flowdesign.javafx.control;

import com.sun.javafx.tk.*;
import com.tallbyte.flowdesign.javafx.popup.DataTypePopup;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;


/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-19)<br/>
 */
public class AutoSizeTextField extends TextField {

    public AutoSizeTextField() {
        setPadding(new Insets(0));
        textProperty().addListener((observable, oldValue, newValue) -> {
            final String newText = newValue != null ? newValue : "";
            /**
             * Warning this is using an internal API which might change in the future.
             *
             * As of now (December 2016) JavaFX has no public text measurement API...
             */
            com.sun.javafx.tk.FontMetrics metrics = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().getFontMetrics(getFont());
            setPrefWidth(metrics.computeStringWidth(newText)+5);

            /**
             * Otherwise the textfield will "scroll" when updated, despite getting bigger
             */
            Platform.runLater(() -> {
                positionCaret(0);
                Platform.runLater(() -> positionCaret(newText.length()));
            });

        });
        setText(null);
        setText("");
        setPrefColumnCount(60);
        setMinWidth(10);
    }

}