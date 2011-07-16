/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.formats.rpf.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.*;
import gov.nasa.worldwind.servers.wms.utilities.*;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * A concrete implementation of MapGenerator that serves RPF dataseries (e.g., CADRG, CIB, etc.). Any framefiles
 * residing below the configured root-directory for this MapGenerator (see {@link gov.nasa.worldwind.servers.wms.MapSource})
 * are identified. The framefiles that are eligible to served can be constrained to specific RPF data-series
 * classification via an optional configuration property (see below); all framefiles are otherwise considered
 * available.
 * <p/>
 * <p>The implementation also attempts to use wavelet encodings of these files to reconstruct small-scale
 * representations of the files. These encodings reside in files named after the individual framefiles with a ".wvt"
 * suffix appended. The encodings are presumed to be co-located with the framefiles, unless otherwise specified with an
 * optional configuration property (see below). If the encodings are not co-located, they must reside in a directory
 * structure that parallels that of this MapGenerator's data root-directory.</p>
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
 *   &lt;!-- constrain the framefiles to be served to this RPF dataseries --&gt;
 *   &lt;property name="data_series" value="..." /&gt;
 * </pre>
 *
 * @author brownrigg
 * @version $Id$
 */

public class RPFGenerator extends AbstractMapGenerator
{
    public String getDataType()
    {
        return "imagery";
    }

    public ServiceInstance getServiceInstance()
    {
        return new RPFServiceInstance();
    }

    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        boolean success = true;  // Assume the best...
        try
        {
            this.mapSource = mapSource;

            // Get these optional properties...
            Properties props = mapSource.getProperties();
            String srcName = mapSource.getName();

            this.smallImageSize = this.getProperty(props, WAVELET_IMAGE_THRESHOLD, this.smallImageSize, srcName);
            int tmp = this.getProperty(props, WAVELET_PRELOAD_SIZE, this.preloadRes, srcName);
            if (!WaveletCodec.isPowerOfTwo(tmp))
            {
                Logging.logger().info( srcName + ": value given for \"" + WAVELET_PRELOAD_SIZE
                    + "\" must be power of two\n given: " + tmp + ",  overriding with default of: " + this.preloadRes );
            }
            else
                this.preloadRes = tmp;

            this.dataSeries = props.getProperty(RPF_DATA_SERIES);
            this.encodingRootDir = props.getProperty(WAVELET_ROOT_DIR);

            // Track down all the RPF frameFiles we can find...
            this.rootDir = mapSource.getRootDir();
            RPFCrawler crawler = new RPFCrawler();
            DataSeriesGrouper grouper = new DataSeriesGrouper(this.dataSeries);
            crawler.invoke(new File(this.rootDir), grouper, false);

            File[] frameFiles = grouper.getFrameFiles();
            if (!(frameFiles.length > 0))
            {
                Logging.logger().severe("RPFGenerator: found no FrameFile's rooted under: " + this.rootDir);
                success = false;
            }

            // convert the list of Files into a more convenient form...
            processFrameFiles(frameFiles);

            // consolidate the individual bounds to get a global bound...
            consolidateBounds();

            // Report configuration out to log...
            dumpToLog(false);

            // Preload (perhaps partially) any framefiles we find. NOTE: we want to perform this *after*
            // any optional properties have been parsed, as some parameters are configurable.

//          preloadWaveletFiles();
        }
        catch (Exception ex)
        {
            String msg = Logging.getMessage( "WMS.MapGenerator.CannotInstantiate", ex.getMessage() );
            Logging.logger().log(java.util.logging.Level.SEVERE, msg, ex);
            throw new WMSServiceException( msg );
        }

        return success;
    }

    public Sector getBBox()
    {
        return this.globalBnds;
    }

    public String[] getCRS()
    {
        return new String[]{crsStr};
    }

    public double getPixelSize()
    {
        return 0d; //TODO
    }

    //
    // Preprocess the framefile "Files", as indentified by the RPFCrawler, into a more
    // suitable form.
    //
    private void processFrameFiles(File[] files)
    {
        this.frameFiles = new FrameFile[files.length];
        int rootLen = this.rootDir.length();

        for (int i = 0; i < files.length; i++)
        {
            // remove the common path prefix (i.e., the "this.rootDir" portion...
            String parent = files[i].getParent();
            if (parent.regionMatches(0, this.rootDir, 0, rootLen))
                parent = parent.substring(rootLen);

            String file = files[i].getName();

            WMSRPFFrameFilename f = new WMSRPFFrameFilename(parent, file);
            this.frameFiles[i] = new FrameFile(f);
        }
    }

    //
    // Find the global bounds for this collection of frame files (i.e., the union of their Sectors).
    //
    private void consolidateBounds()
    {
        this.globalBnds = new Sector(this.frameFiles[0].file.getSector());
        for (int i = 1; i < this.frameFiles.length; i++)
        {
            try
            {
                this.globalBnds = this.globalBnds.union(frameFiles[i].file.getSector());
            }
            catch (Exception e)
            {
                // We've observed that occasionally framefiles will exist that have "invalid"
                // names for a particular series & zone; don't want these to short circuit everything...
                Logging.logger().info("Unable to get Sector for frame: " + frameFiles[i].file.filename.getFilename());
            }
        }
    }

    //
    // Preload any wavelet files associated with a framefile, to the desired resolution.
    //
