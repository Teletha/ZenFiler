/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop;

import java.util.ArrayList;
import java.util.List;

import kiss.Interceptor;

/**
 * @version 2012/03/01 13:01:04
 */
class SpeakInterceptor extends Interceptor<Speak> {

    /** The update stamp. */
    static final List updater = new ArrayList();

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object invoke(Object... params) {
        Object result = null;

        updater.add(that);
        result = super.invoke(params);
        updater.remove(that);

        return result;
    }
}
