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

import java.nio.file.FileSystems;

import org.junit.Test;

/**
 * @version 2016/04/03 23:11:13
 */
public class FSPathTest {

    @Test
    public void root() throws Exception {
        FilePath root = FilePath.Root;

        assert root != null;
        assert root == root.getParent();
        assert root.toString().equals("/");
        assert FilePath.of(root.toString()) == root;
    }

    @Test
    public void drive() throws Exception {
        // Search root directory (in windows, first drive will be returned)
        FilePath path = FilePath.of(FileSystems.getDefault().getRootDirectories().iterator().next());

        for (int i = 0; i < 30; i++) {
            path = path.getParent();
        }
        assert path == FilePath.Root;
    }

}
