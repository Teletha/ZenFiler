/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox.file;

import bebop.util.Order;
import toybox.filesystem.FilePath;

/**
 * @version 2016/04/04 23:10:11
 */
public class Directory {

    public FilePath path;

    public Order sortOrder = Order.Ascending;

    public int sortColumnIndex = 0;
}
