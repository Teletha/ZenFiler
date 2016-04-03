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

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import kiss.I;
import toybox.filesystem.FSPath;
import toybox.filesystem.FSScanner;

/**
 * @version 2016/04/02 16:41:36
 */
public class ZenFiler extends Application {

    private ObservableList<FSPath> paths = I.make(ObservableList.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Table View Sample");

        FSPath.locate("F:\\Application").scan(new FSScanner() {

            @Override
            public void visitFile(FSPath path) {
                paths.add(path);
            }

            @Override
            public void visitDirectory(FSPath path) {
                paths.add(path);
            }
        });

        TableColumn<FSPath, String> nameCol = new TableColumn("ファイル名");
        nameCol.setCellValueFactory(feature -> {
            return new SimpleStringProperty(feature.getValue().getFileName().toString());
        });

        TableColumn<FSPath, Long> sizeCol = new TableColumn("サイズ");
        sizeCol.setMaxWidth(80);
        sizeCol.setMinWidth(80);
        sizeCol.setCellValueFactory(feature -> {
            return new SimpleObjectProperty(feature.getValue().attributes.size());
        });

        TableColumn<FSPath, String> modifiedCol = new TableColumn("最終更新日");
        modifiedCol.setMaxWidth(160);
        modifiedCol.setMinWidth(160);
        modifiedCol.setCellValueFactory(feature -> {
            return new SimpleStringProperty(feature.getValue().attributes.lastModifiedTime().toString());
        });

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
