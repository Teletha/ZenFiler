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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

import kiss.I;

import org.eclipse.swt.graphics.Image;

/**
 * @version 2012/03/07 20:37:01
 */
public abstract class FSPath implements Comparable<FSPath> {

    /** The root path. */
    public static final FSPath Root = new Root();

    /** The zero time. */
    protected static final FileTime TimeZero = FileTime.fromMillis(0);

    /** The basic attribute. */
    private static final BasicFileAttributes attributeForDirectory = new BaseAttributes(true);

    /** The attributes. */
    public final BasicFileAttributes attributes;

    /** The frequently used attribute. */
    public final boolean isDirectory;

    /**
     * Hide constructor.
     */
    protected FSPath(BasicFileAttributes attributes) {
        this.attributes = attributes == null ? attributeForDirectory : attributes;
        this.isDirectory = this.attributes.isDirectory();
    }

    /**
     * <p>
     * Returns a human-readable name element of this path.
     * </p>
     * 
     * @return A name of this path.
     */
    public abstract String getName();

    /**
     * <p>
     * Returns a os-native icon of this path.
     * </p>
     * 
     * @return A icon image.
     */
    public abstract Image getIcon();

    /**
     * <p>
     * Returns the parent path. If this path indicates root path, it will returns itself.
     * </p>
     * 
     * @return A parent path.
     */
    public abstract FSPath getParent();

    /**
     * <p>
     * Scan all child paths.
     * </p>
     * 
     * @param scanner
     */
    public abstract void scan(FSScanner scanner);

    /**
     * <p>
     * Execute the associated native task with this path.
     * </p>
     */
    public abstract void execute();

    /**
     * <p>
     * Convert to {@link Path}.
     * </p>
     * 
     * @return
     */
    public abstract Path toPath();

    /**
     * <p>
     * Compute file name.
     * </p>
     * 
     * @return
     */
    public String getFileName() {
        String name = getName();
        int index = name.lastIndexOf('.');

        return index == -1 ? name : name.substring(0, index);
    }

    /**
     * <p>
     * Compute file extension.
     * </p>
     * 
     * @return
     */
    public String getExtension() {
        String name = getName();
        int index = name.lastIndexOf('.');

        return index == -1 ? "" : name.substring(index + 1);
    }

    /**
     * <p>
     * List up all child paths.
     * </p>
     * 
     * @return
     */
    public List<FSPath> list() {
        Scanner scanner = new Scanner();
        scan(scanner);
        return scanner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FSPath) {
            return ((FSPath) obj).toString().equals(toString());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(FSPath other) {
        return getName().compareToIgnoreCase(other.getName());
    }

    /**
     * <p>
     * Locate path.
     * </p>
     * 
     * @param path
     * @return
     */
    public static final FSPath locate(String path) {
        if (path == null) {
            throw new Error();
        } else if (path.length() == 0) {
            throw new Error();
        } else if (path.charAt(0) == '/') {
            // by File
            path = path.substring(1);

            if (path.length() == 0) {
                return Root;
            } else {
                return new FSPathByFile(path);
            }
        } else {
            // by Path
            return new FSPathByPath(I.locate(path), null);
        }
    }

    /**
     * <p>
     * Locate path.
     * </p>
     * 
     * @param path
     * @return
     */
    public static final FSPath locate(Path path) {
        if (Files.exists(path)) {
            try {
                return new FSPathByPath(path.toRealPath(), null);
            } catch (IOException e) {
                throw I.quiet(e);
            }
        } else {
            return new FSPathByPath(path, null);
        }
    }

    /**
     * @version 2012/03/07 20:56:27
     */
    @SuppressWarnings("serial")
    private static class Scanner extends ArrayList<FSPath> implements FSScanner {

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitFile(FSPath path) {
            add(path);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitDirectory(FSPath path) {
            add(path);
        }
    }

    /**
     * @version 2012/03/08 11:09:02
     */
    private static class BaseAttributes implements BasicFileAttributes {

        /** The type. */
        private final boolean isDirectory;

        /**
         * @param isDirectory
         */
        private BaseAttributes(boolean isDirectory) {
            this.isDirectory = isDirectory;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileTime creationTime() {
            return TimeZero;
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

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isDirectory() {
            return isDirectory;
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
        public boolean isRegularFile() {
            return !isDirectory;
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
        public FileTime lastAccessTime() {
            return TimeZero;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileTime lastModifiedTime() {
            return TimeZero;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long size() {
            return 0;
        }
    }
}
