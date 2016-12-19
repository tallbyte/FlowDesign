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
import javafx.scene.image.Image;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 * Created by rootjk on 12/19/16.
 */
public class GeneralPopupPreparer implements PopupPreparer {
    @Override
    public void prepare(Dialog dialog) {
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/images/realIcon.png"));
        dialog.setGraphic(null);
        dialog.setHeaderText(null);
    }

    @Override
    public void prepare(Stage stage) {
        stage.getIcons().add(new Image("/images/realIcon.png"));
    }

    @Override
    public void prepare(Popup popup) {
        Stage stage = (Stage) popup.getScene().getWindow();
        stage.getIcons().add(new Image("/images/realIcon.png"));
    }
}
