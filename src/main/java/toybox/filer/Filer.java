/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox.filer;

import java.io.Console;

import toybox.filesystem.FilePath;

/**
 * @version 2011/11/17 11:12:28
 */
public class Filer {

    /** The current context directory. */
    private FilePath context;

    /**
     * Get the context property of this {@link Console}.
     * 
     * @return The context property.
     */
    public FilePath getContext() {
        return context;
    }

    /**
     * Set the context property of this {@link Console}.
     * 
     * @param context The context value to set.
     */
    public void setContext(FilePath context) {
        if (context != null) {
            this.context = context;
        }
    }
}
