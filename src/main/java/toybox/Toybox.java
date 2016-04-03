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
package toybox;

import bebop.Application;
import toybox.filer.FilerWindow;

/**
 * @version 2011/11/02 22:15:24
 */
public class Toybox extends Application {

    /**
     * 
     */
    public Toybox() {
        initialApplicationWindow = FilerWindow.class;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        activate(Toybox.class, ActivationPolicy.Latest, args);
    }
}
