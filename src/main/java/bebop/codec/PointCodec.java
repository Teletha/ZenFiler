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

import org.eclipse.swt.graphics.Point;

import kiss.Decoder;
import kiss.Encoder;

/**
 * @version 2010/11/12 21:08:58
 */
public class PointCodec implements Encoder<Point>, Decoder<Point> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String encode(Point value) {
        return value.x + "," + value.y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point decode(String value) {
        String[] values = value.split(",");
        return new Point(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
    }

}
