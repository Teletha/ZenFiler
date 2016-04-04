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
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collections;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;

import bebop.util.Resources;
import kiss.I;

/**
 * @version 2016/04/04 9:30:25
 */
public abstract class FilePath implements Comparable<FilePath> {

    /** The root path. */
    public static final FilePath Root = new Root();

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
    protected FilePath(BasicFileAttributes attributes) {
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
    public abstract FilePath getParent();

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
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FilePath) {
            return ((FilePath) obj).toString().equals(toString());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(FilePath other) {
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
    public static final FilePath of(String path) {
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
                return new ByFile(path);
            }
        } else {
            // by Path
            return new ByPath(I.locate(path), null);
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
    public static final FilePath of(Path path) {
        if (Files.exists(path)) {
            try {
                return new ByPath(path.toRealPath(), null);
            } catch (IOException e) {
                throw I.quiet(e);
            }
        } else {
            return new ByPath(path, null);
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

    /**
     * @version 2012/03/07 20:46:25
     */
    private static class ByPath extends FilePath {

        /** The actual path. */
        private final Path path;

        /** The name. */
        private final String name;

        /**
         * @param path
         */
        protected ByPath(Path path, BasicFileAttributes attributes) {
            this(path, null, attributes);
        }

        /**
         * @param path
         */
        protected ByPath(Path path, String name, BasicFileAttributes attributes) {
            super(attributes != null ? attributes : read(path));

            if (name == null) {
                if (path.getNameCount() == 0) {
                    name = ByFile.view.getSystemDisplayName(path.toFile());
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
        public FilePath getParent() {
            Path parent = path.getParent();

            if (parent == null) {
                // The current path may be system directory (i.e. drive in windows) or root
                // directory.
                if (path.toString().length() == 1) {
                    // root directory
                    return Root;
                } else {
                    // system root directory
                    return new ByFile(ByFile.view.getParentDirectory(path.toFile()));
                }
            } else {
                return new ByPath(parent, null);
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
                    scanner.visitDirectory(new ByPath(dir, attrs));
                    return FileVisitResult.SKIP_SUBTREE;
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile()) {
                    scanner.visitFile(new ByPath(file, attrs));
                } else {
                    scanner.visitDirectory(new ByPath(file, attrs));
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

    /**
     * @version 2012/03/07 20:46:10
     */
    private static class ByFile extends FilePath {

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
        protected ByFile(File file) {
            super(new FileAttribute(file));

            this.file = file;
            this.name = view.getSystemDisplayName(file);
        }

        /**
         * @param path
         */
        protected ByFile(String path) {
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
        public FilePath getParent() {
            File parent = file.getParentFile();

            if (parent.equals(root)) {
                return Root;
            } else {
                return new ByFile(file.getParentFile());
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
                        scanner.visitDirectory(new ByPath(entry.toPath(), view.getSystemDisplayName(entry), null));
                    } else {
                        scanner.visitDirectory(new ByFile(entry));
                    }
                } else {
                    scanner.visitFile(new ByFile(entry));
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
                // If this exception will be thrown, it is bug of this program. So we must rethrow
                // the
                // wrapped error in here.
                throw new Error();
            }
        }
    }

    /**
     * @version 2012/03/07 20:39:55
     */
    private static class Root extends ByFile {

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
        public FilePath getParent() {
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
}
