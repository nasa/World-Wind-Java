/*
Copyright (C) 2001, 2010 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwindx.examples.util;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.*;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * A collection of static utility methods used by the example programs.
 *
 * @author tag
 * @version $Id$
 */
public class ExampleUtil
{
    /**
     * Unzips the sole entry in the specified zip file, and saves it in a temporary directory, and returns a File to the
     * temporary location.
     *
     * @param path   the path to the source file.
     * @param suffix the suffix to give the temp file.
     *
     * @return a {@link File} for the temp file.
     *
     * @throws IllegalArgumentException if the <code>path</code> is <code>null</code> or empty.
     */
    public static File unzipAndSaveToTempFile(String path, String suffix)
    {
        if (WWUtil.isEmpty(path))
        {
            String message = Logging.getMessage("nullValue.PathIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        InputStream stream = null;

        try
        {
            stream = WWIO.openStream(path);

            ByteBuffer buffer = WWIO.readStreamToBuffer(stream);
            File file = WWIO.saveBufferToTempFile(buffer, WWIO.getFilename(path));

            buffer = WWIO.readZipEntryToBuffer(file, null);
            return WWIO.saveBufferToTempFile(buffer, suffix);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            WWIO.closeStream(stream, path);
        }

        return null;
    }

    /**
     * Saves the file at the specified path in a temporary directory and returns a File to the temporary location.  The
     * path may be one of the following: <ul> <li>{@link java.io.InputStream}</li> <li>{@link java.net.URL}</li>
     * <li>absolute {@link java.net.URI}</li> <li>{@link java.io.File}</li> <li>{@link String} containing a valid URL
     * description or a file or resource name available on the classpath.</li> </ul>
     *
     * @param path   the path to the source file.
     * @param suffix the suffix to give the temp file.
     *
     * @return a {@link File} for the temp file.
     *
     * @throws IllegalArgumentException if the <code>path</code> is <code>null</code> or empty.
     */
    public static File saveResourceToTempFile(String path, String suffix)
    {
        if (WWUtil.isEmpty(path))
        {
            String message = Logging.getMessage("nullValue.PathIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        InputStream stream = null;
        try
        {
            stream = WWIO.openStream(path);

            ByteBuffer buffer = WWIO.readStreamToBuffer(stream);
            return WWIO.saveBufferToTempFile(buffer, suffix);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            WWIO.closeStream(stream, path);
        }

        return null;
    }

    /**
     * Causes the View attached to the specified WorldWindow to animate to the specified sector. The View starts
     * animating at its current location and stops when the sector fills the window.
     *
     * @param wwd    the WorldWindow who's View animates.
     * @param sector the sector to go to.
     *
     * @throws IllegalArgumentException if either the <code>wwd</code> or the <code>sector</code> are
     *                                  <code>null</code>.
     */
    public static void goTo(WorldWindow wwd, Sector sector)
    {
        if (wwd == null)
        {
            String message = Logging.getMessage("nullValue.WorldWindow");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (sector == null)
        {
            String message = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Box extent = Sector.computeBoundingBox(wwd.getModel().getGlobe(),
            wwd.getSceneController().getVerticalExaggeration(), sector);
        Angle fov = wwd.getView().getFieldOfView();
        double zoom = extent.getRadius() / fov.cosHalfAngle() / fov.tanHalfAngle();

        wwd.getView().goTo(new Position(sector.getCentroid(), 0d), zoom);
    }
}
