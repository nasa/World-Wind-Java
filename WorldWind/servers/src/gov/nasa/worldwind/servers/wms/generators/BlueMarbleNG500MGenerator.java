/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.*;
import gov.nasa.worldwind.servers.tools.gdal.GDALUtils;
import gov.nasa.worldwind.servers.tools.gdal.Option;
import gov.nasa.worldwind.servers.tools.gdal.ReadWriteFormat;
import gov.nasa.worldwind.util.Logging;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * A MapGenerator implementation for serving high-resolution images from NASA's Blue Marble, Next-Generation image
 * series. <p> It is assumed that the source PNG files for this dataseries, as distributed by NASA, have been converted
 * to geotiffs, with georeferencing explicitly injected into the files. It is also highly recommended, but not required,
 * that the geotiffs have a subtiling organization. All of these steps can be conveniently performed with the GDAL
 * utility <code>gdal_translate</code>. </p> <p> This generator also uses the GDAL utility <code>gdalwarp</code> to
 * extract subregions from the source images. </p> <p/> <p>The NASA source files have a regular naming scheme that
 * encodes the date and georeferencing information. The files are thus otherwise named with a prefix that indicates the
 * features depicted in the image (topography, bathyrmetry, etc.). The naming scheme looks like: </p>
 * <pre>
 *      <i><b>name_prefix</b></i>.<i><b>yyyymm</b></i>.3x21600x21600.A1.<i><b>suffix</b></i>
 *      <i><b>name_prefix</b></i>.<i><b>yyyymm</b></i>.3x21600x21600.A2.<i><b>suffix</b></i>
 *      <i><b>name_prefix</b></i>.<i><b>yyyymm</b></i>.3x21600x21600.B1.<i><b>suffix</b></i>
 *      <i><b>name_prefix</b></i>.<i><b>yyyymm</b></i>.3x21600x21600.B2.<i><b>suffix</b></i>
 *      ...
 *      <i><b>name_prefix</b></i>.<i><b>yyyymm</b></i>.3x21600x21600.D2.<i><b>suffix</b></i>
 * </pre>
 * <p> The substring "yyyymm" indicates the date of the imagery. The codes A1, A2, ..., D1, D2 reflect an implicit
 * goereferencing. The high-resolution images are distributed as a set of 8 90x90 degree tiles. The "1" indicates
 * northern hemisphere (i.e., the latitude for that image ranges from 0N -- 90N), whereas "2" indicates southern
 * hemisphere (0-90S). The letter codes indicate bounds in longitude:</p>
 * <pre>
 *     A = 180W --  90W
 *     B =  90W --   0
 *     C =   0  --  90E
 *     D =  90E -- 180E
 * </pre>
 * <p> There are several required properties needed in the configuration of the {@link
 * gov.nasa.worldwind.servers.wms.MapSource} element that reflect the italicized substrings in the above naming scheme,
 * for the actual filenames of the geotiffs to be served: </p>
 * <pre>
 *   &lt;!-- name_prefix --&gt;
 *   &lt;property name="BlueMarble500M.namingscheme.prefix" value="..." /&gt;
 * <p/>
 *   &lt;!-- suffix --&gt;
 *   &lt;property name="BlueMarble500M.namingscheme.suffix" value="..." /&gt;
 * <p/>
 *   &lt;!-- yyyymm --&gt;
 *   &lt;property name="BlueMarble500M.defaultTime" value="..." /&gt;
 * </pre>
 *
 * @author brownrigg
 * @version $Id$
 */

public class BlueMarbleNG500MGenerator extends AbstractMapGenerator
{
    public String getDataType()
    {
        return "imagery";
    }

    public BlueMarbleNG500MGenerator()
    {
    }

    public ServiceInstance getServiceInstance()
    {
        return new BMNGServiceInstance();
    }

    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        boolean success = true;  // Assume the best until proven otherwise...
        try
        {
            this.mapSource = mapSource;
            this.rootDir = mapSource.getRootDir();

            // Extract expected properties that should have been set in our MapSource
            // configuration...
            Properties myProps = mapSource.getProperties();
            if (myProps == null)
                throw new IllegalArgumentException("Missing properties in configuration for MapSource: "
                    + mapSource.getServiceClass().getName());

            this.defaultMonth = myProps.getProperty(DEFAULT_DATASET);
            this.namePrefix = myProps.getProperty(FILE_PREFIX);
            this.nameSuffix = myProps.getProperty(FILE_SUFFIX);

            if (this.defaultMonth == null || this.namePrefix == null || this.nameSuffix == null)
            {
                StringBuilder errMsg = new StringBuilder();
                errMsg.append("invalid properties file\n");
                errMsg.append(DEFAULT_DATASET).append(" = ").append(this.defaultMonth).append("\n");
                errMsg.append(FILE_PREFIX).append(" = ").append(this.namePrefix).append("\n");
                errMsg.append(FILE_SUFFIX).append(" = ").append(this.nameSuffix).append("\n");
                throw new IllegalArgumentException(errMsg.toString());
            }
        }
        catch (Exception ex)
        {
            Logging.logger().severe("BlueMarbleNG500MGenerator initialization failed: " + ex.getMessage());
            success = false;
        }

