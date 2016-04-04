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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import bebop.In;
import bebop.InUIThread;
import bebop.InWorkerThread;
import bebop.Listen;
import bebop.UIFriendly;
import bebop.input.Key;
import bebop.input.KeyBind;
import bebop.ui.AbstractUI;
import bebop.ui.UIEvent;
import bebop.util.Resources;
import kiss.Disposable;
import kiss.I;
import toybox.filesystem.FSScanner;
import toybox.filesystem.FilePath;
import toybox.filesystem.FilePathList;

/**
 * @version 2016/04/03 16:56:53
 */
public class FilerUI extends AbstractUI<Filer, Table> {

    private static final Random RANDOM = new Random();

    /** The date formatter. */
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /** The directory collection. */
    private FilePathList directories = new FilePathList();

    /** The file collection. */
    private FilePathList files = new FilePathList();

    /** The observer. */
    private Disposable observer;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Table createUI(Composite parent) {
        ui = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL);
        ui.setFont(Resources.getFont("Noto Sans Japanese Light", 8));
        ui.setHeaderVisible(true);

        TableColumn name = new TableColumn(ui, SWT.LEFT);
        name.setText("Name");
        TableColumn size = new TableColumn(ui, SWT.RIGHT);
        size.setText("Size");
        TableColumn date = new TableColumn(ui, SWT.LEFT);
        date.setText("Modified");

        move(model.getContext());

        click(name).merge(click(size), click(date)).to(this::sort);

        setBackground(I.locate("E:\\神縦\\" + RANDOM.nextInt(27) + ".jpg"), 30);

