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

import java.util.concurrent.CopyOnWriteArrayList;

import kiss.Interceptor;

/**
 * @version 2012/03/01 13:01:04
 */
class ListenInterceptor extends Interceptor<Listen> {

    /** The update stamp. */
    private static final CopyOnWriteArrayList<Integer> updater = new CopyOnWriteArrayList();

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object invoke(Object... params) {
        Object result = null;
        Integer hash = that.hashCode() ^ System.identityHashCode(annotation);

        // Wr must check this method invoked by model event at first, then we can check the
        // duplicated call. Because if check duplication call at first and model event updater
        // returns false, duplication call updater can't remove hashstamp at all.
        if (!SpeakInterceptor.updater.contains(that) && updater.addIfAbsent(hash)) {
            result = super.invoke(params);

            updater.remove(hash);
        }
        return result;
    }
}