//    private void preloadWaveletFiles()
//    {
//        for (FrameFile frame : this.frameFiles)
//        {
//            try
//            {
//                frame.wvtcodec = WaveletCodec.loadPartially(getWaveletEncodingFile(frame), this.preloadRes);
//            }
//            catch (IOException ex) { /* no guarantees the wavelet files exist */ }
//        }
//    }

    //
    // A little method to log info about the RPF collection(s) we've loaded.
    //
    private void dumpToLog(boolean dumpFrameFiles)
    {
        Logging.logger().info("RootDir: " + this.rootDir);
        Logging.logger().info("  bounds: " + this.globalBnds.toString());
        Logging.logger().info("  wavlet preload size: " + this.preloadRes + ", wavelet-image generation threshold: "
            + this.smallImageSize);
        Logging.logger().info("  num. frame files: " + this.frameFiles.length);

        if (dumpFrameFiles)
        {
            for (int i = 0; i < this.frameFiles.length; i++)
            {
                Logging.logger().info("     " + this.frameFiles[i].file.getPathToFilename());
            }
        }
    }

    //
    // Convenience method use by this class and its internal ServiceInstance for
    // computing pathname to wavelet-encoding location.
    //
    private File getWaveletEncodingFile(FrameFile frame)
    {
        String path = (this.encodingRootDir != null) ?
            this.encodingRootDir :
            this.rootDir;
        return new File(path + File.separator + frame.file.getPathToFilename() + WaveletCodec.WVT_EXT);
    }

    // --------------------------------------------
    // class ServiceInstance
    //
    // Used to manage per-request state.
    //
    public class RPFServiceInstance extends AbstractServiceInstance
    {

        public ImageFormatter serviceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            try
            {
                // Identify which TOCs and frame files we'll need to satisfy this request...
                Sector reqSector = Sector.fromDegrees(req.getBBoxYMin(), req.getBBoxYMax(),
                    req.getBBoxXMin(), req.getBBoxXMax());

                BufferedImage reqImage = new BufferedImage(req.getWidth(), req.getHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D g2d = (Graphics2D) reqImage.getGraphics();

                int numFramesInRequest = 0;
                int debugMinFrameRes = Integer.MAX_VALUE;
                int debugMaxFrameRes = -Integer.MAX_VALUE;

                for (FrameFile frame : RPFGenerator.this.frameFiles)
                {
                    try
                    {
                        // The call to getSector() can throw an exception if the file is
                        // named with an inappropriate frameNumber for the dataseries/zone.
                        // Also, serving the 5MGlobalNavChart data has been known to throw NPEs
                        // because some of those files have parse errors.
                        // We don't want these to short circuit the entire request, so
                        // trap any such occurances and ignore 'em.
                        if (!reqSector.intersects(frame.file.getSector()))
                            continue;
                    }
                    catch (Exception ex)
                    {
                        /* ignore this framefile */
                        continue;
                    }

                    // Frame overlaps request; attempt to draw it...
                    ++numFramesInRequest;

                    Sector frameSector = frame.file.getSector();

                    // find size of the frame's footprint at the requested image resolution...
                    int footprintX =
                        (int) (frameSector.getDeltaLonDegrees() * reqImage.getWidth() / reqSector.getDeltaLonDegrees());
                    int footprintY = (int) (frameSector.getDeltaLatDegrees() * reqImage.getHeight() / reqSector
                        .getDeltaLatDegrees());

                    // Depending upon footprint, either get image from it RPF framefile, or reconstruct
                    // it from a wavelet encoding.
                    BufferedImage sourceImage;
                    if (footprintX > RPFGenerator.this.smallImageSize || footprintY > RPFGenerator.this.smallImageSize)
                    {
                        RPFFrameTransform.RPFImage[] images = getImageFromRPFSource(frame);
                        if (images == null)
                            continue;
                        for (RPFFrameTransform.RPFImage image : images) {
                            if (image.getSector() == null || image.getImage() == null) continue;
                            drawImageIntoRequest(reqImage, reqSector, image.getImage(), image.getSector());
                        }
                    }
                    else
                    {
                        int maxRes = footprintX;
                        maxRes = (footprintY > maxRes) ? footprintY : maxRes;
                        int power = (int) Math.ceil(Math.log(maxRes) / Math.log(2.));
                        int res = (int) Math.pow(2., power);
                        res = Math.max(1, res);

                        if (res < debugMinFrameRes)
                            debugMinFrameRes = res;
                        if (res > debugMaxFrameRes)
                            debugMaxFrameRes = res;

                        sourceImage = getImageFromWaveletEncoding(frame, res);
                        if (sourceImage == null)
                            continue;
                        drawImageIntoRequest(reqImage, reqSector, sourceImage, frameSector);
                    }

                }

                Logging.logger().info("   " + numFramesInRequest + " frames in req," +
                    " min/max footprint: " + debugMinFrameRes + ", " + debugMaxFrameRes);

                return new BufferedImageFormatter(reqImage);
            }
            catch (Exception ex)
            {
                String msg = Logging.getMessage( "WMS.RequestFailed", ex.getMessage() );
                Logging.logger().log(java.util.logging.Level.SEVERE, msg, ex);
                throw new WMSServiceException( msg );
            }
        }

        private void drawImageIntoRequest(BufferedImage reqImage, Sector reqSector, BufferedImage srcImage, Sector srcSector)
        {

            double tx = (srcSector.getMinLongitude().degrees - reqSector.getMinLongitude().degrees) * (
                reqImage.getWidth() / reqSector.getDeltaLonDegrees());
            double ty = (reqSector.getMaxLatitude().degrees - srcSector.getMaxLatitude().degrees) * (
                reqImage.getHeight() / reqSector.getDeltaLatDegrees());
            double sx = (reqImage.getWidth() / reqSector.getDeltaLonDegrees()) * (
                srcSector.getDeltaLonDegrees() / srcImage.getWidth());
            double sy = (reqImage.getHeight() / reqSector.getDeltaLatDegrees()) * (
                srcSector.getDeltaLatDegrees() / srcImage.getHeight());

            Graphics2D g2d = (Graphics2D) reqImage.getGraphics();
            AffineTransform xform = g2d.getTransform();
            g2d.translate(tx, ty);
            g2d.scale(sx, sy);
            g2d.drawRenderedImage(srcImage, null);
            g2d.setTransform(xform);
        }

        public List<File> serviceRequest(WMSGetImageryListRequest req) throws IOException, WMSServiceException
        {
            try
            {
                // Identify which frame files we'll need to satisfy this request...
                Sector reqSector = Sector.fromDegrees(req.getBBoxYMin(), req.getBBoxYMax(),
                    req.getBBoxXMin(), req.getBBoxXMax());

                List<File> files = new ArrayList<File>(100);

                for (FrameFile frame : RPFGenerator.this.frameFiles)
                {
                    try
                    {
                        // The call to getSector() can throw an exception if the file is
                        // named with an inappropriate frameNumber for the dataseries/zone.
                        // Also, serving the 5MGlobalNavChart data has been known to throw NPEs
                        // because some of those files have parse errors.
                        // We don't want these to short circuit the entire request, so
                        // trap any such occurances and ignore 'em.
                        if (!reqSector.intersects(frame.file.getSector()))
                            continue;
                    }
                    catch (Exception ex)
                    {
                        /* ignore this framefile */
                        continue;
                    }

                    // Frame overlaps request; add it to our list...
                    File f = new File(RPFGenerator.this.rootDir + File.separator + frame.file.getPathToFilename());
                    files.add(f);
                }

                return files;
            }
            catch (Exception ex)
            {
                String msg = Logging.getMessage( "WMS.RequestFailed", ex.getMessage() );
                Logging.logger().log(java.util.logging.Level.SEVERE, msg, ex);
                throw new WMSServiceException( msg );
            }
        }

        //
        // Attempts to return the specified FrameFile as a BufferedImage. Returns null on failure.
        //
        private RPFFrameTransform.RPFImage[] getImageFromRPFSource(FrameFile frame)
        {
            BufferedImage sourceImage = null;
            try
            {
                RPFImageFile sourceFile = RPFImageFile.load(new File(
                    RPFGenerator.this.rootDir + File.separator + frame.file.getPathToFilename()));
                sourceImage = sourceFile.getBufferedImage();
            }
            catch (Exception ex)
            {
                Logging.logger().severe("Failed to load frame file " + frame.file.getPathToFilename() + ": " + ex.toString());
            }

            return frame.file.getFrameTransform().deproject(frame.file.getFrameNumber(), sourceImage);
        }

        //
        // Attempts to reconstruct the given FrameFile as a BufferedImage from a WaveletEncoding.
        // Returns null if encoding does not exist or on any other failure.
        //
        private BufferedImage getImageFromWaveletEncoding(FrameFile frame, int resolution)
        {
            WaveletCodec codec = null;
            BufferedImage sourceImage = null;
            try
            {
                if (resolution <= 0 || null == frame )
                    return sourceImage;

                if (resolution <= RPFGenerator.this.preloadRes)
                {
                    if( null == frame.wvtCodec )
                    {
                        codec = WaveletCodec.loadPartially(getWaveletEncodingFile(frame), RPFGenerator.this.preloadRes );
                        frame.wvtCodec = codec;
                    }
                    else
                    {
                        codec = frame.wvtCodec;
                    }
                }
                else
                {   // read wavelet file...
                    codec = WaveletCodec.loadPartially(getWaveletEncodingFile(frame), resolution);
                }

                if (codec != null)
                    sourceImage = codec.reconstruct(resolution);
            }
            catch (Exception ex)
            {
                String msg = "Failed to reconstruct wavelet from " + frame.file.getPathToFilename();
                Logging.logger().log(java.util.logging.Level.SEVERE, msg, ex);
            }

            return sourceImage;
        }

        public void freeResources()
        { /* No-op */ }
    }

    // ----------------------------------------------------
    // class DataSeriesGrouper
    //
    // RPFCrawler.Grouper that identifies framefiles of a given dataseries.
    //
    private class DataSeriesGrouper extends RPFCrawler.RPFGrouper
    {

        public DataSeriesGrouper(String dataSeries)
        {
            super(RPFFrameProperty.DATA_SERIES);
            this.dataSeries = ("".equals(dataSeries)) ? null : dataSeries;
        }

        public void addToGroup(Object groupKey, File rpfFile, RPFFrameFilename rpfFrameFilename)
        {
            if (this.dataSeries == null || this.dataSeries.equals((String) groupKey))
                this.workList.add(rpfFile);
        }

        public File[] getFrameFiles()
        {
            File[] files = new File[this.workList.size()];
            return this.workList.toArray(files);
        }

        private String dataSeries;
        private List<File> workList = new ArrayList<File>(500);
    }

    // ----------------------------------------------------
    // class WMSRPFFrameFilename
    //
    // This class wraps a RPFFrameFilename, adding a reference to the frame file's
    // "transform" and its bounding rectangle (both of which are resolved lazily).
    //
    private static class WMSRPFFrameFilename
    {

        public WMSRPFFrameFilename(String path, String filename) throws IllegalArgumentException
        {
            this.filename = RPFFrameFilename.parseFilename(filename.toUpperCase());
            this.path = path + File.separator + filename;
        }

        public Sector getSector()
        {
            if (this.bounds == null)
            {
                RPFFrameTransform trans = getFrameTransform();
                this.bounds = trans.computeFrameCoverage(this.filename.getFrameNumber());
            }
            return this.bounds;
        }

        public RPFFrameTransform getFrameTransform()
        {
            if (this.transform == null)
            {
                RPFDataSeries dataSeries = RPFDataSeries.dataSeriesFor(this.filename.getDataSeriesCode());
                this.transform = RPFFrameTransform.createFrameTransform(this.filename.getZoneCode(),
                    dataSeries.rpfDataType, dataSeries.scaleOrGSD);
            }
            return this.transform;
        }

        public int getFrameNumber()
        {
            return this.filename.getFrameNumber();
        }

        public String getPathToFilename()
        {
            return this.path;
        }

        private RPFFrameFilename filename = null;
        private String path = null;
        private Sector bounds = null;
        private RPFFrameTransform transform = null;
    }

    // -----------------------------------------------
    // class FrameFile
    //
    // A small private class to bundle info about framefiles.
    // Public access to fields in intentional.
    //
    private static class FrameFile
    {
        public WMSRPFFrameFilename file;
        public WaveletCodec wvtCodec;
//        public int status = 0;  // 0 = not loaded, 1 = loaded, -1 = error (do not try to re-load)

        public FrameFile(WMSRPFFrameFilename file, WaveletCodec codec)
        {
            this.file = file;
            this.wvtCodec = codec;
        }

        public FrameFile(WMSRPFFrameFilename file)
        {
            this.file = file;
            this.wvtCodec = null;
        }
    }

    private String rootDir;
    private String encodingRootDir = null;
    private FrameFile frameFiles[];
    private Sector globalBnds;
    private String dataSeries = null;

    // performance tuning parameters...
    private int smallImageSize = 256;
    private int preloadRes = 32;

    // Configuration property keys...
    private static final String crsStr = "EPSG:4326";
    private static final String WAVELET_IMAGE_THRESHOLD = "wavelet_image_threshold";
    private static final String WAVELET_PRELOAD_SIZE = "wavelet_preload_size";
    private static final String WAVELET_ROOT_DIR = "wavelet_encoding_root_dir";
    private static final String RPF_DATA_SERIES = "data_series";
}
