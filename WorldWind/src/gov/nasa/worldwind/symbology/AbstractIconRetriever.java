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
}
