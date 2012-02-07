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
 * @author ccrick
 * @version $Id: AbstractIconRetriever.java 90 2011-17-10 23:58:29Z ccrick $
 */
public abstract class AbstractIconRetriever implements IconRetriever
{
    protected String retrieverPath;

    // Must specify in the constructor the URL where the icons for this
    // symbology set can be found.
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

    public BufferedImage retrieveImageFromURL(String path, BufferedImage img)
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

    /*
    *  Sets all colored fill pixels in the icon to be transparent,
    *  while all black, white and grey pixels remain opaque. Destructive.
    */
    public BufferedImage removeIconFillColor(BufferedImage src)
    {
        if (src == null)
        {
            String msg = Logging.getMessage("nullValue.IconImageSource");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        for (int dy = 0; dy < srcHeight; dy++)
        {
            for (int dx = 0; dx < srcWidth; dx++)
            {
                int pixel = src.getRGB(dx, dy);
                Color c = new Color(pixel, true);
                float[] HSBColor = new float[3];
                HSBColor = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), HSBColor);
                if (c.getAlpha() < 255)   // eliminate semi-transparent pixels at borderless edges
                {
                    src.setRGB(dx, dy, 0);
                }
                else if (HSBColor[1] > 0)    // pixel has color, so make it transparent and greyscale
                {
                    // take brightness down a little for nicer anti-aliasing
                    int newColor = Color.HSBtoRGB(0.0f, 0.0f, HSBColor[2] * 0.3f);
                    // convert to a Color object
                    c = new Color(newColor);
                    // opacity will be inversely proportional to the brightness
                    float newAlpha = 1.0f - HSBColor[2];
                    Color newPixel = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (newAlpha * 255));
                    src.setRGB(dx, dy, newPixel.getRGB());
                }
            }
        }

        return src;
    }

    /*
    *  Sets all colored (saturation > 0) pixels in the icon to be the designated hue,
    *  while all black, white and grey pixels remain unchanged. Destructive.
    */
    public BufferedImage changeIconFillColor(BufferedImage src, Color hue)
    {
        if (src == null)
        {
            String msg = Logging.getMessage("nullValue.IconImageSource");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        float[] hueHSB = new float[3];
        Color.RGBtoHSB(hue.getRed(), hue.getGreen(), hue.getBlue(), hueHSB);

        for (int dy = 0; dy < srcHeight; dy++)
        {
            for (int dx = 0; dx < srcWidth; dx++)
            {
                int pixel = src.getRGB(dx, dy);
                Color c = new Color(pixel, true);
                float[] HSBColor = new float[3];
                HSBColor = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), HSBColor);
                if (HSBColor[1] > 0)    // pixel has color, so change it to designated hue
                {
                    int alpha = c.getAlpha();        // save the old alpha value
                    int newColor = Color.HSBtoRGB(hueHSB[0], HSBColor[1], HSBColor[2]);

                    // convert to a Color object
                    c = new Color(newColor);
                    Color newPixel = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
                    src.setRGB(dx, dy, newPixel.getRGB());
                }
            }
        }

        return src;
    }

    /*
    * Sets all non-transparent (alpha > 0) black outline pixels in the icon to be
    * the designated hue. Destructive.
    */
    public BufferedImage changeIconOutlineColor(BufferedImage src, Color hue)
    {
        if (src == null)
        {
            String msg = Logging.getMessage("nullValue.IconImageSource");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        float[] HSBDstPixel = new float[3];
        Color.RGBtoHSB(hue.getRed(), hue.getGreen(), hue.getBlue(), HSBDstPixel);

        for (int dy = 0; dy < srcHeight; dy++)
        {
            for (int dx = 0; dx < srcWidth; dx++)
            {
                int pixel = src.getRGB(dx, dy);
                Color c = new Color(pixel, true);
                int alpha = c.getAlpha();

                float[] HSBSrcPixel = new float[3];
                HSBSrcPixel = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), HSBSrcPixel);

                float brightness = HSBSrcPixel[2];

                if (alpha > 0 && brightness < 0.75f)
                {
                    int newColor = Color.HSBtoRGB(HSBDstPixel[0], 1 - brightness, HSBDstPixel[2]);
                    //int newColor = Color.HSBtoRGB(HSBDstPixel[0], HSBDstPixel[1], HSBDstPixel[2]);
                    // convert to a Color object
                    c = new Color(newColor);
                    Color newPixel = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
                    src.setRGB(dx, dy, newPixel.getRGB());
                }
            }
        }

        return src;
    }

    /*
    * Uses the transparency values of the provided mask to create the inverse (!)
    * transparency pattern in the src image.  Destructive.
    */
    public BufferedImage applyInverseTransparencyMask(BufferedImage src, BufferedImage mask)
    {
        if (src == null)
        {
            String msg = Logging.getMessage("nullValue.IconImageSource");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (mask == null)
        {
            String msg = Logging.getMessage("nullValue.ImageIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        for (int dy = 0; dy < srcHeight; dy++)
        {
            for (int dx = 0; dx < srcWidth; dx++)
            {
                // retrieve the current alpha value from the mask
                int pixel = mask.getRGB(dx, dy);
                Color cMask = new Color(pixel, true);
                // all pixels in the mask that are not 100% transparent will become transparent
                // while all completely transparent pixels will become opaque
                int newAlpha = (cMask.getAlpha() > 0) ? 0 : 255;

                // retrieve the current color from the src
                pixel = src.getRGB(dx, dy);
                // convert to a Color object
                Color cSrc = new Color(pixel, true);

                // handle special case where base icon has a colored frame and no fill color.
                // here, use alpha from the base icon instead.
                if (cSrc.getAlpha() < 255
                    && newAlpha == 255)       // transparent in both mask and semitransparent in src
                    newAlpha = cSrc.getAlpha();

                Color newPixel = new Color(cSrc.getRed(), cSrc.getGreen(), cSrc.getBlue(), newAlpha);
                src.setRGB(dx, dy, newPixel.getRGB());
            }
        }

        return src;
    }
}
