/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.util.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.InputStream;
import java.net.URL;

/**
 * Base class for icon retrievers. This class provides methods to load and manipulate icons. Icons may be loaded from
 * either a local or remote symbol store.
 * <p/>
 * <h1>How to implement a retriever for a new symbol set</h1>
 * <p/>
 * <h2>Basic retrieval</h2>
 * <p/>
 * <h2>Composite symbols</h2>
 * <p/>
 * Complicated symbols may be made up of several different graphical elements. This base class includes a {@link
 * #drawImage(java.awt.image.BufferedImage, java.awt.image.BufferedImage) drawImage} method to help build a complex
 * symbol from simple pieces. For example, if a symbol is composed of a frame and an icon, the icon retriever could load
 * appropriate frame and icon independently, draw the icon over the frame, and return the composite image. This would
 * look something like this:
 * <pre>
 *     // Load the frame and icon as separate pieces.
 *     BufferedImage frame = this.readImage("/path/to/frame.png");
 *     BufferedImage icon = this.readImage("/path/to/icon.png");
 *
 *     // Draw the icon on top of the frame. This call modifies the frame image.
 *     this.drawImage(icon, frame);
 *
 *     // Return the composite image.
 *     return frame;
 * </pre>
 * <p/>
 * <h2>Changing the color of a symbol</h2>
 * <p/>
 * For a symbol set that requires multiple copies of a symbol in different colors it may be useful to create a single
 * copy of the symbol and set the color when the icon is retrieved. To do this, create the symbol with a white
 * foreground, and call {@link #multiply(java.awt.image.BufferedImage, java.awt.Color) multiply} to change the color.
 * The white pixels will be replaced with the multiplication color, while maintaining transparency and anti-aliasing.
 *
 * @author ccrick
 * @version $Id: AbstractIconRetriever.java 90 2011-17-10 23:58:29Z ccrick $
 */
public abstract class AbstractIconRetriever implements IconRetriever
{
    protected String retrieverPath;

    /**
     * Create a new retriever.
     *
     * @param retrieverPath Path to the base symbol directory.
     */
    public AbstractIconRetriever(String retrieverPath)
    {
        if (retrieverPath == null || retrieverPath.length() == 0)
        {
            String msg = Logging.getMessage("nullValue.PathIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.retrieverPath = retrieverPath;
    }

    public String getRetrieverPath()
    {
        return this.retrieverPath;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        AbstractIconRetriever that = (AbstractIconRetriever) o;
        return this.retrieverPath != null ? this.retrieverPath.equals(that.retrieverPath) : that.retrieverPath == null;
    }

    @Override
    public int hashCode()
    {
        return this.retrieverPath != null ? this.retrieverPath.hashCode() : 0;
    }

    /**
     * Load an image from a local or remote path.
     *
     * @param path Path to the icon resource, relative to this retriever's retrieval path.
     *
     * @return The requested icon as a BufferedImage, or null if the icon cannot be loaded.
     */
    protected BufferedImage readImage(String path)
    {
        if (path == null)
        {
            String msg = Logging.getMessage("nullValue.PathIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(WWIO.stripTrailingSeparator(this.getRetrieverPath()));
        sb.append("/");
        sb.append(WWIO.stripLeadingSeparator(path));

        InputStream is = null;
        try
        {
            URL url = WWIO.makeURL(sb.toString());
            if (url != null)
                return ImageIO.read(url);

            is = WWIO.openFileOrResourceStream(sb.toString(), this.getClass());
            if (is != null)
                return ImageIO.read(is);
        }
        catch (Exception e)
        {
            String msg = Logging.getMessage("generic.ExceptionWhileReading", sb.toString());
            Logging.logger().fine(msg);
        }
        finally
        {
            WWIO.closeStream(is, sb.toString());
        }

        return null;
    }

    /**
     * Draw an image into a buffered image.
     *
     * @param src  Image to drawn into {@code dest}.
     * @param dest Image to draw on.
     *
     * @return {@code dest} BufferedImage.
     */
    protected BufferedImage drawImage(BufferedImage src, BufferedImage dest)
    {
        if (src == null)
        {
            String msg = Logging.getMessage("nullValue.SourceIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (dest == null)
        {
            String msg = Logging.getMessage("nullValue.DestinationIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Graphics2D g = null;
        try
        {
            g = dest.createGraphics();
            g.drawImage(src, 0, 0, null);
        }
        finally
        {
            if (g != null)
                g.dispose();
        }

        return dest;
    }

    /**
     * Multiply each pixel in an image by a color. White pixels in the image to be replaced by the multiplication color,
     * black pixels will be unaffected.
     *
     * @param image Image to operate on.
     * @param color Color to multiply by.
     */
    protected void multiply(BufferedImage image, Color color)
    {
        if (image == null)
        {
            String msg = Logging.getMessage("nullValue.ImageIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (color == null)
        {
            String msg = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        int w = image.getWidth();
        int h = image.getHeight();

        if (w == 0 || h == 0)
            return;

        int[] pixels = new int[w];
        int c = color.getRGB();
        float ca = ((c >> 24) & 0xff) / 255f;
        float cr = ((c >> 16) & 0xff) / 255f;
        float cg = ((c >> 8) & 0xff) / 255f;
        float cb = (c & 0xff) / 255f;

        for (int y = 0; y < h; y++)
        {
            image.getRGB(0, y, w, 1, pixels, 0, w);

            for (int x = 0; x < w; x++)
            {
                int s = pixels[x];
                float sa = ((s >> 24) & 0xff) / 255f;
                float sr = ((s >> 16) & 0xff) / 255f;
                float sg = ((s >> 8) & 0xff) / 255f;
                float sb = (s & 0xff) / 255f;

                int fa = (int) (ca * sa * 255 + 0.5);
                int fr = (int) (cr * sr * 255 + 0.5);
                int fg = (int) (cg * sg * 255 + 0.5);
                int fb = (int) (cb * sb * 255 + 0.5);

                pixels[x] = (fa & 0xff) << 24
                    | (fr & 0xff) << 16
                    | (fg & 0xff) << 8
                    | (fb & 0xff);
            }

            image.setRGB(0, y, w, 1, pixels, 0, w);
        }
    }
}
