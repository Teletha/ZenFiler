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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @version 2012/03/12 9:03:40
 */
@SuppressWarnings("serial")
public class FilePathList extends ArrayList<FilePath> {

    /**
     * <p>
     * Insert {@link FilePath}.
     * </p>
     * 
     * @param path
     * @return
     */
    public int insert(FilePath path) {
        int index = Collections.binarySearch(this, path);

        if (index < 0) {
            index = -index - 1;
            add(index, path);
        }
        return index;
    }

    /**
     * <p>
     * Insert all {@link FilePath}.
     * </p>
     * 
     * @param paths
     */
    public void insert(List<? extends FilePath> paths) {
        int size = paths.size();

        if (size != 0) {
            int index = Collections.binarySearch(this, paths.get(0));

            addAll(-index - 1, paths);
        }
    }

    /**
     * <p>
     * Delete {@link FilePath}.
     * </p>
     * 
     * @param path
     * @return
     */
    public int delete(FilePath path) {
        int index = Collections.binarySearch(this, path);

        if (0 <= index) {
            remove(index);

            return index;
        }
        return -1;
    }

    /**
     * <p>
     * Search {@link FilePath}.
     * </p>
     * 
     * @param path
     * @return
     */
    public int search(FilePath path) {
        int index = Collections.binarySearch(this, path);

        return index < 0 ? -1 : index;
    }
}
