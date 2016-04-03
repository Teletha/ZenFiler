/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.util;

import org.eclipse.swt.widgets.Widget;

import kiss.model.Model;

/**
 * @version 2012/03/02 11:33:39
 */
public final class SWTUtil {

    /**
     * @param model
     */
    public static <M> M get(Widget widget, Class<M> model) {
        return (M) widget.getData(model.getName());
    }

    /**
     * @param model
     */
    public static void set(Widget widget, Object model) {
        widget.setData(Model.of(model.getClass()).type.getName(), model);
    }
}
