/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox.filer;

import java.lang.annotation.RetentionPolicy;

import bebop.dialog.Inputable;
import bebop.dialog.ModalDialog;

/**
 * @version 2012/03/12 15:13:47
 */
public class RenameFileIdealDialog extends ModalDialog {

    @Inputable(label = "ファイル名", order = 1)
    public TextField name = text("ファイル名").onFocus();

    @Inputable(label = "拡張子")
    public String extension;

    @Inputable(label = "実行形態")
    public RetentionPolicy policy;

    /**
     * 
     */
    public RenameFileIdealDialog() {
        super("ファイル名を変更します");
    }

    /**
     * @return
     */
    String getNewName() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);

        if (extension != null && extension.length() != 0) {
            builder.append('.').append(extension);
        }
        return builder.toString();
    }

    protected TextField text(String label) {
        return null;
    }

    public static class Field<V, F extends Field> {

        public V value;

        public F onFocus() {
            return (F) this;
        }
    }

    private static class TextField extends Field<String, TextField> {

    }
}
