/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.util;

import org.eclipse.swt.SWT;

/**
 * @version 2012/03/14 22:32:25
 */
public enum Platform {

    Windows,

    Linux,

    Mac;

    /** The ccurent platform. */
    public static final Platform Current;

    static {
        String name = SWT.getPlatform();

        if (name.equals("win32")) {
            Current = Windows;
        } else {
            Current = Linux;
        }
    }
}
