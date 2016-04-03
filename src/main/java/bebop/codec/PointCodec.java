/*
 * Copyright (C) 2010 Nameless Production Committee.
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
