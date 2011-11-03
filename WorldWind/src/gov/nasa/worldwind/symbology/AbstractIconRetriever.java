/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.util.Logging;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.net.URL;

/**
 * @author ccrick
 * @version $Id: AbstractIconRetriever.java 90 2011-17-10 23:58:29Z ccrick $
 */
public abstract class AbstractIconRetriever implements IconRetriever
{
    protected String iconRepository;

    // Must specify in the constructor the URL where the icons for this
    // symbology set can be found.
    public AbstractIconRetriever(String url)
    {
        if (url == null)
        {
            String msg = Logging.getMessage("Symbology.RepositoryURLIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        // make sure last character in URL is '/'
        if (!url.endsWith("/"))
            url = url + "/";

        iconRepository = url;
    }

    public String getRepository()
    {
        return iconRepository;
    }

    public BufferedImage retrieveImageFromURL(String filename, BufferedImage img)
    {
        if (filename == null)
        {
            String msg = Logging.getMessage("Symbology.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (this.getRepository() == null)
        {
            String msg = Logging.getMessage("Symbology.RepositoryURLIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        try
        {
            URL myURL = new URL(this.getRepository() + filename);
            img = ImageIO.read(myURL);
        }
        catch (Exception e)
        {
            // TODO: error handling
            return null;
        }

        return img;
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
                if (HSBColor[1] > 0)    // pixel has color, so make it transparent and greyscale
                {
                    int newColor = Color.HSBtoRGB(0.0f, 0.0f, HSBColor[2]);
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
    *  Sets all colored fill pixels in the icon to be the designated hue,
    *  while all black, white and grey pixels remain unchanged. Destructive.
    */
    public BufferedImage changeIconFillColor(BufferedImage src, int hue)
    {
        if (src == null)
        {
            String msg = Logging.getMessage("nullValue.IconImageSource");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        float hueRatio = (float) (hue % 360) / 359.0f;

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
                    int newColor = Color.HSBtoRGB(hueRatio, HSBColor[1], HSBColor[2]);
                    // convert to a Color object
                    c = new Color(newColor);
                    Color newPixel = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
                    src.setRGB(dx, dy, newPixel.getRGB());
                }
            }
        }

        return src;
    }
}
