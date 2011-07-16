/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.*;
import gov.nasa.worldwind.servers.wms.utilities.*;
import gov.nasa.worldwind.servers.tools.gdal.GDALUtils;
import gov.nasa.worldwind.servers.tools.gdal.Option;
import gov.nasa.worldwind.servers.tools.gdal.ReadWriteFormat;
import gov.nasa.worldwind.util.Logging;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * A MapGenerator implementation that serves a collection of Landsat imagery. The imagery is presumed to be broken up
 * into 2x2 degree tiles, with a naming scheme of dd{ns}ddd{ew}.tif". <p> The GDAL utility <code>gdalwarp</code> is used
 * to extract subregions from the tiles. </p> <p>The implementation also attempts to use wavelet encodings of these
 * files to reconstruct small-scale representations of the files. These encodings reside in files named after the
 * individual tiles with a ".wvt" suffix appended. The encodings are presumed to be co-located with the tiles, unless
 * otherwise specified with an optional configuration property (see below).</p>
 * <p/>
 * <p>Several optional properties may be included in the XML configuration of the corresponding {@link
 * gov.nasa.worldwind.servers.wms.MapSource} element:
 * <p/>
 * <pre>
 *   &lt;!-- if a tile's footprint in a map request is below this size (in pixels),
 *        the image is reconstructed from a wavelet encoding --&gt;
 *   &lt;property name="wavelet_image_threshold" value="..." /&gt;
 * <p/>
 *   &lt;!-- amount of wavelet encodings to preload ( size in pixels, sq.), --&gt;
 *   &lt;property name="wavelet_preload_size" value="..." /&gt;
 * <p/>
 *   &lt;!-- root directory where the wavelet encodings reside; the encodings are
 *        otherwise presumed to be co-located with the image tiles. --&gt;
 *   &lt;property name="wavelet_encoding_root_dir" value="..." /&gt;
 * <p/>
 *   &lt;!-- A color value (integer in range 0 -- 255), whereby a pixel is considered
 *        to be transparent of all three of its color components are below this value --&gt;
 *   &lt;property name="background_color_threshold" value="..." /&gt;
 * </pre>
 *
 * @author brownrigg
 * @version $Id$
 */

public class EsatGenerator extends AbstractMapGenerator
{
    public String getDataType()
    {
        return "imagery";
    }

