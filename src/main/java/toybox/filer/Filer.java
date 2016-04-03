/*
 * Copyright (C) 2011 Nameless Production Committee.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package toybox.filer;

import java.io.Console;

import toybox.filesystem.FSPath;

/**
 * @version 2011/11/17 11:12:28
 */
public class Filer {

    /** The current context directory. */
    private FSPath context;

    /**
     * Get the context property of this {@link Console}.
     * 
     * @return The context property.
     */
    public FSPath getContext() {
        return context;
    }

    /**
     * Set the context property of this {@link Console}.
     * 
     * @param context The context value to set.
     */
    public void setContext(FSPath context) {
        if (context != null) {
            this.context = context;
        }
    }
}
