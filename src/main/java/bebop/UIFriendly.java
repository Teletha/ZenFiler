/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop;

import kiss.I;

/**
 * @version 2011/12/02 10:03:56
 */
public final class UIFriendly {

    /**
     * <p>
     * Wait worker thread.
     * </p>
     * 
     * @param time
     */
    public static void waitForCancel(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw I.quiet(e);
        }
    }
}
