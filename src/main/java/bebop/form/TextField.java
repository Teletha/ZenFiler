/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.form;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import bebop.Application;
import bebop.util.Colors;
import bebop.util.Platform;

/**
 * @version 2012/03/14 22:23:17
 */
public class TextField extends Composite {

    /** The actual text field. */
    public final Text text;

    /**
     * @param parent
     * @param style
     */
    public TextField(Composite parent, int style) {
        super(parent, style);

        // layout for internal position
        GridLayout layout = new GridLayout();
        setLayout(layout);

        if (Platform.Current == Platform.Windows) {
            layout.marginHeight = 6;
            layout.marginWidth = 6;

            // stop drawing native border
            style &= ~SWT.BORDER;

            // draw cool text box
            Stylist stylist = new Stylist();
            addListener(SWT.Resize, stylist);
            setBackgroundMode(SWT.INHERIT_FORCE);

            this.text = new Text(this, style);
            this.text.addFocusListener(stylist);
        } else {
            this.text = new Text(this, style | SWT.BORDER);
        }
        this.text.setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /**
     * @version 2012/03/14 22:53:30
     */
    private class Stylist implements Listener, FocusListener {

        /** The border width. */
        private int border = 1;

        /** The border arc radius. */
        private int borderArc = 4;

        /**
         * <p>
         * Draw box.
         * </p>
         * 
         * @param borderColor
         */
        private void draw(Color borderColor, int borderAlpha, Color upper, Color lower) {
            Rectangle area = getClientArea();

            // create background image
            Image image = new Image(Application.display, area.width, area.height);

            // create canvas
            GC canvas = new GC(image);
            canvas.setAdvanced(true);
            canvas.setAntialias(SWT.ON);

            int extra = 3;

            // background
            canvas.setForeground(upper);
            canvas.setBackground(lower);
            canvas.fillGradientRectangle(area.x + border, area.y + border, area.width - border * 2, area.height - border * 2 - extra, true);
            canvas.fillRectangle(area.x + border, area.height - border * 2 - extra, area.width - border * 2, extra);

            // box
            canvas.setAlpha(borderAlpha);
            canvas.setForeground(borderColor);
            canvas.drawRoundRectangle(0, 0, area.width - border, area.height - border, borderArc, borderArc);

            // release resource
            canvas.dispose();

            // apply
            setBackgroundImage(image);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void handleEvent(Event event) {
            draw(Colors.DarkGray, 255, Colors.WidgetBackground, Colors.White);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void focusGained(FocusEvent event) {
            draw(Colors.ListSelection, 180, Colors.White, Colors.White);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void focusLost(FocusEvent event) {
            draw(Colors.DarkGray, 255, Colors.WidgetBackground, Colors.White);
        }
    }
}
