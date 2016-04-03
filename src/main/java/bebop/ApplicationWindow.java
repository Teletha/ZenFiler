/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop;

import kiss.Extensible;
import kiss.I;
import kiss.Manageable;
import kiss.Preference;
import kiss.model.ClassUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import bebop.input.Key;
import bebop.ui.AbstractUI;
import bebop.ui.UIEvent;
import bebop.util.Resources;

/**
 * @version 2012/03/02 14:41:48
 */
@Manageable(lifestyle = Preference.class)
public class ApplicationWindow<RootModel> implements Extensible {

    /** The screen size. */
    private static Rectangle screen = Application.display.getPrimaryMonitor().getClientArea();

    /** The actual window. */
    public final Shell shell;

    /** The window is maximized or not. */
    public boolean maximized = false;

    /** The window size and position. */
    public Rectangle bounds = new Rectangle(screen.width / 4, screen.height / 4, screen.width / 2, screen.height / 2);

    /** The root user interface type for this window. */
    protected final Class modelType;

    /** The root model for this window. */
    protected final RootModel model;

    /**
     * 
     */
    protected ApplicationWindow() {
        Class[] types = ClassUtil.getParameter(getClass(), ApplicationWindow.class);
        modelType = types[0];

        model = I.make((Class<RootModel>) modelType);
        shell = createWindow(Application.display);
    }

    /**
     * <p>
     * Create application window. You can custom window to override this method.
     * </p>
     * 
     * @param display A computer display.
     * @return Your window.
     */
    protected Shell createWindow(Display display) {
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout(SWT.VERTICAL));

        return shell;
    }

    /**
     * Open window.
     */
    final void open() {
        AbstractUI rootUI = I.find(AbstractUI.class, modelType);
        rootUI.initialize(shell, model);

        Key.bind(this, rootUI.ui);
        UIEvent.listen(rootUI.ui, this);

        // Restore window location and size.
        shell.setBounds(bounds);

        shell.addControlListener(new WindowLocationChaser());
        shell.setImage(Resources.getImage(I.locate("icon.ico")));

        shell.layout();

        shell.open();
    }

    @InUIThread
    public void active() {
        shell.forceActive();
    }

    /**
     * @version 2012/02/29 8:45:53
     */
    private class WindowLocationChaser implements ControlListener {

        /**
         * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
         */
        public void controlMoved(ControlEvent e) {
            bounds = ((Shell) e.widget).getBounds();
        }

        /**
         * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
         */
        public void controlResized(ControlEvent e) {
            bounds = ((Shell) e.widget).getBounds();
        }
    }
}
