/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox;

import bebop.Application;
import toybox.filer.FilerWindow;

/**
 * @version 2011/11/02 22:15:24
 */
public class Toybox extends Application {

    /**
     * 
     */
    public Toybox() {
        initialApplicationWindow = FilerWindow.class;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        activate(Toybox.class, ActivationPolicy.Latest, args);
    }
}
