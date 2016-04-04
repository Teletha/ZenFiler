/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.util;

import java.util.Comparator;

import org.eclipse.swt.SWT;

/**
 * @version 2016/04/04 14:07:51
 */
public enum Order {

    Ascending(SWT.UP), Descending(SWT.DOWN);

    /** The swt magic number. */
    public final int swtId;

    /**
     * <p>
     * Hide Constractor.
     * </p>
     * 
     * @param swt
     */
    private Order(int swt) {
        this.swtId = swt;
    }

    /**
     * <p>
     * Return inverted direction.
     * </p>
     * 
     * @return
     */
    public Order invert() {
        return swtId == SWT.UP ? Descending : Ascending;
    }

    /**
     * <p>
     * Create ascending {@link Comparator}.
     * </p>
     * 
     * @param comparator
     * @return
     */
    public <T> Comparator<T> byAscending(Comparator<T> comparator) {
        return this == Ascending ? comparator : comparator.reversed();
    }

    /**
     * <p>
     * Create a descending {@link Comparator}.
     * </p>
     * 
     * @param comparator
     * @return
     */
    public <T> Comparator<T> byDescending(Comparator<T> comparator) {
        return this == Descending ? comparator : comparator.reversed();
    }
}
