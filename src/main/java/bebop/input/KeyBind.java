/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.input;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 2011/11/09 21:47:59
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KeyBind {

    /**
     * <p>
     * Specify key.
     * </p>
     * 
     * @return
     */
    Key key();

    /**
     * <p>
     * Specify control(command) key state.
     * </p>
     * 
     * @return A state of control(command) key.
     */
    boolean ctrl() default false;

    /**
     * <p>
     * Specify shift key state.
     * </p>
     * 
     * @return A state of shift key.
     */
    boolean shift() default false;

    /**
     * <p>
     * Specify alt key state.
     * </p>
     * 
     * @return A state of alt key.
     */
    boolean alt() default false;
}