    public ServiceInstance getServiceInstance()
    {
        return new EsatServiceInstance();
    }

    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        boolean success = true;  // Assume the best...
        try
        {
            this.mapSource = mapSource;
            String rootDir = mapSource.getRootDir();

            // Get these optional properties...
            Properties props = mapSource.getProperties();
            String srcName = mapSource.getName();
            this.smallImageSize = this.getProperty(props, WAVELET_IMAGE_THRESHOLD, this.smallImageSize, srcName);
            int tmp = this.getProperty(props, WAVELET_PRELOAD_SIZE, preloadRes, srcName);
            if (!WaveletCodec.isPowerOfTwo(tmp))
            {
                Logging.logger().fine( srcName + ": value given for \""
                        + WAVELET_PRELOAD_SIZE + "\" must be power of two; "
                        + "  given: " + tmp + ",  overriding with default of: " + this.preloadRes);
            }
            else
                this.preloadRes = tmp;

            this.encodingRootDir = props.getProperty(WAVELET_ROOT_DIR);
            if (this.encodingRootDir == null)
                this.encodingRootDir = rootDir;

            this.blackThreshold = this.getProperty(props, BACKGROUND_COLOR_THRESHOLD, this.blackThreshold, srcName);

            // Get all the files from rootDir that appear to be esat tiles...
            Pattern regex = Pattern.compile("(\\d\\d)([ns])(\\d\\d\\d)([ew])\\x2etif", Pattern.CASE_INSENSITIVE);
            File rootDirFile = new File(rootDir);

            Logging.logger().info( "[Debug] EsatGenerator: searching files under " + rootDirFile.getAbsolutePath());
            File[] tiles = rootDirFile.listFiles(new EsatTilenameFilter(regex));
            Logging.logger().info( "[Debug] EsatGenerator: found tiles count = " + tiles.length );
            
            this.tileIndex = new EsatTile[180][90];
            Logging.logger().info( "[Debug] EsatGenerator: start loading wavelets" );

            int count = 0;
            for (File tile : tiles)
            {
                try
                {
                    String filename = tile.getName();
                    Matcher parser = regex.matcher(filename);
                    if (!parser.matches())
                        throw new IllegalArgumentException("ESAT-tilename not according to expectations: " + filename);
                    double lat = Double.parseDouble(parser.group(1));
                    if (parser.group(2).equalsIgnoreCase("s"))
                        lat = -lat;
                    double lon = Double.parseDouble(parser.group(3));
                    if (parser.group(4).equalsIgnoreCase("w"))
                        lon = -lon;
                    // tiles are named after their *eastern* most edge, but we want indexing based on
                    // a simple right-handed coordinate system, with indices increase to the east&north.
                    // Hence we translate the incoming longitude by the tile-size.
                    int ix = lonToIndex(lon - TILE_SIZE);
                    int iy = latToIndex(lat);

                    this.tileIndex[ix][iy] = new EsatTile();

                    this.tileIndex[ix][iy].tileFile = tile;

//                    this.tileIndex[ix][iy].waveletFile = new File(
//                        this.encodingRootDir + File.separator + tile.getName() + WaveletCodec.WVT_EXT);

                    this.tileIndex[ix][iy].waveletCodec = null;

                    // this.tileIndex[ix][iy].waveletCodec =
                    // WaveletCodec.loadPartially( this.tileIndex[ix][iy].waveletFile, this.preloadRes );
                    count++;
                }
                catch (Exception ex)
                {
                    Logging.logger().severe("Error preloading Esat Wavelet: " + ex.toString());
                }
            }
            Logging.logger().info( "[Debug] EsatGenerator: loaded wavelets count = " + count );
        }
        catch (Exception ex)
        {
            success = false;
            Logging.logger().severe("EsatGenerator init failed: " + ex.getMessage());
        }

