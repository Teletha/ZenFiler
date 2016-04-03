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
package bebop.util;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import kiss.I;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import bebop.Application;

/**
 * @version 2011/11/17 21:40:06
 */
public class Resources {

    /** The color manager. */
    private static final Map<Integer, Color> colors = new HashMap();

    /** The font manager. */
    private static final Map<FontData, Font> fonts = new HashMap();

    /** The actual file system. */
    private static final FileSystemView view = FileSystemView.getFileSystemView();

    /** The normal images. */
    private static final ConcurrentHashMap<ImageInfo, Image> images = new ConcurrentHashMap();

    /** The image registry by path. */
    private static final Map<File, Image> paths = new HashMap();

    /** The image registry by extension. */
    private static final Map<String, Image> extensions = new HashMap();

    /** The un-cacheable extensions. */
    private static final String[] uniques = {"exe"};

    /** The file icon image. */
    private static Image fileIcon;

    /** The directory icon image. */
    private static Image directoryIcon;

    // initialization
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                // clean up resources
                for (Color color : colors.values()) {
                    color.dispose();
                }

                for (Font font : fonts.values()) {
                    font.dispose();
                }

                for (Image image : paths.values()) {
                    image.dispose();
                }

                for (Image image : extensions.values()) {
                    image.dispose();
                }

                for (Image image : images.values()) {
                    image.dispose();
                }

                if (fileIcon != null) {
                    fileIcon.dispose();
                }