        return success;
    }

    public Sector getBBox()
    {
        return Sector.FULL_SPHERE;
    }

    public double getPixelSize()
    {
        return 0.0041666666d;    // 90 degrees / 21600 pixels
    }

    public String[] getCRS()
    {
        return new String[]{crsStr};
    }

    public class BMNGServiceInstance extends AbstractServiceInstance
    {

        public ImageFormatter serviceRequest( IMapRequest req) throws IOException, WMSServiceException
        {
            BufferedImage reqImage = this.buildBufferedImage( req );
            return new BufferedImageFormatter(reqImage);
        }

        public BufferedImage buildBufferedImage(IMapRequest req) throws IOException, WMSServiceException
        {

            // the image to be created...
            BufferedImage reqImage = new BufferedImage(req.getWidth(), req.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

            // Figure out what parts of the BMNG grid the request overlaps...
            Sector reqSector = Sector.fromDegrees(req.getBBoxYMin(), req.getBBoxYMax(), req.getBBoxXMin(),
                req.getBBoxXMax());
            int[] lonIndices = pigeonHoleLon(req.getBBoxXMin(), req.getBBoxXMax());
            int[] latIndices = pigeonHoleLat(req.getBBoxYMin(), req.getBBoxYMax());

            // Extract source images from overlapped tiles...
            for (int iLonCell = lonIndices[0]; iLonCell <= lonIndices[1]; iLonCell++)
            {
                for (int iLatCell = latIndices[0]; iLatCell <= latIndices[1]; iLatCell++)
                {
                    // compute request overlap with the BMNG tile...
                    double minLon =
                        (req.getBBoxXMin() <= lonBounds[iLonCell]) ? lonBounds[iLonCell] : req.getBBoxXMin();
                    double maxLon =
                        (req.getBBoxXMax() >= lonBounds[iLonCell + 1]) ? lonBounds[iLonCell + 1] : req.getBBoxXMax();
                    double minLat =
                        (req.getBBoxYMin() <= latBounds[iLatCell]) ? latBounds[iLatCell] : req.getBBoxYMin();
                    double maxLat =
                        (req.getBBoxYMax() >= latBounds[iLatCell + 1]) ? latBounds[iLatCell + 1] : req.getBBoxYMax();
                    Sector tileSector = Sector.fromDegrees(minLat, maxLat, minLon, maxLon);
                    Sector overlap = reqSector.intersection(tileSector);
                    if (overlap == null)
                    {
                        continue;
                    }

                    // compute name of BMNG tile...
                    StringBuilder source = new StringBuilder(BlueMarbleNG500MGenerator.this.rootDir);
                    source.append(File.separator).append(BlueMarbleNG500MGenerator.this.namePrefix).append(".").
                        append(BlueMarbleNG500MGenerator.this.defaultMonth).append(".").
                        append(BMNG_NAME_CONSTANT).append(".").
                        append(lonCodes[iLonCell]).append(latCodes[iLatCell]).append(".").
                        append(BlueMarbleNG500MGenerator.this.nameSuffix);

                    // footprint of this tile in the destination image...
                    int dx1 = (int) (
                        (overlap.getMinLongitude().degrees - reqSector.getMinLongitude().degrees) * reqImage.getWidth()
                            / reqSector.getDeltaLonDegrees());
                    int dx2 = (int) (
                        (overlap.getMaxLongitude().degrees - reqSector.getMinLongitude().degrees) * reqImage.getWidth()
                            / reqSector.getDeltaLonDegrees());
                    int dy1 = (int) (
                        (reqSector.getMaxLatitude().degrees - overlap.getMaxLatitude().degrees) * reqImage.getHeight()
                            / reqSector.getDeltaLatDegrees());
                    int dy2 = (int) (
                        (reqSector.getMaxLatitude().degrees - overlap.getMinLatitude().degrees) * reqImage.getHeight()
                            / reqSector.getDeltaLatDegrees());

                    // hand off to GDAL to do the work...
                    BufferedImage sourceImage = getImageFromSource(source.toString(), overlap, (dx2 - dx1),
                        (dy2 - dy1));
                    if (sourceImage == null)
                    {
                        Logging.logger().severe("BlueMarbleNG500MGenerator:  getImageFromSource returned null!!! why??" );
                        continue;
                    }

                    Graphics2D g2d = (Graphics2D) reqImage.getGraphics();
                    g2d.drawImage(sourceImage, dx1, dy1, dx2, dy2, 0, 0, sourceImage.getWidth(),
                        sourceImage.getHeight(), null);

                    g2d.dispose();
                }
            }

            return reqImage;
        }

        public void freeResources()
        {
        }

        //
        // Attempts to return the specified image as a BufferedImage. Returns null on failure.
        //
        private BufferedImage getImageFromSource(String tilename, Sector extent, int xres, int yres)
            throws WMSServiceException
        {
            BufferedImage sourceImage = null;
            File tmpFile = TempFile.getTempFile();
            try
            {
                GDALUtils gdal = GDALUtils.getGDAL();

                if (extent.getDeltaLon().getDegrees() < FILTER_THRESHOLD ||
                    extent.getDeltaLat().getDegrees() < FILTER_THRESHOLD)
                {
                    // We use gdalwarp at these larger-scale requests, as it has better filtering behaviors...
                    // We use gdalwarp at these larger-scale requests,
                    // as it has better filtering behaviors...
                    gdal.warp( Long.toString( this.threadId ),
                        Option.Warp.Resampling.Cubic,
                        new String[] {
//                            "--config", "GDAL_CACHEMAX", "1024",
//                            "-wm", "1024"
                        },
                        new File[] { new File(tilename) },
                        extent, xres, yres,
                        ReadWriteFormat.GTiff,
                        tmpFile
                    );
                }
                else
                {
                    // We use gdal_translate at smaller scale requests, as it can make use of embedded
                    // overview images if present...
                    gdal.translate( Long.toString( this.threadId ),
                        new String[] {
//                            "--config", "GDAL_CACHEMAX", "1024",
//                            "--debug", "ON",
//                            "-quiet",
                            "-not_strict",
                            "-of", "png",
                            "-projwin",
                            Double.toString(extent.getMinLongitude().degrees),
                            Double.toString(extent.getMaxLatitude().degrees),
                            Double.toString(extent.getMaxLongitude().degrees),
                            Double.toString(extent.getMinLatitude().degrees),
                            "-outsize",
                            Integer.toString( xres ), Integer.toString( yres )
                        },
                        new File(tilename),
                        tmpFile
                    );
                }
                sourceImage = ImageIO.read(tmpFile);
            }
            catch (Exception ex)
            {
                String msg = this.threadId +  ex.toString();
                Logging.logger().severe( msg );
            }
            finally
            {
                try {
                    tmpFile.delete();
                }
                catch(Exception ignore) {}
            }

            return sourceImage;
        }

        // Pigeon-holing note:  Requests that have one of the bbox min/max's fall on the BMNG 2x4 grid boundaries
        // are problemmatic, as which cell does that bbox edge lie in (?) The rule used here is that the request min's'
        // are assigned to the highest-number cell they can fit it, whereas the max's are placed in the lowest
        // numbered cell.

        private int[] pigeonHoleLon(double min, double max)
        {
            min = (min < -180.) ? -180. : min;
            max = (max > 180.) ? 180. : max;
            int[] ret = new int[2];
            ret[0] = 0;
            ret[1] = lonBounds.length - 2;

            for (int i = lonBounds.length - 1; i > 0; i--)
            {
                if (min >= lonBounds[i - 1] && min <= lonBounds[i])
                {
                    ret[0] = i - 1;
                    break;
                }
            }

            for (int i = 1; i < lonBounds.length; i++)
            {
                if (max >= lonBounds[i - 1] && max <= lonBounds[i])
                {
                    ret[1] = i - 1;
                    break;
                }
            }

            return ret;
        }

        private int[] pigeonHoleLat(double min, double max)
        {
            min = (min < -90.) ? -90. : min;
            max = (max > 90.) ? 90. : max;
            int[] ret = new int[2];
            ret[0] = 0;
            ret[1] = latBounds.length - 2;

            for (int i = latBounds.length - 1; i > 0; i--)
            {
                if (min >= latBounds[i - 1] && min <= latBounds[i])
                {
                    ret[0] = i - 1;
                    break;
                }
            }

            for (int i = 1; i < latBounds.length; i++)
            {
                if (max >= latBounds[i - 1] && max <= latBounds[i])
                {
                    ret[1] = i - 1;
                    break;
                }
            }

            return ret;
        }

        private long threadId;  // Used as part of logging...
    }

    private String rootDir = null;
    private String defaultMonth = null;
    private String nameSuffix = null;
    private String namePrefix = null;

    private static final double[] lonBounds = {-180., -90., 0., 90., 180.};
    private static final double[] latBounds = {-90., 0., 90.};
    private static final String[] lonCodes = {"A", "B", "C", "D"};
    private static final String[] latCodes = {"2", "1"};
    private static final String crsStr = "EPSG:4326";
    private static final String DEFAULT_DATASET = "BlueMarble500M.defaultTime";
    private static final String FILE_PREFIX = "BlueMarble500M.namingscheme.prefix";
    private static final String FILE_SUFFIX = "BlueMarble500M.namingscheme.suffix";
    private static final String BMNG_NAME_CONSTANT = "3x21600x21600";
    private static final double FILTER_THRESHOLD = 5.0;
}
