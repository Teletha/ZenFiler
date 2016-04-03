/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.form;

import kiss.I;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import bebop.Listen;
import bebop.ui.UIEvent;

/**
 * @version 2012/03/16 14:02:54
 */
public class TextForm extends Form<Text> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Text buildField(Composite parent) {
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = 250;

        TextField text = new TextField(parent, SWT.SINGLE);
        text.setLayoutData(data);

        return text.text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void load(Object initial) {
        ui.setText(I.transform(initial, String.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void save() {
        model.set(instance, property, ui.getText());
    }

    /**
     * <p>
     * Select all text if needed.
     * </p>
     */
    @Listen(UIEvent.FocusIn)
    protected void select() {
        if (description.select()) {
            ui.selectAll();
        }
    }
}