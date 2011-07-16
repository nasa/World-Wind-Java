/* Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.data.*;
import gov.nasa.worldwind.formats.tiff.GeotiffReader;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.tools.gdal.*;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.*;
import gov.nasa.worldwind.util.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class MultiFileImageLayer extends AbstractMapGenerator implements FileFilter
{
    protected static final String[] CRS = {"EPSG:4326"};
    protected Sector bbox = null;
    protected HashMap<File, Sector> files = new HashMap<File, Sector>();
    protected double pixelHeight = Double.MAX_VALUE;
    protected AVList params = null;
    protected short missingDataSignal = 0;
    protected short missingDataReplacement = 0;
    protected double overviewPixelHeight = 0;

    protected DataRaster overview = null;

    @Override
    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        this.mapSource = mapSource;

        File source = new File(this.mapSource.getRootDir());
        if (!source.exists())
        {
            String msg = Logging.getMessage("generic.FolderDoesNotExist", this.mapSource.getRootDir());
            Logging.logger().severe(msg);
            throw new FileNotFoundException(msg);
        }

        this.params = mapSource.getParameters();
        if (null == this.params)
        {
            String msg = Logging.getMessage("nullValue.AVListIsNull");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        this.readMissingDataSignal();
        this.readMissingDataReplacement();
        this.readOverviewRaster();

        this.findImageFiles(source);

        Logging.logger().finest("Sector=" + this.bbox.toString() + ", best found resolution = " + this.pixelHeight);

        return true;
    }

    private void readOverviewRaster()
    {
        if (null == this.params)
        {
            String msg = Logging.getMessage("nullValue.ParamsIsNull");
            Logging.logger().finest(msg);
            return;
        }
        if (!this.params.hasKey(AVKey.OVERVIEW_FILE_NAME))
        {
            String msg = Logging.getMessage("nullValue.FileIsNull");
            Logging.logger().finest(msg);
            return;
        }

        Object o = this.params.getValue(AVKey.OVERVIEW_FILE_NAME);
        if (null == o)
        {
            String msg = Logging.getMessage("nullValue.FileIsNull");
            Logging.logger().finest(msg);
            return;
        }

        File ovFile = null;
        if (o instanceof String)
        {
            ovFile = new File((String) o);
        }
        else if (o instanceof File)
        {
            ovFile = (File) o;
        }
        else
        {
            String msg = Logging.getMessage("generic.UnknownContentType", o.getClass().getName());
            Logging.logger().finest(msg);
            return;
        }

        if (!ovFile.exists())
        {
            String msg = Logging.getMessage("generic.FileNotFound", ovFile.getAbsolutePath());
            Logging.logger().finest(msg);
            return;
        }

        GeotiffReader reader = null;
        try
        {
            reader = new GeotiffReader(ovFile);
            if (reader.isGeotiff(0))
            {
                DataRaster raster = reader.readDataRaster(0);

                if (raster.hasKey(AVKey.HEIGHT) && raster.hasKey(AVKey.SECTOR))
                {
                    double height = Double.parseDouble("" + raster.getValue(AVKey.HEIGHT));
                    Sector sector = (Sector) raster.getValue(AVKey.SECTOR);
                    if (height > 0d)
                    {
                        this.overviewPixelHeight = sector.getDeltaLatDegrees() / height;
                        this.overview = raster;
                        Logging.logger().finest("Overview image " + ovFile.getAbsolutePath()
                            + " loaded with resolution " + this.overviewPixelHeight);
                    }
                }
            }
        }
        catch (Exception e)
        {
            Logging.logger().finest(e.toString());
            return;
        }
        finally
        {
            if (null != reader)
            {
                reader.close();
            }
        }
    }

    private void readMissingDataSignal()
    {
        if (null != this.params && this.params.hasKey(AVKey.MISSING_DATA_SIGNAL))
        {
            Object o = this.params.getValue(AVKey.MISSING_DATA_SIGNAL);
            if (null != o)
            {
                try
                {
                    double value = Double.parseDouble("" + o);
                    this.missingDataSignal = (short) value;
                }
                catch (Exception ignore)
                {
                }
            }
        }
    }

    private void readMissingDataReplacement()
    {
        if (null != this.params && this.params.hasKey(AVKey.MISSING_DATA_REPLACEMENT))
        {
            Object o = this.params.getValue(AVKey.MISSING_DATA_REPLACEMENT);
            if (null != o)
            {
                try
                {
                    double value = Double.parseDouble("" + o);
                    this.missingDataReplacement = (short) value;
                }
                catch (Exception ignore)
                {
                }
            }
        }
    }

    private void readCoverageSector()
    {
        if (null != this.params && this.params.hasKey(AVKey.SECTOR))
        {
            Object o = this.params.getValue(AVKey.SECTOR);
            if (null != o)
            {
                try
                {
                    if (o instanceof Sector)
                    {
                        this.bbox = (Sector) o;
                    }
                    else if (o instanceof String)
                    {
                        // TODO create Sector from a string representation
                        // (44.02326090190996�, -123.05739156268746�),(44.050933432899434�, -123.02203779057403�)
                    }
                    else
                    {
                        String msg = Logging.getMessage("generic.SectorSizeInvalid", o);
                        Logging.logger().finest(msg);
                    }
                }
                catch (Exception ignore)
                {
                }
            }
        }
    }

    @Override
    public String getThreadId()
    {
        String threadId = "[" + Thread.currentThread().getId() + "]: ";

        if (null != this.mapSource && null != this.mapSource.getName())
        {
            return this.mapSource.getName() + " " + threadId;
        }

        return threadId;
    }

    @Override
    public ServiceInstance getServiceInstance()
    {
        return new MyServiceInstance();
    }

    @Override
    public Sector getBBox()
    {
        return this.bbox;
    }

    @Override
    public String[] getCRS()
    {
        return CRS;
    }

    @Override
    public String getDataType()
    {
        return "imagery";
    }

    @Override
    public double getPixelSize()
    {
        return this.pixelHeight;
    }

    @Override
    public boolean accept(File pathname)
    {
        String filename = null;
        if (null != pathname
            && pathname.isFile()
            && null != (filename = pathname.getName())
            && !filename.toLowerCase().startsWith("overview")
            && (filename.endsWith(".tiff")
            || filename.endsWith(".tif")
            || filename.endsWith(".gtif")
            || filename.endsWith(".gtiff")
        )
            )
        {
            try
            {
                GeotiffRasterReader reader = new GeotiffRasterReader();

                AVList params = new AVListImpl();

                if (!reader.canRead(pathname, params))
                {
                    return false;
                }

                reader.readMetadata(pathname, params);

                if (params.hasKey(AVKey.SECTOR) && params.hasKey(AVKey.WIDTH) && params.hasKey(AVKey.HEIGHT))
                {

                    Object o = params.getValue(AVKey.SECTOR);
                    if (null != o && o instanceof Sector)
                    {
                        Sector sector = (Sector) o;
                        this.bbox = (null == this.bbox) ? sector : this.bbox.union(sector);

                        this.files.put(pathname, sector);
                        Logging.logger().finest("Added --> " + pathname.getAbsolutePath());

                        o = params.getValue(AVKey.HEIGHT);
                        if (o != null && o instanceof Integer)
                        {
                            int height = (Integer) o;
                            if (height > 0)
                            {
                                double pixelHeight = sector.getDeltaLatDegrees() / (double) height;
                                if (pixelHeight < this.pixelHeight)
                                {
                                    this.pixelHeight = pixelHeight;
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return false;
    }

    private void findImageFiles(File folder)
    {
        Logging.logger().info("Searching in the folder " + folder.getAbsolutePath());

        FileTree fileTree = new FileTree(folder);
        fileTree.setMode(FileTree.FILES_ONLY);
        fileTree.asList(this);
    }

    public short getMissingDataReplacement()
    {
        return this.missingDataReplacement;
    }

    public short getMissingDataSignal()
    {
        return this.missingDataSignal;
    }

    public class MyServiceInstance extends AbstractServiceInstance
    {
        private Sector intersects(Sector a, Sector b)
        {
            if (null != a && null != b)
            {
                Sector overlap = a.intersection(b);
                if (overlap != null
                    && overlap.getDeltaLon().degrees > 0d
                    && overlap.getDeltaLat().degrees > 0d
                    )
                {
                    return overlap;
                }
            }
            return null;
        }

        @Override
        public ImageFormatter serviceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            BufferedImageFormatter formatter = null;
            try
            {
                BufferedImage image = this.buildBufferedImage(req);
                if (null != image)
                {
                    formatter = new BufferedImageFormatter(image);
                }
            }
            catch (Exception ex)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
            }

            return formatter;
        }

        public BufferedImage buildBufferedImage(IMapRequest req)
            throws IOException, WMSServiceException
        {
            BufferedImage reqImage = null;

            int reqWidth = 512, reqHeight = 512;

            ArrayList<File> tiles = new ArrayList<File>();

            try
            {
                Sector reqSector = req.getExtent();

                reqWidth = (req.getWidth() > 0) ? req.getWidth() : reqWidth;
                reqHeight = (req.getHeight() > 0) ? req.getHeight() : reqHeight;

                double missingDataReplacement = (double) MultiFileImageLayer.this.missingDataReplacement;
                try
                {
                    String s = req.getBGColor();
                    if (null != s)
                    {
                        missingDataReplacement = Double.parseDouble(s);
                    }
                }
                catch (Exception e)
                {
                    missingDataReplacement = MultiFileImageLayer.this.missingDataReplacement;
                }

                BufferedImageRaster reqRaster = new BufferedImageRaster(
                    reqWidth, reqHeight, Transparency.TRANSLUCENT, reqSector);

                reqImage = reqRaster.getBufferedImage();

                double reqPixelHeight = reqSector.getDeltaLatDegrees() / (double) reqHeight;
                if (MultiFileImageLayer.this.overview != null
                    && reqPixelHeight >= MultiFileImageLayer.this.overviewPixelHeight)
                {
                    Logging.logger().finest(
                        "Overview image used to satisfy the request with pixel size " + reqPixelHeight);

                    // satisfy request from the overview image
                    MultiFileImageLayer.this.overview.drawOnTo(reqRaster);

                    this.makeNoDataTransparent(reqImage, MultiFileImageLayer.this.missingDataSignal,
                        (short) missingDataReplacement);

                    return reqImage;
                }

                reqImage = new BufferedImage(reqWidth, reqHeight, BufferedImage.TYPE_4BYTE_ABGR);

                for (File file : MultiFileImageLayer.this.files.keySet())
                {
                    Sector sec = MultiFileImageLayer.this.files.get(file);
                    if (null != this.intersects(sec, reqSector))
                    {
                        tiles.add(file);
                    }
                }

                if (tiles.size() > 0)
                {
                    File[] sourceFiles = new File[tiles.size()];
                    sourceFiles = tiles.toArray(sourceFiles);

                    BufferedImage sourceImage = this.mosaic(
                        sourceFiles,
                        reqSector,
                        req.getWidth(),
                        req.getHeight(),
                        MultiFileImageLayer.this.missingDataSignal,
                        MultiFileImageLayer.this.missingDataReplacement
                    );

                    if (null != sourceImage && null != reqImage)
                    {
                        Graphics2D g2d = (Graphics2D) reqImage.getGraphics();
                        g2d.drawImage(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), null);
                        g2d.dispose();

                        this.makeNoDataTransparent(reqImage, MultiFileImageLayer.this.missingDataSignal,
                            (short) missingDataReplacement);
                    }
                }
            }
            catch (Exception ex)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
            }

            return reqImage;
        }

        private BufferedImage mosaic(File[] sourceFiles, Sector extent, int width, int height, short srcNoData,
            short destNoData)
        {
            BufferedImage sourceImage = null;
            File tmpFile = TempFile.getTempFile();
            try
            {
                GDALUtils gdal = GDALUtils.getGDAL();

                gdal.warp(MultiFileImageLayer.this.getThreadId(),
                    Option.Warp.Resampling.Cubic,
                    new String[] {
//                              "--debug", "ON",
//                              "-q",
//                              "--config", "GDAL_CACHEMAX", "1024",
//                              "-wm", "1024",
                        "-srcnodata", String.valueOf(srcNoData),
                        "-dstnodata", String.valueOf(destNoData),
                        "-t_srs", "EPSG:4326"
//                                "-t_srs", "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs"
                    },
                    sourceFiles,
                    extent, width, height,
                    ReadWriteFormat.GTiff,
                    tmpFile
                );

                GeotiffReader reader = new GeotiffReader(tmpFile);
                sourceImage = reader.read();
            }
            catch (Exception ex)
            {
                String msg = MultiFileImageLayer.this.getThreadId() + ex.toString();
                Logging.logger().severe(msg);
            }
            finally
            {
                try
                {
                    tmpFile.delete();
                }
                catch (Exception ignore)
                {
                }
            }
            return sourceImage;
        }

        @Override
        public void freeResources()
        {
        }

        private void makeNoDataTransparent(BufferedImage image, short missingDataSignal, short missingDataReplacement)
        {
            WritableRaster raster = null;

            if (null != image
                && (image.getType() == BufferedImage.TYPE_4BYTE_ABGR || image.getType() == BufferedImage.TYPE_INT_ARGB)
                && null != (raster = image.getRaster())
                )
            {
                int nodata_r = missingDataSignal & 0xff;
                int nodata_g = missingDataSignal & 0xff;
                int nodata_b = missingDataSignal & 0xff;

                int[] transparentPixel = new int[] {0xFF & missingDataReplacement, 0xFF & missingDataReplacement,
                    0xFF & missingDataReplacement, 0x00 /* alpha */};

                int[] pixel = new int[4];

                int width = image.getWidth();
                int height = image.getHeight();
                for (int j = 0; j < height; j++)
                {
                    for (int i = 0; i < width; i++)
                    {
                        // We know, by the nature of this source, that we are dealing with RGBA or ARGB rasters...
                        raster.getPixel(i, j, pixel);
                        if (pixel[0] == nodata_r && pixel[1] == nodata_g && pixel[2] == nodata_b)
                        {
                            raster.setPixel(i, j, transparentPixel);
                        }
                        else
                        {
                            pixel[3] = 0xff; // non-transparent
                            raster.setPixel(i, j, pixel);
                        }
                    }
                }
            }
        }
    }
}

