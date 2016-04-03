/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.model;

/**
 * @version 2011/11/28 22:54:49
 */
public interface SelectableListener<T> {

    /**
     * <p>
     * Notify item selection event.
     * </p>
     * 
     * @param item
     */
    public void select(T item);

    /**
     * <p>
     * Notiify item deselection event.
     * </p>
     * 
     * @param item
     */
    public void deselect(T item);

    /**
     * <p>
     * Notify item selection event.
     * </p>
     * 
     * @param item
     */
    public void add(T item);

    /**
     * <p>
     * Notiify item deselection event.
     * </p>
     * 
     * @param item
     * @param index TODO
     */
    public void remove(T item, int index);
}