                if (directoryIcon != null) {
                    directoryIcon.dispose();
                }
            }
        }));
    }

    /**
     * <p>
     * Retrieve the color.
     * </p>
     * 
     * @param red
     * @param green
     * @param blue
     * @return
     */
    public static Color getColor(int red, int green, int blue) {
        Integer hash = ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 0);
        Color color = colors.get(hash);

        if (color == null) {
            color = new Color(Application.display, red, green, blue);
            colors.put(hash, color);
        }
        return color;
    }

    /**
     * <p>
     * Get the application font.
     * </p>
     * 
     * @param pixel
     * @return
     */
    public static Font getFont() {
        return getFont(12);
    }

    /**
     * <p>
     * Get font by the specified size.
     * </p>
     * 
     * @param pixel
     * @return
     */
    public static Font getFont(int pixel) {
        FontData data = new FontData("Noto Sans Japanease", pixel, SWT.NORMAL);
        Font font = fonts.get(data);

        if (font == null) {
            font = new Font(Application.display, data);
            fonts.put(data, font);
        }

        // API definition
        return font;
    }

    /**
     * <p>
     * Get the application font.
     * </p>
     * 
     * @param pixel
     * @return
     */
    public static Font getFont(String name) {
        return getFont(12);
    }

    /**
     * <p>
     * Get font by the specified size.
     * </p>
     * 
     * @param pixel
     * @return
     */
    public static Font getFont(String name, int pixel) {
        FontData data = new FontData(name, pixel, SWT.NORMAL);
        Font font = fonts.get(data);

        if (font == null) {
            font = new Font(Application.display, data);
            fonts.put(data, font);
        }

        // API definition
        return font;
    }

    /**
     * <p>
     * Retrieve the icon data for the specified file path.
     * </p>
     * 
     * @param path A file path.
     * @return An associated icon image.
     */
    public static Image getIcon(Path path) {
        try {
            return getIcon(path, Files.isRegularFile(path));
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Retrieve the icon data for the specified file path.
     * </p>
     * 
     * @param path A file path.
     * @param attributes A file attribute.
     * @return An associated icon image.
     */
    public static Image getIcon(Path path, boolean isFile) {
        return getIcon(path.toFile(), isFile);
    }

    /**
     * <p>
     * Retrieve the icon data for the specified file path.
     * </p>
     * 
     * @param path A file path.
     * @param attributes A file attribute.
     * @return An associated icon image.
     */
    public static Image getIcon(File path, boolean isFile) {
        if (isFile) {
            String name = path.getName();
            int index = name.lastIndexOf('.');

            if (index == -1) {
                if (fileIcon == null) {
                    fileIcon = getNativeIcon(path);
                }
                return fileIcon;
            } else {
                String extension = name.substring(index + 1);

                if (isUnique(extension)) {
                    Image image = paths.get(path);

                    if (image == null) {
                        image = getNativeIcon(path);
                        paths.put(path, image);
                    }
                    return image;
                } else {
                    Image image = extensions.get(extension);

                    if (image == null) {
                        image = getNativeIcon(path);
                        extensions.put(extension, image);
                    }
                    return image;
                }
            }
        } else {
            if (view.isFileSystem(path) && !view.isFileSystemRoot(path)) {
                // normal directory
                if (directoryIcon == null) {
                    directoryIcon = getNativeIcon(path);
                }
                return directoryIcon;
            } else {
                // root component
                Image image = paths.get(path);

                if (image == null) {
                    image = getNativeIcon(path);
                    paths.put(path, image);
                }
                return image;
            }
        }
    }

    /**
     * <p>
     * Check the specified extension is acceptable or not.
     * </p>
     * 
     * @param extension
     * @return
     */
    private static boolean isUnique(String extension) {
        for (String unique : uniques) {
            if (extension.equals(unique)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * Convert AWT image to SWT image.
     * </p>
     * 
     * @param path
     * @return
     */
    private static Image getNativeIcon(Path path) {
        return getNativeIcon(path.toFile());
    }

    /**
     * <p>
     * Convert AWT image to SWT image.
     * </p>
     * 
     * @param path
     * @return
     */
    private static Image getNativeIcon(File path) {
        ImageIcon icon = (ImageIcon) view.getSystemIcon(path);
        BufferedImage image = (BufferedImage) icon.getImage();
        ColorModel colorModel = image.getColorModel();

        if (colorModel instanceof DirectColorModel) {
            DirectColorModel model = (DirectColorModel) colorModel;
            PaletteData palette = new PaletteData(model.getRedMask(), model.getGreenMask(), model.getBlueMask());
            ImageData data = new ImageData(image.getWidth(), image.getHeight(), model.getPixelSize(), palette);

            for (int y = 0; y < data.height; y++) {
                for (int x = 0; x < data.width; x++) {
                    int rgb = image.getRGB(x, y);
                    data.setPixel(x, y, palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF)));

                    if (model.hasAlpha()) {
                        data.setAlpha(x, y, (rgb >> 24) & 0xFF);
                    }
                }
            }
            return new Image(Application.display, data);
        } else {
            IndexColorModel model = (IndexColorModel) colorModel;
            int size = model.getMapSize();
            byte[] red = new byte[size];
            byte[] green = new byte[size];
            byte[] blue = new byte[size];
            model.getReds(red);
            model.getGreens(green);
            model.getBlues(blue);

            RGB[] rgbs = new RGB[size];

            for (int i = 0; i < rgbs.length; i++) {
                rgbs[i] = new RGB(red[i] & 0xFF, green[i] & 0xFF, blue[i] & 0xFF);
            }

            PaletteData palette = new PaletteData(rgbs);
            ImageData data = new ImageData(image.getWidth(), image.getHeight(), model.getPixelSize(), palette);
            data.transparentPixel = model.getTransparentPixel();

            int[] pixel = new int[1];
            WritableRaster raster = image.getRaster();

            for (int y = 0; y < data.height; y++) {
                for (int x = 0; x < data.width; x++) {
                    raster.getPixel(x, y, pixel);
                    data.setPixel(x, y, pixel[0]);
                }
            }
            return new Image(Application.display, data);
        }
    }

    /**
     * <p>
     * Retrieve a scaled image with the specified size and alpha.
     * </p>
     * 
     * @param path A path to image.
     * @return A scaled image.
     */
    public static Image getImage(Path path) {
        return getImage(path, null, 255);
    }

    /**
     * <p>
     * Retrieve a scaled image with the specified size and alpha.
     * </p>
     * 
     * @param path A path to image.
     * @param size Your requested size.
     * @param alpha Your requested alpha value
     * @return A scaled image.
     */
    public static Image getImage(Path path, Rectangle size, int alpha) {
        ImageInfo info = new ImageInfo(path, size, alpha);
        Image scaled = images.get(info);

        if ((scaled == null || scaled.isDisposed()) && Files.exists(path)) {
            Image original = new Image(Application.display, new ImageData(path.toAbsolutePath().toString()));
            Rectangle originalSize = original.getBounds();
            Rectangle scaledSize;

            if (size == null) {
                size = originalSize;
            }

            // keep aspect ratio
            float ratioVertical = (float) size.height / originalSize.height;
            float ratioHorizontal = (float) size.width / originalSize.width;

            if (ratioHorizontal < ratioVertical) {
                int width = Math.round(originalSize.width * ratioVertical);
                scaledSize = new Rectangle((size.width - width) / 2, 0, width, size.height);
            } else {
                int height = Math.round(originalSize.height * ratioHorizontal);
                scaledSize = new Rectangle(0, (size.height - height) / 2, size.width, height);
            }

            // create scaled image
            scaled = new Image(Application.display, size);

            // write scaled image
            GC canvas = new GC(scaled);
            canvas.setInterpolation(SWT.HIGH);
            canvas.setAntialias(SWT.ON);
            canvas.setAdvanced(true);
            canvas.setAlpha(info.alpha);
            canvas.drawImage(original, 0, 0, originalSize.width, originalSize.height, scaledSize.x, scaledSize.y, scaledSize.width, scaledSize.height);

            // dispose used resouces
            canvas.dispose();
            original.dispose();

            // register to dispose
            images.put(info, scaled);
        }

        // API definition
        return scaled;
    }

    /**
     * @version 2011/12/06 10:47:36
     */
    private static class ImageInfo {

        /** The image path. */
        private final Path path;

        /** The size. */
        private final Rectangle size;

        /** The alpha value. */
        private final int alpha;

        /**
         * @param path
         * @param size
         * @param alpha
         */
        private ImageInfo(Path path, Rectangle size, int alpha) {
            if (alpha < 0) {
                alpha = 0;
            }

            if (255 < alpha) {
                alpha = 255;
            }

            this.path = path;
            this.size = size;
            this.alpha = alpha;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + alpha;
            result = prime * result + ((path == null) ? 0 : path.hashCode());
            result = prime * result + ((size == null) ? 0 : size.hashCode());
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            ImageInfo other = (ImageInfo) obj;
            if (alpha != other.alpha) return false;
            if (path == null) {
                if (other.path != null) return false;
            } else if (!path.equals(other.path)) return false;
            if (size == null) {
                if (other.size != null) return false;
            } else if (!size.equals(other.size)) return false;
            return true;
        }
    }
}
