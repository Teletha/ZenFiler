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
        require("org.eclipse.swt", "org.eclipse.swt.win32.win32.x86_64", "4.4");
        require("com.ibm.icu", "icu4j", "4.8.1.1");

        repository("https://swt-repo.googlecode.com/svn/repo/");
    }
}
