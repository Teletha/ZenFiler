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
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;

import bebop.util.Resources;

/**
 * @version 2012/03/07 20:46:25
 */
public class FSPathByPath extends FSPath {

    /** The actual path. */
    private final Path path;

    /** The name. */
    private final String name;

    /**
     * @param path
     */
    protected FSPathByPath(Path path, BasicFileAttributes attributes) {
        this(path, null, attributes);
    }

    /**
     * @param path
     */
    protected FSPathByPath(Path path, String name, BasicFileAttributes attributes) {
        super(attributes != null ? attributes : read(path));

        if (name == null) {
            if (path.getNameCount() == 0) {
                name = FSPathByFile.view.getSystemDisplayName(path.toFile());
            } else {
                name = path.getFileName().toString();
            }
        }

        this.path = path.toAbsolutePath();
        this.name = name;
    }

    /**
     * <p>
     * Read attributes.
     * </p>
     * 
     * @param path
     * @return
     */
    private static BasicFileAttributes read(Path path) {
        try {
            return Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            return null;
        }
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
        System.out.println(path);
        return Resources.getIcon(path, attributes.isRegularFile());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FSPath getParent() {
        Path parent = path.getParent();

        if (parent == null) {
            // The current path may be system directory (i.e. drive in windows) or root directory.
            if (path.toString().length() == 1) {
                // root directory
                return Root;
            } else {
                // system root directory
                return new FSPathByFile(FSPathByFile.view.getParentDirectory(path.toFile()));
            }
        } else {
            return new FSPathByPath(parent, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scan(FSScanner scanner) {
        try {
            Files.walkFileTree(path, Collections.singleton(FileVisitOption.FOLLOW_LINKS), 1, new Scanner(scanner));
        } catch (Exception e) {
            // ignore?
            System.out.println(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        Program.launch(toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path toPath() {
        return path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return path.toString().replace(File.separatorChar, '/');
    }

    /**
     * @version 2012/03/08 2:16:02
     */
    private final class Scanner extends SimpleFileVisitor<Path> {

        /** The delegator. */
        private final FSScanner scanner;

        /**
         * @param scanner
         */
        private Scanner(FSScanner scanner) {
            this.scanner = scanner;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (path == dir) {
                // skip root directory
                return FileVisitResult.CONTINUE;
            } else {
                System.out.println(dir);
                scanner.visitDirectory(new FSPathByPath(dir, attrs));
                return FileVisitResult.SKIP_SUBTREE;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (attrs.isRegularFile()) {
                scanner.visitFile(new FSPathByPath(file, attrs));
            } else {
                scanner.visitDirectory(new FSPathByPath(file, attrs));
            }
            return FileVisitResult.CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return super.visitFileFailed(file, exc);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return super.postVisitDirectory(dir, exc);
        }
    }
}
