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
 * @version 2012/03/08 2:13:48
 */
public interface FSScanner {

    void visitFile(FSPath path);

    void visitDirectory(FSPath path);
}