        return success;
    }

    public Sector getBBox()
    {
        return new Sector(
            Angle.fromDegreesLatitude(MIN_LAT),
            Angle.fromDegreesLatitude(MAX_LAT),
            Angle.fromDegreesLongitude(MIN_LON),
            Angle.fromDegreesLongitude(MAX_LON));
    }

    public double getPixelSize()
    {
        return 0.000138888888889d;    // 2 degrees / 14410 pixels
    }

    public String[] getCRS()
    {
        return new String[]{crsStr};
    }

    //
    // Convert lat-lon to indices into tileIndex array. Our convention here is
    // that -180,-90 is [0][0], with indices increasing to the east and north.
    //
    private static int lonToIndex(double lon)
    {
        return (int) ((lon - MIN_LON) / TILE_SIZE);
    }

    private static int latToIndex(double lat)
    {
        return (int) ((lat - MIN_LAT) / TILE_SIZE);
    }

    // --------------------------------------------
    // class EsatServiceInstance
    //
    // Used to manage per-request state.
    //
    public class EsatServiceInstance extends AbstractServiceInstance
    {


        public ImageFormatter serviceRequest( IMapRequest req) throws IOException, WMSServiceException
        {
            BufferedImage reqImage = this.buildBufferedImage( req );
            return new BufferedImageFormatter(reqImage);
        }

        public BufferedImage buildBufferedImage( IMapRequest req) throws IOException, WMSServiceException
        {
            this.threadId = "LandSAT ("+ Thread.currentThread().getId() + ")";

            try
            {
                // determine the tiles overlapped by the request...
                int iMinX = Math.max(0, Math.min(lonToIndex(req.getBBoxXMin()), EsatGenerator.this.tileIndex.length - 1));
                int iMaxX = Math.max(0, Math.min(lonToIndex(req.getBBoxXMax()), EsatGenerator.this.tileIndex.length - 1));
                int iMinY = Math.max(0, Math.min(latToIndex(req.getBBoxYMin()), EsatGenerator.this.tileIndex[0].length - 1));
                int iMaxY = Math.max(0, Math.min(latToIndex(req.getBBoxYMax()), EsatGenerator.this.tileIndex[0].length - 1));
                Sector reqSector = Sector.fromDegrees(req.getBBoxYMin(), req.getBBoxYMax(), req.getBBoxXMin(),
                    req.getBBoxXMax());

                // the image to be created...
                BufferedImage reqImage = new BufferedImage(req.getWidth(), req.getHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);

                int debugMinFrameRes = Integer.MAX_VALUE;
                int debugMaxFrameRes = -Integer.MAX_VALUE;

                for (int ix = iMinX; ix <= iMaxX; ix++)
                {
                    for (int iy = iMinY; iy <= iMaxY; iy++)
                    {
                        // tiles over the ocean don't exist...
                        if (EsatGenerator.this.tileIndex[ix][iy] == null)
                        {
                            Logging.logger().finest( "[esat] tile does not exist (maybe over ocean). Skipping" );
                            continue;
                        }

                        Sector tileSector = Sector.fromDegrees(MIN_LAT + iy * TILE_SIZE, MIN_LAT + (iy + 1) * TILE_SIZE,
                            MIN_LON + ix * TILE_SIZE, MIN_LON + (ix + 1) * TILE_SIZE);
                        Sector overlap = reqSector.intersection(tileSector);
                        if (overlap == null)
                        {
                            Logging.logger().finest( "[esat] ok - no overlap. Skipping" );
                            continue;
                        }

                        // find size of the tile's footprint at the requested image resolution...
                        int footprintX = Math.abs( (int) (TILE_SIZE * reqImage.getWidth() / reqSector.getDeltaLonDegrees()) );
                        int footprintY = Math.abs( (int) (TILE_SIZE * reqImage.getHeight() / reqSector.getDeltaLatDegrees()) );

                        // Destination subimage...
                        int dx1 = (int) ((overlap.getMinLongitude().degrees - reqSector.getMinLongitude().degrees)
                            * reqImage.getWidth() / reqSector.getDeltaLonDegrees());
                        int dx2 = (int) ((overlap.getMaxLongitude().degrees - reqSector.getMinLongitude().degrees)
                            * reqImage.getWidth() / reqSector.getDeltaLonDegrees());
                        int dy1 = (int) ((reqSector.getMaxLatitude().degrees - overlap.getMaxLatitude().degrees)
                            * reqImage.getHeight() / reqSector.getDeltaLatDegrees());
                        int dy2 = (int) ((reqSector.getMaxLatitude().degrees - overlap.getMinLatitude().degrees)
                            * reqImage.getHeight() / reqSector.getDeltaLatDegrees());

                        // Depending upon footprint, either get image from the tile, or reconstruct
                        // it from a wavelet encoding.
                        BufferedImage sourceImage = null;
                        int sx1, sx2, sy1, sy2;
                        if (footprintX > EsatGenerator.this.smallImageSize || footprintY > EsatGenerator.this.smallImageSize)
                        {
                            Logging.logger().info( "[esat] getting image from a source" );

                            sourceImage = getImageFromSource(EsatGenerator.this.tileIndex[ix][iy], overlap, (dx2 - dx1), (dy2 - dy1));
                            if (sourceImage == null)
                                continue;
                            sx1 = sy1 = 0;
                            sx2 = sourceImage.getWidth();
                            sy2 = sourceImage.getHeight();
                        }
                        else
                        {
                            Logging.logger().info( "[esat] getting image from a wavelet" );
                            int maxRes = footprintX;
                            maxRes = (footprintY > maxRes) ? footprintY : maxRes;
                            int power = (int) Math.ceil(Math.log(maxRes) / Math.log(2.));
                            int res = (int) Math.pow(2., power);
                            sourceImage = getImageFromWaveletEncoding(EsatGenerator.this.tileIndex[ix][iy], res);
                            if (sourceImage == null)
                                continue;

                            // find overlap coordinates in source image...
                            sx1 = (int) ((overlap.getMinLongitude().degrees - tileSector.getMinLongitude().degrees)
                                * sourceImage.getWidth() / tileSector.getDeltaLonDegrees());
                            sx2 = (int) ((overlap.getMaxLongitude().degrees - tileSector.getMinLongitude().degrees)
                                * sourceImage.getWidth() / tileSector.getDeltaLonDegrees());
                            sx1 = Math.max(0, sx1);
                            sx2 = Math.min(sourceImage.getWidth() - 1, sx2);

                            sy1 = (int) ((tileSector.getMaxLatitude().degrees - overlap.getMaxLatitude().degrees)
                                * sourceImage.getHeight() / tileSector.getDeltaLatDegrees());
                            sy2 = (int) ((tileSector.getMaxLatitude().degrees - overlap.getMinLatitude().degrees)
                                * sourceImage.getHeight() / tileSector.getDeltaLatDegrees());
                            sy1 = Math.max(0, sy1);
                            sy2 = Math.min(sourceImage.getHeight() - 1, sy2);

                            // debugging and performance analysis info...
                            if (res < debugMinFrameRes)
                                debugMinFrameRes = res;
                            if (res > debugMaxFrameRes)
                                debugMaxFrameRes = res;
                        }

                        if( null != sourceImage )
                        {
                            Graphics2D g2d = (Graphics2D) reqImage.getGraphics();
                            g2d.drawImage(sourceImage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
                            g2d.dispose();
                        }
                        else
                            Logging.logger().severe("[esat] source image is null. Why?? " );
                    }
                }
                Logging.logger().info("   " + (iMaxX - iMinX + 1) * (iMaxY - iMinY + 1) + " tiles in request" +
                    "min/max recon. sizes: " + debugMinFrameRes + ", " + debugMaxFrameRes);

                setBlackToTransparent(reqImage);
                return reqImage;
            }
            catch (Exception ex)
            {
                Logging.logger().fine("ESat request failed: " + ex.getMessage());
                throw new WMSServiceException("ESat request failed: " + ex.getMessage());
            }
        }


        //
        // Attempts to return the specified image as a BufferedImage. Returns null on failure.
        //
        private BufferedImage getImageFromSource(EsatTile tile, Sector extent, int xres, int yres)
        {
            BufferedImage sourceImage = null;
            File tmpFile = TempFile.getTempFile();
            try
            {
                GDALUtils gdal = GDALUtils.getGDAL();

                if (extent.getDeltaLon().getDegrees() < FILTER_THRESHOLD ||
                    extent.getDeltaLat().getDegrees() < FILTER_THRESHOLD)
                {
                    // We use gdalwarp at these larger-scale requests,
                    // as it has better filtering behaviors...
                    gdal.warp( this.threadId,
                        Option.Warp.Resampling.Cubic,
                        new String[] {
                        },
                        new File[] { tile.tileFile },
                        extent, xres, yres,
                        ReadWriteFormat.GTiff,
                        tmpFile
                    );
                }
                else
                {
                    // We use gdal_translate at small-scale requests,
                    // as it makes intelligent use of any overview
                    // images that might have been installed with gdaladdo.
                    // In these cases, the images also look
                    // better than those produced by gdalwarp.
                    gdal.translate( this.threadId,
                        new String[] {
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
                        tile.tileFile,
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

        //
        // Attempts to reconstruct the given FrameFile as a BufferedImage from a WaveletEncoding.
        // Returns null if encoding does not exist or on any other failure.
        //
        private BufferedImage getImageFromWaveletEncoding(EsatTile tile, int resolution )
        {
            WaveletCodec codec = null;
            BufferedImage sourceImage = null;
            try
            {
                if ( resolution <= 0 || null == tile )
                    return sourceImage;

                File wvtFile = new File( EsatGenerator.this.encodingRootDir
                                    + File.separator + tile.tileFile.getName() + WaveletCodec.WVT_EXT);

                if( resolution <= EsatGenerator.this.preloadRes )
                {
                    if( null == tile.waveletCodec )
                    {
                        codec = WaveletCodec.loadPartially( wvtFile, EsatGenerator.this.preloadRes );
                        tile.waveletCodec = codec;
                    }
                    else
                    {
                        codec = tile.waveletCodec;
                    }
                }
                else
                {
                    codec = WaveletCodec.loadPartially( wvtFile, resolution );
                }

                if (codec != null)
                    sourceImage = codec.reconstruct( resolution );
            }
            catch (Exception ex)
            {
                Logging.logger().severe("Failed to reconstruct wavelet from " + tile.tileFile.getName() + ": " + ex.toString());
            }

            return sourceImage;
        }

        //
        // Sets "black" pixels in the image to be transparent. This is done to for edge-blending with
        // underlying imagery.  As the wavelet-reconstructions are not exact, the color black is
        // defined here by a threshold value.
        //
        private void setBlackToTransparent(BufferedImage image)
        {
            WritableRaster raster = image.getRaster();
            int[] pixel = new int[4];
            int width = image.getWidth();
            int height = image.getHeight();
            for (int j = 0; j < height; j++)
            {
                for (int i = 0; i < width; i++)
                {
                    // We know, by the nature of this source, that we are dealing with RGBA rasters...
                    raster.getPixel(i, j, pixel);
                    if (pixel[0] <= EsatGenerator.this.blackThreshold &&
                        pixel[1] <= EsatGenerator.this.blackThreshold &&
                        pixel[2] <= EsatGenerator.this.blackThreshold)
                    {
                        pixel[3] = 0;
                        raster.setPixel(i, j, pixel);
                    }
                }
            }
        }

        public void freeResources()
        { /* No-op */ }

        private String threadId = "LandSAT ";
    }

    // ----------------------------------------------------
    // class EsatTile
    //
    // A bundle of info we keep track of for each tile.
    //
    private static class EsatTile
    {
        File tileFile = null;
        WaveletCodec waveletCodec = null;
    }

    // class EsatTilenameFilter
    //
    // A class used to filter a list of filenames, favoring those that conform to the
    // Esat-tile filenaming convention.
    //
    private class EsatTilenameFilter implements FilenameFilter
    {

        public EsatTilenameFilter(Pattern regex)
        {
            super();
            this.regex = regex;
        }

        public boolean accept(File dir, String name)
        {
            Matcher matcher = this.regex.matcher(name);
            return matcher.matches();
        }

        private Pattern regex;
    }

    private String encodingRootDir = null;
    private EsatTile[][] tileIndex;

    private static final double MIN_LON = -180.;
    private static final double MAX_LON = 180.;
    private static final double MIN_LAT = -90.;
    private static final double MAX_LAT = 90.;
    private static final int TILE_SIZE = 2;  // 2 degree square tiles.
    private static final String crsStr = "EPSG:4326";

    // performance tuning parameters...
    private int smallImageSize = 512;
    private int preloadRes = 32;
    private int blackThreshold = 3;

    // Configuration property keys...
    private static final String WAVELET_IMAGE_THRESHOLD = "wavelet_image_threshold";
    private static final String WAVELET_PRELOAD_SIZE = "wavelet_preload_size";
    private static final String WAVELET_ROOT_DIR = "wavelet_encoding_root_dir";
    private static final String BACKGROUND_COLOR_THRESHOLD = "background_color_threshold";
    private static final double FILTER_THRESHOLD = 0.1;  // empirically determined for this dataset    
}
