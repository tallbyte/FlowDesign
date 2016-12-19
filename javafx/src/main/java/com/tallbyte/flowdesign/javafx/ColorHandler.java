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

import javafx.scene.control.Dialog;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 * Created by rootjk on 12/19/16.
 */
public class ColorHandler implements PopupPreparer {

    public enum Style {
        DARK("/css/dark.css"),
        DARK_CONTRAST("/css/darkContrast.css"),
        LIGHT("/css/light.css");

        private final String cssFile;

        Style(String cssFile) {
            this.cssFile = cssFile;
        }

        public String getCssFile() {
            return cssFile;
        }
    }
    private Style style = Style.DARK;

    @Override
    public void prepare(Dialog dialog) {
        dialog.getDialogPane().getStylesheets().add(style.getCssFile());
    }

    @Override
    public void prepare(Stage stage) {
        stage.getScene().getStylesheets().add(style.getCssFile());
    }

    @Override
    public void prepare(Popup popup) {
        popup.getScene().getStylesheets().add(style.getCssFile());
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }
}
