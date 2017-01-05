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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;


/**
 * This file is part of project flowDesign.
 * <p/>
 * Authors:<br/>
 * - julian (2016-12-19)<br/>
 */
public class AutoSizeTextField extends TextField {

    public AutoSizeTextField() {
        setPadding(new Insets(0));
        setAlignment(Pos.BASELINE_CENTER);
        textProperty().addListener((observable, oldValue, newValue) -> {
            final String newText = newValue != null ? newValue : "";
            /**
             * Warning this is using an internal API which might change in the future.
             *
             * As of now (December 2016) JavaFX has no public text measurement API...
             */
            com.sun.javafx.tk.FontMetrics metrics = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().getFontMetrics(getFont());
            setPrefWidth(metrics.computeStringWidth(newText)+3);
        });
        setText(null);
        setText("");
        setPrefColumnCount(60);
        setMinWidth(10);

        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (!event.getCode().isModifierKey() && event.getCode() != KeyCode.UNDEFINED) {
                Runnable r = getScene().getAccelerators().get(
                        new KeyCodeCombination(
                                event.getCode(),
                                event.isShiftDown() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
                                event.isControlDown() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
                                event.isAltDown() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
                                event.isMetaDown() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,
                                KeyCombination.ModifierValue.UP
                        )
                );
                if (r != null) {
                    r.run();
                }
            }

        });
    }

}
