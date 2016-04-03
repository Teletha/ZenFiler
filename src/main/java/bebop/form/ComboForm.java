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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * @version 2012/03/16 15:10:11
 */
public class ComboForm extends Form<Combo> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Combo buildField(Composite parent) {
        Combo combo = new Combo(parent, SWT.READ_ONLY);

        for (Object value : property.model.type.getEnumConstants()) {
            combo.add(((Enum) value).name());
        }

        // API definition
        return combo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void load(Object initial) {
        Enum enumuration = (Enum) initial;

        ui.select(enumuration.ordinal());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void save() {
        int index = ui.getSelectionIndex();

        if (index != -1) {
            Enum enumuration = Enum.valueOf(property.model.type, ui.getItem(index));

            model.set(instance, property, enumuration);
        }
    }
}
