/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package zen;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiPredicate;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import kiss.I;

/**
 * @version 2016/04/02 16:41:36
 */
public class ZenFiler extends Application {

    private ObservableList<Path> paths = I.make(ObservableList.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Table View Sample");

        List<Path> walk = I.walk(I.locate("E:\\"), (BiPredicate) (path, attr) -> {
            return true;
        });

        for (Path path : walk) {
            paths.add(path);
        }

        TableColumn<Path, String> nameCol = new TableColumn("ファイル名");
        nameCol.setCellValueFactory(feature -> {
            return new SimpleStringProperty(feature.getValue().getFileName().toString());
        });

        TableColumn sizeCol = new TableColumn("サイズ");
        sizeCol.setMaxWidth(80);
        sizeCol.setMinWidth(80);
        TableColumn modifiedCol = new TableColumn("最終更新日");
        modifiedCol.setMaxWidth(150);
        modifiedCol.setMinWidth(150);

        TableView table = new TableView();
        table.setItems(paths);
        table.setMinWidth(500);
        table.setMinHeight(800);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.getColumns().addAll(nameCol, sizeCol, modifiedCol);

        // リサイズ可
        AnchorPane root = new AnchorPane(table);
        AnchorPane.setBottomAnchor(table, 0d);
        AnchorPane.setTopAnchor(table, 0d);
        AnchorPane.setRightAnchor(table, 0d);
        AnchorPane.setLeftAnchor(table, 0d);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * <a>Zen Filer entry point.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Application.launch(ZenFiler.class);
    }
}
