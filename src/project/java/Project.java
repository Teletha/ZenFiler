/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
public class Project extends bee.api.Project {

    {
        product("npc", "ZenFiler", "0.1");

        require("npc", "sinobu", "1.0");
        require("org.w3c.css", "sac", "1.3");
        require("org.eclipse.swt", "org.eclipse.swt.win32.win32.x86_64", "4.6M6");
        require("com.ibm.icu", "icu4j", "4.8.1.1");

        repository("http://maven-eclipse.github.io/dev-releases/maven");
    }
}
