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
package bebop.input;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Event;

/**
 * @version 2011/11/16 11:50:21
 */
class KeyStroke {

    /** The typed key. */
    private final int key;

    /** The modifier key. */
    private final boolean alt;

    /** The modifier key. */
    private final boolean ctrl;

    /** The modifier key. */
    private final boolean shift;

    /**
     * 
     */
    KeyStroke(Event event) {
        this.key = event.keyCode;
        this.alt = (event.stateMask & SWT.ALT) != 0;
        this.ctrl = (event.stateMask & SWT.CTRL) != 0;
        this.shift = (event.stateMask & SWT.SHIFT) != 0;
    }

    /**
     * 
     */
    KeyStroke(VerifyEvent event) {
        this.key = event.keyCode;
        this.alt = (event.stateMask & SWT.ALT) != 0;
        this.ctrl = (event.stateMask & SWT.CTRL) != 0;
        this.shift = (event.stateMask & SWT.SHIFT) != 0;
    }

    /**
     * 
     */
    KeyStroke(TraverseEvent event) {
        this.key = event.keyCode;
        this.alt = (event.stateMask & SWT.ALT) != 0;
        this.ctrl = (event.stateMask & SWT.CTRL) != 0;
        this.shift = (event.stateMask & SWT.SHIFT) != 0;
    }

    /**
     * 
     */
    KeyStroke(KeyBind key) {
        this.key = key.key().code;
        this.alt = key.alt();
        this.ctrl = key.ctrl();
        this.shift = key.shift();

    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (alt ? 1231 : 1237);
        result = prime * result + (ctrl ? 1231 : 1237);
        result = prime * result + key;
        result = prime * result + (shift ? 1231 : 1237);
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        KeyStroke other = (KeyStroke) obj;
        if (alt != other.alt) return false;
        if (ctrl != other.ctrl) return false;
        if (key != other.key) return false;
        if (shift != other.shift) return false;
        return true;
    }
}
