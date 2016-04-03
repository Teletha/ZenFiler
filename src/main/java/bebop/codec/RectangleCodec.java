/*
 * Copyright (C) 2016 ZenFiler Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package bebop.codec;

import static java.lang.Integer.*;

import org.eclipse.swt.graphics.Rectangle;

import kiss.Decoder;
import kiss.Encoder;

/**
 * @version 2010/11/12 21:31:08
 */
class RectangleCodec implements Encoder<Rectangle>, Decoder<Rectangle> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode(Rectangle value) {
        return value.x + " " + value.y + " " + value.width + " " + value.height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle decode(String value) {
        String[] values = value.split(" ");
        return new Rectangle(parseInt(values[0]), parseInt(values[1]), parseInt(values[2]), parseInt(values[3]));
    }

}
