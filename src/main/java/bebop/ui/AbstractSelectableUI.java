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

import org.eclipse.swt.widgets.Composite;

import bebop.model.Selectable;
import bebop.model.SelectableListener;
import kiss.I;

/**
 * @version 2012/03/02 10:42:23
 */
public abstract class AbstractSelectableUI<Model extends Selectable<SubModel>, SubModel, W extends Composite> extends AbstractUI<Model, W>
        implements SelectableListener<SubModel> {

    /** The type of child model. */
    protected Class<SubModel> subModelType;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Composite parent, Model model) {
        super.initialize(parent, model);

        subModelType = (Class) kiss.model.Model.collectParameters(model.getClass(), Selectable.class)[0];

        model.listen(this);
    }

    /**
     * <p>
     * Create user interface for the child model.
     * </p>
     * 
     * @param index
     * @return
     */
    protected AbstractUI createChildUI(SubModel subModel) {
        AbstractUI childUI = I.find(AbstractUI.class, subModelType);

        childUI.initialize(ui, subModel);

        return childUI;
    }
}
