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

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import javax.swing.filechooser.FileSystemView;

import kiss.I;

import org.eclipse.swt.graphics.Image;

import bebop.util.Resources;

/**
 * @version 2012/03/07 20:46:10
 */
class FSPathByFile extends FSPath {

    /** The platform native file system. */
    protected static FileSystemView view = FileSystemView.getFileSystemView();

    /** The actual root path. */
    protected static final File root = view.getRoots()[0];

    /** The actual file. */
    protected final File file;

    /** The actual name for cache. */
    private final String name;

    /**
     * @param file
     */
    protected FSPathByFile(File file) {
        super(new FileAttribute(file));

        this.file = file;
        this.name = view.getSystemDisplayName(file);
    }

    /**
     * @param path
     */
    protected FSPathByFile(String path) {
        this(resolve(path));
    }

    /**
     * <p>
     * Helper method to resolve path.
     * </p>
     * 
     * @param path
     * @return
     */
    private static File resolve(String path) {
        File file = root;

        for (String name : path.split("/")) {
            file = resolveChild(file, name);

            if (file == null) {
                throw I.quiet(new FileNotFoundException(path));
            }
        }
        return file;
    }

    /**
     * <p>
     * Helper method to resolve path.
     * </p>
     * 
     * @param path
     * @return
     */
    private static File resolveChild(File file, String name) {
        File[] children = file.listFiles();

        if (children != null) {
            for (File child : children) {
                if (child.toString().equals(name)) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getIcon() {
        return Resources.getIcon(file, file.isFile());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FSPath getParent() {
        File parent = file.getParentFile();

        if (parent.equals(root)) {
            return Root;
        } else {
            return new FSPathByFile(file.getParentFile());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scan(FSScanner scanner) {
        for (File entry : file.listFiles()) {
            if (entry.isDirectory()) {
                if (view.isFileSystemRoot(entry)) {
                    scanner.visitDirectory(new FSPathByPath(entry.toPath(), view.getSystemDisplayName(entry), null));
                } else {
                    scanner.visitDirectory(new FSPathByFile(entry));
                }
            } else {
                scanner.visitFile(new FSPathByFile(entry));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path toPath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        File file = this.file;

        while (file != root) {
            builder.insert(0, view.getSystemDisplayName(file)).insert(0, '/');
            file = file.getParentFile();
        }

        return builder.toString();
    }

    /**
     * @version 2012/03/08 11:23:30
     */
    private static final class FileAttribute implements BasicFileAttributes {

        /** The actual file. */
        private final File file;

        /**
         * @param file
         */
        private FileAttribute(File file) {
            this.file = file;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileTime lastModifiedTime() {
            return FileTime.fromMillis(file.lastModified());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileTime lastAccessTime() {
            return FileTime.fromMillis(file.lastModified());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileTime creationTime() {
            return FileTime.fromMillis(file.lastModified());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRegularFile() {
            return file.isFile();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isDirectory() {
            return file.isDirectory();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSymbolicLink() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isOther() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long size() {
            return file.length();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object fileKey() {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }
    }
}
