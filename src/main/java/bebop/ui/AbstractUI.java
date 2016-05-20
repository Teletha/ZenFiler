/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.ui;

import java.nio.file.Path;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;

import bebop.input.Key;
import bebop.util.Resources;
import kiss.Events;
import kiss.Extensible;
import kiss.model.Model;

/**
 * @version 2012/03/02 10:07:36
 */
public abstract class AbstractUI<M, W extends Composite> implements Extensible {

    /** The actual ui widget. */
    public W ui;

    /** The actual model object. */
    protected M model;

    /** The type of model object. */
    protected final Class modelType = (Class) Model.collectParameters(getClass(), AbstractUI.class)[0];

    /** The background image painter. */
    private BackgroundImagePainter painter = new BackgroundImagePainter();

    /**
     * <p>
     * Initalize this user interface.
     * </p>
     * 
     * @param parent
     * @return
     */
    public void initialize(Composite parent, M model) {
        this.model = model;
        this.ui = createUI(parent);

        Key.bind(this, ui);
        UIEvent.listen(ui, this);

        // Register a listener to display the background image. In order to display the background
        // image properly when the widget shows, we have to register it before all other listeners
        // which may do painting.
        ui.addListener(SWT.Resize, painter);

        // Table can't properly draw background image while mouse scroll, so we have to
        // redraw background canvas explicitly.
        if (ui instanceof Table) {
            ui.addMouseWheelListener(painter);
        }
    }

    /**
     * <p>
     * Set user interface background image.
     * </p>
     * 
     * @param image
     */
    protected final void setBackground(Path image) {
        setBackground(image, 255);
    }

    /**
     * <p>
     * Set user interface background image.
     * </p>
     * 
     * @param image A path to image.
     * @param alpha An alpha value.
     */
    protected final void setBackground(Path image, int alpha) {
        painter.image = image;
        painter.alpha = alpha;

        if (ui != null) {
            painter.handleEvent(null);
        }
    }

    /**
     * <p>
     * Create click {@link Events}.
     * </p>
     * 
     * @param widget
     * @return
     */
    protected final <A extends Widget> Events<A> click(A widget) {
        return listen(SWT.Selection, widget);
    }

    /**
     * <p>
     * Create UI {@link Events}.
     * </p>
     * 
     * @param type
     * @param widget
     * @return
     */
    private <A extends Widget> Events<A> listen(int type, A widget) {
        return new Events<>(observer -> {
            Listener listener = e -> {
                observer.accept((A) e.widget);
            };

            widget.addListener(type, listener);

            return () -> {
                widget.removeListener(type, listener);
            };
        });
    }

    /**
     * <p>
     * Create user interface.
     * </p>
     * 
     * @param parent
     * @return
     */
    protected abstract W createUI(Composite parent);

    /**
     * @version 2012/03/04 13:24:22
     */
    private class BackgroundImagePainter implements Listener, MouseWheelListener {

        /** The actual background image. */
        private Path image;

        /** The alpha value. */
        private int alpha;

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseScrolled(MouseEvent event) {
            if (image != null) {
                ui.redraw();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void handleEvent(Event event) {
            if (image != null) {
                Rectangle size = ui.getClientArea();

                if (!size.isEmpty()) {
                    // apply new image
                    ui.setBackgroundImage(Resources.getImage(image, ui.getParent().getClientArea(), alpha));
                }
            }
        }
    }
}
