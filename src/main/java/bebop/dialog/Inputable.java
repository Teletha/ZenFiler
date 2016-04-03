/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.dialog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 2012/03/12 18:57:05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Inputable {

    /**
     * <p>
     * Describe the annotated input field.
     * </p>
     * 
     * @return
     */
    String label();

    /**
     * <p>
     * Position order.
     * </p>
     * 
     * @return
     */
    int order() default 100;

    /**
     * <p>
     * Select item on focus.
     * </p>
     * 
     * @return
     */
    boolean select() default false;
}
