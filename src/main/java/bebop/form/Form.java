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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import bebop.Listen;
import bebop.dialog.Inputable;
import bebop.ui.UIEvent;
import kiss.I;
import kiss.model.Model;
import kiss.model.Property;

/**
 * <p>
 * User input field which is binding to the specified property.
 * </p>
 * 
 * @version 2012/03/16 14:01:39
 */
public abstract class Form<C extends Control> implements Comparable<Form> {

    /** The binding instance. */
    protected Object instance;

    /** The binding model. */
    protected Model model;

    /** The binding property. */
    protected Property property;

    /** The description. */
    protected Inputable description;

    /** The actual control. */
    protected C ui;

    /**
     * {@inheritDoc}
     */
    @Override
    public final int compareTo(Form form) {
        if (description.order() < form.description.order()) {
            return -1;
        } else if (form.description.order() < description.order()) {
            return 1;
        } else {
            return property.name.compareToIgnoreCase(form.property.name);
        }
    }

    /**
     * <p>
     * Create form field.
     * </p>
     * 
     * @param parent
     * @return
     */
    protected abstract C buildField(Composite parent);

    /**
     * <p>
     * Load the property value to the binding form.
     * </p>
     * 
     * @param initial A initial value. Not <code>null</code>.
     */
    protected abstract void load(Object initial);

    /**
     * <p>
     * Save the form value to the binding property.
     * </p>
     */
    @Listen(UIEvent.Dispose)
    protected abstract void save();

    /**
     * <p>
     * Create form field.
     * </p>
     * 
     * @param parent
     */
    private void bind(Object instance, Model model, Property property) {
        this.instance = instance;
        this.model = model;
        this.property = property;
        this.description = property.getAnnotation(Inputable.class);
    }

    /**
     * <p>
     * Create form UI.
     * </p>
     * 
     * @param parent A parent UI.
     */
    private void build(Composite parent) {
        // At first, build label for this form.
        Label label = new Label(parent, SWT.NONE);
        label.setText(description.label() + "  :   ");
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        // Then, build input field.
        ui = buildField(parent);

        // Bind initial value if needed.
        Object initial = model.get(instance, property);

        if (initial != null) {
            load(initial);
        }

        //
        UIEvent.listen(ui, this);
    }

    /**
     * <p>
     * Build stylish bindable user-input forms automatically from the specified model.
     * </p>
     * 
     * @param ui A parent UI.
     * @param instance A model instance.
     */
    public static final void build(Composite ui, Object instance) {
        List<Form> forms = new ArrayList();
        Model model = Model.of(instance.getClass());

        for (Property property : model.properties) {
            Inputable inputable = property.getAnnotation(Inputable.class);

            if (inputable != null) {
                Class type = property.model.type;
                Class<? extends Form> definition = null;

                if (type.isEnum()) {
                    definition = ComboForm.class;
                } else {
                    definition = TextForm.class;
                }

                // create form definition
                Form form = I.make(definition);
                form.bind(instance, model, property);

                forms.add(form);
            }
        }

        // sort form definition by its order and name
        Collections.sort(forms);

        // build actual form interface
        for (Form form : forms) {
            form.build(ui);
        }
    }
}