        return ui;
    }

    /**
     * <p>
     * Sort column.
     * </p>
     */
    private void sort(TableColumn column) {
        // change model
        model.sortColumnIndex = ui.indexOf(column);
        if (ui.getSortColumn() == column) {
            model.sortOrder = model.sortOrder.invert();
        }

        // sort ui model
        directories.sort(comparator());
        files.sort(comparator());

        // change ui
        ui.setSortColumn(column);
        ui.setSortDirection(model.sortOrder.swtId);
        ui.clearAll();
    }

    /**
     * <p>
     * Execute the selected path.
     * </p>
     */
    @Listen(UIEvent.MouseDoubleClick)
    @KeyBind(key = Key.Enter)
    public void executeSelection() {
        TableItem[] items = ui.getSelection();

        if (items.length != 0) {
            FilePath path = (FilePath) items[0].getData();

            if (path.attributes.isDirectory()) {
                move(path);
            } else {
                path.execute();
            }
        }
    }

    /**
     * <p>
     * Move to the parent directory of the current path.
     * </p>
     */
    @KeyBind(key = Key.A)
    @InWorkerThread
    public void moveToUpperDirectory() {
        FilePath current = model.getContext();

        if (current != null) {
            FilePath parent = current.getParent();

            if (parent != current) {
                moveAndSelect(parent, current);
            }
        }
    }

    /**
     * <p>
     * Move to the registered directory.
     * </p>
     */
    @KeyBind(key = Key.N1)
    @InWorkerThread
    public void moveToRegister1() {
        moveAndSelect(FilePath.of("E:\\"), model.getContext());
    }

    /**
     * <p>
     * Create new directory.
     * </p>
     */
    @KeyBind(key = Key.K)
    public void createDirectory() {
        try {
            Files.createDirectory(model.getContext().toPath().resolve("test"));
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Delete the selection items.
     * </p>
     */
    @KeyBind(key = Key.D)
    @InUIThread
    public void deleteSelection() {
        FontDialog dialog = new FontDialog(ui.getShell());
        // 初期フォントをセット
        // ダイアログを開く
        FontData font = dialog.open();

        if (font != null) {
            // ラベルに新しいフォントを設定
            System.out.println(font.getName() + "  " + font.getStyle() + "  ");
        }
        // TableItem[] items = ui.getSelection();
        //
        // if (items.length != 0) {
        // for (TableItem item : items) {
        // try {
        // Files.delete(((FilePath) item.getData()).toPath());
        // } catch (IOException e) {
        // throw I.quiet(e);
        // }
        // }
        // }
    }

    /**
     * <p>
     * Select all items.
     * </p>
     */
    @KeyBind(key = Key.A, ctrl = true)
    @InUIThread
    public void selectAll() {
        ui.selectAll();
    }

    /**
     * <p>
     * Rename selected items.
     * </p>
     */
    @KeyBind(key = Key.R)
    @InUIThread
    public void rename() {
        TableItem[] items = ui.getSelection();

        if (items.length == 1) {
            FilePath path = (FilePath) items[0].getData();

            if (path.isDirectory) {
                System.out.println("dire");
            } else {
                RenameFileDialog dialog = I.make(RenameFileDialog.class);
                dialog.name = path.getFileName();
                dialog.extension = path.getExtension();

                if (dialog.open(this)) {
                    Path p = path.toPath();

                    try {
                        Files.move(p, p.resolveSibling(dialog.getNewName()));
                    } catch (IOException e) {
                        throw I.quiet(e);
                    }
                } else {
                    System.out.println("out");
                }
            }
        }
    }

    /**
     * <p>
     * Pack all resources.
     * </p>
     * 
     * @param location
     */
    @KeyBind(key = Key.U)
    @InUIThread
    public void unpack() {
        TableItem[] items = ui.getSelection();

        if (items.length == 1) {
            FilePath path = (FilePath) items[0].getData();

            if (path.isDirectory) {
                System.out.println("dire");
            } else {
                // create output directory
                Path output;

                try {
                    output = model.getContext().toPath().resolve(path.getFileName());
                    if (Files.notExists(output)) Files.createDirectory(output);
                } catch (IOException e) {
                    throw I.quiet(e);
                }

                try {
                    unpack(path.toPath(), output, I.$encoding);
                } catch (IllegalArgumentException e) {
                    unpack(path.toPath(), output, Charset.forName(System.getProperty("sun.jnu.encoding")));
                }
            }
        }
    }

    private void unpack(Path input, Path output, Charset endcoding) {
        try (ZipFile zip = new ZipFile(input.toFile(), endcoding)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path file = output.resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(file);
                } else {
                    Files.createDirectories(file.getParent());
                    I.copy(zip.getInputStream(entry), Files.newOutputStream(file), false);
                }
            }
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Select item by path.
     * </p>
     * 
     * @param path
     */
    @InUIThread
    public void select(FilePath path) {
        int size = directories.size();

        for (int i = 0; i < size; i++) {
            if (directories.get(i).equals(path)) {

                select(i);
                return;
            }
        }

        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).equals(path)) {

                select(i + size);
                return;
            }
        }
    }

    /**
     * <p>
     * Select item by index.
     * </p>
     * 
     * @param index A index of target item.
     */
    @InUIThread
    public void select(int index) {
        ui.setSelection(index);
    }

    /**
     * <p>
     * Move to the specified directory.
     * </p>
     * 
     * @param path
     */
    public void move(FilePath path) {
        moveAndSelect(path, null);
    }

    /**
     * <p>
     * Move to the specified directory.
     * </p>
     * 
     * @param path
     */
    @InWorkerThread(execute = In.Single)
    public void moveAndSelect(FilePath path, FilePath selection) {
        // change context path
        model.setContext(path);

        // clear current items
        clearTableItems();

        // start scanning
        DirectoryScanner scanner = new DirectoryScanner();
        path.scan(scanner);
        scanner.files.sort(comparator());
        scanner.directories.sort(comparator());

        // notify remaining resources
        updateTableItems(scanner.directories, scanner.files);

        // select first item
        if (selection == null) {
            select(0);
        } else {
            select(selection);
        }

        // observe directory
        if (observer != null) {
            observer.dispose();
            observer = null;
        }

        Path nativePath = path.toPath();

        if (nativePath != null) {
            I.observe(nativePath, "*").to(e -> {
                if (e.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    create(e.context());
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    delete(e.context());
                } else if (e.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    modify(e.context());
                }
            });
        }
    }

    /**
     * <p>
     * Clear all table items.
     * </p>
     */
    @InUIThread
    protected void clearTableItems() {
        ui.removeAll();
        directories.clear();
        files.clear();
    }

    /**
     * <p>
     * Update table items.
     * </p>
     * 
     * @param directories
     * @param files
     */
    @InUIThread
    protected void updateTableItems(List<FilePath> directories, List<FilePath> files) {
        this.directories.insert(directories);
        this.files.insert(files);
        ui.setItemCount(this.directories.size() + this.files.size());
    }

    /**
     * <p>
     * Create table item lazy.
     * </p>
     * 
     * @param event
     */
    @Listen(UIEvent.SetData)
    protected void createTableItem(Event event) {
        TableItem item = (TableItem) event.item;
        int i = event.index;

        int size = directories.size();

        if (i < size) {
            FilePath file = directories.get(i);

            item.setText(0, file.getName());

            long last = file.attributes.lastModifiedTime().toMillis();

            if (last != 0) item.setText(2, format.format(new Date(last)));
            item.setImage(file.getIcon());
            item.setData(file);
        } else {
            FilePath file = files.get(i - size);

            item.setText(0, file.getName());
            item.setText(1, formatSize(file.attributes.size()));
            item.setText(2, format.format(new Date(file.attributes.lastModifiedTime().toMillis())));
            item.setImage(file.getIcon());
            item.setData(file);
        }
    }

    /**
     * <p>
     * Helper method to calculate file size.
     * </p>
     * 
     * @return
     */
    private String formatSize(double size) {
        if (size < 1024) {
            return String.valueOf((int) size); // byte
        }

        size = size / 1024;

        if (size < 1024) {
            return ((int) (size * 100)) / 100f + "KB";
        }

        size = size / 1024;

        if (size < 1024) {
            return ((int) (size * 100)) / 100f + "MB";
        }

        size = size / 1024;

        if (size < 1024) {
            return ((int) (size * 100)) / 100f + "GB";
        }
        return ((int) (size * 100)) / 100f + "TB";
    }

    /**
     * <p>
     * Arrange table column width properly and automatically.
     * </p>
     */
    @Listen(UIEvent.Resize)
    protected void arrangeColumn(Event event) {
        GC canvas = new GC(ui);

        Rectangle area = ui.getClientArea();
        int bar = ui.getItemCount() <= area.height / ui.getItemHeight() ? ui.getVerticalBar().getSize().x : 0;
        int size = canvas.textExtent("MMM.MMMB").x;
        int modified = canvas.textExtent("MMMM/MM/MM XX:XX XX").x + bar;

        ui.getColumn(2).setWidth(modified);
        ui.getColumn(1).setWidth(size);
        ui.getColumn(0).setWidth(area.width - size - modified);

        canvas.dispose();
    }

    @InUIThread
    protected void create(Path nativePath) {
        FilePath path = FilePath.of(nativePath);

        if (path.isDirectory) {
            // update model
            int index = directories.insert(path);

            // update UI
            update(path, new TableItem(ui, SWT.NONE, index), true);
        } else {
            // update model
            int index = files.insert(path);

            // update UI
            update(path, new TableItem(ui, SWT.NONE, index + directories.size()), true);
        }
    }

    @InUIThread
    protected void delete(Path nativePath) {
        FilePath path = FilePath.of(nativePath);

        // update model
        int index = directories.delete(path);

        if (index != -1) {
            // update UI
            ui.remove(index);
            ui.redraw();
            return;
        }

        // update model
        index = files.delete(path);

        if (index != -1) {
            // update UI
            ui.remove(index + directories.size());
            ui.redraw();
            return;
        }
    }

    @InUIThread
    protected void modify(Path nativePath) {
        FilePath path = FilePath.of(nativePath);

        if (path.isDirectory) {
            // update model
            int index = directories.search(path);

            // update UI
            update(path, ui.getItem(index), true);
        } else {
            // update model
            int index = files.insert(path);

            // update UI
            update(path, ui.getItem(index + directories.size()), true);
        }
    }

    /**
     * <p>
     * Update table item.
     * </p>
     * 
     * @param path
     * @param index
     * @param create
     */
    private void update(FilePath path, TableItem item, boolean redraw) {
        if (path.isDirectory) {
            long last = path.attributes.lastModifiedTime().toMillis();

            item.setText(0, path.getName());
            if (last != 0) item.setText(2, format.format(new Date(last)));
            item.setImage(path.getIcon());
            item.setData(path);
        } else {
            item.setText(0, path.getName());
            item.setText(1, formatSize(path.attributes.size()));
            item.setText(2, format.format(new Date(path.attributes.lastModifiedTime().toMillis())));
            item.setImage(path.getIcon());
            item.setData(path);
        }

        if (redraw) {
            ui.redraw();
        }
    }

    /**
     * <p>
     * Create {@link Comparator}.
     * </p>
     * 
     * @param name
     * @param order
     * @return
     */
    private Comparator<FilePath> comparator() {
        Comparator<FilePath> comparator;

        switch (model.sortColumnIndex) {
        case 2:
            comparator = Comparator.comparing(path -> path.attributes.lastModifiedTime());
            break;

        case 1:
            comparator = Comparator.comparingLong(path -> path.attributes.size());
            break;

        default:
            comparator = Comparator.comparing(FilePath::getName);
            break;
        }
        return model.sortOrder.byAscending(comparator);
    }

    /**
     * @version 2012/03/07 13:13:51
     */
    private class DirectoryScanner implements FSScanner {

        /** The limit size of resources. */
        private final int limit = 512;

        /** The current resource size. */
        private int counter = 0;

        /** The directory collection. */
        private List<FilePath> directories = new ArrayList();

        /** The file collection. */
        private List<FilePath> files = new ArrayList();

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitFile(FilePath path) {
            files.add(path);

            if (limit < ++counter) {
                // notify all resources
                updateTableItems(directories, files);

                UIFriendly.waitForCancel(10);

                // reset
                counter = 0;
                directories = new ArrayList();
                files = new ArrayList();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitDirectory(FilePath path) {
            directories.add(path);

            if (limit < ++counter) {
                // notify all resources
                updateTableItems(directories, files);

                UIFriendly.waitForCancel(10);

                // reset
                counter = 0;
                directories = new ArrayList();
                files = new ArrayList();
            }
        }
    }
}
