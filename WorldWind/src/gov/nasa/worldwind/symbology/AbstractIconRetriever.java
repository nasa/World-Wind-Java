/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.util.Logging;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.net.URL;

/**
 * @author ccrick
 * @version $Id: AbstractIconRetriever.java 90 2011-17-10 23:58:29Z ccrick $
 */
public abstract class AbstractIconRetriever implements IconRetriever
{
    String iconRepository;

    // Specify the URL where the icons for this symbology set can be found.
    public void setRepository(String url)
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

    public BufferedImage retrieveImageFromURL(String server, String path, String filename, BufferedImage img)
    {
        if (server == null)
        {
            String msg = Logging.getMessage("Symbology.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (path == null)
        {
            String msg = Logging.getMessage("Symbology.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (filename == null)
        {
            String msg = Logging.getMessage("Symbology.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        try
        {
            URL myURL = new URL("HTTP", server, path + filename);
            img = ImageIO.read(myURL);
        }
        catch (Exception e)
        {
            // TODO: error handling
            return null;
        }

        return img;
    }

    public BufferedImage retrieveImageFromFile(String path, String filename, BufferedImage img)
    {
        if (path == null)
        {
            String msg = Logging.getMessage("Symbology.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (filename == null)
        {
            String msg = Logging.getMessage("Symbology.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        try
        {
            File file = new File(path + filename);
            img = ImageIO.read(file);
        }
        catch (Exception e)
        {
            // TODO: error handling
            return null;
        }

        return img;
    }
}
