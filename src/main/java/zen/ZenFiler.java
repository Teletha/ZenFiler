/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package zen;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * @version 2016/04/02 16:41:36
 */
public class ZenFiler extends Application {

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Custom Browser");
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600);

        WebView view = new WebView();
        root.getChildren().add(view);
        WebEngine engine = view.getEngine();
        engine.load("http://google.co.jp/");

        primaryStage.setScene(scene);
        primaryStage.show();
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
