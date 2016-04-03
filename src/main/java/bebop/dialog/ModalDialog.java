/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;

import bebop.Application;
import bebop.form.Form;
import bebop.ui.AbstractUI;
import bebop.util.Colors;

/**
 * @version 2012/03/12 15:11:12
 */
public abstract class ModalDialog {

    /** The title. */
    private final String title;

    /** The layout data for input form. */
    private final int width;

    /** The dialog area. */
    private Composite area;

    /** The ok button. */
    private Button ok;

    /** The cancel button. */
    private Button cancel;

    /** The dialog shell. */
    private Shell dialog;

    /** The cancel listener. */
    private final Canceler canceler = new Canceler();

    /**
     * @param title
     * @param width
     */
    protected ModalDialog(String title) {
        this(title, 250);
    }

    /**
     * @param title
     * @param width
     */
    protected ModalDialog(String title, int width) {
        this.title = title;
        this.width = width;
    }

    /**
     * <p>
     * Open this modal dialog.
     * </p>
     * 
     * @param ui
     */
    public final boolean open(AbstractUI ui) {
        return open(ui.ui.getShell());
    }

    /**
     * <p>
     * Open this modal dialog.
     * </p>
     * 
     * @param shell
     */
    public final boolean open(Shell shell) {
        // Dialog root layout
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 15;
        layout.marginHeight = 5;

        dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setText(title);
        dialog.setLayout(layout);
        dialog.setBackgroundMode(SWT.INHERIT_DEFAULT);
        dialog.addListener(SWT.Resize, new FormStyler());
        dialog.addListener(SWT.Close, canceler);

        // User defined dialog area
        area = new Composite(dialog, SWT.None);
        area.setLayoutData(new GridData(GridData.FILL_BOTH));
        createUI(area);

        // Separator
        Label separator = new Label(dialog, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

        // Footer
        GridLayout footerLayout = new GridLayout(2, false);
        footerLayout.horizontalSpacing = 10;

        Composite footer = new Composite(dialog, SWT.NONE);
        footer.setLayout(footerLayout);
        footer.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        ok = new Button(footer, SWT.PUSH);
        ok.setText("OK");
        ok.setLayoutData(new GridData(80, SWT.DEFAULT));
        ok.addListener(SWT.Selection, new Accepter());

        cancel = new Button(footer, SWT.PUSH);
        cancel.setText("Cancel");
        cancel.setLayoutData(new GridData(80, SWT.DEFAULT));
        cancel.addListener(SWT.Selection, canceler);

        // layout
        dialog.pack();

        // centering
        Rectangle base = shell.getBounds();
        Rectangle size = dialog.getBounds();
        dialog.setBounds(base.x + (base.width - size.width) / 2, base.y + (base.height - size.height) / 2, size.width, size.height);

        // open
        dialog.open();
        dialog.setDefaultButton(ok);

        // wait
        Display display = dialog.getDisplay();

        while (!dialog.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        // API definition
        return !canceler.isCanceled;
    }

    /**
     * <p>
     * Create your own UI.
     * </p>
     * 
     * @param parent
     */
    protected void createUI(Composite parent) {
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 15;
        layout.horizontalSpacing = 0;
        layout.marginHeight = 10;

        parent.setLayout(layout);

        Form.build(parent, this);
    }

    /**
     * @version 2012/03/15 20:30:57
     */
    private class Accepter implements Listener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void handleEvent(Event event) {
            // close dialog
            if (dialog.isEnabled()) {

                dialog.setEnabled(false);
                dialog.close();
            }
        }
    }

    /**
     * @version 2012/03/15 20:30:57
     */
    private class Canceler implements Listener {

        /** The cancel flag. */
        private boolean isCanceled = false;

        /**
         * {@inheritDoc}
         */
        @Override
        public void handleEvent(Event event) {
            // close dialog
            if (dialog.isEnabled()) {
                this.isCanceled = true;

                dialog.setEnabled(false);
                dialog.close();
            }
        }
    }

    /**
     * @version 2012/03/14 14:36:17
     */
    private static class FormStyler implements Listener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void handleEvent(Event event) {
            Scrollable composite = (Scrollable) event.widget;
            Rectangle rect = composite.getClientArea();

            // create new background image
            Image image = new Image(Application.display, 1, rect.height);

            // draw background image
            GC canvas = new GC(image);

            canvas.setForeground(Colors.WidgetBackground);
            canvas.setBackground(Colors.White);
            canvas.fillGradientRectangle(rect.x, rect.y, 1, rect.height, true);
            canvas.dispose();

            // apply
            composite.setBackgroundImage(image);
        }
    }
}
