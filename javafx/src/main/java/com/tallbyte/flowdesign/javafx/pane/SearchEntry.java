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

package com.tallbyte.flowdesign.javafx.pane;

import com.tallbyte.flowdesign.data.Diagram;
import com.tallbyte.flowdesign.javafx.Action;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import static com.tallbyte.flowdesign.javafx.ResourceUtils.getResourceString;

/**
 * Created by rootjk on 12/20/16.
 */
public class SearchEntry extends HBox {

    public SearchEntry(Diagram diagram) {
        getStyleClass().add("searchEntry");

        Label name = new Label();
        name.getStyleClass().add("labelName");
        name.setText(diagram.getName());

        Label type = new Label();
        type.getStyleClass().add("labelType");
        type.setText(getResourceString("tree.overview."+diagram.getClass().getSimpleName(),
                diagram.getClass().getSimpleName()
        ));

        getChildren().addAll(name, type);
    }

}
