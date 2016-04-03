/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox.filesystem;


/**
 * @version 2012/03/07 20:39:55
 */
class Root extends FSPathByFile {

    /**
     * 
     */
    Root() {
        super(root);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FSPath getParent() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "/";
    }
}
