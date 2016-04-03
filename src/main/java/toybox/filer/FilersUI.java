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

import kiss.I;

import org.eclipse.swt.custom.CTabItem;

import toybox.Toybox;
import bebop.Bind;
import bebop.InUIThread;
import bebop.input.Key;
import bebop.input.KeyBind;
import bebop.ui.TabUI;

/**
 * @version 2012/03/07 1:17:34
 */
public class FilersUI extends TabUI<Filers, Filer> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(Filer item) {
        super.select(item);

        ui.getShell().setText(item.getContext().toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Bind
    @InUIThread
    protected void labelTab(CTabItem tab, Filer subModel) {
        tab.setText("  " + subModel.getContext().getName() + "    ");

        if (model.getSelection() == subModel) {
            tab.getParent().getShell().setText(subModel.getContext().toString());
        }
    }

    /**
     * 
     */
    @KeyBind(key = Key.T, ctrl = true)
    public void createNew() {
        Filer filer = new Filer();

        if (model.getSelectionIndex() == -1) {
            filer.setContext(I.make(Toybox.class).contextDirectory);
        } else {
            filer.setContext(model.getSelection().getContext());
        }
        model.add(filer);
    }
}
