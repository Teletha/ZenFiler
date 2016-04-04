/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox.filesystem;

import kiss.Decoder;
import kiss.Encoder;

/**
 * @version 2016/04/04 9:30:42
 */
public class FilePathCodec implements Encoder<FilePath>, Decoder<FilePath> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode(FilePath value) {
        return value.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FilePath decode(String value) {
        return FilePath.of(value);
    }
}
